package com.afterglow.protocol

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RadialGradient
import android.graphics.Rect
import android.graphics.Shader

// ===== 도트 그림 준비 =====
class Es(val n: Spr, val n2: Spr, val w: Spr, val w2: Spr, val f: Spr, val f2: Spr)
class Hs(val n: Spr, val w: Spr)
class Gs(val n: Spr, val sc: Double, val gl: String, val gr: Double)

val ES = HashMap<String, Es>()
val HSC = HashMap<String, List<Hs>>()
val GS = HashMap<String, Gs>()
private val WSPR = HashMap<String, Spr>()
private val RIC = HashMap<String, Spr>()
private var TL: List<List<Spr>> = emptyList()
private val TINT = listOf("rgba(90,40,130,.09)", "rgba(30,100,120,.09)", "rgba(130,60,30,.07)", "rgba(0,0,0,0)")

private fun MR(r: String) = r + r.reversed()
private fun frB(c: Spr, y0: Int, y1: Int, dl: Int, dr: Int, dy: Int): Spr {
    val b = Bitmap.createBitmap(c.b.width, c.b.height, Bitmap.Config.ARGB_8888)
    val x = Canvas(b); val w = c.b.width; val h = w shr 1
    x.drawBitmap(c.b, 0f, 0f, null)
    x.drawRect(0f, y0.toFloat(), w.toFloat(), y1.toFloat(), Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) })
    x.drawBitmap(c.b, Rect(0, y0, h, y1), Rect(dl, y0 + dy, dl + h, y1 + dy), null)
    x.drawBitmap(c.b, Rect(h, y0, w, y1), Rect(h + dr, y0 + dy, w + dr, y1 + dy), null)
    return Spr(b)
}
private fun EM(rows: List<String>, r: String, R: String, A: String, y0: Int, y1: Int, dl: Int, dr: Int, dy: Int): Es {
    val c = mkS(rows.map(::MR), mapOf('o' to "#1a1630", 'w' to "#fff", 'k' to "#111", 'r' to r, 'R' to R, 'A' to A, 'y' to "#ffe14a"))
    val c2 = frB(c, y0, y1, dl, dr, dy)
    return Es(c, c2, tintC(c, "#fff", 1.0), tintC(c2, "#fff", 1.0), tintC(c, "#9ef", .55), tintC(c2, "#9ef", .55))
}
private fun MG(rows: List<String>, c: String, C: String, A: String) =
    mkS(rows.map(::MR), mapOf('o' to "#ffffff", 'w' to "#ffffff", 'c' to c, 'C' to C, 'A' to A))

private fun rng(s0: Long): () -> Double {
    var s = s0 and 0xFFFFFFFFL
    return { s = (s * 1664525L + 1013904223L) and 0xFFFFFFFFL; s / 4294967296.0 }
}
private fun mkTile(kind: Int, seed: Int): Spr {
    val b = Bitmap.createBitmap(16, 16, Bitmap.Config.ARGB_8888)
    val x = Canvas(b); val r = rng(seed * 977L + kind * 131L + 7L); val p = Paint()
    fun px(c: String, a: Int, y: Int, w: Int = 1, h: Int = 1) { p.color = Col.parse(c); x.drawRect(a.toFloat(), y.toFloat(), (a + w).toFloat(), (y + h).toFloat(), p) }
    fun ri(n: Int) = (r() * n).toInt()
    px(listOf("#131830", "#151b34", "#12172d", "#161c36")[seed and 3], 0, 0, 16, 16)
    for (i in 0 until 16) { val c = if (r() < .5) "#1a2142" else "#0f1428"; val a = ri(16); val y = ri(16); px(c, a, y, 1 + ri(2)) }
    px("#1c2447", 0, 0, 16, 1); px("#1c2447", 0, 0, 1, 16)
    if (kind == 1) {
        var a = (r() * 10 + 2).toInt()
        px("#090c1a", a, 0)
        for (y in 1 until 16) { a = Math.max(0, Math.min(14, a + ri(3) - 1)); px("#090c1a", a, y); if (r() < .2) px("#090c1a", a + 1, y) }
    }
    if (kind == 2) {
        val y = 4 + ri(8); val c = if (r() < .5) "#c97a2a" else "#2a8ab0"
        px(c, 0, y, 10); px(c, 10, y, 1, 5); px(c, 10, y + 5, 6); px("#ffd27a", 10, y + 5); px(c, 3, y - 3, 1, 3)
    }
    if (kind == 3) for (i in 0 until 3) {
        val a = 3 + ri(9); val bb = 7 + ri(6); val h = 3 + ri(4); val c = if (r() < .5) "#5fd8ff" else "#c77dff"
        px(c, a, bb - h, 1, h); px("#fff", a, bb - h); px("#2a5a7a", a + 1, bb - h + 1, 1, h - 1)
    }
    if (kind == 4) for (i in 0 until 4) {
        val a = ri(13); val bb = ri(13)
        px("#3a4066", a, bb, 3, 2); px("#5a6290", a, bb, 2, 1); px("#1a1f38", a + 1, bb + 2, 2)
    }
    if (kind == 5) {
        val c = "#232b52"
        px(c, 1, 1, 14, 1); px(c, 1, 14, 14, 1); px(c, 1, 1, 1, 14); px(c, 14, 1, 1, 14)
        px("#3a4578", 2, 2); px("#3a4578", 13, 2); px("#3a4578", 2, 13); px("#3a4578", 13, 13)
    }
    return Spr(b)
}

fun initSprites() {
    if (ES.isNotEmpty()) return
    val s = dataSpr
    ES["crawl"] = EM(s["CR"]!!, "#e55", "#f99", "#a33", 8, 10, 1, -1, 0)
    ES["charge"] = EM(s["CH"]!!, "#b86a3a", "#e0a070", "#6a3a1a", 8, 9, 1, -1, 0)
    ES["split"] = EM(s["SPL"]!!, "#6d4", "#af8", "#3a8", 4, 10, -1, 1, 0)
    ES["spit"] = EM(s["SPT"]!!, "#c4c", "#f8f", "#828", 8, 12, 1, 1, 0)
    ES["tank"] = EM(s["TK"]!!, "#6a8aa8", "#a8c4dc", "#3a5068", 11, 12, 1, -1, 0)
    ES["boss"] = EM(s["BS"]!!, "#8a2b3d", "#c4506a", "#4d1424", 21, 24, 1, -1, 0)
    ES["mini"] = ES["split"]!!
    ES["bomb"] = EM(s["BMB"]!!, "#59608f", "#8a93c8", "#2f3354", 11, 12, 1, -1, 0)
    ES["hboss"] = ES["boss"]!!
    val blank = Spr(Bitmap.createBitmap(14, 5, Bitmap.Config.ARGB_8888))
    ES["bat"] = Es(blank, blank, blank, blank, blank, blank)
    val g = dataGems
    GS["crawl"] = Gs(MG(g["G_DI"]!!, "#2a8cff", "#8fd0ff", "#1a4fb0"), 2.0, "#3a9cff", 14.0)
    GS["bat"] = Gs(MG(g["G_OR"]!!, "#ff4fa0", "#ff9fd0", "#b02068"), 2.0, "#ff4fa0", 15.0)
    GS["split"] = Gs(MG(g["G_DR"]!!, "#3ddc5a", "#9dffb0", "#1c8a35"), 2.0, "#3ddc5a", 15.0)
    GS["charge"] = Gs(MG(g["G_SQ"]!!, "#ff9a2a", "#ffd08a", "#b35a00"), 2.0, "#ff9a2a", 16.0)
    GS["bomb"] = Gs(MG(g["G_ST"]!!, "#ffe14a", "#fff4a0", "#c9a000"), 2.0, "#ffe14a", 17.0)
    GS["spit"] = Gs(MG(g["G_CR"]!!, "#b45cff", "#e0b0ff", "#6a24b0"), 2.0, "#b45cff", 18.0)
    GS["tank"] = Gs(MG(g["G_SQ"]!!, "#9fb4c8", "#e0eaf2", "#5a6e82"), 3.0, "#9fb4c8", 22.0)
    GS["boss"] = Gs(MG(g["G_ST"]!!, "#ff3b3b", "#ffa0a0", "#a01010"), 5.0, "#ff3b3b", 40.0)
    GS["mini"] = Gs(GS["split"]!!.n, 1.0, "#3ddc5a", 10.0)
    for (c in CHR) {
        val l = ArrayList<Hs>()
        val a = c.pal['a']!!; val A = c.pal['A']!!
        for (i in 0 until 3) for (q in 0 until 2) {
            val sc = if (q != 0) listOf(Triple(3, 7, a), Triple(2, 8, a), Triple(1, 8, A), Triple(0, 9, A)) else listOf(Triple(3, 7, a), Triple(2, 7, a), Triple(1, 8, A), Triple(0, 8, A))
            val cv = mkS(dataTop + dataLg[i], c.pal, sc + c.ex)
            l.add(Hs(cv, tintC(cv, "#ffffff", 1.0)))
        }
        HSC[c.id] = l
    }
    TL = List(6) { kd -> List(4) { sd -> mkTile(kd, sd) } }
}
fun wsp(k: String): Spr = WSPR.getOrPut(k) {
    val v = WV[k]!!
    val pal = hashMapOf('o' to "#14102a", 'w' to "#ffffff", 'y' to "#fff27a", 'C' to "#e8f0ff", 'c' to "#888", 'H' to "#ccc", 'A' to "#555")
    pal.putAll(v.second)
    mkS(WSH[v.first]!!, pal)
}
fun ric(k: String): Spr = RIC.getOrPut(k) { mkS(IC[k]!!, ICP) }

