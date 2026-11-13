# 잔광 프로토콜 배경 음악 2곡을 직접 작곡·합성한다 (외부 음원 없음).
#  game: A단조 120BPM 16마디(32초) 신스웨이브 — 아르페지오 + 베이스 + 멜로디
#  boss: E단조 150BPM 20마디(32초) — 질주하는 16분 베이스 + 날카로운 리드
# 끝부분 잔향을 앞으로 감아 넣어 끊김 없이 반복된다.
import sys, wave
import numpy as np

SR = 44100
rng = np.random.default_rng(7)


def mtof(m): return 440.0 * 2 ** ((m - 69) / 12)


def addh(f, t, kind, n=None):
    """대역 제한 파형(배음 합성, 앨리어싱 없음)"""
    nyq = SR / 2
    out = np.zeros_like(t)
    k = 1
    while k * f < nyq * .9 and k < 60:
        if kind == 'saw':
            out += np.sin(2 * np.pi * k * f * t) / k
        elif kind == 'sq':
            if k % 2: out += np.sin(2 * np.pi * k * f * t) / k
        elif kind == 'tri':
            if k % 2: out += ((-1) ** ((k - 1) // 2)) * np.sin(2 * np.pi * k * f * t) / (k * k)
        elif kind == 'pulse':  # 25% 펄스
            out += np.sin(np.pi * k * .25) * np.cos(2 * np.pi * k * f * t) / k
        k += 1
    return out


def env(n, a, d, s, r, dur_s):
    t = np.arange(n) / SR
    e = np.ones(n) * s
    if a > 0: e = np.where(t < a, t / a, e)
    m = (t >= a) & (t < a + d)
    e[m] = 1 - (1 - s) * (t[m] - a) / max(d, 1e-6)
    rel = t > dur_s
    e[rel] *= np.exp(-(t[rel] - dur_s) / max(r, 1e-6))
    return e


def lp(x, cut):
    """한 단 저역 통과(부드럽게)"""
    a = np.exp(-2 * np.pi * cut / SR)
    y = np.empty_like(x); z = 0.0
    for i in range(len(x)):
        z = (1 - a) * x[i] + a * z; y[i] = z
    return y


class Track:
    def __init__(self, bpm, bars):
        self.bpm = bpm; self.beat = 60 / bpm; self.L = int(round(bars * 4 * self.beat * SR))
        self.buf = {}

    def bus(self, name):
        if name not in self.buf: self.buf[name] = np.zeros(self.L + SR * 3)
        return self.buf[name]

    def put(self, name, start_beat, sig):
        b = self.bus(name); i = int(round(start_beat * self.beat * SR))
        j = min(len(b), i + len(sig)); b[i:j] += sig[:j - i]

    def note(self, name, start, dur, m, kind, vol, adsr=(.005, .1, .7, .15), vib=0, det=0):
        ds = dur * self.beat; n = int((ds + adsr[3] * 5) * SR)
        t = np.arange(n) / SR; f = mtof(m)
        if vib:
            ph = np.cumsum(2 * np.pi * f * (1 + vib * np.sin(2 * np.pi * 5.5 * t) * np.clip(t * 3 - .3, 0, 1)) / SR)
            tt = ph / (2 * np.pi * f)
        else:
            tt = t
        s = addh(f, tt, kind)
        if det: s = .5 * s + .5 * addh(f * (1 + det), tt, kind)
        self.put(name, start, s * env(n, *adsr[:3], adsr[3], ds) * vol)

    # 북소리
    def kick(self, b, v=1.0):
        n = int(.35 * SR); t = np.arange(n) / SR
        f = 45 + 110 * np.exp(-t * 28); ph = np.cumsum(2 * np.pi * f / SR)
        self.put('dr', b, (np.sin(ph) * np.exp(-t * 9) + .3 * np.exp(-t * 300) * rng.standard_normal(n) * .3) * v)

    def snare(self, b, v=1.0):
        n = int(.25 * SR); t = np.arange(n) / SR
        no = rng.standard_normal(n); no = no - lp(no, 1200)
        self.put('dr', b, (no * np.exp(-t * 16) * .55 + np.sin(2 * np.pi * 190 * t) * np.exp(-t * 22) * .5) * v)

    def hat(self, b, v=1.0, op=False):
        n = int((.25 if op else .06) * SR); t = np.arange(n) / SR
        no = rng.standard_normal(n); no = no - lp(no, 7000)
        self.put('dr', b, no * np.exp(-t * (10 if op else 70)) * .35 * v)

    def mix(self, gains, delay=None):
        out = np.zeros(self.L + SR * 3)
        for k, g in gains.items():
            if k not in self.buf: continue
            x = self.buf[k] * g
            if delay and k in delay:  # 박자에 맞춘 메아리
                dt, fb = delay[k]; d = int(dt * self.beat * SR); y = x.copy()
                for r in range(1, 5):
                    y[d * r:] += x[:len(x) - d * r] * fb ** r
                x = y
            out += x
        # 끝 잔향을 앞으로 감아 끊김 없는 반복
        o = out[:self.L].copy(); tail = out[self.L:]
        o[:len(tail)] += tail[:self.L]
        o = np.tanh(o * 1.1) / np.tanh(1.1)
        return o


CH = {'Am': [57, 60, 64], 'F': [53, 57, 60], 'C': [48, 52, 55], 'G': [55, 59, 62], 'E': [52, 56, 59], 'Dm': [50, 53, 57],
      'Em': [52, 55, 59], 'D': [50, 54, 57], 'B': [47, 51, 54]}
N = {n: i for i, n in enumerate(['C', 'C#', 'D', 'D#', 'E', 'F', 'F#', 'G', 'G#', 'A', 'A#', 'B'])}


def nm(s):  # 'E5' → MIDI
    o = int(s[-1]); return 12 * (o + 1) + N[s[:-1]]


def game():
    T = Track(120, 16)
    prog = ['Am', 'F', 'C', 'G', 'Am', 'F', 'C', 'E', 'F', 'G', 'Am', 'Am', 'F', 'G', 'E', 'E']
    for bar, c in enumerate(prog):
        b0 = bar * 4; ch = CH[c]; root = ch[0] - 12 if ch[0] >= 52 else ch[0]
        root = root if root < 48 else root - 12
        # 패드
        for m in ch: T.note('pad', b0, 4, m, 'saw', .05, (.4, .5, .8, .6), det=.004)
        # 베이스: 8분, 마디 끝은 한 옥타브 위로
        for i in range(8):
            m = root + (12 if i in (3, 7) and bar % 2 else 0)
            T.note('bass', b0 + i * .5, .42, m - 12 + 12, 'saw', .30, (.003, .08, .55, .05))
        # 아르페지오(16분, 2옥타브)
        pat = [0, 1, 2, 3, 4, 3, 2, 1]
        tones = ch + [ch[0] + 12, ch[1] + 12]
        for i in range(16):
            m = tones[pat[i % 8]] + 12
            T.note('arp', b0 + i * .25, .2, m, 'pulse', .10 if bar >= 4 else .13, (.002, .06, .3, .05))
        # 북
        for i in range(4):
            T.kick(b0 + i, .9 if i % 2 == 0 else .55)
            if i in (1, 3) and bar >= 2: T.snare(b0 + i, .8)
        if bar >= 1:
            for i in range(8): T.hat(b0 + i * .5 + .5 * 0, .5 if i % 2 else .3)
            T.hat(b0 + 3.5, .5, op=True)
        if bar in (7, 15):
            for i in range(4): T.snare(b0 + 3 + i * .25, .35 + i * .15)
    mel = {
        4: [('A4', 0, .5), ('C5', .5, .5), ('E5', 1, 1), ('D5', 2, .5), ('C5', 2.5, .5), ('D5', 3, 1)],
        5: [('C5', 0, 1.5), ('A4', 1.5, .5), ('C5', 2, 1), ('D5', 3, 1)],
        6: [('E5', 0, 1.5), ('G5', 1.5, .5), ('E5', 2, 1), ('D5', 3, .5), ('C5', 3.5, .5)],
        7: [('B4', 0, 2), ('G#4', 2, 1), ('B4', 3, 1)],
        8: [('A5', 0, 1), ('G5', 1, .5), ('F5', 1.5, .5), ('E5', 2, 1), ('C5', 3, 1)],
        9: [('D5', 0, 1), ('E5', 1, .5), ('D5', 1.5, .5), ('B4', 2, 2)],
        10: [('C5', 0, .5), ('E5', .5, .5), ('A5', 1, 1), ('G5', 2, .5), ('E5', 2.5, .5), ('C5', 3, 1)],
        11: [('E5', 0, 3)],
        12: [('A5', 0, 1), ('G5', 1, .5), ('F5', 1.5, .5), ('E5', 2, 1), ('F5', 3, 1)],
        13: [('G5', 0, 1.5), ('F5', 1.5, .5), ('E5', 2, 1), ('D5', 3, 1)],
        14: [('E5', 0, 1), ('D5', 1, .5), ('C5', 1.5, .5), ('B4', 2, 1), ('G#4', 3, 1)],
        15: [('B4', 0, 2), ('E5', 2, 2)],
    }
    for bar, ns in mel.items():
        for s, b, d in ns:
            T.note('lead', bar * 4 + b, d * .92, nm(s), 'sq', .16, (.01, .15, .75, .12), vib=.006, det=.003)
    return T.mix({'pad': 1, 'bass': 1, 'arp': 1, 'lead': 1, 'dr': .9}, delay={'arp': (.75, .35), 'lead': (.75, .28)})


def boss():
    T = Track(150, 20)
    prog = ['Em', 'C', 'D', 'B', 'Em', 'C', 'D', 'B', 'Am', 'F', 'Em', 'B', 'C', 'D', 'Em', 'F', 'Em', 'F', 'Em', 'B']
    for bar, c in enumerate(prog):
        b0 = bar * 4; ch = CH[c]; root = ch[0]
        while root >= 48: root -= 12
        for m in ch: T.note('pad', b0, 4, m + 12, 'saw', .04, (.2, .4, .8, .4), det=.006)
        # 질주 베이스: 16분, 다다닥 리듬
        for i in range(16):
            if i % 4 == 1 and bar % 4 != 3: continue
            m = root + (12 if i % 8 == 6 else 0)
            T.note('bass', b0 + i * .25, .2, m, 'saw', .30, (.002, .05, .5, .03))
        # 아르페지오(높게, 16분)
        tones = [ch[0] + 24, ch[2] + 12, ch[1] + 24, ch[2] + 24]
        if bar >= 8:
            for i in range(16): T.note('arp', b0 + i * .25, .15, tones[i % 4], 'pulse', .07, (.001, .04, .25, .04))
        # 북: 네 박 모두 킥, 2·4 스네어, 16분 하이햇. 마지막 4마디는 8분 더블 킥
        for i in range(4):
            T.kick(b0 + i, 1.0)
            if bar >= 16: T.kick(b0 + i + .5, .6)
            if i in (1, 3): T.snare(b0 + i, .9)
        for i in range(16): T.hat(b0 + i * .25, .45 if i % 2 else .25)
        if bar % 4 == 3:
            for i in range(8): T.snare(b0 + 2 + i * .25, .3 + i * .08)
    mel = {
        4: [('E5', 0, 1), ('B4', 1, .5), ('E5', 1.5, .5), ('G5', 2, 1), ('F#5', 3, 1)],
        5: [('E5', 0, 1.5), ('D5', 1.5, .5), ('C5', 2, 1), ('B4', 3, 1)],
        6: [('A4', 0, .5), ('B4', .5, .5), ('C5', 1, .5), ('D5', 1.5, .5), ('F#5', 2, 1), ('A5', 3, 1)],
        7: [('G5', 0, 1), ('F#5', 1, 1), ('D#5', 2, 2)],
        8: [('A5', 0, 1.5), ('G5', 1.5, .5), ('E5', 2, 1), ('C5', 3, 1)],
        9: [('F5', 0, 1.5), ('E5', 1.5, .5), ('C5', 2, 1), ('A4', 3, 1)],
        10: [('B4', 0, .5), ('E5', .5, .5), ('G5', 1, .5), ('B5', 1.5, .5), ('A5', 2, 1), ('G5', 3, 1)],
        11: [('F#5', 0, 2), ('D#5', 2, 1), ('B4', 3, 1)],
        12: [('E5', 0, .5), ('G5', .5, .5), ('C6', 1, 1), ('B5', 2, .5), ('G5', 2.5, .5), ('E5', 3, 1)],
        13: [('F#5', 0, .5), ('A5', .5, .5), ('D6', 1, 1), ('C6', 2, .5), ('A5', 2.5, .5), ('F#5', 3, 1)],
        14: [('G5', 0, 1), ('F#5', 1, .5), ('E5', 1.5, .5), ('B5', 2, 2)],
        15: [('C6', 0, 1), ('B5', 1, .5), ('A5', 1.5, .5), ('F5', 2, 2)],
        16: [('E5', 0, 2), ('G5', 2, 2)],
        17: [('F5', 0, 2), ('A5', 2, 2)],
        18: [('G5', 0, 2), ('B5', 2, 2)],
        19: [('D#5', 0, 4)],
    }
    for bar, ns in mel.items():
        for s, b, d in ns:
            T.note('lead', bar * 4 + b, d * .9, nm(s), 'saw', .13, (.005, .1, .7, .1), vib=.008, det=.005)
    return T.mix({'pad': 1, 'bass': 1, 'arp': 1, 'lead': 1, 'dr': 1}, delay={'lead': (.75, .25), 'arp': (.5, .3)})


def save(x, path, sr, target_rms):
    x = x / (np.sqrt(np.mean(x ** 2)) + 1e-9) * target_rms
    x = np.clip(x, -.98, .98)
    if sr != SR:  # 반으로 줄이기 전 저역 통과(창 씌운 sinc)
        k = np.arange(-64, 65); h = .45 * np.sinc(.45 * k) * np.hamming(129)
        x = np.convolve(np.concatenate([x[-64:], x, x[:64]]), h, mode='same')[64:-64][::SR // sr]
    w = wave.open(path, 'wb'); w.setnchannels(1); w.setsampwidth(2); w.setframerate(sr)
    w.writeframes((x * 32767).astype('<i2').tobytes()); w.close()


if __name__ == '__main__':
    out = sys.argv[1]; rms = float(sys.argv[2]) if len(sys.argv) > 2 else .16
    for name, fn in (('game', game), ('boss', boss)):
        x = fn()
        save(x, f'{out}/bgm_{name}_44k.wav', 44100, rms)
        save(x, f'{out}/bgm_{name}_22k.wav', 22050, rms)
        print(name, len(x) / SR, 's')
