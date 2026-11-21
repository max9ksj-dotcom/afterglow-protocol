package com.afterglow.protocol

// ===== 금화·보급 상자·충전 신전·능력치 책 (웹판 BK/bkOpts/chestOpen/shrineUse/woUpd/drawWo/drawWoEdge와 같음) =====
// 금화: 처치 시 획득(일반 1, 엘리트 15, 보스 50, 탐욕의 책으로 증가). 1페이지와 심층에서만 맵에 신전·상자가 놓인다(보스 러시 제외)
// 보급 상자: 금화를 내고 열면 선택지 3개 중 하나(책은 희귀 이상), 열 때마다 값 ×1.4 (처음 12)
// 충전 신전: 둘레 안에 4초 서 있으면 충전(벗어나면 천천히 줄어듦) → 능력치 축복 3개 중 하나(등급 무작위), 사용 후 30초 뒤 새 신전
// 능력치 책: 레벨업 선택지에 나오는 한 판용 강화, 3칸, 각 10레벨. 고를 때마다 등급(일반·희귀·영웅·전설)에 따라 상승량 ×1/1.5/2/3
class Bk(val n: String, val t: String, val v: Double, val ic: String)
val BK = linkedMapOf(
    "dm" to Bk("힘의 책", "피해", .05, "b_dm"), "iv" to Bk("속도의 책", "공격 속도", .04, "b_iv"), "mv" to Bk("질주의 책", "이동 속도", .04, "b_mv"),
    "hp" to Bk("생명의 책", "최대 체력", 8.0, "b_hp"), "cr" to Bk("치명의 책", "치명타 확률", .025, "b_cr"), "mg" to Bk("자석의 책", "수집 범위", .12, "b_mg"),
    "xp" to Bk("지식의 책", "경험치", .06, "b_xp"), "lk" to Bk("행운의 책", "높은 등급 확률", .15, "b_lk"), "gd" to Bk("탐욕의 책", "금화 획득", .15, "b_gd"))
val BKM = doubleArrayOf(1.0, 1.5, 2.0, 3.0)
const val BKX = 10
const val BKS = 3
val SHV = linkedMapOf("dm" to .08, "iv" to .06, "mv" to .06, "hp" to 15.0, "cr" to .04, "mg" to .2, "xp" to .1, "gd" to .25)
const val SHT = 4.0
const val SHR = 70.0
const val CHC = 12.0
const val CHG = 1.4
class Wo(val t: Char, var x: Double, var y: Double) { var c = 0.0; var nm = 0.0; var g = false; var ot = 0.0 }
/** 도트 그림 캐시: v = d(꺼진 신전) b(빛나는 신전) y(금색 화살표) */
private val WSC = HashMap<String, Spr>()
fun wos(k: String, v: String = ""): Spr = WSC.getOrPut(k + v) {
    val p = HashMap(WOP)
    when (v) { "d" -> { p['C'] = "#3f8fb0"; p['V'] = "#5fd8ff"; p['c'] = "#2c335c" }; "b" -> { p['c'] = "#e0faff"; p['C'] = "#e0faff"; p['V'] = "#ffffff" }; "y" -> p['f'] = "#ffd24a" }
    mkS(WOS[k]!!, p)
}