fun hsh(a: Int, b: Int): Long {
    var h = a * 374761393 + b * 668265263
    h = (h xor (h ushr 13)) * 1274126177
    return (h xor (h ushr 16)).toLong() and 0xFFFFFFFFL
}

// ===== 화면 버퍼 =====
var frame: Bitmap? = null
// 화면 폭에 따라 위쪽 정보가 버튼·카메라 구멍에 가려지지 않게 자리를 정한다 (단위 dp)
var TBW = 0.0
var TBH = 0.0
var SAT = 0.0
var SAL = 0.0
var SAR = 0.0
var SAB = 0.0
private var PY = 86.0
private var PH = 0.0
private var CL = false
fun hudBox() = Math.min(if (SV.hud == 1) 208.0 else 168.0, Math.max(150.0, W * .5))
private fun topLay() {
    val bo = S.bo; val hm = SV.hud; val w2 = Math.min(340.0, W - 24); val x0 = (W - w2) / 2
    val hb = 6 + (if (hm == 1) 112.0 else if (hm == 0) 66.0 else 50.0); val tbb = TBH + 6
    CL = x0 < 6 + hudBox() + 4 || x0 + w2 > W - TBW - 8 - SAL - SAR
    PY = if (CL) Math.max(86.0, Math.max(hb, tbb) + 4) else 86.0
    PH = if ((S.pg < 2 || S.pg >= 4) && bo != null && !bo.dead && bo.k == "boss" && S.cin <= 0) 30.0
    else if (S.pg >= 4 && S.T < 1e8 && S.mut != null) 48.0
    else if (S.pg == 2) 30.0
    else if (S.pg >= 3 && bo != null && !bo.dead && bo.wl != null) 58.0 else 0.0
}
const val RS = 1.0
fun snapshot(): Spr = Spr(frame!!.copy(Bitmap.Config.ARGB_8888, false), W, H)
private var bugBmp: Bitmap? = null
private var dkShader: Shader? = null
private var dkW = 0.0
private var dkH = 0.0
private var bugC: Canvas? = null

private fun scrT(t: String, chars: String, p: Double): String {
    val b = StringBuilder()
    for (c in t) b.append(if (c != ' ' && rnd() < p) chars[(rnd() * chars.length).toInt()] else c)
    return b.toString()
}
private fun scr(t: String) = if (S.pg >= 2 && S.bt > 0) scrT(t, "#%@?&░▓", .3) else t
private fun two(n: Int) = n.toString().padStart(2, '0')
private fun fl(v: Double) = Math.floor(v)
private fun rd(v: Double) = Math.round(v).toDouble()
private fun odd(v: Double) = Math.floor(v).toLong() % 2 != 0L

