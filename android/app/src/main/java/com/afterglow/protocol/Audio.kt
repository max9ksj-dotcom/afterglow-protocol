package com.afterglow.protocol

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.media.MediaPlayer

// ===== 배경 음악: 원본 WAV(44.1kHz 스테레오)를 그대로 재생. 1페이지 게임 BGM, 2페이지부터 보스 BGM =====
private val BGV = doubleArrayOf(0.0, .3, .6)
var BGC: String? = null
private var mpGame: MediaPlayer? = null
private var mpBoss: MediaPlayer? = null

fun bgmInit(ctx: Context) {
    if (mpGame == null) mpGame = MediaPlayer.create(ctx.applicationContext, R.raw.bgm_game)?.apply { isLooping = true }
    if (mpBoss == null) mpBoss = MediaPlayer.create(ctx.applicationContext, R.raw.bgm_boss)?.apply { isLooping = true }
}
fun bgmSet(k: String?) {
    BGC = k
    val want = if (k != null && SV.vol > 0) k else null
    for ((n, m) in listOf("game" to mpGame, "boss" to mpBoss)) {
        if (m == null) continue
        try {
            if (n == want) {
                val v = BGV[SV.vol].toFloat()
                m.setVolume(v, v)
                if (!m.isPlaying) m.start()
            } else if (m.isPlaying) { m.pause(); m.seekTo(0) }
        } catch (e: Exception) { }
    }
}
fun bgmPause() {
    for (m in listOf(mpGame, mpBoss)) try { if (m != null && m.isPlaying) m.pause() } catch (e: Exception) { }
}

// ===== 효과음: 원본처럼 파형을 직접 만들어 낸다(주파수가 떨어지는 음 + 저역 통과 필터) =====
private class Voice(val f0: Double, val f1: Double, val d: Double, val ty: Int, val v: Double) {
    var t = 0.0
    var ph = 0.0
}

object Synth {
    private const val RATE = 22050
    private const val BUF = 512
    private val voices = ArrayList<Voice>()
    @Volatile private var running = false
    @Volatile var master = .6
    private var th: Thread? = null

    fun start() {
        if (running) return
        running = true
        th = Thread({ loop() }, "sfx").apply { isDaemon = true; start() }
    }
    fun stop() { running = false; th = null; synchronized(voices) { voices.clear() } }

    fun play(f: Double, d: Double, ty: String, v: Double) {
        val k = when (ty) { "sine" -> 0; "triangle" -> 1; else -> 2 }
        synchronized(voices) { if (voices.size < 24) voices.add(Voice(f, Math.max(40.0, f * .55), d, k, v)) }
    }

    private fun wave(ty: Int, p: Double): Double = when (ty) {
        0 -> Math.sin(p * 6.283185307)
        1 -> 4 * Math.abs(p - .5) - 1
        else -> 2 * p - 1
    }

    private fun loop() {
        val min = AudioTrack.getMinBufferSize(RATE, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT)
        val tr = try {
            AudioTrack.Builder()
                .setAudioAttributes(AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build())
                .setAudioFormat(AudioFormat.Builder().setEncoding(AudioFormat.ENCODING_PCM_16BIT).setSampleRate(RATE).setChannelMask(AudioFormat.CHANNEL_OUT_MONO).build())
                .setBufferSizeInBytes(Math.max(min, BUF * 4)).setTransferMode(AudioTrack.MODE_STREAM).build()
        } catch (e: Exception) { running = false; return }
        tr.play()
        // 2400Hz 저역 통과 필터(원본의 lowpass, Q .5)
        val w = 2 * Math.PI * 2400 / RATE
        val al = Math.sin(w) / (2 * .5)
        val cs = Math.cos(w)
        val a0 = 1 + al
        val b0 = (1 - cs) / 2 / a0; val b1 = (1 - cs) / a0; val b2 = b0
        val a1 = -2 * cs / a0; val a2 = (1 - al) / a0
        var x1 = 0.0; var x2 = 0.0; var y1 = 0.0; var y2 = 0.0
        val out = ShortArray(BUF)
        val dt = 1.0 / RATE
        while (running) {
            synchronized(voices) {
                for (i in 0 until BUF) {
                    var s = 0.0
                    for (o in voices) {
                        if (o.t >= o.d) continue
                        val u = o.t / o.d
                        val f = o.f0 * Math.pow(o.f1 / o.f0, u)
                        o.ph += f * dt
                        if (o.ph >= 1) o.ph -= Math.floor(o.ph)
                        val g = if (o.t < .008) .0001 + (o.v - .0001) * o.t / .008
                        else o.v * Math.pow(.0005 / o.v, (o.t - .008) / Math.max(.001, o.d - .008))
                        s += wave(o.ty, o.ph) * g
                        o.t += dt
                    }
                    val y = b0 * s + b1 * x1 + b2 * x2 - a1 * y1 - a2 * y2
                    x2 = x1; x1 = s; y2 = y1; y1 = y
                    out[i] = (y * master * 32767).coerceIn(-32767.0, 32767.0).toInt().toShort()
                }
                voices.removeAll { it.t >= it.d }
            }
            tr.write(out, 0, BUF)
        }
        try { tr.stop(); tr.release() } catch (e: Exception) { }
    }
}

private val VOLS = doubleArrayOf(0.0, .35, .6)
private var sndL = 0.0
private var sndH = 0.0
fun snd(f0: Double, d0: Double, ty0: String, v: Double) {
    if (FX == 0 || SV.vol == 0) return
    val t = System.nanoTime() / 1e9
    val hi = f0 > 600
    if (t - sndL < .04) return
    if (hi && t - sndH < .12) return
    sndL = t
    if (hi) sndH = t
    val f = Math.min(f0, 1100.0)
    var ty = ty0
    if (ty == "square" || (ty == "sawtooth" && f > 300)) ty = "triangle"
    val d = Math.max(d0, .07) * 1.15
    Synth.master = VOLS[SV.vol]
    Synth.play(f, d, ty, Math.max(.0002, v * Math.min(1.0, FA()) * (if (hi) .6 else 1.0)))
}