fun bv(k: String): Double = Sn?.bkv?.get(k) ?: 0.0
fun chCost(): Int = Math.round(CHC * Math.pow(CHG, S.chn.toDouble())).toInt()
fun rar(mn: Int = 0): Int {
    val L = bv("lk"); val w = doubleArrayOf(50.0, 30 * (1 + L), 15 * (1 + 1.5 * L), 5 * (1 + 2 * L))
    var t = 0.0; for (i in mn until 4) t += w[i]
    var x = rnd() * t
    for (i in mn until 4) { x -= w[i]; if (x <= 0) return i }
    return 3
}
private fun pct(v: Double): String { val r = Math.round(v * 1000) / 10.0; return if (r == Math.floor(r)) r.toLong().toString() else r.toString() }
fun bkFmt(k: String, v: Double): String = BK[k]!!.t + " +" + (if (k == "hp") Math.round(v).toString() else pct(v) + (if (k == "cr") "%p" else "%"))
fun bkAdd(k: String, v0: Double) {
    var v = v0
    if (k == "hp") { v = Math.round(v).toDouble(); P.mh += v; P.hp += v }
    S.bkv[k] = (S.bkv[k] ?: 0.0) + v
}
fun bkOpts(mn: Int): List<Opt> {
    val o = ArrayList<Opt>(); val n = S.bks.size
    for ((k, B) in BK) {
        val l = S.bks[k] ?: 0
        if (l >= BKX || (l == 0 && n >= BKS)) continue
        val r = rar(mn); val v = B.v * BKM[r]
        o.add(Opt("[" + RGN[r] + "] " + B.n + " Lv" + l + " → " + (l + 1), bkFmt(k, v), ic = B.ic, tg = "능력치 책", tc = RGC[r], lv = l, mx = BKX) { S.bks[k] = l + 1; bkAdd(k, v) })
    }
    return o
}
fun chestOpen(o: Wo) {
    burst(o.x, o.y, 30.0, "#ffd24a", 300.0, .7, 4.0); fxR(o.x, o.y, 90.0, "#ffd24a", .4); snd(520.0, .3, "triangle", .08)
    var q: List<Opt> = opts(1).shuffled()
    q = (q.filter { it.ev } + q.filter { !it.ev }).take(3)
    if (q.isEmpty()) q = listOf(Opt("체력 회복", "체력 +30") { P.hp = Math.min(P.mh, P.hp + 30) })
    choose("보급 상자 — 하나를 고르세요", q, "#ffd24a")
}
fun shrineUse(o: Wo) {
    burst(o.x, o.y, 36.0, "#5fd8ff", 340.0, .8, 4.0); fxR(o.x, o.y, 140.0, "#5fd8ff", .5); snd(740.0, .4, "sine", .08)
    val ks = SHV.keys.shuffled().take(3)
    choose("신전 충전 완료 — 축복 하나를 고르세요", ks.map { k ->
        val r = rar(0); val v = SHV[k]!! * BKM[r]
        Opt("[" + RGN[r] + "] 신전의 축복: " + BK[k]!!.t, bkFmt(k, v), ic = if (k == "gd") "pg" else k, tg = "신전", tc = RGC[r]) { bkAdd(k, v) }
    }, "#5fd8ff")
}
fun woOn(): Boolean { val s = Sn ?: return false; return s.run && !s.rush && !s.ended && (s.pg < 2 || s.pg >= 4) }
private fun woSpot(t: Char): Wo { val a = rnd() * TAU; val d = (650 + rnd() * 450) / Math.min(1.0, ZM); return Wo(t, P.x + cos(a) * d, P.y + sin(a) * d) }
fun woUpd(dt: Double) {
    if (!woOn()) return
    S.wst -= dt
    for (o in S.wo) if (o.g) o.ot -= dt
    S.wo.removeAll { (it.g && it.ot <= 0) || hyp(it.x - P.x, it.y - P.y) >= 2400 }
    val ns = S.wo.count { it.t == 's' && !it.g }; val nc = S.wo.count { it.t == 'c' && !it.g }
    if (ns < 2 && S.wst <= 0) S.wo.add(woSpot('s'))
    if (nc < 2) S.wo.add(woSpot('c'))
    for (o in S.wo) {
        if (o.g) continue
        val d = hyp(o.x - P.x, o.y - P.y)
        if (o.t == 's') {
            if (d < SHR) { o.c += dt / SHT; if (rnd() < .25) burst(o.x + (rnd() - .5) * SHR, o.y + (rnd() - .5) * SHR, 1.0, "#5fd8ff", 60.0, .5, 2.0) }
            else o.c = Math.max(0.0, o.c - dt / (SHT * 2))
            if (o.c >= 1) { o.g = true; o.ot = .6; S.wst = 30.0; shrineUse(o); return }
        } else {
            o.nm -= dt
            if (d < 30) {
                val c = chCost()
                if (S.gd >= c) { S.gd -= c; S.chn++; o.g = true; o.ot = .8; chestOpen(o); return }
                if (o.nm <= 0) { o.nm = 2.0; S.msg = "금화 부족 (" + Math.floor(S.gd).toInt() + "/" + c + ")"; S.mt = 1.0 }
            }
        }
    }
}
fun drawWo(g: G) {
    for (o in S.wo) {
        val x = Math.round(o.x).toDouble(); val y = Math.round(o.y).toDouble(); val al = if (o.g) Math.max(0.0, o.ot / (if (o.t == 's') .6 else .8)) else 1.0
        val s = o.t == 's'; val col = if (s) "#5fd8ff" else "#ffd24a"; val pu = .5 + .5 * sin(S.t * 3 + o.x)
        g.globalAlpha = al
        // 멀리서도 보이게: 바닥 빛 + 위로 솟는 빛기둥 (1.5배 크기 그림)
        if (!o.g) {
            g.lighter = true; g.fillStyle = col
            g.globalAlpha = .18 + .12 * pu; g.fillRect(x - 5, y - 170, 10.0, 150.0)
            g.globalAlpha = .1 + .08 * pu; g.fillRect(x - 10, y - 150, 20.0, 130.0)
            g.globalAlpha = .25 + .15 * pu; g.circleFill(x, y + 4, 30.0)
            g.lighter = false
            g.strokeStyle = col; g.lineWidth = 2.0; g.globalAlpha = al * (.45 + .35 * pu); g.circleStroke(x, y + 4, 34 + 4 * pu); g.globalAlpha = al
        }
        g.fillStyle = "rgba(0,0,0,.4)"; g.fillRect(x - 20, y + 10, 40.0, 6.0); g.fillRect(x - 14, y + 16, 28.0, 3.0)
        if (s) {
            val n = 24; val on = Math.floor(o.c * n).toInt()
            if (!o.g) for (i in 0 until n) {
                val a = -1.571 + i.toDouble() / n * TAU
                g.fillStyle = if (i < on) "#5fd8ff" else "rgba(95,216,255,.3)"; g.fillRect(Math.round(x + cos(a) * SHR) - 3.0, Math.round(y + sin(a) * SHR) - 3.0, 6.0, 6.0)
            }
            val v = if (o.g) "d" else if (o.c > .75 && Math.floor(S.t * 10).toInt() % 2 == 1) "b" else ""
            val sp = wos("sh", v); val bb = Math.round(sin(S.t * 2.5) * 3).toDouble()
            if (!o.g) { g.lighter = true; g.globalAlpha = .25 + .45 * o.c; g.fillStyle = "#5fd8ff"; g.circleFill(x, y - 50 + bb, 14 + 14 * o.c); g.lighter = false; g.globalAlpha = al }
            g.drawImage(sp, 0.0, 7.0, 16.0, 16.0, x - 24, y - 36, 48.0, 48.0); g.drawImage(sp, 0.0, 0.0, 16.0, 7.0, x - 24, y - 60 + bb, 48.0, 21.0)
        } else {
            val op = o.g; val sp = wos(if (op) "cho" else "ch"); val b = if (op) 0.0 else Math.round(sin(S.t * 3 + o.x) * 1.5).toDouble()
            if (op) { g.lighter = true; g.globalAlpha = .5 * al; g.fillStyle = "#ffd24a"; g.circleFill(x, y - 8, 34.0); g.lighter = false; g.globalAlpha = al }
            g.drawImage(sp, x - 24, y - 20 + b, 48.0, 33.0)
            if (!op) {
                val c = chCost(); val ok = S.gd >= c; val t = "" + c
                g.font(14.0, true); val w = 16 + 4 + g.measure(t); val lx = Math.round(x - w / 2).toDouble()
                g.drawImage(wos("coin"), lx, y - 44, 16.0, 16.0)
                g.lineWidth = 4.0; g.strokeStyle = "#14102a"; g.strokeText(t, lx + 20, y - 31); g.fillStyle = if (ok) "#ffd24a" else "#ff8a8a"; g.fillText(t, lx + 20, y - 31)
            }
        }
    }
    g.globalAlpha = 1.0
}
/** 화면 밖 신전(하늘색)·상자(금색) 방향: 가장자리에 도트 화살표 + 작은 그림 */
fun drawWoEdge(g: G, cm: DoubleArray) {
    if (!woOn()) return
    for (o in S.wo) {
        if (o.g) continue
        val sx = W / 2 + ZM * (o.x - cm[0]); val sy = H / 2 + ZM * (o.y - cm[1])
        if (sx > 0 && sx < W && sy > 0 && sy < H) continue
        val a = Math.atan2(sy - H / 2, sx - W / 2); val m = 20.0
        val k = Math.min((W / 2 - m) / Math.max(.001, Math.abs(cos(a))), (H / 2 - m) / Math.max(.001, Math.abs(sin(a))))
        val x = W / 2 + cos(a) * k; val y = H / 2 + sin(a) * k; val s = o.t == 's'; val ic = wos(if (s) "shi" else "coin")
        g.save(); g.globalAlpha = .85; g.translate(Math.round(x).toDouble(), Math.round(y).toDouble()); g.rotate(a); g.drawImage(wos("arr", if (s) "" else "y"), -3.0, -7.0, 14.0, 14.0); g.restore()
        g.globalAlpha = .85; g.drawImage(ic, Math.round(x - cos(a) * 18 - ic.w).toDouble(), Math.round(y - sin(a) * 18 - ic.h).toDouble(), ic.w * 2, ic.h * 2); g.globalAlpha = 1.0
    }
}
/** 금화: HUD 첫 줄 오른쪽에 금화 그림 + 숫자 (신전·상자가 있는 곳에서만) */
fun drawGold(g: G, bw: Double) {
    if (S.rush || (S.pg >= 2 && S.pg < 4)) return
    val t = "" + Math.floor(S.gd).toInt(); g.font(11.0, true); val x = Math.round(12 + bw - g.measure(t)).toDouble()
    g.drawImage(wos("coin"), x - 14, 33.0, 10.0, 10.0); g.fillStyle = "#ffd24a"; g.fillText(t, x, 42.0)
}