fun drawBossBar(g: G, e: En, name: String, tag: String, ticks: DoubleArray, col: String) {
    val w2 = Math.min(340.0, W - 24); val x0 = rd(W / 2 - w2 / 2); val r = Math.max(0.0, e.hp / e.mh)
    val tr = Math.max(r, Math.min(1.0, (e.tr ?: e.hp) / e.mh))
    g.fillStyle = "rgba(8,10,24,.7)"; g.fillRect(x0, 86.0, w2, 30.0); g.fillStyle = "#2a1018"; g.fillRect(x0 + 6, 104.0, w2 - 12, 9.0)
    g.fillStyle = "#ffffff"; g.globalAlpha = .75; g.fillRect(x0 + 6, 104.0, (w2 - 12) * tr, 9.0); g.globalAlpha = 1.0
    g.fillStyle = if (e.inv) "#9aa0c0" else col; g.fillRect(x0 + 6, 104.0, (w2 - 12) * r, 9.0)
    g.fillStyle = "rgba(255,255,255,.25)"; g.fillRect(x0 + 6, 104.0, (w2 - 12) * r, 2.0); g.fillStyle = "#000"
    for (t in ticks) g.fillRect(rd(x0 + 6 + (w2 - 12) * t) - 1, 102.0, 2.0, 13.0)
    g.font(12.0, true); g.textAlign = "left"; g.fillStyle = "#ffd0d0"; g.fillText(scr(name), x0 + 6, 99.0)
    g.textAlign = "right"; g.fillStyle = if (tag.isNotEmpty()) "#ff5a5a" else "#c8cce8"
    g.fillText(scr((if (tag.isNotEmpty()) "$tag  " else "") + Math.ceil(r * 100).toInt() + "%"), x0 + w2 - 6, 99.0); g.textAlign = "left"
}
fun drawBossArrow(g: G, e: En, cx: Double, cy: Double) {
    val sx = e.x + cx; val sy = e.y + cy; val m = 34.0
    if (sx > -10 && sx < W + 10 && sy > -10 && sy < H + 10) return
    val a = atan2(sy - H / 2, sx - W / 2); val hx = W / 2 - m; val hy = H / 2 - m
    val k = Math.min(hx / Math.max(1e-6, Math.abs(cos(a))), hy / Math.max(1e-6, Math.abs(sin(a))))
    val x = W / 2 + cos(a) * k; val y = H / 2 + sin(a) * k; val p = .6 + .4 * sin(S.t * 8)
    g.save(); g.translate(x, y); g.rotate(a); g.globalAlpha = p; g.fillStyle = "#ff3b3b"; g.strokeStyle = "#ffffff"; g.lineWidth = 2.0
    g.beginPath(); g.moveTo(16.0, 0.0); g.lineTo(-8.0, -11.0); g.lineTo(-3.0, 0.0); g.lineTo(-8.0, 11.0); g.closePath(); g.fill(); g.stroke(); g.restore(); g.globalAlpha = 1.0
}
fun drawEdgeWarn(g: G) {
    for (f in S.fx) if (f.t == 'e') {
        val bl = if (odd(f.l * 10)) 1.0 else .45; val th = 10.0
        g.globalAlpha = bl * .8; g.fillStyle = "#ff3b3b"
        for (s in f.s) {
            if (s == 'L') g.fillRect(0.0, 0.0, th, H)
            if (s == 'R') g.fillRect(W - th, 0.0, th, H)
            if (s == 'T') g.fillRect(0.0, 0.0, W, th)
            if (s == 'B') g.fillRect(0.0, H - th, W, th)
        }
        g.globalAlpha = bl; g.font(22.0, true); g.textAlign = "center"; g.fillStyle = "#ffffff"
        for (s in f.s) {
            val x = if (s == 'L') 28.0 else if (s == 'R') W - 28 else W / 2
            val y = if (s == 'T') 36.0 else if (s == 'B') H - 60 else H / 2
            g.fillText("⚠", x, y)
        }
        g.textAlign = "left"; g.globalAlpha = 1.0
    }
}
private fun drawBug(g: G) {
    val src = frame ?: return
    if (bugBmp == null || bugBmp!!.width != src.width || bugBmp!!.height != src.height) {
        bugBmp = Bitmap.createBitmap(src.width, src.height, Bitmap.Config.ARGB_8888); bugC = Canvas(bugBmp!!)
    }
    bugC!!.drawBitmap(src, 0f, 0f, null)
    val B = Spr(bugBmp!!, W, H)
    val k = Math.min(1.0, S.bt / .35) * doubleArrayOf(0.0, .5, 1.0, 1.3)[FX]
    g.save(); g.globalAlpha = 1.0
    for (i in 0 until 6) { val y = rnd() * H; val h = 8 + rnd() * 30; val dx = (rnd() - .5) * 80 * k; g.drawImage(B, 0.0, y, W, h, dx, y, W, h) }
    g.lighter = true; g.globalAlpha = .25 * k; g.drawImage(B, 5 * k, 0.0); g.drawImage(B, -5 * k, 0.0); g.lighter = false
    var i = 0
    while (i < 10 * k) {
        g.fillStyle = listOf("#ffffff", "#ff3b6b", "#3bf4ff")[i % 3]; g.globalAlpha = .4
        g.fillRect(rnd() * W, rnd() * H, 10 + rnd() * 80, 2 + rnd() * 4); i++
    }
    g.restore()
}
/** 적 탄환 그림: 크기 7가지를 한 번만 그려 두고 붙인다 (탄환이 많은 2·3페이지에서 원 3번 그리기 → 그림 1장) */
private val EPC = HashMap<Int, Spr>()
private fun epSpr(ri: Int): Spr = EPC.getOrPut(ri) {
    val r = ri / 4.0; val R = r + 7; val sz = Math.ceil(R * 2 + 4).toInt(); val c = sz / 2.0
    val b = Bitmap.createBitmap(sz, sz, Bitmap.Config.ARGB_8888); val cg = G(Canvas(b))
    cg.fillStyle = "rgba(255,60,100,.28)"; cg.circleFill(c, c, R); cg.fillStyle = "#ff3b6b"; cg.circleFill(c, c, r)
    cg.strokeStyle = "#ffffff"; cg.lineWidth = 2.0; cg.circleStroke(c, c, r); cg.fillStyle = "#ffffff"; cg.fillRect(c - 2, c - 2, 4.0, 4.0)
    Spr(b)
}
/** 2페이지 화면 금: 모양이 바뀌지 않으므로 한 번만 그려 두고 붙인다 (예전엔 매 프레임 굵은 선 3겹을 그려 4~5ms 걸림) */
private var ckSpr: Spr? = null
private var ckRef: Any? = null
private fun drawCracks(g: G) {
    val ck = S.ck ?: return
    var sp = ckSpr
    if (sp == null || ckRef !== ck || sp.w != W || sp.h != H) {
        sp?.b?.recycle()
        val b = Bitmap.createBitmap(Math.ceil(W * RS).toInt(), Math.ceil(H * RS).toInt(), Bitmap.Config.ARGB_8888)
        val c = Canvas(b); c.scale(RS.toFloat(), RS.toFloat())
        val cg = G(c); cg.roundJoin = true
        for ((w2, col, al) in listOf(Triple(7.0, "#000000", .55), Triple(3.0, "#99eeff", .7), Triple(1.2, "#ffffff", 1.0))) {
            cg.strokeStyle = col; cg.globalAlpha = al; cg.lineWidth = w2
            for (p in ck) { cg.beginPath(); cg.moveTo(p[0][0], p[0][1]); for (i in 1 until p.size) cg.lineTo(p[i][0], p[i][1]); cg.stroke() }
        }
        sp = Spr(b, W, H); ckSpr = sp; ckRef = ck
    }
    g.drawImage(sp, 0.0, 0.0, W, H)
}
private fun drawTf(g: G) {
    val t = S.tf
    g.save(); g.lighter = false
    val fl0 = if (t < .7) t / .7 else if (t < 2.4) 1.0 else Math.max(0.0, 1 - (t - 2.4) / 1.2)
    g.globalAlpha = Math.min(.94, fl0 * .94); g.fillStyle = "#ffffff"; g.fillRect(0.0, 0.0, W, H)
    if (t > .5 && t < 3.6) {
        val a = Math.min(1.0, (t - .5) / .4) * (if (t > 3) Math.max(0.0, 1 - (t - 3) / .6) else 1.0)
        val fs = rd(Math.min(W, H * 1.4) * .12); val m = Math.max(0.0, Math.min(1.0, (t - 1.2) / .9))
        val a1 = ES["hboss"]!!.n; val cs = S.rcs
        g.globalAlpha = a; g.textAlign = "center"; g.font(fs, true); g.fillStyle = "#10152a"; g.strokeStyle = "#ffffff"; g.lineWidth = 6.0
        g.strokeText("PAGE 3", W / 2, H * .4); g.fillText("PAGE 3", W / 2, H * .4); g.fit("버그를 걷어내고 원래의 세계로 — ERROR_CLONE", W - 16, rd(fs * .4), false, 10.0); g.fillText("버그를 걷어내고 원래의 세계로 — ERROR_CLONE", W / 2, H * .4 + fs * .7)
        g.globalAlpha = a * (1 - m); g.drawImage(a1, W / 2 - a1.w * 2, H * .64 - a1.h * 2, a1.w * 4, a1.h * 4)
        if (cs != null) { g.globalAlpha = a * m; g.drawImage(cs, W / 2 - cs.w * 2.5, H * .64 - cs.h * 2.5, cs.w * 5, cs.h * 5) }
    }
    g.restore()
}
private fun drawGlitch(g: G) {
    val t = S.gl; val k = S.snap!!
    val gi = if (t < 1.2) 0.0 else Math.min(1.0, (t - 1.2) / 1.6)
    val sp = if (t < 3.4) 0.0 else Math.min(1.0, (t - 3.4) / 1.5)
    val ez = sp * sp * (3 - 2 * sp)
    g.globalAlpha = 1.0; g.lighter = false; g.fillStyle = "#05060f"; g.fillRect(0.0, 0.0, W, H)
    if (sp <= 0) {
        val sh = Math.ceil(H / 22)
        var y = 0.0
        while (y < H) { val dx = if (rnd() < gi * .6) (rnd() - .5) * gi * 140 else 0.0; g.drawImage(k, 0.0, y, W, sh, dx, y, W, sh); y += sh }
        if (gi > .15) { g.lighter = true; g.globalAlpha = .3 * gi; g.drawImage(k, 7 * gi, 0.0); g.drawImage(k, -7 * gi, 2.0); g.lighter = false; g.globalAlpha = 1.0 }
    } else {
        g.fillShader(LinearGradient(0f, 0f, W.toFloat(), H.toFloat(), Col.parse("#1a0630"), Col.parse("#001a30"), Shader.TileMode.CLAMP), 0.0, 0.0, W, H)
        val off = ez * W * .58
        for (side in intArrayOf(-1, 1)) {
            g.save(); g.beginPath(); g.moveTo(if (side < 0) 0.0 else W, 0.0)
            for (p in S.zz) g.lineTo(p[0], p[1])
            g.lineTo(if (side < 0) 0.0 else W, H); g.closePath(); g.clip(); g.translate(side * off, 0.0); g.drawImage(k, 0.0, 0.0); g.restore()
        }
    }
    if (gi > 0 && sp < .9) {
        for (i in 0 until fl(26 * gi).toInt()) {
            g.fillStyle = listOf("#ffffff", "#ff3b6b", "#3bf4ff", "#000000")[i and 3]; g.globalAlpha = .25 + rnd() * .5
            g.fillRect(rnd() * W, rnd() * H, 20 + rnd() * 120, 2 + rnd() * 8)
        }
        g.globalAlpha = 1.0
    }
    if (t > 2.1 && sp < .6) {
        val cp = Math.min(1.0, (t - 2.1) / 1.1)
        for (p in S.cr) {
            val n = fl(cp * (p.size - 1)).toInt()
            if (n < 1) continue
            for ((w2, c) in listOf(Pair(6.0, "#000000"), Pair(2.0, "#ffffff"))) {
                g.strokeStyle = c; g.lineWidth = w2; g.beginPath(); g.moveTo(p[0][0], p[0][1]); for (i in 1..n) g.lineTo(p[i][0], p[i][1]); g.stroke()
            }
        }
    }
    g.textAlign = "center"
    val fs = rd(Math.min(W, H * 1.4) * .1)
    if (t < 3.6) {
        val ta = if (t > 3.2) Math.max(0.0, 1 - (t - 3.2) / .4) else Math.min(1.0, t / .4)
        val txt = if (gi > 0) scrT("생존 성공", "?#%@&░▓", gi * .45) else "생존 성공"
        g.font(fs, true)
        if (gi > 0) for ((ox, col) in listOf(Pair(-4 * gi, "#ff3b6b"), Pair(4 * gi, "#3bf4ff"))) {
            g.lighter = true; g.globalAlpha = ta * .8; g.fillStyle = col; g.fillText(txt, W / 2 + ox + (rnd() - .5) * gi * 20, H * .42)
        }
        g.lighter = false; g.globalAlpha = ta; g.fillStyle = "#ffffff"; g.strokeStyle = "#000000"; g.lineWidth = 6.0
        g.strokeText(txt, W / 2, H * .42); g.fillText(txt, W / 2, H * .42)
        if (t > 1) {
            val qa = Math.min(1.0, (t - 1) / .3); val sz = fs * (1.1 + sin(t * 30) * .15 * gi)
            g.font(rd(sz * 1.6), true); g.globalAlpha = qa * ta * (if (gi > .3 && sin(t * 40) > .6) .4 else 1.0)
            g.fillStyle = "#ff3b6b"; g.fillText("?", W / 2 + fs * 2.6, H * .42 + fs * .5)
        }
        g.globalAlpha = 1.0
    }
    if (sp > .25) {
        g.globalAlpha = Math.min(1.0, (sp - .25) / .5); g.font(rd(fs * 1.6), true); g.fillStyle = "#ffffff"; g.strokeStyle = "#ff3b6b"; g.lineWidth = 4.0
        g.strokeText("PAGE 2", W / 2, H * .48); g.fillText("PAGE 2", W / 2, H * .48); g.font(rd(fs * .45)); g.fillStyle = "#bfe8ff"
        g.fillText("오류의 세계", W / 2, H * .48 + fs * .9); g.globalAlpha = 1.0
    }
    g.textAlign = "left"
    if (t > 5) { g.globalAlpha = Math.min(1.0, (t - 5) / .6); g.fillStyle = "#05060f"; g.fillRect(0.0, 0.0, W, H); g.globalAlpha = 1.0 }
}
private fun camPos(): DoubleArray {
    val b = S.bo
    if (S.cin > 0 && b != null) {
        val w = Math.min(1.0, Math.min((2.6 - S.cin) / .6, S.cin / .6))
        return doubleArrayOf(P.x + (b.x - P.x) * w, P.y + (b.y - P.y) * w)
    }
    return doubleArrayOf(P.x, P.y)
}
private fun drawBack(g: G, px: Double, py: Double) {
    val d = P.dr; val keys = S.ws.keys.toList()
    val hk = if (S.lw != null && S.ws.containsKey(S.lw!!)) S.lw else keys.firstOrNull()
    keys.filter { it != hk }.forEachIndexed { i, k ->
        val c = wsp(k)
        g.save(); g.translate(px - d * 14, py - 12 + i * 7 + rd(sin(S.t * 3 + i))); g.rotate(-1.57); g.drawImage(c, -c.w / 2, -c.h / 2); g.restore()
    }
}
private fun drawHeld(g: G, px: Double, py: Double) {
    val keys = S.ws.keys.toList()
    if (keys.isEmpty()) return
    val d = P.dr
    val k = if (S.lw != null && S.ws.containsKey(S.lw!!)) S.lw!! else keys[0]
    val c = wsp(k); val dt = S.t - S.lt
    val ang = if (dt < .6) S.la else if (d > 0) .3 else Math.PI - .3
    val rec = Math.max(0.0, .12 - dt) * 30
    val swg = WV[k]!!.first
    val sw = if ((swg == "blade" || swg == "fz_slash") && dt < .2) (1 - dt / .2) * 1.3 * (if (cos(ang) < 0) 1 else -1) else 0.0
    g.save(); g.translate(px + d * 7, py + 3); g.rotate(ang + sw)
    if (cos(ang) < 0) g.scale(1.0, -1.0)
    g.scale(2.0, 2.0); g.drawImage(c, -2 - rec, -c.h / 2); g.restore()
}
private fun drawWorm(g: G, e: En) {
    val sc = e.sc; val dx = P.x - e.x; val dy = P.y - e.y; val ax = Math.abs(dx); val ay = Math.abs(dy)
    val cur = e.ax
    if (cur == null) e.ax = if (ax >= ay) 0 else 1
    else if (cur == 0 && ay > ax * 1.3) e.ax = 1
    else if (cur == 1 && ax > ay * 1.3) e.ax = 0
    val axis = e.ax!!
    val sg = if (axis == 0) (if (dx < 0) -1 else 1) else (if (dy < 0) -1 else 1)
    val fl0 = e.fl > 0; val sl = e.sl > 0; val t = S.t * 9 + e.ph
    val k = Math.floor((((t / 6.283) % 1) + 1) % 1 * WFR).toInt() % WFR
    val f = wormFrame(sc, if (fl0) 1 else if (sl) 2 else 0, k)
    g.save(); g.translate(rd(e.x), rd(e.y))
    wm.setValues(if (axis == 0) floatArrayOf(sg.toFloat(), 0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f) else floatArrayOf(0f, 1f, 0f, sg.toFloat(), 0f, 0f, 0f, 0f, 1f))
    g.c.concat(wm)
    g.drawImage(f.s, -f.ox, -f.oy)
    g.restore()
}
/** 지렁이 그림: 꿈틀거림 24단계 × 색 3가지(보통/맞음/느려짐)를 처음 쓸 때 한 번만 그려 둔다 (예전엔 매 프레임 점 60여 개를 찍음) */
private const val WFR = 24
private class WFrame(val s: Spr, val ox: Double, val oy: Double)
private val WFC = HashMap<String, WFrame>()
private val wm = android.graphics.Matrix()
private fun wormFrame(sc: Double, st: Int, k: Int): WFrame = WFC.getOrPut("$sc|$st|$k") {
    val pad = Math.ceil(sc * .9 + 1.7)
    val b = Bitmap.createBitmap(Math.ceil(14 * sc).toInt(), Math.ceil(5 * sc + pad * 2).toInt(), Bitmap.Config.ARGB_8888)
    val cg = G(Canvas(b)); val tk = k.toDouble() / WFR * 6.283
    for (p in WP) {
        val hh = ((p.x * 7 + p.y * 13) % 5) * .9
        val oz = rd(sin(tk + p.x * .7) * sc * .9 + sin(tk * 2 + hh * 3) * .7)
        cg.fillStyle = if (st == 1) "#ffffff" else if (st == 2) p.fc else p.c
        cg.fillRect(rd(p.x * sc), rd(p.y * sc) + pad + oz, sc, sc)
    }
    WFrame(Spr(b), 7 * sc, 2.5 * sc + pad)
}
/** 배경 덩어리: 8×8칸(256px)을 한 번만 그려 두고 다시 쓴다 (예전엔 매 프레임 칸 수백 개를 그림). 반짝이는 칸 목록도 함께 둔다 */
private class BgChunk(val s: Spr, val gl: List<LongArray>)
private val BGK = object : LinkedHashMap<Long, BgChunk>(64, .75f, true) {
    override fun removeEldestEntry(e: MutableMap.MutableEntry<Long, BgChunk>?): Boolean {
        if (size > bgLim) { e?.value?.s?.b?.recycle(); return true }
        return false
    }
}
private var bgLim = 48
private fun bgChunk(cx: Int, cy: Int): BgChunk {
    val key = (cx.toLong() shl 32) or (cy.toLong() and 0xffffffffL)
    BGK[key]?.let { return it }
    val b = Bitmap.createBitmap(256, 256, Bitmap.Config.ARGB_8888)
    val cg = G(Canvas(b)); val gl = ArrayList<LongArray>()
    for (j in 0 until 8) for (i in 0 until 8) {
        val tx = cx * 8 + i; val ty = cy * 8 + j
        val h = hsh(tx, ty); val r = (h % 100).toInt()
        val k = if (r < 68) 0 else if (r < 80) 5 else if (r < 88) 1 else if (r < 94) 2 else if (r < 98) 4 else 3
        cg.drawImage(TL[k][((h shr 8) and 3).toInt()], i * 32.0, j * 32.0, 32.0, 32.0)
        if (k == 2 || k == 3) gl.add(longArrayOf(tx.toLong(), ty.toLong(), k.toLong(), h))
    }
    cg.fillStyle = TINT[(hsh(cx, cy) and 3).toInt()]; cg.fillRect(0.0, 0.0, 256.0, 256.0)
    b.setHasAlpha(false) // 불투명 → 덮어 그리기가 빨라짐
    val c = BgChunk(Spr(b), gl); BGK[key] = c
    return c
}
private fun drawBG(g: G, cm: DoubleArray) {
    val T = 32
    val x0 = fl((cm[0] - W / 2) / T).toInt() - 1; val y0 = fl((cm[1] - H / 2) / T).toInt() - 1
    val x1 = x0 + Math.ceil(W / T).toInt() + 3; val y1 = y0 + Math.ceil(H / T).toInt() + 3
    val gl = ArrayList<LongArray>()
    val cx0 = x0 shr 3; val cy0 = y0 shr 3; val cx1 = x1 shr 3; val cy1 = y1 shr 3
    bgLim = Math.max(48, (cx1 - cx0 + 1) * (cy1 - cy0 + 1) * 2)
    for (cy in cy0..cy1) for (cx in cx0..cx1) {
        val c = bgChunk(cx, cy)
        g.drawImage(c.s, cx * 256.0, cy * 256.0, 256.0, 256.0)
        for (q in c.gl) if (q[0] >= x0 && q[0] <= x1 && q[1] >= y0 && q[1] <= y1) gl.add(q)
    }
    if (S.pg == 2 || S.pg >= 4) { g.fillStyle = if (S.pg >= 4) "rgba(0,30,80,.42)" else "rgba(60,0,100,.38)"; g.fillRect(x0 * 32.0, y0 * 32.0, (x1 - x0 + 1) * 32.0, (y1 - y0 + 1) * 32.0) }
    g.lighter = true
    for (q in gl) {
        g.fillStyle = if (q[2] == 2L) "#ffaa3c" else "#5adcff"
        g.globalAlpha = .05 + .04 * sin(S.t * 2 + q[3] % 7)
        g.fillRect(q[0] * 32.0 - 8, q[1] * 32.0 - 8, 48.0, 48.0)
    }
    g.lighter = false; g.globalAlpha = 1.0
}
data class SB(val lx: Double, val ly: Double, val rx: Double, val ry: Double)
fun stickBase() = SB(Math.max(80.0, W * .14), H - 100, W - Math.max(80.0, W * .14), H - 100)
private fun drawSticks(g: G) {
    val manual = tb("aa") <= 0; val bs = stickBase()
    g.save()
    fun one(j: Joy?, bx: Double, by: Double, col: String, lab: String) {
        val cx = j?.ox ?: bx; val cy = j?.oy ?: by
        g.globalAlpha = if (j != null) .9 else .4; g.strokeStyle = col; g.lineWidth = 2.0; g.circleStroke(cx, cy, SR)
        g.fillStyle = col; g.globalAlpha = if (j != null) .5 else .25
        g.circleFill(cx + (if (j != null) j.x * SR else 0.0), cy + (if (j != null) j.y * SR else 0.0), 20.0)
        if (lab.isNotEmpty() && j == null) { g.globalAlpha = .7; g.fillStyle = "#ffffff"; g.font(11.0, true); g.textAlign = "center"; g.fillText(lab, cx, cy - SR - 8) }
    }
    if (manual) { one(joy, bs.lx, bs.ly, "#ffffff", "이동"); one(aimJ, bs.rx, bs.ry, "#ffd24a", "조준") }
    else { if (joy != null) one(joy, bs.lx, bs.ly, "#ffffff", ""); if (aimJ != null) one(aimJ, bs.rx, bs.ry, "#ffd24a", "") }
    g.restore()
}
/** 화면 가장자리 그라데이션(어둡게/붉게)을 화면 크기 그림으로 한 번만 만들어 둔다.
 *  예전엔 매 프레임 픽셀마다 그라데이션을 계산해서 그리기 시간의 절반(6~8ms)을 썼다. 세기는 globalAlpha로 조절 */
private val OVL = HashMap<String, Spr>()
private var ovW = 0.0
private var ovH = 0.0
private fun ovl(key: String, r0: Double, r1: Double, rgb: Int, a: Int): Spr {
    if (ovW != W || ovH != H) { OVL.values.forEach { it.b.recycle() }; OVL.clear(); ovW = W; ovH = H }
    return OVL.getOrPut(key) {
        val w = Math.ceil(W * RS).toInt(); val h = Math.ceil(H * RS).toInt()
        val b = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val c = rgb and 0xFFFFFF
        val p = Paint().apply { shader = RadialGradient(w / 2f, h / 2f, (r1 * RS).toFloat(), intArrayOf(c, (a shl 24) or c), floatArrayOf((r0 / r1).toFloat(), 1f), Shader.TileMode.CLAMP) }
        Canvas(b).drawRect(0f, 0f, w.toFloat(), h.toFloat(), p)
        Spr(b, W, H)
    }
}
private var vgShader: Shader? = null
private var vgW = 0.0
private var vgH = 0.0

/** 메인 메뉴 화면이면 true (뒤에 지난 판 화면 대신 메뉴 배경을 그림) */
var MENU = true
/** 메인 메뉴 뒤 배경: 천천히 흐르는 회로 격자 + 떠오르는 잔광 입자 */
fun menuBG(g: G) {
    val t = System.nanoTime() / 1e9; val gs = 48.0
    g.fillStyle = "#1a1f3a"
    var x = -(t * 8) % gs; while (x < W) { g.fillRect(rd(x), 0.0, 1.0, H); x += gs }
    var y = -(t * 5) % gs; while (y < H) { g.fillRect(0.0, rd(y), W, 1.0); y += gs }
    for (i in 0 until 70) {
        val sp = 12 + hsh(i, 2) % 30; val z = 1 + hsh(i, 4) % 3
        val px = ((hsh(i, 1) % W.toLong().coerceAtLeast(1)) + Math.sin(t * .6 + i) * 18 + W) % W
        val py = H - ((hsh(i, 3) % H.toLong().coerceAtLeast(1) + t * sp) % (H + 40)) + 20
        g.globalAlpha = .25 + .45 * Math.abs(Math.sin(t * 1.3 + i))
        g.fillStyle = if (i % 3 != 0) "#ffb347" else "#5fd8ff"
        g.fillRect(rd(px), rd(py), z * 2.0, z * 2.0)
    }
    g.globalAlpha = 1.0
}
fun draw(g: G) {
    g.resetState()
    g.fillStyle = "#0d1020"; g.fillRect(0.0, 0.0, W, H)
    if (Sn == null || MENU) { menuBG(g); return }
    mk(0)
    if (S.gl > 0 && S.snap != null) { drawGlitch(g); return }
    val sx = (rnd() - .5) * S.sh; val sy = (rnd() - .5) * S.sh
    val cm = camPos(); val cx = W / 2 - cm[0] + sx; val cy = H / 2 - cm[1] + sy
    g.save(); g.translate(rd(cx), rd(cy))
    drawBG(g, cm)
    mk(1)
    for (mn in S.mn) {
        val ar = mn.ar <= 0; val pu = if (ar) .5 + .5 * sin(S.t * 12) else .3
        g.fillStyle = "#333"; g.fillRect(mn.x - 6, mn.y - 6, 12.0, 12.0)
        if (ar) { g.fillStyle = "#ff5a46"; g.globalAlpha = .4 + .6 * pu } else g.fillStyle = "#cb4"
        g.fillRect(mn.x - 3, mn.y - 3, 6.0, 6.0); g.globalAlpha = 1.0
        if (ar) { g.strokeStyle = "#ff785a"; g.globalAlpha = .15 + .2 * pu; g.lineWidth = 1.0; g.circleStroke(mn.x, mn.y, mn.r); g.globalAlpha = 1.0 }
    }
    if (FX >= 2 && S.gm.size <= 80) {
        g.lighter = true
        for (q in S.gm) { val s = GS[q.k] ?: GS["crawl"]!!; g.fillStyle = s.gl; g.globalAlpha = .2 + .1 * sin(S.t * 5 + q.x); g.circleFill(rd(q.x), rd(q.y), s.gr) }
        g.lighter = false; g.globalAlpha = 1.0
    }
    drawCaps(g)
    for (q in S.gm) {
        val s = GS[q.k] ?: GS["crawl"]!!; val w = s.n.w * s.sc; val h = s.n.h * s.sc; val b = rd(sin(S.t * 4 + q.x * .1) * 2)
        g.drawImage(s.n, rd(q.x - w / 2), rd(q.y - h / 2 + b), w, h)
    }
    val cvx = W / 2 + 240; val cvy = H / 2 + 240
    for (e in S.en) {
        if (!e.boss && (Math.abs(e.x - cm[0]) > cvx || Math.abs(e.y - cm[1]) > cvy)) continue // 화면 밖 적은 그리지 않음
        val s = ES[e.c] ?: ES["crawl"]!!
        val q = e.sq; val w = s.n.w * e.sc; val h = s.n.h * e.sc; val tel = e.st == 1 || e.tl > 0
        val F = odd(S.t * (if (e.boss) 3 else 7) + e.ph)
        val white = e.fl > 0 || (tel && odd(S.t * 16))
        val im = if (white) (if (F) s.w2 else s.w) else if (e.sl > 0) (if (F) s.f2 else s.f) else (if (F) s.n2 else s.n)
        val bb = if (e.k == "hboss") rd(sin(S.t * 2) * 6) else if (e.k == "bat") rd(sin(S.t * 14 + e.ph) * 3) else if (F && (e.k == "split" || e.k == "crawl")) -e.sc else 0.0
        if ((e.k == "charge" || e.k == "boss" || e.k == "rival") && e.st == 1) { g.strokeStyle = "rgba(255,80,60,.6)"; g.lineWidth = 2.0; g.line(e.x, e.y, e.x + e.dx * 220, e.y + e.dy * 220) }
        if (e.k == "boss" && e.rg) { g.save(); g.lighter = true; g.globalAlpha = .22 + .14 * sin(S.t * 10); g.fillStyle = "#ff3b3b"; g.circleFill(e.x, e.y, e.r * 1.9); g.restore() }
        g.fillStyle = "rgba(0,0,0,.3)"; g.fillRect(rd(e.x - w / 2), rd(e.y + e.r - 2), w, 4.0)
        if (e.k == "bat") drawWorm(g, e)
        else {
            g.save()
            g.translate(rd(e.x) + (if (S.pg >= 2 && e.k != "hboss" && rnd() < .1) rd((rnd() - .5) * 10) else 0.0),
                rd(e.y + e.r + bb + (if (e === S.bo && S.cin > 1.4) -(S.cin - 1.4) / 1.2 * 320 else 0.0)))
            g.scale(1 + .35 * q, 1 - .3 * q)
            if (e.k == "hboss" || e.k == "rival") {
                val n = 8; val sh = im.h / n
                for (i in 0 until n) {
                    val dx = if (rnd() < .22) rd((rnd() - .5) * e.sc * 4) else 0.0
                    g.drawImage(im, 0.0, i * sh, im.w, sh, -w / 2 + dx, -h + i * sh * e.sc, w, sh * e.sc)
                }
            } else g.drawImage(im, -w / 2, -h, w, h)
            g.restore()
        }
        if (e.el && !e.dead) {
            g.strokeStyle = "#ffd24a"; g.lineWidth = 2.0; g.circleStroke(rd(e.x), rd(e.y), e.r + 10 + sin(S.t * 6) * 2)
            g.fillStyle = "#2a1018"; g.fillRect(e.x - 24, e.y - e.r - 26, 48.0, 5.0); g.fillStyle = "#ffd24a"; g.fillRect(e.x - 24, e.y - e.r - 26, 48 * Math.max(0.0, e.hp / e.mh), 5.0)
        }
        val cw = e.cw
        if (e.k == "rival" && cw != null) {
            val c = wsp(cw); val aa = atan2(P.y - e.y, P.x - e.x)
            g.save(); g.translate(rd(e.x + cos(aa) * 34), rd(e.y + sin(aa) * 34)); g.rotate(aa)
            if (cos(aa) < 0) g.scale(1.0, -1.0)
            g.scale(3.0, 3.0); g.drawImage(c, -2.0, -c.h / 2); g.restore()
        }
        if (e.stn > 0) { g.strokeStyle = "#ff6"; g.lineWidth = 2.0; g.beginPath(); g.arc(e.x, e.y, e.r + 6, 0.0, TAU); g.stroke() }
        if (e.mk > 0) {
            val R = (e.r + 8) * (1 + .15 * sin(S.t * 20))
            g.strokeStyle = "#9ef"; g.lineWidth = 2.0; g.beginPath(); g.moveTo(e.x, e.y - R); g.lineTo(e.x + R, e.y); g.lineTo(e.x, e.y + R); g.lineTo(e.x - R, e.y); g.closePath(); g.stroke()
        }
    }
    g.lighter = true
    OB()?.let { o ->
        if (oVis() && !(oPh() > OON - .6 && odd(S.oct * 12))) {
            val r = o.r * stat().ar
            for (i in 0 until o.n) for (j in 0 until 5) {
                val a = S.oa - j * .1 + i * TAU / o.n; val z = o.sz - j * 1.6
                g.globalAlpha = 1 - j * .2; g.fillStyle = if (j != 0) o.c else "#ffffff"
                g.fillRect(P.x + cos(a) * r - z / 2, P.y + sin(a) * r - z / 2, z, z)
            }
        }
    }
    g.globalAlpha = 1.0
    if (S.dc > 0 && S.bg) {
        val s = if (S.pg >= 3 && S.bo?.k == "rival" && ES["rival"] != null) ES["rival"]!! else ES["boss"]!!
        val w = s.n.w * 4; val h = s.n.h * 4; val u = 1 - S.dc / 2.4; val sh = h * (1 - .5 * u)
        g.save(); g.translate(rd(S.bx + (rnd() - .5) * 8 * (1 - u)), rd(S.by + 40)); g.globalAlpha = Math.max(0.0, 1 - u * u)
        g.drawImage(if (odd(S.dc * 14)) s.w else s.n, -w / 2, -sh, w, sh); g.restore(); g.globalAlpha = 1.0
    }
    for (p in S.pr) {
        g.strokeStyle = "#6cf"; g.lineWidth = 4.0
        for (i in 1 until p.tr.size) { g.globalAlpha = i.toDouble() / p.tr.size * .6; g.line(p.tr[i - 1][0], p.tr[i - 1][1], p.tr[i][0], p.tr[i][1]) }
        g.globalAlpha = 1.0; g.fillStyle = "#fff"; g.fillRect(p.x - 3, p.y - 3, 6.0, 6.0)
    }
    mk(2)
    for (p in S.pt) { val u = p.l / p.m; val z = p.s * (.4 + u); g.globalAlpha = u; g.fillStyle = p.c; g.fillRect(p.x - z / 2, p.y - z / 2, z, z) }
    mk(3)
    for (f in S.fx) {
        val u = 1 - f.l / f.m
        g.globalAlpha = 1 - u
        when (f.t) {
            'l' -> {
                val w = f.w * (1 - u)
                g.roundCap = true; g.strokeStyle = f.c; g.lineWidth = w * 3; g.globalAlpha = (1 - u) * .4; g.line(f.x1, f.y1, f.x2, f.y2)
                g.globalAlpha = 1 - u; g.strokeStyle = "#fff"; g.lineWidth = w; g.line(f.x1, f.y1, f.x2, f.y2); g.roundCap = false
            }
            'z' -> {
                g.roundJoin = true
                for ((col, w, al) in listOf(Triple(f.c, 7.0, .45), Triple("#fff", 2.0, 1.0))) {
                    g.strokeStyle = col; g.lineWidth = w; g.globalAlpha = (1 - u) * al; g.beginPath()
                    f.pts!!.forEachIndexed { i, q -> if (i != 0) g.lineTo(q[0], q[1]) else g.moveTo(q[0], q[1]) }
                    g.stroke()
                }
                g.roundJoin = false
            }
            'r' -> { g.strokeStyle = f.c; g.lineWidth = 1 + 4 * (1 - u); g.circleStroke(f.x, f.y, f.r * (.3 + .7 * sin(u * 1.57))) }
            'o' -> { val rr = if (f.k) f.r * (1 + u * .3) else f.r * (1 - u); g.fillStyle = f.c; g.globalAlpha = (if (f.k) 1 - u else u) * .5; g.circleFill(f.x, f.y, Math.max(1.0, rr)) }
            'a' -> {
                val ha = f.ha; val a0 = f.a - ha; val a1 = a0 + 2 * ha * Math.min(1.0, u * 3.2)
                g.fillStyle = f.c; g.globalAlpha = (1 - u) * .8; g.beginPath(); g.arc(P.x, P.y, f.r, a0, a1); g.arc(P.x, P.y, f.r * (.55 + .25 * u), a1, a0, true); g.closePath(); g.fill()
                g.strokeStyle = "#fff"; g.lineWidth = 3.0; g.globalAlpha = 1 - u; g.beginPath(); g.arc(P.x, P.y, f.r, a0, a1); g.stroke()
            }
            't' -> {
                g.strokeStyle = "#ff4040"; g.globalAlpha = .12 + .5 * u; g.lineWidth = f.w * 2; g.line(f.x, f.y, f.x + cos(f.a) * f.len, f.y + sin(f.a) * f.len)
                g.globalAlpha = .9; g.lineWidth = 2.0; g.strokeStyle = "#ffffff"; g.line(f.x, f.y, f.x + cos(f.a) * f.len, f.y + sin(f.a) * f.len)
            }
            'd' -> {
                g.fillStyle = "#f33"; g.globalAlpha = .12 + .25 * u; g.circleFill(f.x, f.y, f.r)
                g.strokeStyle = "#ff5050"; g.globalAlpha = 1.0; g.lineWidth = 3.0; g.circleStroke(f.x, f.y, f.r * u); g.circleStroke(f.x, f.y, f.r)
            }
            'p' -> {
                val rr = 110 * (f.l / .35)
                g.strokeStyle = "#a8f"; g.lineWidth = 3.0; g.circleStroke(f.x, f.y, rr); g.fillStyle = "#fff"
                for (i in 0 until 6) { val an = i * 1.047 + u * 10; g.fillRect(f.x + cos(an) * rr * .7 - 2, f.y + sin(an) * rr * .7 - 2, 4.0, 4.0) }
                g.fillStyle = "#a8f"; g.circleFill(f.x, f.y, 10 + u * 14)
            }
            'G' -> {
                val a0 = Math.min(1.0, f.l / .3); val rr = f.r * (1 - (S.t * 1.5) % 1.0)
                g.strokeStyle = "#a8f"; g.lineWidth = 2.0; g.globalAlpha = .25 * a0; g.circleStroke(f.x, f.y, f.r); g.globalAlpha = .5 * a0; g.circleStroke(f.x, f.y, rr); g.globalAlpha = 1.0
            }
        }
    }
    g.lighter = false; g.roundJoin = false
    for (f in S.fx) if (f.t == 'm') {
        val u = 1 - f.l / f.m
        val tx = if (u < .25 || rnd() < .2) scrT(f.txt, "#%@?&░", .25) else f.txt
        g.globalAlpha = Math.min(1.0, f.l * 3); g.font(15.0, true); g.textAlign = "center"
        g.fillStyle = "#ff3b6b"; g.fillText(tx, f.x - 2 + (rnd() - .5) * 3, f.y - u * 20)
        g.fillStyle = "#3bf4ff"; g.fillText(tx, f.x + 2, f.y - u * 20 + 1)
        g.fillStyle = "#ffffff"; g.fillText(tx, f.x, f.y - u * 20); g.textAlign = "left"
    }
    for (f in S.fx) if (f.t == 'n') {
        val u = 1 - f.l / f.m
        val fs = (if (f.cr) 21.0 else 13.0) * (if (u < .12) 1 + (.12 - u) * 6 else 1.0)
        g.globalAlpha = Math.min(1.0, f.l * 3); g.font(fs, true); g.lineWidth = 3.0; g.strokeStyle = "#000"
        val tx = f.vs ?: f.v.toString(); g.strokeText(tx, f.x - 8, f.y - u * 30); g.fillStyle = if (f.cr) "#ffd24a" else "#fff"; g.fillText(tx, f.x - 8, f.y - u * 30)
    }
    g.globalAlpha = 1.0
    run {
        val mv = P.mvg; val i = (fl(P.at * 10).toLong() % 4).toInt()
        val fr = if (mv) intArrayOf(0, 1, 0, 2)[i] else 0
        val sc = (fl(P.at * (if (mv) 10 else 3)).toLong() % 2).toInt()
        val bob = if (!mv && sc != 0) 1.0 else 0.0
        val H2 = HSC[SV.ch]!![fr * 2 + sc]
        val im = if (P.inv > .45) H2.w else H2.n
        val px = rd(P.x); val py = rd(P.y)
        g.save(); g.lighter = true; g.fillStyle = "rgba(95,216,255,.13)"; g.circleFill(px, py, 22.0); g.restore()
        g.fillStyle = "rgba(0,0,0,.35)"; g.fillRect(px - 10, py + 14, 20.0, 4.0); drawBack(g, px, py)
        g.save(); g.translate(px, py - bob); g.scale(P.dr * 2, 2.0); g.globalAlpha = .8
        for ((ox, oy) in listOf(Pair(-1.0, 0.0), Pair(1.0, 0.0), Pair(0.0, -1.0), Pair(0.0, 1.0))) g.drawImage(H2.w, -8 + ox, -8 + oy)
        g.globalAlpha = 1.0
        if (P.inv > 0 && P.inv <= .45 && odd(P.inv * 20)) g.globalAlpha = .4
        g.drawImage(im, -8.0, -8.0); g.restore(); g.globalAlpha = 1.0; drawHeld(g, px, py - bob)
    }
    if (tb("aa") <= 0 && S.run && !S.pz) {
        val aa = aimAngle(); val qx = P.x; val qy = P.y
        g.save(); g.strokeStyle = "rgba(255,255,255,.4)"; g.lineWidth = 2.0; g.setLineDash(4.0, 6.0)
        g.line(qx + cos(aa) * 42, qy + sin(aa) * 42, qx + cos(aa) * 118, qy + sin(aa) * 118); g.clearLineDash()
        g.circleStroke(qx + cos(aa) * 130, qy + sin(aa) * 130, 7.0); g.restore()
    }
    val bo = S.bo
    if (bo != null && !bo.dead) for (o in S.os) for (i in 0 until 4) {
        val an = o.a + i * 1.571; val x = bo.x + cos(an) * 85; val y = bo.y + sin(an) * 85
        g.fillStyle = "#ff3b6b"; g.fillRect(x - 6, y - 6, 12.0, 12.0); g.strokeStyle = "#ffffff"; g.lineWidth = 2.0; g.strokeRect(x - 6, y - 6, 12.0, 12.0)
    }
    for (q in S.ep) { val s = epSpr(Math.round((7 + sin(S.t * 18 + q.x) * 1.2) * 4).toInt()); g.drawImage(s, rd(q.x - s.w / 2), rd(q.y - s.h / 2)) }
    g.restore()
    mk(4)
    if (S.pg == 2) drawCracks(g)
    if (bo != null && !bo.dead && (bo.k == "boss" || bo.k == "rival") && S.cin <= 0) drawBossArrow(g, bo, rd(cx), rd(cy))
    drawEdgeWarn(g)
    for (i in 0 until intArrayOf(0, 12, 40, 40)[FX]) {
        val sx2 = (((hsh(i, 1).toDouble() % W - cm[0] * .4 + S.t * (6 + hsh(i, 2) % 12)) % W) + W) % W
        val sy2 = (((hsh(i, 3).toDouble() % H - cm[1] * .4 - S.t * (10 + hsh(i, 4) % 16)) % H) + H) % H
        g.globalAlpha = .25 + .3 * Math.abs(sin(S.t * 1.5 + i))
        g.fillStyle = if (S.pg == 2) (if (i % 3 != 0) "#ff4fa0" else "#5fd8ff") else (if (i % 3 != 0) "#ffb347" else "#5fd8ff")
        g.fillRect(rd(sx2), rd(sy2), 2.0, 2.0)
    }
    g.globalAlpha = 1.0
    mk(5)
    g.drawImage(ovl("vg", Math.min(W, H) * .35, Math.max(W, H) * .75, 0x050610, 127), 0.0, 0.0, W, H)
    mk(6)
    if (S.pg == 2 && FX >= 2) { g.fillStyle = "rgba(0,0,0,.12)"; var y = 0.0; while (y < H) { g.fillRect(0.0, y, W, 1.0); y += 4 } }
    if (S.pg >= 4 && S.mut?.id == "dark") g.drawImage(ovl("dk", 100.0, 250.0, 0x000000, 237), 0.0, 0.0, W, H)
    if (S.run && P.hp > 0 && P.hp < P.mh * .3) {
        g.globalAlpha = (1 - P.hp / (P.mh * .3)) * .35 * (.6 + .4 * Math.abs(Math.sin(S.t * 3.2)))
        g.drawImage(ovl("lh", Math.min(W, H) * .38, Math.max(W, H) * .72, 0xFF1E32, 255), 0.0, 0.0, W, H); g.globalAlpha = 1.0
    }
    if (S.vg > 0) {
        g.globalAlpha = Math.min(.6, S.vg)
        g.drawImage(ovl("hit", Math.min(W, H) * .3, Math.max(W, H) * .7, 0xFF2828, 255), 0.0, 0.0, W, H); g.globalAlpha = 1.0
    }
    if (S.cin > 0) {
        val w = Math.min(1.0, Math.min((2.6 - S.cin) / .4, S.cin / .4)); val bh = H * .11 * w
        g.fillStyle = "#000"; g.globalAlpha = .3 * w; g.fillRect(0.0, 0.0, W, H); g.globalAlpha = 1.0; g.fillRect(0.0, 0.0, W, bh); g.fillRect(0.0, H - bh, W, bh)
        if (S.cin < 1.4) {
            g.globalAlpha = Math.min(1.0, Math.min((1.4 - S.cin) / .3, S.cin / .4)); g.textAlign = "center"; g.font(44.0, true); g.lineWidth = 6.0; g.strokeStyle = "#000"
            val ty = H * .78
            g.strokeText("잔광 거수", W / 2, ty); g.fillStyle = "#ff5a5a"; g.fillText("잔광 거수", W / 2, ty); g.font(16.0); g.fillStyle = "#ffd0d0"; g.fillText("— 심연의 수호자 —", W / 2, ty + 28)
            g.textAlign = "left"; g.globalAlpha = 1.0
        }
    }
    if (S.dc > 0) { val a = Math.max(0.0, 1 - S.dc / .6); if (a > 0) { g.fillStyle = "#ffffff"; g.globalAlpha = a; g.fillRect(0.0, 0.0, W, H); g.globalAlpha = 1.0 } }
    g.save(); g.translate(SAL, SAT)
    topLay()
    drawHud(g)
    g.save(); g.translate(0.0, PY - 86)
    if ((S.pg < 2 || S.pg >= 4) && bo != null && !bo.dead && bo.k == "boss" && S.cin <= 0) drawBossBar(g, bo, if (S.pg >= 4) "심층 수호자 — " + S.fl + "층" else "잔광 거수 — 심연의 수호자", if (bo.rg) "분노" else "", doubleArrayOf(.5), "#ff3b3b")
    val mu = S.mut
    if (S.pg >= 4 && S.T < 1e8 && mu != null) {
        val rem = Math.max(0.0, S.T - S.t); val w2 = Math.min(300.0, W - 24); val x0 = rd(W / 2 - w2 / 2)
        g.fillStyle = "rgba(8,10,24,.6)"; g.fillRect(x0, 86.0, w2, 30.0); g.fillStyle = "#5fd8ff"; g.fillRect(x0, 110.0, w2 * Math.max(0.0, rem / S.T), 5.0)
        g.font(13.0, true); g.textAlign = "center"; g.fillStyle = "#ffffff"
        g.fillText(scr("심층 " + S.fl + "층 · " + mu.n + "  " + Math.ceil(rem).toInt() + "초"), W / 2, 104.0)
        g.font(11.0, true); g.lineWidth = 3.0; g.strokeStyle = "#000"; g.strokeText(mu.d, W / 2, 130.0); g.fillStyle = "#9fe8ff"; g.fillText(mu.d, W / 2, 130.0); g.textAlign = "left"
    }
    if (S.pg == 2) {
        val rem = Math.max(0.0, S.T - S.t); val w2 = Math.min(300.0, W - 24); val x0 = rd(W / 2 - w2 / 2)
        g.fillStyle = "rgba(8,10,24,.6)"; g.fillRect(x0, 86.0, w2, 30.0); g.fillStyle = "#ff3b6b"; g.fillRect(x0, 110.0, w2 * Math.max(0.0, rem / S.T), 5.0)
        g.font(13.0, true); g.textAlign = "center"; g.fillStyle = "#ffffff"
        g.fillText(scr("ERROR.exe — 버텨라!  " + fl(rem / 60).toInt() + ":" + two(fl(rem % 60).toInt()) + "  P" + (if (S.t < 60) 1 else if (S.t < 120) 2 else 3)), W / 2, 104.0); g.textAlign = "left"
    } else if (S.pg >= 3 && bo != null && !bo.dead && bo.wl != null) {
        val e = bo; val w2 = Math.min(340.0, W - 24); val x0 = rd(W / 2 - w2 / 2); val cc = CHR.find { it.id == e.cid } ?: CHR[0]; val cw = (w2 - 12) / 5
        g.fillStyle = "rgba(8,10,24,.65)"; g.fillRect(x0, 86.0, w2, 58.0)
        val r3 = Math.max(0.0, e.hp / e.mh); val t3 = Math.max(r3, Math.min(1.0, (e.tr ?: e.hp) / e.mh))
        g.fillStyle = "#2a1018"; g.fillRect(x0 + 6, 104.0, w2 - 12, 9.0); g.fillStyle = "#ffffff"; g.globalAlpha = .75; g.fillRect(x0 + 6, 104.0, (w2 - 12) * t3, 9.0); g.globalAlpha = 1.0
        g.fillStyle = if (e.inv) "#9aa0c0" else "#ff3b6b"; g.fillRect(x0 + 6, 104.0, (w2 - 12) * r3, 9.0); g.fillStyle = "#000"
        for (tk in doubleArrayOf(.7, .4, .15)) g.fillRect(rd(x0 + 6 + (w2 - 12) * tk) - 1, 102.0, 2.0, 13.0)
        g.font(12.0, true); g.textAlign = "right"; g.fillStyle = "#c8cce8"; g.fillText(Math.ceil(r3 * 100).toInt().toString() + "%", x0 + w2 - 6, 99.0)
        g.textAlign = "left"; g.fillStyle = "#ffd0ee"; g.fillText(scr("ERROR_CLONE: " + cc.n + "  PHASE " + (e.ph.toInt() + 1)), x0 + 6, 99.0)
        e.wl!!.forEachIndexed { i, k ->
            val cs = wsp(k); val sc2 = if (cs.w > 10) 1.4 else 1.8; val x = x0 + 6 + i * cw
            g.strokeStyle = if (WD[k]!!.fz) "#ffd24a" else "#2c335c"; g.lineWidth = 1.0; g.strokeRect(x + .5, 119.5, cw - 3, 22.0)
            g.drawImage(cs, rd(x + (cw - 3) / 2 - cs.w * sc2 / 2), rd(130 - cs.h * sc2 / 2), rd(cs.w * sc2), rd(cs.h * sc2))
        }
    }
    g.restore()
    if (S.mt > 0) { g.fit(S.msg, W - 16 - SAL - SAR, 26.0, true, 13.0); g.fillStyle = "#f55"; g.textAlign = "center"; g.fillText(S.msg, W / 2 - SAL, if (CL) Math.max(PY + PH + 28 + (if (S.cb >= 3) 20 else 0), if (S.rl.values.any { it > 0 }) PY + PH + 56 else 0.0) else 80.0); g.textAlign = "left" }
    g.restore()
    drawSticks(g)
    mk(7)
    if (S.sw > 0) {
        g.fillStyle = "rgba(60,255,255,.09)"; g.fillRect(0.0, 0.0, W, H); g.font(16.0, true); g.textAlign = "center"; g.fillStyle = "#99ffff"; g.fillText("입력 반전", W / 2, H * .3); g.textAlign = "left"
    }
    if (S.pg >= 2 && S.bt > 0 && FX > 0) drawBug(g)
    if (S.tf > 0) drawTf(g)
}

private fun pc(v: Double) = Math.round(v * 100).toString() + "%"
private fun drawHud(g: G) {
    val m = stat(); val hm = SV.hud; val t = fl(S.t).toInt()
    val tt = "" + t / 60 + ":" + two(t % 60) + (if (S.T > 1e8) "" else " / " + fl(S.T / 60).toInt() + ":" + two((S.T % 60).toInt()))
    val bw = hudBox() - 12
    g.fillStyle = "rgba(8,10,24,.55)"; g.fillRect(6.0, 6.0, bw + 12, if (hm == 1) 112.0 else if (hm == 0) 66.0 else 50.0)
    g.fillStyle = "#2a1018"; g.fillRect(12.0, 12.0, bw, 9.0)
    run { // 체력바: 잃은 부분이 노랗게 남았다가 천천히 줄어든다, 30% 아래에서 깜빡임
        val hr = (P.hp / P.mh).coerceIn(0.0, 1.0)
        P.hpT = if (P.hpT < 0 || P.hpT < hr) hr else Math.max(hr, P.hpT - .6 / 60)
        g.fillStyle = "#ffe0a0"; g.fillRect(12.0, 12.0, bw * P.hpT, 9.0)
        g.fillStyle = if (hr < .3 && Math.floor(S.t * 4).toInt() % 2 == 1) "#ff8a8a" else "#f55"; g.fillRect(12.0, 12.0, bw * hr, 9.0)
    }
    g.fillStyle = "#10202a"; g.fillRect(12.0, 24.0, bw, 5.0); g.fillStyle = "#5fd8ff"; g.fillRect(12.0, 24.0, bw * (P.xp / P.nx).coerceIn(0.0, 1.0), 5.0)
    val hl1 = "HP " + Math.ceil(P.hp).toInt() + "/" + Math.round(P.mh) + "  Lv" + P.lv + (if (S.pg > 1) "  PAGE " + S.pg else "")
    val hl2 = tt + "  처치 " + S.kills + (if (tb("aa") > 0) "" else " · 수동조준")
    g.fit(hl1, bw, 11.0, true); g.fillStyle = "#e8ecff"; g.fillText(scr(hl1), 12.0, 42.0)
    g.fit(hl2, bw, 11.0); g.fillStyle = "#aab2e0"; g.fillText(scr(hl2), 12.0, 55.0); g.font(10.0, true)
    if (hm == 0) {
        val i1 = listOf(Triple("공격", "+" + pc(m.dm - 1), "#ffb347"), Triple("공속", "+" + pc(1 / m.iv - 1), "#ffd24a"), Triple("치명", pc(m.cr), "#f6f"))
        i1.forEachIndexed { i, (a, b, c) -> val x = 12.0 + i * bw / 3; g.fillStyle = "#8a92b8"; g.fillText(a, x, 67.0); g.fillStyle = c; g.fillText(b, x + 22, 67.0) }
    } else if (hm == 1) {
        val i2 = listOf(Triple("공격", "+" + pc(m.dm - 1), "#ffb347"), Triple("공속", "+" + pc(1 / m.iv - 1), "#ffd24a"), Triple("이동", "+" + pc(m.mv - 1), "#8f8"), Triple("치명", pc(m.cr), "#f6f"),
            Triple("치피", "×" + String.format(java.util.Locale.US, "%.2f", m.cd), "#fb8"), Triple("범위", "+" + pc(m.ar - 1), "#9ef"), Triple("방어", pc(1 - m.am), "#8af"), Triple("경험", "+" + pc(tb("xp")), "#5fd8ff"))
        i2.forEachIndexed { i, (a, b, c) -> val x = 12.0 + (i and 1) * bw / 2; val y = 68.0 + (i shr 1) * 14; g.fillStyle = "#8a92b8"; g.fillText(a, x, y); g.fillStyle = c; g.fillText(b, x + 30, y) }
    }
    val ks = S.ws.keys.toList(); val cw = 40.0; val x0 = rd(W / 2 - SAL - ks.size * cw / 2); val y0 = H - 38 - SAT - SAB
    ks.forEachIndexed { i, k ->
        val x = x0 + i * cw; val fz = WD[k]!!.fz; val c = wsp(k); val sc = if (c.w > 10) 1.6 else 2.0
        g.fillStyle = "rgba(8,10,24,.6)"; g.fillRect(x, y0, cw - 4, 28.0); g.strokeStyle = if (fz) "#ffd24a" else "#2c335c"; g.lineWidth = if (fz) 2.0 else 1.0; g.strokeRect(x + .5, y0 + .5, cw - 5, 27.0)
        g.drawImage(c, rd(x + (cw - 4) / 2 - c.w * sc / 2), rd(y0 + 10 - c.h * sc / 2), rd(c.w * sc), rd(c.h * sc))
        g.font(9.0, true); g.fillStyle = "#fff"; g.fillText("Lv" + S.ws[k]!!.l, x + 4, y0 + 25)
    }
    if (hm != 2) {
        val ps = S.ps.entries.joinToString("  ") { PD[it.key]!!.n + it.value }
        if (ps.isNotEmpty()) { g.fit(ps, W - 12 - SAL - SAR, 10.0, false, 7.0); g.fillStyle = "rgba(170,178,224,.85)"; g.textAlign = "center"; g.fillText(ps, W / 2 - SAL, y0 - 6); g.textAlign = "left" }
    }
    S.rl.entries.filter { it.value > 0 }.forEachIndexed { i, en ->
        val r = RM[en.key]!!; val x = 8.0 + i * 28; val y = Math.max(if (hm == 1) 124.0 else if (hm == 0) 80.0 else 60.0, if (CL && PH > 0) PY + PH + 6 else 0.0)
        g.fillStyle = "rgba(8,10,24,.65)"; g.fillRect(x, y, 26.0, 26.0); g.strokeStyle = RGC[r.g]; g.lineWidth = 2.0; g.strokeRect(x + 1, y + 1, 24.0, 24.0)
        g.drawImage(ric(r.ic), x + 3, y + 3, 20.0, 20.0)
        if (en.value > 1) { g.font(10.0, true); g.fillStyle = "#ffffff"; g.fillText(en.value.toString(), x + 16, y + 24) }
    }
    if (S.cb >= 3) {
        g.font(14.0, true); g.textAlign = "center"; g.lineWidth = 3.0; g.strokeStyle = "#000"; g.globalAlpha = Math.max(.3, Math.min(1.0, S.ct))
        val ct = "COMBO x" + S.cb + "  경험치 +" + Math.min(50, S.cb * 2) + "%"
        val bo = S.bo
        val cby = if (CL) PY + PH + 16 else if (S.pg == 3) 160.0 else if (S.pg == 2 || (bo != null && !bo.dead && bo.k == "boss")) 134.0 else 112.0
        g.strokeText(ct, W / 2, cby); g.fillStyle = "#ffd24a"; g.fillText(ct, W / 2, cby); g.globalAlpha = 1.0; g.textAlign = "left"
    }
}

/** 개발용 구간별 그리기 시간 */
val DPT = LongArray(8)
val DACC = LongArray(8)
fun mk(i: Int) { if (!DEBUG_PERF) return; val t = System.nanoTime(); if (i > 0 && DPT[i - 1] > 0) DACC[i] += t - DPT[i - 1]; DPT[i] = t; if (i == 7) java.util.Arrays.fill(DPT, 0L) }