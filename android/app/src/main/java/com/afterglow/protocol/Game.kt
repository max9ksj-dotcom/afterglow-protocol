package com.afterglow.protocol

// 잔광 프로토콜 게임 로직. afterglow_protocol.html의 수치·순서를 그대로 옮겼다.

typealias CB = (En, Double) -> Unit

const val TAU = 6.283
var W = 400.0
var H = 800.0
var FX = 2
fun FA(): Double = doubleArrayOf(0.0, .5, 1.0, 1.6)[FX]
fun hyp(a: Double, b: Double) = Math.sqrt(a * a + b * b)
fun rnd() = Math.random()
fun sin(a: Double) = Math.sin(a)
fun cos(a: Double) = Math.cos(a)
fun atan2(y: Double, x: Double) = Math.atan2(y, x)
inline fun <T> ArrayList<T>.each(f: (T) -> Unit) { var i = 0; while (i < size) { f(this[i]); i++ } }

class En(val k: String) {
    var c = k; var col = "#ffffff"
    var x = 0.0; var y = 0.0; var hp = 0.0; var mh = 0.0; var sp = 0.0; var r = 0.0; var xp = 0.0; var sc = 0.0
    var boss = false; var fl = 0.0; var mk = 0.0; var md = 0.0; var oc = 0.0; var sl = 0.0; var ph = 0.0; var st = 0; var cd = 0.0
    var kx = 0.0; var ky = 0.0; var es = 0.0; var dead = false; var inv = false; var el = false; var sq = 0.0; var ang = 0.0
    var tl = 0.0; var dx = 0.0; var dy = 0.0; var tr: Double? = null; var rg = false; var na = 0; var lp: String? = null; var sm = 0.0
    var php: Int? = null; var dmf = 1.0; var sm2 = 0.0; var tp: Double? = null; var cast = 0; var cw: String? = null
    var wl: List<String>? = null; var cid: String? = null; var rd: String? = null; var dcv = 12.0; var ax: Int? = null
}
class Pl {
    var x = 0.0; var y = 0.0; var hp = 100.0; var mh = 100.0; var mh0 = 100.0; var xp = 0.0; var lv = 1; var nx = 8.0; var inv = 0.0
    var fx = 1.0; var fy = 0.0; var at = 0.0; var dr = 1.0; var mvg = false; var slow = 0.0
}
class Wp(var l: Int, var t: Double)
class Prj(var x: Double, var y: Double, var a: Double, val d: Double, val tg: En?, var l: Double, val tn: Double, val cb: CB? = null, val ch: Boolean = false) {
    val tr = ArrayList<DoubleArray>()
}
class EP(var x: Double, var y: Double, var vx: Double, var vy: Double, var l: Double, val d: Double = 8.0, val h: Double = 0.0, val rd: String? = null)
class Mine(val x: Double, val y: Double, var ar: Double, val d: Double, val r: Double, var x_: Boolean = false)
class Gem(var x: Double, var y: Double, val v: Double, val k: String, var a: Double = 0.0, var g: Boolean = false)
class Pt(var x: Double, var y: Double, var vx: Double, var vy: Double, var l: Double, val m: Double, val c: String, val s: Double)
class Q(var t: Double, val f: () -> Unit)
class Os(var t: Double, var a: Double, val d: Double, var cd: Double, val rd: String?)
class Hbs(var t: Double, var n: Int = 0, var p: Int = 0, var last: Int = -1)
class Pull(val x: Double, val y: Double, var t: Double, val s: Double = 240.0)
class Fx(val t: Char, var l: Double, val m: Double) {
    var x = 0.0; var y = 0.0; var r = 0.0; var c = "#ffffff"; var k = false; var a = 0.0; var ha = .87
    var x1 = 0.0; var y1 = 0.0; var x2 = 0.0; var y2 = 0.0; var w = 3.0; var pts: List<DoubleArray>? = null; var len = 0.0
    var txt = ""; var v = 0L; var cr = false; var d = 0.0; var s: List<Char> = emptyList()
}
class Opt(val a: String, val b: String, val ev: Boolean = false, val f: () -> Unit)
class Orb(val w: Wp, val n: Int, val r: Double, val d: Double, val c: String, val hr: Double, val sz: Double, val cb: CB? = null)
class Stat(val dm: Double, val iv: Double, val ar: Double, val pc: Double, val cr: Double, val cd: Double, val bd: Double, val mv: Double, val am: Double, val ch: Double)
class RelX(val dm: Double, val ar: Double, val iv: Double, val am: Double, val mv: Double, val mg: Double)
class Joy(val ox: Double, val oy: Double, var x: Double = 0.0, var y: Double = 0.0)

class St(val w0: String, @JvmField var T: Double) {
    var run = true; var t = 0.0; var sp = 1
    val ws = LinkedHashMap<String, Wp>(); val ps = LinkedHashMap<String, Int>(); val rl = LinkedHashMap<String, Int>()
    val en = ArrayList<En>(); val pr = ArrayList<Prj>(); val fx = ArrayList<Fx>(); val gm = ArrayList<Gem>(); val mn = ArrayList<Mine>()
    val q = ArrayList<Q>(); val ep = ArrayList<EP>(); val os = ArrayList<Os>(); val pt = ArrayList<Pt>()
    var sc = 0.0; var rc = 0.0; var rc2 = 0.0; var oct = 0.0; var shd = 0.0; var shcd = 0.0; var mvb = 0.0; var kc = 0; var ea = 0.0
    var rpend = false; var rco: List<Rel>? = null; var elt = 50.0; var xl = false; var kills = 0; var boss = false; var msg = ""; var mt = 0.0
    var oa = 0.0; var sh = 0.0; var lt = -9.0; var lw: String? = null; var la = 0.0; var bt = 0.0; var bn: Double? = null; var pg = 1
    var gl = 0.0; var tf = 0.0; var pull: Pull? = null; var hb: Hbs? = null; var ck: List<List<DoubleArray>>? = null; var k0 = 0; var pr0 = 0
    var nu = ArrayList<String>(); var gb = false; var cb = 0; var ct = 0.0; var rn = 0.0; var nk = 0.0; var pz = false; var ended = false
    var cin = 0.0; var dc = 0.0; var hs = 0.0; var vg = 0.0; var rv = 0; var rr = 0; var sl = 0; var cc = "#ffffff"
    var sw = 0.0; var bx = 0.0; var by = 0.0; var bg = false; var ex = 0.0; var cl = false; var snap: Spr? = null; var gs = false
    var cr: List<List<DoubleArray>> = emptyList(); var zz: List<DoubleArray> = emptyList(); var bo: En? = null; var lhp: Double? = null
    var hf = 1.0; var xo = 0.0; var lastSwap: Double? = null; var rivalId: String? = null; var rcs: Spr? = null; var t3 = false
    var cur: List<Opt> = emptyList(); var bonus = false
}

lateinit var P: Pl
var Sn: St? = null
val S: St get() = Sn!!
var joy: Joy? = null
var aimJ: Joy? = null
const val SR = 52.0

fun pl(k: String): Double = (S.ps[k] ?: 0).toDouble()
fun rlc(id: String): Int = Sn?.rl?.get(id) ?: 0

fun relX(): RelX {
    if (Sn == null) return RelX(0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
    fun c(k: String) = rlc(k).toDouble()
    return RelX(.22 * c("amp") + .45 * c("heart") + (if (P.mvg) .15 * c("dash") else 0.0) + (if (c("heart") > 0 && P.hp < P.mh * .3) .2 else 0.0) + (if (c("combo") > 0 && S.cb >= 10) .2 else 0.0),
        -.1 * c("amp") + .15 * c("lens"), .25 * c("glass"), -.35 * c("glass"), .1 * c("dash") + (if (S.mvb > 0) .2 * c("coll") else 0.0), .35 * c("coll"))
}
fun stat(): Stat {
    val rx = relX()
    return Stat(1.15 + .06 * pl("over") + tb("dm") + chm("dm") + up("dm") + rx.dm, 1 - .05 * pl("acc") - tb("iv") - rx.iv,
        1 + .07 * pl("lens") + tb("ar") + chm("ar") + up("ar") + rx.ar, pl("pierce"), .05 + .04 * pl("prec") + tb("cr") + chm("cr") + up("cr"),
        1.5 + tb("cd"), 1 + tb("bd"), 1 + .05 * pl("mob") + tb("mv") + chm("mv") + up("mv") + rx.mv,
        Math.max(.3, 1 - .08 * pl("arm") - tb("am") - chm("am") - (if (SV.stG < 3) .2 else 0.0) - rx.am - (if (P.hp < P.mh * .3) up("lh") else 0.0)) * (if (S.shd > 0) .5 else 1.0),
        pl("chain"))
}

// ===== 효과 =====
fun shake(v: Double) { S.sh = Math.min(14.0, Math.max(S.sh, v * FA())) }
fun stop(t: Double) { if (FX != 0) S.hs = Math.max(S.hs, t * Math.min(1.3, FA())) }
fun burst(x: Double, y: Double, n0: Double, c: String, sp: Double, l: Double, sz: Double, a: Double? = null, sr: Double = 0.0) {
    val n = Math.ceil(n0 * FA() * (if (S.pt.size > 200) .6 else 1.0)).toInt()
    var i = 0
    val cap = if (S.en.size > 90) 220 else 320
    while (i < n && S.pt.size < cap) {
        val an = if (a == null) rnd() * TAU else a + (rnd() - .5) * sr
        val v = sp * (.3 + rnd() * .7)
        S.pt.add(Pt(x, y, cos(an) * v, sin(an) * v, l * (.6 + rnd() * .4), l, c, sz * (.6 + rnd())))
        i++
    }
}
fun fxR(x: Double, y: Double, r: Double, c: String, l: Double) { S.fx.add(Fx('r', l, l).also { it.x = x; it.y = y; it.r = r; it.c = c }) }
fun fxO(x: Double, y: Double, r: Double, c: String, l: Double, k: Boolean = false) { S.fx.add(Fx('o', l, l).also { it.x = x; it.y = y; it.r = r; it.c = c; it.k = k }) }
fun fxD(x: Double, y: Double, r: Double, l: Double) { S.fx.add(Fx('d', l, l).also { it.x = x; it.y = y; it.r = r }) }
fun fxP(x: Double, y: Double, r: Double, d: Double = 0.0) { S.fx.add(Fx('p', .35, .35).also { it.x = x; it.y = y; it.r = r; it.d = d }) }
fun fxA(a: Double, r: Double, ha: Double, l: Double, c: String) { S.fx.add(Fx('a', l, l).also { it.a = a; it.r = r; it.ha = ha; it.c = c }) }
fun ln(x1: Double, y1: Double, x2: Double, y2: Double, c: String, w: Double = 3.0, m: Double = .12) {
    S.fx.add(Fx('l', m, m).also { it.x1 = x1; it.y1 = y1; it.x2 = x2; it.y2 = y2; it.c = c; it.w = w })
}
fun bolt(x1: Double, y1: Double, x2: Double, y2: Double, c: String) {
    val dx = x2 - x1; val dy = y2 - y1
    val L = hyp(dx, dy).let { if (it == 0.0) 1.0 else it }
    val n = Math.max(3, Math.floor(L / 18).toInt())
    val pts = ArrayList<DoubleArray>()
    for (i in 0..n) {
        val u = i.toDouble() / n
        val o = if (i == 0 || i == n) 0.0 else (rnd() - .5) * 18
        pts.add(doubleArrayOf(x1 + dx * u - dy / L * o, y1 + dy * u + dx / L * o))
    }
    S.fx.add(Fx('z', .22, .22).also { it.pts = pts; it.c = c })
    burst(x2, y2, 4.0, c, 170.0, .25, 2.0)
}
fun bugName(e: En, txt: String) {
    S.fx.add(Fx('m', 1.2, 1.2).also { it.x = e.x; it.y = e.y - e.r - 30; it.txt = txt })
    S.bt = Math.max(S.bt, .18)
}
fun gainXP(v: Double) { P.xp += v * (1 + tb("xp") + up("xp")) * (1 + Math.min(.5 + .5 * rlc("combo"), S.cb * .02)) }

// ===== 합체 무기 부가 효과 =====
val RI = mapOf("cleave" to "#ffd0a0", "ricochet" to "#f6f", "pull" to "#a8f", "shards" to "#8f8", "chain" to "#ff6", "mark" to "#9ef", "blast" to "#f86")
fun applyR(e: En, d: Double, r: String, c: String) {
    when (r) {
        "cleave" -> aoe(e.x, e.y, 42.0, d * .45, hashSetOf(e), c)
        "ricochet" -> beam(atan2(e.y - P.y, e.x - P.x), 170.0, 6.0, 2, d * .5, c, hashSetOf(e), e.x, e.y)
        "pull" -> {
            S.en.each { o ->
                if (!(o.dead || o.boss || o === e)) {
                    val dd = hyp(e.x - o.x, e.y - o.y)
                    if (dd < 120 && dd > 1) { o.kx += (e.x - o.x) / dd * 320; o.ky += (e.y - o.y) / dd * 320 }
                }
            }
            burst(e.x, e.y, 6.0, c, 160.0, .3, 2.0)
        }
        "shards" -> for (i in 0 until 3) S.pr.add(Prj(e.x, e.y, rnd() * TAU, d * .35, null, .6, 0.0))
        "chain" -> chainFrom(e, d * .5, 3)
        "mark" -> { e.mk = .8; e.md = d * .6 }
        "blast" -> aoe(e.x, e.y, 55.0, d * .6, hashSetOf(e), c)
    }
}
fun RC(r: String, c: String): CB = { e, d -> applyR(e, d, r, c) }

fun gfire(D: Wd, ds: Double, m: Stat, ra: Double, t: En, a: Double) {
    val cb = RC(D.rd!!, D.c); val c = D.c; val g0 = D.gd; val d = D.d * ds
    when (g0) {
        "pulse" -> {
            for (o in doubleArrayOf(-.15, .15)) S.pr.add(Prj(P.x, P.y, a + o, d, if (tb("aa") > 0 || pl("aim") > 0) t else null, 2.0, 3 + pl("aim") * .5, cb))
            burst(P.x, P.y, 5.0, c, 180.0, .2, 2.0, a, .7); snd(650.0, .06, "triangle", .04)
        }
        "blade" -> slash(a, ra, d, 1.1, c, 0.0, cb)
        "rail" -> { shake(3.0); snd(480.0, .25, "sawtooth", .06); beam(a, ra, 10.0, 4 + m.pc.toInt(), d, c, null, null, null, cb) }
        "grav" -> { fxO(t.x, t.y, 75.0, c, .14); S.q.add(Q(.12) { aoe(t.x, t.y, 62 * m.ar, d, null, c, cb) }) }
        "arc" -> {
            val vs = arrayListOf(t); var cur = t
            bolt(P.x, P.y, t.x, t.y, c); hit(t, d, c); cb(t, d)
            while (vs.size < 4 + m.ch) {
                val nx = near(cur.x, cur.y, 130.0, vs) ?: break
                bolt(cur.x, cur.y, nx.x, nx.y, c); hit(nx, d, c); cb(nx, d); vs.add(nx); cur = nx
            }
        }
        "frost" -> {
            val ts = S.en.filter { !it.dead && !it.inv && hyp(it.x - P.x, it.y - P.y) < ra }.sortedBy { hyp(it.x - P.x, it.y - P.y) }.take(3)
            for (e in ts) { ln(P.x, P.y, e.x, e.y, c, 2.0, .15); hit(e, d, c); e.mk = .8; e.md = d * .6; cb(e, d) }
        }
    }
}

// 궤도형 무기는 OON초 동안 돌다가 OOF초 동안 사라진다
const val OON = 4.0
const val OOF = 2.5
fun oPh(): Double = (Sn?.oct ?: 0.0) % (OON + OOF)
fun oVis() = oPh() < OON
fun OB(): Orb? {
    for ((k, w) in S.ws) {
        val D = WD[k]!!
        val ex = (if (w.l >= 3) 1 else 0) + (if (w.l >= 5) 1 else 0)
        if (k == "fz_blade") return Orb(w, 4 + ex, 64.0, 26.0, "#fc6", 20.0, 16.0)
        if (D.gd == "orbit") return Orb(w, 4 + ex, 58.0, 22.0, D.c, 18.0, 15.0, RC(D.rd!!, D.c))
    }
    val q = S.ws["orbit"] ?: return null
    return Orb(q, 3 + (if (q.l >= 2) 1 else 0) + (if (q.l >= 4) 1 else 0), 46.0, 16.0, "#8f8", 16.0, 13.0)
}
fun angDiff(dy: Double, dx: Double, a: Double): Double { var df = Math.abs(atan2(dy, dx) - a); if (df > Math.PI) df = TAU - df; return df }
fun nearCone(r: Double, a: Double, ha: Double): En? {
    var b: En? = null; var bd = 1e9
    S.en.each { e ->
        if (!(e.dead || e.inv)) {
            val dx = e.x - P.x; val dy = e.y - P.y; val d = hyp(dx, dy) - e.r
            if (d <= r && d < bd && angDiff(dy, dx, a) < ha) { bd = d; b = e }
        }
    }
    return b
}
fun faceA() = if (P.fx != 0.0 || P.fy != 0.0) atan2(P.fy, P.fx) else 0.0
fun aimAngle(): Double { val j = aimJ; return if (j != null && hyp(j.x, j.y) > .25) atan2(j.y, j.x) else faceA() }
fun slash(a: Double, r: Double, d: Double, ha: Double, c: String, mk: Double, cb: CB? = null) {
    S.cc = c
    S.en.each { e ->
        if (!e.dead) {
            val dx = e.x - P.x; val dy = e.y - P.y
            if (hyp(dx, dy) < r + e.r && angDiff(dy, dx, a) < ha) {
                hit(e, d); cb?.invoke(e, d)
                if (mk != 0.0) { e.mk = .8; e.md = mk }
            }
        }
    }
    fxA(a, r, ha, .26, c)
    burst(P.x + cos(a) * r * .8, P.y + sin(a) * r * .8, 10.0, c, 320.0, .28, 3.0, a, 1.8); shake(3.0); snd(220.0, .1, "triangle", .06)
}
fun chainFrom(t: En, d: Double, n: Int) {
    val vs = arrayListOf(t); var c = t
    while (vs.size <= n) {
        val nx = near(c.x, c.y, 130.0, vs) ?: break
        bolt(c.x, c.y, nx.x, nx.y, "#ff6"); hit(nx, d, "#ff6"); vs.add(nx); c = nx
    }
}
fun near(x: Double, y: Double, r: Double, ex: List<En>? = null): En? {
    var b: En? = null; var bd = 1e9
    S.en.each { e ->
        if (!(e.dead || e.inv || (ex != null && ex.contains(e)))) {
            val d = hyp(e.x - x, e.y - y) - e.r
            if (d <= r && d < bd) { bd = d; b = e }
        }
    }
    return b
}

// ===== 유물 =====
fun relApply(id: String) {
    if (id == "cell") S.rv++
    if (id == "heart") { P.mh = Math.round(P.mh0 * .7).toDouble(); P.hp = Math.min(P.hp, P.mh) }
}
fun relBoom(e: En) { S.xl = true; aoe(e.x, e.y, 80.0, 40 / stat().dm, hashSetOf(e), "#ff9a2a"); S.xl = false }
fun relArc(e: En, d: Double) {
    S.xl = true
    val n = rlc("arcx"); val m = stat()
    val ts = S.en.filter { !it.dead && !it.inv && it !== e && hyp(it.x - e.x, it.y - e.y) < 170 }.sortedBy { hyp(it.x - e.x, it.y - e.y) }.take(n)
    for (t in ts) { bolt(e.x, e.y, t.x, t.y, "#ffff66"); hit(t, d * .4 / m.dm, "#ffff66") }
    S.xl = false
}
fun relKill(e: En) {
    S.kc++
    val cp = rlc("cap")
    if (cp > 0 && S.kc % 20 == 0) P.hp = Math.min(P.mh, P.hp + P.mh * .06 * cp)
    val b = rlc("burst")
    if (b > 0 && !S.xl && rnd() < .1 * b) relBoom(e)
    if (e.el) { S.rpend = true; S.msg = "엘리트 처치! 유물 획득"; S.mt = 1.5 }
}

// ===== 피해 =====
fun hit(e: En, d0: Double, c0: String? = null) {
    if (e.dead || e.inv) return
    val c = c0 ?: S.cc
    val m = stat()
    val cr = !S.xl && rnd() < m.cr
    val d = d0 * m.dm * (if (cr) m.cd else 1.0) * (if (e.boss) m.bd else 1.0) * (if (e.k == "tank") .6 else 1.0) * (if (e.k == "rival") .7 else 1.0) *
        (if (rlc("core") > 0 && e.hp < e.mh * .3) 1 + .5 * rlc("core") else 1.0)
    e.hp -= d; e.fl = .09; e.sq = 1.0
    if (cr && rlc("arcx") > 0 && !S.xl) relArc(e, d)
    val a = atan2(e.y - P.y, e.x - P.x)
    val k = (if (e.boss) 40.0 else if (e.k == "tank") 60.0 else 260.0) * (if (cr) 1.6 else 1.0) / (e.r / 10)
    e.ang = a; e.kx += cos(a) * k; e.ky += sin(a) * k
    val kk = hyp(e.kx, e.ky)
    if (kk > 520) { e.kx *= 520 / kk; e.ky *= 520 / kk }
    burst(e.x, e.y, if (cr) 11.0 else 5.0, c, if (cr) 280.0 else 190.0, .3, 3.0, a, 1.2); burst(e.x, e.y, 2.0, "#fff", 220.0, .18, 2.0, a, 1.5)
    if ((if (S.en.size > 90) 30 else 60) > S.fx.size) S.fx.add(Fx('n', .7, .7).also { it.x = e.x + (rnd() - .5) * 12; it.y = e.y - e.r; it.v = Math.round(d); it.cr = cr })
    snd(if (cr) 560.0 else 300 + rnd() * 90, .05, "square", .04)
    if (cr) { shake(4.0); stop(.05); fxR(e.x, e.y, e.r * 2, "#ffd24a", .2) } else if (e.boss) shake(1.5)
    if (e.hp <= 0) {
        e.dead = true; S.kills++; S.cb++; S.ct = 2.2 + Math.min(2.0, tb("ct")) + rlc("combo"); S.nk = 0.0
        relKill(e)
        if (tb("ms") > 0 && S.rc2 <= 0) { P.hp = Math.min(P.mh, P.hp + Math.min(5.0, tb("ms"))); S.rc2 = .5 }
        if (e.boss) {
            S.dc = 2.4; S.bx = e.x; S.by = e.y; S.bg = true; S.ex = 0.0
            S.en.each { o -> if (o !== e && !o.dead) { o.dead = true; burst(o.x, o.y, 5.0, o.col, 200.0, .4, 3.0) } }
        }
        if (e.k == "split") { spawn("mini", e.x - 8, e.y); spawn("mini", e.x + 8, e.y) }
        S.gm.add(Gem(e.x, e.y, if (e.boss) 60.0 else e.xp, e.k))
        burst(e.x, e.y, if (e.boss) 70.0 else 16.0, e.col, 320.0, .6, 4.0); burst(e.x, e.y, 6.0, "#fff", 260.0, .3, 3.0); fxR(e.x, e.y, e.r * 2.4, e.col, .3)
        stop(if (e.boss) .3 else if (e.r > 14) .07 else .03); shake(if (e.boss) 12.0 else if (e.r > 14) 4.0 else 1.5); snd(130.0, .16, "sawtooth", .06)
        if (!e.boss && pl("rec") > 0 && S.rc <= 0) { P.hp = Math.min(P.mh, P.hp + pl("rec")); S.rc = 1.0 }
    }
}
fun aoe(x: Double, y: Double, r: Double, d: Double, ex: Set<En>? = null, c0: String? = null, cb: CB? = null) {
    val c = c0 ?: S.cc
    S.cc = c
    S.en.each { e -> if (!e.dead && hyp(e.x - x, e.y - y) < r + e.r && !(ex != null && ex.contains(e))) { hit(e, d); cb?.invoke(e, d) } }
    fxR(x, y, r, c, .34); fxO(x, y, r * .8, "#fff", .12, true); burst(x, y, Math.min(26.0, r / 3), c, r * 4, .5, 3.0); shake(Math.min(7.0, r / 14)); snd(110.0, .22, "sawtooth", .06)
}
fun beam(a: Double, len: Double, w: Double, n: Int, d: Double, c: String, ex: HashSet<En>? = null, ox0: Double? = null, oy0: Double? = null, cb: CB? = null) {
    val ox = ox0 ?: P.x; val oy = oy0 ?: P.y
    val hs = ArrayList<Pair<Double, En>>()
    S.en.each { e ->
        if (!(e.dead || (ex != null && ex.contains(e)))) {
            val dx = e.x - ox; val dy = e.y - oy
            val pr = dx * cos(a) + dy * sin(a); val pp = Math.abs(-dx * sin(a) + dy * cos(a))
            if (pr > 0 && pr < len + e.r && pp < e.r + w) hs.add(Pair(pr, e))
        }
    }
    hs.sortBy { it.first }
    hs.take(n).forEachIndexed { i, h ->
        val dm = d * Math.max(.4, Math.pow(.75, i.toDouble()))
        hit(h.second, dm); cb?.invoke(h.second, dm); ex?.add(h.second)
    }
    ln(ox, oy, ox + cos(a) * len, oy + sin(a) * len, c, w + 4, .3)
}

// ===== 무기 발사 =====
fun fire(k: String, w: Wp): Boolean {
    val m = stat(); val D = WD[k]!!
    val ds = (1 + .2 * (w.l - 1)) * (if (D.fz && rlc("cata") > 0) 1.25 else 1.0)
    val ra = D.r * m.ar
    S.cc = D.c
    if (k == "orbit" || D.gd == "orbit") return true
    val auto = tb("aa") > 0
    val aa0 = if (auto) 0.0 else aimAngle()
    val t = (if (auto) near(P.x, P.y, ra) else nearCone(ra, aa0, .7)) ?: return false
    val a = if (auto) atan2(t.y - P.y, t.x - P.x) else aa0
    S.la = a; S.lw = k; S.lt = S.t
    val tg = if (auto || pl("aim") > 0) t else null
    when {
        k == "pulse" -> {
            val n = if (w.l >= 5) doubleArrayOf(-.2, 0.0, .2) else if (w.l >= 3) doubleArrayOf(-.12, .12) else doubleArrayOf(0.0)
            val dm = 20 * ds * (if (n.size == 1) 1.0 else if (n.size == 2) .75 else .65)
            for (o in n) S.pr.add(Prj(P.x, P.y, a + o, dm, tg, 2.0, 3 + pl("aim") * .5))
            burst(P.x + cos(a) * 12, P.y + sin(a) * 12, 4.0, D.c, 170.0, .15, 2.0, a, .6); snd(660.0, .06, "triangle", .03)
        }
        k == "blade" -> {
            val dd = hyp(t.x - P.x, t.y - P.y)
            val lg0 = Math.min(34.0, Math.max(0.0, dd - 32))
            val dx0 = P.x + cos(a) * lg0; val dy0 = P.y + sin(a) * lg0
            val lg = if (S.en.count { !it.dead && hyp(it.x - dx0, it.y - dy0) < 50 } > 2) 0.0 else lg0
            burst(P.x, P.y, 5.0, "#ffb347", 120.0, .3, 3.0)
            P.x += cos(a) * lg; P.y += sin(a) * lg
            if (lg > 0) P.inv = Math.max(P.inv, .25)
            fun cone(r: Double, d: Double, c: String?, ha: Double) {
                S.cc = D.c
                S.en.each { e ->
                    if (!e.dead) {
                        val dx = e.x - P.x; val dy = e.y - P.y
                        if (hyp(dx, dy) < r + e.r && angDiff(dy, dx, a) < ha) hit(e, d)
                    }
                }
                fxA(a, r, ha, .24, c ?: D.c)
                burst(P.x + cos(a) * r * .8, P.y + sin(a) * r * .8, 8.0, c ?: D.c, 300.0, .25, 3.0, a, 1.6); shake(2.0); snd(if (c != null) 260.0 else 200.0, .09, "triangle", .05)
            }
            cone(ra, 40 * ds, null, 1.05)
            S.q.add(Q(.2) { cone(ra * .9, 24 * ds, "#fff", 1.2) })
        }
        k == "rail" -> { shake(2.5); snd(700.0, .22, "sawtooth", .05); burst(P.x, P.y, 8.0, D.c, 300.0, .2, 3.0, a, .5); beam(a, ra, 14.0, 5 + m.pc.toInt(), 50 * ds, D.c) }
        k == "grav" -> { val r = 45 * m.ar; fxO(t.x, t.y, r * 1.7, D.c, .14); S.q.add(Q(.12) { aoe(t.x, t.y, r, 34 * ds, null, D.c) }) }
        k == "arc" -> {
            val vs = arrayListOf(t); var c = t; val n = 3 + m.ch
            bolt(P.x, P.y, t.x, t.y, D.c); snd(900.0, .07, "square", .03); hit(t, 18 * ds)
            while (vs.size < n) {
                val nx = near(c.x, c.y, 120.0, vs) ?: break
                bolt(c.x, c.y, nx.x, nx.y, D.c); hit(nx, 18 * ds); vs.add(nx); c = nx
            }
        }
        k == "frost" -> { hit(t, 16 * ds); t.mk = .8; t.md = 12 * ds; ln(P.x, P.y, t.x, t.y, D.c, 2.0, .15); burst(t.x, t.y, 5.0, "#cff", 120.0, .3, 3.0); snd(1000.0, .08, "sine", .04) }
        k == "fz_slash" -> slash(a, ra, 55 * ds, 1.3, D.c, 30 * ds)
        k == "fz_core" -> fxP(t.x, t.y, 70 * m.ar, 110 * ds)
        k == "fz_storm" -> {
            for (o in doubleArrayOf(-.2, .2)) S.pr.add(Prj(P.x, P.y, a + o, 36 * ds, tg, 2.0, 4.0, null, true))
            burst(P.x, P.y, 6.0, D.c, 200.0, .2, 2.0, a, .8); snd(700.0, .06, "triangle", .04)
        }
        k == "fz_rail" -> {
            shake(3.0); snd(500.0, .3, "sawtooth", .06)
            val ex = HashSet<En>()
            beam(a, ra, 10.0, 5, 70 * ds, D.c, ex)
            for (o in doubleArrayOf(-.3, .3)) beam(a + o, ra * .85, 7.0, 3, 30 * ds, D.c, ex)
        }
        k == "fz_blade" -> slash(a, ra * .9, 40 * ds, 1.6, D.c, 0.0)
        D.gd != null -> gfire(D, ds, m, ra, t, a)
        k == "mine" -> {
            if (S.mn.size >= 8) S.mn.removeAt(0)
            S.mn.add(Mine(t.x, t.y, .7, 60 * ds, 60 * m.ar))
        }
    }
    return true
}

// ===== 적 =====
class Ed(val hp: Double, val sp: Double, val r: Double, val xp: Double, val sc: Double)
val ED = mapOf("crawl" to Ed(30.0, 70.0, 11.0, 1.0, 2.0), "bat" to Ed(18.0, 95.0, 10.0, 2.0, 2.0), "charge" to Ed(60.0, 50.0, 12.0, 4.0, 2.0), "split" to Ed(45.0, 60.0, 12.0, 2.0, 2.0),
    "mini" to Ed(10.0, 95.0, 7.0, 1.0, 1.0), "spit" to Ed(35.0, 60.0, 12.0, 5.0, 2.0), "bomb" to Ed(26.0, 90.0, 11.0, 3.0, 2.0), "hboss" to Ed(9999.0, 0.0, 34.0, 0.0, 4.0),
    "rival" to Ed(1.0, 100.0, 28.0, 0.0, 4.0), "tank" to Ed(150.0, 45.0, 17.0, 8.0, 3.0), "boss" to Ed(5000.0, 55.0, 40.0, 0.0, 4.0))
val CLR = mapOf("crawl" to "#e55", "bat" to "#e88", "charge" to "#b86a3a", "split" to "#6d4", "mini" to "#6d4", "spit" to "#c4c", "tank" to "#6a8aa8", "bomb" to "#fa3", "hboss" to "#c4506a", "rival" to "#ff8ad8", "boss" to "#c4506a")
const val CAP = 150
fun aliveN(): Int { var n = 0; S.en.each { if (!it.dead && !it.inv) n++ }; return n }
fun tmf() = if (S.t < 300) S.t / 30 else 10 + (S.t - 300) / 90
fun pickE(): String {
    val t = S.t + (if (S.pg > 1) 150 else 0) + S.xo; val r = rnd()
    if (t > 480 && r < .1) return "tank"
    if (t > 420 && r < .2) return "spit"
    if (t > 330 && r < .3) return "bomb"
    if (t > 240 && r < .4) return "charge"
    if (t > 120 && r < .5) return "split"
    if (t > 60 && r < .65) return "bat"
    return "crawl"
}
fun spawn(k: String, x: Double? = null, y: Double? = null): En? {
    if (k != "boss" && k != "hboss" && k != "rival" && aliveN() >= CAP) return null
    val D = ED[k]!!; val tm = tmf(); val a = rnd() * TAU; val d = hyp(W, H) * .55 + 30; val b = k == "boss"
    val hp = if (b) D.hp else D.hp * 1.8 * (1 + .18 * tm) * (if (S.pg > 1) 1.5 else 1.0)
    val e = En(k)
    e.col = CLR[k]!!; e.x = x ?: (P.x + cos(a) * d); e.y = y ?: (P.y + sin(a) * d); e.hp = hp; e.mh = hp
    e.sp = D.sp * 1.05 * (if (S.pg > 1) 1.08 else 1.0); e.r = D.r; e.xp = D.xp; e.sc = D.sc; e.boss = b; e.ph = rnd() * 6; e.cd = 1 + rnd() * 2
    S.en.add(e)
    return e
}

// ===== 1페이지 보스 =====
fun bossAtk(e: En) {
    val x = e.x; val y = e.y; val R = if (e.hp < e.mh * .5) 210.0 else 170.0
    fxD(x, y, R, .9)
    S.q.add(Q(.9) {
        fxR(x, y, R, "#f55", .4); burst(x, y, 40.0, "#f84", 420.0, .6, 4.0); shake(10.0); snd(70.0, .35, "sawtooth", .1)
        if (hyp(P.x - x, P.y - y) < R && P.inv <= 0) { P.hp -= 25 * stat().am; P.inv = invT(); stop(.08); S.vg = .6; shake(12.0) }
    })
}
fun bRing(e: En, n: Int, off: Double, spd: Double, d: Double) {
    for (i in 0 until n) hb(e.x, e.y, off + i * TAU / n, spd, d, 0.0, null)
    fxR(e.x, e.y, e.r * 2.2, "#ff7744", .35); snd(180.0, .2, "square", .06)
}
val BPTN = mapOf("slam" to "내려찍기", "ring" to "잔광 방출", "stomp" to "연속 강타", "cross" to "균열 광선")
fun bptFire(k: String, e: En, rg: Boolean) {
    when (k) {
        "slam" -> bossAtk(e)
        "ring" -> {
            fxO(e.x, e.y, e.r * 2.4, "#ff7744", .7)
            S.q.add(Q(.7) {
                if (!e.dead) {
                    val o = rnd() * 6
                    bRing(e, if (rg) 22 else 16, o, 165.0, 10.0)
                    if (rg) S.q.add(Q(.45) { if (!e.dead) bRing(e, 22, o + .14, 145.0, 10.0) })
                }
            })
        }
        "stomp" -> { val n = if (rg) 4 else 3; for (i in 0 until n) S.q.add(Q(i * .5) { if (!e.dead) harea(P.x, P.y, 78.0, 16.0, .75, "#ff7744") }) }
        "cross" -> {
            val o = if (rnd() < .5) 0.0 else .785; val n = if (rg) 8 else 4
            for (i in 0 until n) hbeam(e.x, e.y, o + i * TAU / n, 900.0, 15.0, 20.0, 1.05, "#ff5a3a", null)
        }
    }
}
fun bossPick(e: En, rg: Boolean): String {
    val pool = if (rg) listOf("slam", "ring", "stomp", "cross", "ring") else listOf("slam", "ring", "stomp", "cross")
    var k: String; var t = 0
    do { k = pool[(rnd() * pool.size).toInt()] } while (k == e.lp && t++ < 6)
    e.lp = k
    return k
}
fun bossRage(e: En) {
    e.rg = true; e.inv = true; S.q.clear(); S.fx.removeAll { it.t == 'd' || it.t == 't' }; e.st = 0; e.cd = Math.max(e.cd, 1.5)
    S.q.add(Q(1.2) { e.inv = false }); S.ep.clear(); P.inv = Math.max(P.inv, 1.0)
    val dx = P.x - e.x; val dy = P.y - e.y; val dl = Math.max(1.0, hyp(dx, dy))
    S.pull = Pull(P.x + dx / dl * 170, P.y + dy / dl * 170, .35, 520.0)
    fxR(e.x, e.y, 320.0, "#ff3b3b", .7); fxO(e.x, e.y, 140.0, "#ffffff", .18, true); burst(e.x, e.y, 70.0, "#ff5a3a", 520.0, .8, 5.0)
    shake(16.0); stop(.18); S.bt = .4; snd(55.0, .7, "sawtooth", .13); S.msg = "잔광 거수 — 분노!"; S.mt = 2.2; bugName(e, "분노")
}

// ===== 보스 공격 도구 =====
fun hurtP(d: Double) {
    if (P.inv > 0) return
    P.hp -= d * stat().am; P.inv = invT(); S.cb = 0; shake(7.0); stop(.05); S.vg = .5; burst(P.x, P.y, 10.0, "#f55", 250.0, .35, 3.0); snd(90.0, .2, "sawtooth", .08)
}
fun hb(x: Double, y: Double, a: Double, spd: Double, d: Double, h: Double, rd: String?) {
    if (S.ep.size > 220) return
    S.ep.add(EP(x, y, cos(a) * spd, sin(a) * spd, 5.0, d, h, rd))
}
fun harea(x: Double, y: Double, r: Double, d: Double, delay0: Double, c: String, rd: String? = null) {
    val delay = delay0 * (if (S.pg == 3) S.hf else 1.0)
    fxD(x, y, r, delay)
    S.q.add(Q(delay) {
        fxR(x, y, r, c, .35); burst(x, y, Math.min(24.0, r / 3), c, r * 4, .5, 3.0); shake(3.0); snd(110.0, .18, "triangle", .06)
        if (hyp(P.x - x, P.y - y) < r + 10) hurtP(d)
        if (rd != null) rvR(rd, x, y, d)
    })
}
fun hbeam(x: Double, y: Double, a: Double, len: Double, w: Double, d: Double, delay0: Double, c: String, rd: String?) {
    val delay = delay0 * (if (S.pg == 3) S.hf else 1.0)
    S.fx.add(Fx('t', delay, delay).also { it.x = x; it.y = y; it.a = a; it.len = len; it.w = w })
    S.q.add(Q(delay) {
        ln(x, y, x + cos(a) * len, y + sin(a) * len, c, w + 4, .3); shake(4.0); snd(500.0, .2, "sawtooth", .06)
        val dx = P.x - x; val dy = P.y - y
        val pr = dx * cos(a) + dy * sin(a); val pp = Math.abs(-dx * sin(a) + dy * cos(a))
        if (pr > 0 && pr < len && pp < w + 8) hurtP(d)
        if (rd != null) { val q = Math.max(0.0, Math.min(len, pr)); rvR(rd, x + cos(a) * q, y + sin(a) * q, d) }
    })
}
val RVM = mapOf("fz_slash" to Pair("blade", "mark"), "fz_core" to Pair("grav", "pull"), "fz_storm" to Pair("pulse", "chain"), "fz_rail" to Pair("rail", "ricochet"), "fz_blade" to Pair("orbit", "cleave"))
fun rvInfo(k: String): Pair<String, String?> { val D = WD[k]!!; return if (D.gd != null) Pair(D.gd, D.rd) else RVM[k] ?: Pair(k, null) }
fun rvR(rd: String, x: Double, y: Double, d: Double) {
    when (rd) {
        "pull" -> S.pull = Pull(x, y, .5)
        "mark" -> P.slow = 2.0
        "shards" -> for (i in 0 until 6) hb(x, y, i * 1.047, 170.0, d * .4, 0.0, null)
        "chain" -> { harea(P.x + 60, P.y - 40, 40.0, d * .5, .6, "#ffff66"); harea(P.x - 60, P.y + 40, 40.0, d * .5, .9, "#ffff66") }
        "blast", "cleave" -> harea(x, y, 70.0, d * .5, .5, "#ff8866")
        "ricochet" -> hbeam(x, y, rnd() * TAU, 260.0, 10.0, d * .5, .7, "#ff66ff", null)
    }
}
val RVN = mapOf("pulse" to "NaN_BULLET", "blade" to "OUT_OF_RANGE", "rail" to "ARRAY[-1]", "grav" to "RECURSION", "orbit" to "INFINITE_LOOP", "arc" to "RACE_CONDITION", "frost" to "DEADLOCK", "mine" to "UNHANDLED_EXCEPTION")
fun rivalAtk(e: En, k: String, a: Double) {
    val (dl, rd) = rvInfo(k)
    val D = WD[k]!!
    val bd = Math.min(18.0, Math.max(8.0, D.d * .3)) * 1.7 * e.dmf
    val c = (if (rd != null) RI[rd] else null) ?: D.c
    fun dm() = if (rnd() < .5) bd * 1.7 else bd
    val px = P.x; val py = P.y; val ex = e.x; val ey = e.y; val rs = 1 + .1 * e.ph
    when (dl) {
        "pulse" -> for (o in doubleArrayOf(-.3, 0.0, .3)) hb(ex, ey, a + o, 230.0, dm(), 1.3, rd)
        "blade" -> { e.st = 1; e.cd = .7; e.dx = cos(a); e.dy = sin(a); e.rd = rd; e.dcv = bd }
        "rail" -> hbeam(ex, ey, a, 600.0, 16.0, dm() * 1.3, .9, c, rd)
        "grav" -> harea(px, py, 85 * rs, dm(), 1.0, c, rd ?: "pull")
        "orbit" -> S.os.add(Os(4.5, 0.0, dm() * .7, 0.0, rd))
        "arc" -> for (i in 0 until 3) harea(px + (if (i != 0) (rnd() - .5) * 220 else 0.0), py + (if (i != 0) (rnd() - .5) * 220 else 0.0), 48 * rs, dm(), .7 + i * .3, c, rd)
        "frost" -> harea(px, py, 75 * rs, dm(), .9, c, rd ?: "mark")
        "mine" -> for (i in 0 until 3) { val an = i * 2.09 + rnd(); harea(px + cos(an) * 100, py + sin(an) * 100, 58 * rs, dm(), 1.3 + i * .15, c, rd) }
    }
    bugName(e, RVN[dl] ?: "ERROR"); snd(240.0, .12, "triangle", .05)
}
fun FM(ph: Int) = 1 + .18 * ph
fun HW() = Math.min(W, 700.0) / 2
fun HH() = Math.min(H, 700.0) / 2
fun ew(s: List<Char>, l: Double) { S.fx.add(Fx('e', l, l).also { it.s = s }) }

// 2페이지 보스 패턴 13종
val HPT: List<Pair<String, (En, Int) -> Unit>> = listOf(
    "NULL_POINTER" to { e, ph -> val n = 3 + ph * 2; val a = atan2(P.y - e.y, P.x - e.x); for (i in -n..n) hb(e.x, e.y, a + i * .15, 215.0 + ph * 15, 9 * FM(ph), 0.0, null) },
    "STACK_OVERFLOW" to { e, ph ->
        val n = 14 + ph * 4; val d = 8 * FM(ph)
        for (i in 0 until n) hb(e.x, e.y, i * TAU / n + S.t, 170.0 + ph * 12, d, 0.0, null)
        if (ph >= 1) S.q.add(Q(.45) { for (i in 0 until n) hb(e.x, e.y, (i + .5) * TAU / n + S.t, 150.0, d, 0.0, null) })
    },
    "MEMORY_LEAK" to { e, ph -> val n = 3 + ph; val a = atan2(P.y - e.y, P.x - e.x); for (i in 0 until n) hb(e.x, e.y, a + (i - (n - 1) / 2.0) * .45, 150.0 + ph * 10, 10 * FM(ph), 1.8 + ph * .25, null) },
    "404_NOT_FOUND" to { _, ph ->
        val d = 13 * FM(ph)
        harea(P.x, P.y, 50.0, d, .95, "#ff8844")
        for (i in 0 until 5 + ph * 3) harea(P.x + (rnd() - .5) * 480, P.y + (rnd() - .5) * 480, 50.0, d, .95 + i * .07, "#ff8844")
    },
    "SEGFAULT" to { e, ph -> val n = 2 + ph; val a = atan2(P.y - e.y, P.x - e.x); for (i in 0 until n) hbeam(e.x, e.y, a + (i - (n - 1) / 2.0) * .32, 850.0, 14.0, 17 * FM(ph), 1.0, "#ff66ff", null) },
    "FORK_BOMB" to { _, ph ->
        var c = 0
        S.xo = ph * 80.0
        val n = 7 + ph * 3
        for (i in 0 until n) { val an = rnd() * TAU; if (spawn(pickE(), P.x + cos(an) * 330, P.y + sin(an) * 330) != null) c++ }
        S.xo = 0.0
        if (c > 0) { S.msg = "버그 소환!"; S.mt = 1.2 }
    },
    "BUFFER_OVERFLOW" to { _, ph ->
        val dir = if (rnd() < .5) 1 else -1; val hz = rnd() < .5; val sp = 150.0 + ph * 25; val d = 9 * FM(ph); val gap = 2 + Math.floor(rnd() * 12).toInt()
        ew(listOf(if (hz) (if (dir > 0) 'L' else 'R') else (if (dir > 0) 'T' else 'B')), .7)
        S.q.add(Q(.55) {
            if (S.pg >= 2) for (i in 0 until 16) {
                if (i >= gap && i < gap + 3) continue
                val o = (i - 8) * 44.0
                if (hz) hb(P.x - dir * (HW() + 40), P.y + o, if (dir > 0) 0.0 else Math.PI, sp, d, 0.0, null)
                else hb(P.x + o, P.y - dir * (HH() + 40), if (dir > 0) Math.PI / 2 else -Math.PI / 2, sp, d, 0.0, null)
            }
        })
    },
    "INDEX_OUT_OF_RANGE" to { _, ph ->
        val d = 15 * FM(ph); val gap = Math.floor(rnd() * 7).toInt() - 3
        for (k in -3..3) if (k != gap) hbeam(P.x - 700, P.y + k * 72, 0.0, 1400.0, 13.0, d, 1.15, "#ff66ff", null)
        if (ph >= 2) { val g2 = Math.floor(rnd() * 7).toInt() - 3; for (k in -3..3) if (k != g2) hbeam(P.x + k * 72, P.y - 700, Math.PI / 2, 1400.0, 13.0, d, 1.75, "#66ffff", null) }
    },
    "DIV_BY_ZERO" to { e, ph -> S.pull = Pull(e.x, e.y, 1.5, 140.0); harea(e.x, e.y + 40, 160.0, 20 * FM(ph), 1.6, "#ffffff") },
    "RACE_CONDITION" to { _, ph ->
        val d = 9 * FM(ph)
        ew(listOf('L', 'R', 'T', 'B'), .6)
        S.q.add(Q(.45) {
            if (S.pg >= 2) for ((ox, oy) in listOf(Pair(HW() + 30, 0.0), Pair(-HW() - 30, 0.0), Pair(0.0, HH() + 30), Pair(0.0, -HH() - 30))) {
                val x = P.x + ox; val y = P.y + oy; val a = atan2(P.y - y, P.x - x)
                for (i in -1..1) hb(x, y, a + i * .16, 200.0, d, 0.0, null)
            }
        })
    },
    "DEADLOCK" to { _, ph ->
        val d = 11 * FM(ph)
        harea(P.x, P.y, 75.0, d, .9, "#99ccff", "mark")
        for (i in 0 until 4) { val an = i * 1.571 + rnd(); harea(P.x + cos(an) * 150, P.y + sin(an) * 150, 75.0, d, 1 + i * .12, "#99ccff", "mark") }
    },
    "INFINITE_LOOP" to { e, ph ->
        val d = 8 * FM(ph)
        for (k in 0 until 12) S.q.add(Q(k * .25) { if (S.pg == 2) for (j in 0 until 3) hb(e.x, e.y, S.t * 2.2 + j * 2.094, 190.0, d, 0.0, null) })
    },
    "CONTROL_SWAP" to { e, ph ->
        if ((S.lastSwap ?: -99.0) < S.t - 25) {
            S.lastSwap = S.t
            S.q.add(Q(.9) { if (S.pg == 2) { S.sw = 2.0; S.bt = .4; S.msg = "입력 반전!"; S.mt = 1.5 } })
        } else hptFire(0, e, ph)
    })
fun hptFire(i: Int, e: En, ph: Int) = HPT[i].second(e, ph)

fun hbAtk(e: En, dt: Double) {
    val Hh = S.hb ?: return
    Hh.t -= dt
    if (Hh.t > 0) return
    val ph = if (S.t < 60) 0 else if (S.t < 120) 1 else 2
    val left = S.T - S.t
    Hh.n++
    if (ph > Hh.p) {
        Hh.p = ph; S.msg = "PHASE " + (ph + 1) + " — 공격이 거세진다"; S.mt = 2.2; S.bt = .5; shake(8.0); snd(90.0, .4, "sawtooth", .1); bugName(e, "PHASE_" + (ph + 1)); Hh.t = 1.0
        return
    }
    Hh.t = doubleArrayOf(2.1, 1.65, 1.3)[ph] * (if (left < 30) .85 else 1.0)
    val pool = (0..(if (ph == 0) 6 else if (ph == 1) 10 else 12)).toList()
    var k: Int
    do { k = pool[(rnd() * pool.size).toInt()] } while (k == Hh.last)
    Hh.last = k
    bugName(e, HPT[k].first); hptFire(k, e, ph)
    if (ph == 2 && rnd() < .3) {
        var k2: Int
        do { k2 = pool[(rnd() * pool.size).toInt()] } while (k2 == k || k2 == 5 || k2 == 12)
        val k3 = k2
        S.q.add(Q(.8) { if (S.pg == 2 && !e.dead) { bugName(e, HPT[k3].first); hptFire(k3, e, ph) } })
    }
    snd(200.0, .15, "triangle", .05); shake(2.0)
}

// ===== 매 프레임 갱신 =====
fun upd(dt0: Double) {
    var dt = dt0
    if (S.hs > 0) { S.hs -= dt; dt *= .04 }
    S.t += dt; S.rc -= dt; S.rc2 -= dt
    val m = stat(); val tm = tmf()
    var vx0 = joy?.x ?: 0.0; var vy0 = joy?.y ?: 0.0
    if (S.sw > 0) { vx0 = -vx0; vy0 = -vy0 }
    val vl = hyp(vx0, vy0)
    P.mvg = vl > 0; P.at += dt
    if (Math.abs(vx0) > .2) P.dr = if (vx0 > 0) 1.0 else -1.0
    if (vl > 0) {
        if (rnd() < .3) burst(P.x, P.y + 15, 1.0, "#6a7099", 40.0, .3, 2.0)
        val k = Math.min(1.0, vl) / vl
        P.fx = vx0 / vl; P.fy = vy0 / vl
        val sl = if (P.slow > 0) .65 else 1.0
        P.x += vx0 * k * 180 * m.mv * sl * dt; P.y += vy0 * k * 180 * m.mv * sl * dt
    }
    if (P.slow > 0) P.slow -= dt
    if (S.sw > 0) S.sw -= dt
    S.pull?.let { p ->
        p.t -= dt
        val pdx = p.x - P.x; val pdy = p.y - P.y; val pd = Math.max(1.0, hyp(pdx, pdy))
        P.x += pdx / pd * p.s * dt; P.y += pdy / pd * p.s * dt
        if (p.t <= 0) S.pull = null
    }
    P.inv -= dt; P.hp = Math.min(P.mh, P.hp + tb("rg") * dt); S.ct -= dt
    val dmgd = P.hp < (S.lhp ?: P.hp) - .5
    if (S.ct <= 0 || dmgd) S.cb = 0
    if (dmgd && rlc("shield") > 0 && S.shcd <= 0) { S.shd = 1.2; S.shcd = 14.0 - 3 * (rlc("shield") - 1) }
    S.shcd -= dt; S.shd -= dt; S.mvb -= dt
    if (rlc("echo") > 0) {
        S.ea -= dt
        if (S.ea <= 0) {
            S.ea = 3.0
            val rr = 110.0 + 30 * (rlc("echo") - 1)
            fxR(P.x, P.y, rr, "#99ccff", .4)
            S.en.each { e -> if (!e.dead && !e.inv && !e.boss && hyp(e.x - P.x, e.y - P.y) < rr + e.r) e.es = 1.6 }
        }
    }
    S.lhp = P.hp
    if (S.pg >= 2) {
        val b = (S.bn ?: 4.0) - dt
        S.bn = b
        if (b <= 0) { S.bn = 5 + rnd() * 4; S.bt = .35; snd(160.0, .1, "triangle", .05) }
    }
    if (S.bt > 0) S.bt -= dt
    S.nk += dt
    if (S.nk > 5 && S.t > 30 && S.pg < 2) {
        S.nk = 2.0
        var c = 0; val ba = faceA()
        for (i in 0 until 7) {
            val an = ba + (i - 3) * .3
            val e = spawn("crawl", P.x + cos(an) * 270, P.y + sin(an) * 270)
            if (e != null) { c++; fxR(e.x, e.y, 24.0, "#e55", .4) }
        }
        if (c > 2) { S.msg = "앞길이 막힌다!"; S.mt = 1.5 }
    }
    if (!(P.x + P.y).isFinite()) { P.x = 0.0; P.y = 0.0 }
    if (!P.hp.isFinite()) P.hp = P.mh
    if (!P.xp.isFinite()) P.xp = 0.0
    S.sc -= dt
    if (S.sc <= 0 && S.pg < 2) {
        val n = Math.min(Math.min(5, 1 + Math.floor(tm / 2.5).toInt()), CAP - aliveN())
        for (i in 0 until n) spawn(pickE())
        S.sc = Math.max(.3, .85 - .04 * tm)
    }
    S.elt -= dt
    if (S.elt <= 0 && S.pg < 2) {
        S.elt = 70.0
        if (!S.en.any { it.el && !it.dead } && aliveN() < CAP) {
            var ty: String; var tr = 0
            do { ty = pickE() } while (ty in listOf("bat", "bomb", "mini") && tr++ < 8)
            val e = spawn(ty)
            if (e != null) { e.el = true; e.hp *= 6; e.mh = e.hp; e.sc += 1; e.r = Math.round(e.r * 1.3).toDouble(); e.sp *= 1.05; e.xp *= 5; S.msg = "엘리트 출현! (처치하면 유물)"; S.mt = 2.5 }
        }
    }
    S.rn -= dt
    if (S.rn <= 0 && S.t > 45 && S.pg < 2) {
        S.rn = 24.0
        var c = 0
        for (i in 0 until 8) {
            val an = i * .785
            val e = spawn("crawl", P.x + cos(an) * 290, P.y + sin(an) * 290)
            if (e != null) { c++; fxR(e.x, e.y, 26.0, "#e55", .4) }
        }
        if (c > 3) { S.msg = "포위!"; S.mt = 1.5 }
    }
    if (!S.boss && S.t >= 540) {
        S.boss = true
        val ba = rnd() * TAU
        S.bo = spawn("boss", P.x + cos(ba) * 300, P.y + sin(ba) * 300); S.cin = 2.6; S.cl = false
    }
    S.mt -= dt
    for ((k, w) in S.ws.entries.toList()) {
        if (k == "orbit") continue
        w.t -= dt
        if (w.t <= 0) w.t = if (fire(k, w)) Math.max(.15, WD[k]!!.i * m.iv) else .1
    }
    OB()?.let { o ->
        S.oct += dt; S.oa += 3.4 * dt
        val r = o.r * m.ar
        if (oVis()) for (i in 0 until o.n) {
            val a = S.oa + i * TAU / o.n; val x = P.x + cos(a) * r; val y = P.y + sin(a) * r
            S.en.each { e ->
                if (!e.dead && e.oc <= S.t && hyp(e.x - x, e.y - y) < e.r + o.hr) {
                    val dm = o.d * (1 + .2 * (o.w.l - 1))
                    hit(e, dm, o.c); o.cb?.invoke(e, dm); e.oc = S.t + .35
                }
            }
            S.ep.each { q -> if (q.l > 0 && hyp(q.x - x, q.y - y) < o.hr) { q.l = 0.0; burst(q.x, q.y, 6.0, o.c, 160.0, .25, 3.0); snd(500.0, .05, "square", .03) } }
        }
    }
    S.en.each { e -> if (!e.dead) enemyStep(e, dt, m) }
    S.ep.each { q ->
        q.l -= dt
        if (q.h != 0.0) {
            val ca = atan2(q.vy, q.vx); val sp0 = hyp(q.vx, q.vy)
            var df = atan2(P.y - q.y, P.x - q.x) - ca
            while (df > Math.PI) df -= TAU
            while (df < -Math.PI) df += TAU
            val na = ca + Math.max(-q.h * dt, Math.min(q.h * dt, df))
            q.vx = cos(na) * sp0; q.vy = sin(na) * sp0
        }
        q.x += q.vx * dt; q.y += q.vy * dt
        if (P.inv <= 0 && hyp(P.x - q.x, P.y - q.y) < 14) { q.l = 0.0; hurtP(q.d); if (q.rd != null) rvR(q.rd, q.x, q.y, q.d) }
    }
    if (S.os.isNotEmpty()) {
        val b = S.bo
        if (b != null && !b.dead) {
            S.os.each { o ->
                o.t -= dt; o.a += 3 * dt; o.cd -= dt
                for (i in 0 until 4) {
                    val an = o.a + i * 1.571; val x = b.x + cos(an) * 85; val y = b.y + sin(an) * 85
                    if (o.cd <= 0 && P.inv <= 0 && hyp(P.x - x, P.y - y) < 20) { hurtP(o.d); o.cd = .6; if (o.rd != null) rvR(o.rd, x, y, o.d) }
                }
            }
            S.os.removeAll { it.t <= 0 }
        } else S.os.clear()
    }
    S.ep.removeAll { it.l <= 0 }
    S.pr.each { p ->
        p.l -= dt
        val tg = p.tg
        if (tg != null && !tg.dead) {
            var df = atan2(tg.y - p.y, tg.x - p.x) - p.a
            while (df > Math.PI) df -= TAU
            while (df < -Math.PI) df += TAU
            p.a += Math.max(-p.tn * dt, Math.min(p.tn * dt, df))
        }
        p.x += cos(p.a) * 360 * dt; p.y += sin(p.a) * 360 * dt
        p.tr.add(doubleArrayOf(p.x, p.y))
        if (p.tr.size > 9) p.tr.removeAt(0)
        var i = 0
        while (i < S.en.size) {
            val e = S.en[i]
            if (!e.dead && hyp(e.x - p.x, e.y - p.y) < e.r + 5) {
                hit(e, p.d, if (p.ch) "#ff6" else "#6cf")
                if (p.ch) chainFrom(e, p.d * .6, 3)
                p.cb?.invoke(e, p.d)
                p.l = 0.0
                break
            }
            i++
        }
    }
    S.pr.removeAll { it.l <= 0 }
    S.fx.each { f ->
        f.l -= dt
        if (f.t == 'p') {
            S.en.each { e ->
                if (!e.dead && !e.boss) {
                    val d = hyp(f.x - e.x, f.y - e.y)
                    if (d < 110 && d > 2) { e.x += (f.x - e.x) / d * 250 * dt; e.y += (f.y - e.y) / d * 250 * dt }
                }
            }
            if (f.l <= 0) aoe(f.x, f.y, f.r * 1.2, if (f.d != 0.0) f.d else 48.0, null, "#a8f")
        }
    }
    S.fx.removeAll { it.l <= 0 }
    S.q.each { q -> q.t -= dt; if (q.t <= 0) q.f() }
    S.q.removeAll { it.t <= 0 }
    S.mn.each { mn ->
        mn.ar -= dt
        if (mn.ar <= 0 && !mn.x_ && S.en.any { !it.dead && hyp(it.x - mn.x, it.y - mn.y) < it.r + 25 }) {
            mn.x_ = true
            aoe(mn.x, mn.y, mn.r, mn.d, null, "#f86"); burst(mn.x, mn.y, 26.0, "#fa3", 380.0, .6, 4.0); burst(mn.x, mn.y, 10.0, "#444", 200.0, .8, 5.0); shake(5.0); stop(.05)
        }
    }
    S.mn.removeAll { it.x_ }
    val far = hyp(W, H) * 1.2
    S.en.removeAll { it.dead || hyp(it.x - P.x, it.y - P.y) >= far }
    val RMG = 1 + tb("mg") + up("mg") + relX().mg
    S.gm.each { gm ->
        gm.a += dt
        val dx = P.x - gm.x; val dy = P.y - gm.y; val d = Math.max(.001, hyp(dx, dy)); val R = 110 * RMG
        var sp = 0.0
        if (d < R) sp = 240 + 420 * (1 - d / R) else if (gm.a > 5) sp = 110.0
        if (sp > 0) { val mv = Math.min(d, sp * dt); gm.x += dx / d * mv; gm.y += dy / d * mv }
        if (d < 22) { gm.g = true; gainXP(gm.v); if (rlc("coll") > 0) S.mvb = 1.5 }
    }
    if (S.gm.size > 260) {
        val n = S.gm.size - 200
        var v = 0.0
        for (i in 0 until n) v += S.gm[i].v
        S.gm.subList(0, n).clear()
        gainXP(v)
    }
    S.gm.removeAll { it.g }
    if (P.xp >= P.nx && S.run) {
        P.xp -= P.nx; P.lv++; P.nx = 6.0 + P.lv * 4; P.hp = Math.min(P.mh, P.hp + P.mh * up("lu")); lvup()
    }
    if (S.rpend && S.run && S.pg < 2) { S.rpend = false; relicPick() }
    if (P.hp <= 0) {
        if (S.rv > 0) { S.rv--; P.hp = P.mh * .5; P.inv = 2.0; aoe(P.x, P.y, 220.0, 90.0, null, "#ffd24a"); S.msg = "부활!"; S.mt = 2.0 } else end(false)
    } else if (S.t >= S.T) {
        if (S.pg == 2) startP3()
        else if (S.boss && !S.bg) end(false, "제한 시간 안에 보스를 쓰러뜨리지 못했습니다")
        else end(true)
    }
    S.sh *= Math.pow(.01, dt); S.vg = Math.max(0.0, S.vg - dt * 1.6)
    val dr = Math.pow(.04, dt)
    S.pt.each { p -> p.l -= dt; p.x += p.vx * dt; p.y += p.vy * dt; p.vx *= dr; p.vy *= dr }
    S.pt.removeAll { it.l <= 0 }
}

private fun enemyStep(e: En, dt: Double, m: Stat) {
    if (e.mk > 0) {
        e.mk -= dt
        if (e.mk <= 0) {
            hit(e, e.md, "#9ef"); e.sl = 1.2; burst(e.x, e.y, 14.0, "#cff", 300.0, .5, 4.0); burst(e.x, e.y, 6.0, "#fff", 200.0, .4, 3.0)
            fxR(e.x, e.y, e.r * 3, "#9ef", .3); snd(1300.0, .12, "triangle", .05)
        }
    }
    if (e.boss) {
        val tr = e.tr
        e.tr = if (tr == null || tr < e.hp) e.hp else Math.max(e.hp, tr - (e.mh * .12 + (tr - e.hp) * 1.6) * dt)
    }
    e.sl -= dt; e.es -= dt; e.fl -= dt; e.sq = Math.max(0.0, e.sq - dt * 7); e.cd -= dt
    val a = atan2(P.y - e.y, P.x - e.x); val dd = hyp(P.x - e.x, P.y - e.y); val kn = hyp(e.kx, e.ky)
    var sp = e.sp * (if (e.sl > 0) .8 else 1.0) * (if (e.es > 0) .65 else 1.0) * (if (kn > 120) .25 else 1.0)
    var vx = 0.0; var vy = 0.0
    when (e.k) {
        "bat" -> { val z = sin(S.t * 6 + e.ph) * 25; vx = -sin(a) * z; vy = cos(a) * z }
        "charge" -> {
            if (e.st == 0) { if (e.cd <= 0 && dd < 280) { e.st = 1; e.cd = .6; e.dx = cos(a); e.dy = sin(a) } }
            else if (e.st == 1) { sp = 0.0; if (e.cd <= 0) { e.st = 2; e.cd = .45 } }
            else { sp = 0.0; vx = e.dx * 430; vy = e.dy * 430; if (e.cd <= 0) { e.st = 0; e.cd = 2.6 } }
        }
        "spit" -> {
            if (dd < 170) sp = -sp else if (dd < 240) sp = 0.0
            if (e.cd <= 0) { e.cd = 2.4; e.tl = .45 }
            if (e.tl > 0) { e.tl -= dt; if (e.tl <= 0) S.ep.add(EP(e.x, e.y, cos(a) * 170, sin(a) * 170, 3.5)) }
        }
        "bomb" -> {
            if (e.st == 0) { if (dd < 75) { e.st = 1; e.cd = .8; fxD(e.x, e.y, 85.0, .8) } }
            else {
                sp = 0.0
                if (e.cd <= 0) {
                    e.dead = true
                    val x = e.x; val y = e.y
                    fxR(x, y, 85.0, "#fa3", .35); fxO(x, y, 70.0, "#fff", .12, true); burst(x, y, 26.0, "#fa3", 380.0, .6, 4.0); burst(x, y, 8.0, "#444", 200.0, .7, 5.0)
                    shake(7.0); stop(.04); snd(80.0, .3, "sawtooth", .1)
                    if (hyp(P.x - x, P.y - y) < 95 && P.inv <= 0) { P.hp -= 15 * m.am; P.inv = invT(); S.vg = .5; shake(9.0) }
                }
            }
        }
        "hboss" -> {
            sp = 0.0; e.kx = 0.0; e.ky = 0.0
            e.x = P.x + sin(S.t * .6) * Math.min(W, 600.0) * .18; e.y = P.y - Math.min(H, 700.0) * .34
            hbAtk(e, dt)
        }
        "rival" -> {
            val hpr = e.hp / e.mh
            val ph = if (hpr > .7) 0 else if (hpr > .4) 1 else if (hpr > .15) 2 else 3
            val spd = doubleArrayOf(100.0, 115.0, 130.0, 150.0)[ph]
            val V = if (sin(S.t * .7) > 0) 1 else -1
            val PN = listOf("", "OVERFLOW", "KERNEL_PANIC", "CRITICAL_ERROR")
            if (e.php == null) e.php = 0
            if (ph > e.php!!) {
                e.php = ph; S.msg = "PHASE " + (ph + 1) + " — " + PN[ph]; S.mt = 2.5; S.bt = .5; bugName(e, PN[ph]); shake(12.0); stop(.12)
                // 단계 전환: 탄막을 지우고 충격파로 밀어내며 잠깐 숨 돌릴 틈을 준다
                S.ep.clear(); S.os.clear(); e.cd = Math.max(e.cd, 1.2); e.st = 0; P.inv = Math.max(P.inv, .8)
                val qx = P.x - e.x; val qy = P.y - e.y; val ql = Math.max(1.0, hyp(qx, qy))
                S.pull = Pull(P.x + qx / ql * 150, P.y + qy / ql * 150, .3, 500.0)
                fxR(e.x, e.y, 300.0, "#ff8ad8", .6); fxO(e.x, e.y, 120.0, "#ffffff", .16, true); burst(e.x, e.y, 50.0, "#ff8ad8", 480.0, .7, 4.0); snd(70.0, .5, "sawtooth", .12)
            }
            e.ph = ph.toDouble(); e.dmf = doubleArrayOf(1.0, 1.2, 1.45, 1.8)[ph]; S.hf = doubleArrayOf(1.0, .9, .8, .7)[ph]
            if (e.st == 0) {
                if (dd > 340) sp = spd
                else if (dd < 190) sp = -spd
                else { vx = -sin(a) * (70 + ph * 10) * V; vy = cos(a) * (70 + ph * 10) * V; sp = 0.0 }
                if (ph >= 2) {
                    e.sm2 += dt
                    if (e.sm2 > 11) {
                        e.sm2 = 0.0; S.xo = 240.0
                        var c = 0
                        for (i in 0 until 5) { val an = rnd() * TAU; if (spawn(pickE(), e.x + cos(an) * 120, e.y + sin(an) * 120) != null) c++ }
                        S.xo = 0.0
                        if (c > 0) { S.msg = "버그 소환!"; S.mt = 1.2 }
                    }
                    val tp = (e.tp ?: 6.0) - dt
                    e.tp = tp
                    if (tp <= 0) {
                        e.tp = if (ph == 3) 5.0 else 8.0
                        fxR(e.x, e.y, 40.0, "#ff66ff", .3)
                        val an = rnd() * TAU
                        e.x = P.x + cos(an) * 230; e.y = P.y + sin(an) * 230
                        fxR(e.x, e.y, 40.0, "#66ffff", .4); bugName(e, "SEGMENTATION_FAULT"); e.cd = Math.max(e.cd, .5)
                    }
                }
                if (e.cd <= 0) {
                    e.cd = doubleArrayOf(1.0, .8, .62, .45)[ph] * (if (S.t > 90) .85 else 1.0); e.cast++
                    val n = intArrayOf(1, 2, 2, 3)[ph]
                    val ws = e.wl!!.shuffled().take(n)
                    e.cw = ws[0]
                    for (k in ws) rivalAtk(e, k, a)
                    if (ph >= 1 && e.cast % 3 == 0) {
                        val pool = if (ph >= 2) listOf(0, 1, 2, 3, 4, 6, 7, 8, 9, 10, 11, 12) else listOf(0, 1, 2, 3, 4, 6, 9)
                        val hk = pool[(rnd() * pool.size).toInt()]
                        bugName(e, HPT[hk].first); hptFire(hk, e, Math.min(2, ph))
                    }
                }
            } else if (e.st == 1) { sp = 0.0; if (e.cd <= 0) { e.st = 2; e.cd = .4 } }
            else {
                sp = 0.0; vx = e.dx * (470 + ph * 30); vy = e.dy * (470 + ph * 30)
                if (e.cd <= 0) { e.st = 0; e.cd = .8; e.rd?.let { rvR(it, e.x, e.y, e.dcv) } }
            }
        }
        "boss" -> {
            val rage = e.hp < e.mh * .5
            if (rage && !e.rg) bossRage(e)
            if (e.st == 0) {
                if (rage) sp *= 1.4
                if (e.cd <= 0) {
                    e.cd = if (rage) 3.0 else 4.2; e.na++
                    if (e.na % 3 == 0) { e.st = 1; e.cd = .9; e.dx = cos(a); e.dy = sin(a) }
                    else { val k = bossPick(e, rage); bptFire(k, e, rage); bugName(e, BPTN[k]!!) }
                }
            } else if (e.st == 1) { sp = 0.0; if (e.cd <= 0) { e.st = 2; e.cd = .55 } }
            else {
                sp = 0.0; vx = e.dx * 470; vy = e.dy * 470
                if (e.cd <= 0) { e.st = 0; e.cd = if (rage) 2.2 else 2.6; if (rage) bRing(e, 12, rnd() * 6, 150.0, 9.0) }
            }
            e.sm += dt
            if (e.sm > 14) {
                e.sm = 0.0
                var c = 0
                for (i in 0 until 5) if (spawn("crawl", e.x + cos(i * 1.25) * 60, e.y + sin(i * 1.25) * 60) != null) c++
                if (c > 0) { S.msg = "심연의 새끼들!"; S.mt = 2.0 }
            }
        }
    }
    e.x += (cos(a) * sp + vx + e.kx) * dt; e.y += (sin(a) * sp + vy + e.ky) * dt
    val kd = Math.pow(.003, dt)
    e.kx *= kd; e.ky *= kd
    if (P.inv <= 0 && !e.inv && hyp(P.x - e.x, P.y - e.y) < e.r + 12) {
        P.hp -= (if (e.boss) 22 else 10) * m.am; P.inv = invT(); shake(8.0); stop(.07); S.vg = .55; burst(P.x, P.y, 14.0, "#f55", 280.0, .4, 4.0); snd(90.0, .22, "sawtooth", .09)
    }
}

// ===== 레벨업 선택지 =====
fun opts(): ArrayList<Opt> {
    val o = ArrayList<Opt>()
    for ((k, w) in S.ws) { val D = WD[k]!!; if (w.l < 5) o.add(Opt(D.n + " Lv" + w.l + " → " + (w.l + 1), "피해 +20%") { w.l++ }) }
    val need = Math.max(3.0, 5 - chm("fl") - rlc("cata"))
    for ((k, D) in WD) {
        if (D.fz && !S.ws.containsKey(k) && D.req.all { r -> (S.ws[r]?.l ?: 0) >= need }) {
            o.add(Opt("★ 합체: " + D.req.joinToString(" + ") { WD[it]!!.n } + " → " + D.n, D.t, true) {
                SV.stF++; sv()
                D.req.forEach { S.ws.remove(it) }
                S.ws[k] = Wp(1, 0.0)
                fxR(P.x, P.y, 180.0, D.c, .5); burst(P.x, P.y, 40.0, D.c, 420.0, .8, 4.0); shake(10.0); snd(200.0, .5, "sawtooth", .1)
            })
        }
    }
    if (S.ws.size < 4 + tb("ws")) for ((k, D) in WD) if (!S.ws.containsKey(k) && UW(k)) o.add(Opt("새 무기: " + D.n, D.t) { S.ws[k] = Wp(1, .2) })
    val np = S.ps.size
    for ((k, D) in PD) {
        val l = (S.ps[k] ?: 0)
        if (l >= D.mx) continue
        if (l == 0 && np >= 4 + tb("ps")) continue
        o.add(Opt(D.n + " Lv" + l + " → " + (l + 1), D.t) { S.ps[k] = l + 1 })
    }
    return o
}

// ===== 시작·정산 =====
fun startGame(w: String) {
    val hp = 100 + tb("hp") + chm("hp") + up("hp")
    P = Pl().also { it.hp = hp; it.mh = hp; it.mh0 = hp }
    val s = St(w, 300 + tb("tm"))
    Sn = s
    s.ws[w] = Wp(1, .2)
    s.rl.putAll(SV.rk)
    s.rv = tb("rv").toInt(); s.rr = tb("rr").toInt(); s.sl = tb("sl").toInt()
    joy = null; aimJ = null
    bgmSet("game")
    uiGameStart()
    if (s.sl > 0) { s.sl--; P.lv++; lvup() }
}
fun unlockChk(): List<String> {
    val r = ArrayList<String>()
    for (c in CHR) {
        val ok = when (c.id) { "kai" -> true; "lira" -> SV.stW >= 1; "bolt" -> SV.stMk >= 300; "nova" -> SV.stF >= 1; "ember" -> SV.stB >= 1; else -> false }
        if (!SV.u.contains(c.id) && ok) { SV.u.add(c.id); r.add(c.n) }
    }
    sv()
    return r
}
fun bank(w: Boolean): Int {
    val first = !S.gb && SV.stG < 3
    if (!S.gb) { SV.stG++; S.gb = true }
    val dk = S.kills - S.k0
    SV.stW += if (w) 1 else 0
    SV.stMk = Math.max(SV.stMk, S.kills)
    SV.stB += if (S.bg) 1 else 0
    val nu = unlockChk()
    val base = Math.max(1, Math.floor((dk / 80.0 + S.t / 150 + (if (w) (if (dk >= 100) 3 else 1) else 0) + (if (S.bg) 3 else 0)) * (1 + tb("pg"))).toInt())
    val bonus = if (first) 3 else 0
    val sh = base + bonus
    SV.p += sh; sv()
    S.k0 = S.kills; S.pr0 += sh; S.nu.addAll(nu)
    if (bonus > 0) S.bonus = true
    return sh
}
fun end(w: Boolean, msg0: String? = null) {
    if (S.ended) return
    if (w && S.pg < 2 && S.gl == 0.0 && S.bg) { bank(true); startGlitch(); return }
    var msg = msg0
    if (w && S.pg < 2 && !S.bg && msg == null) msg = "보스(9분 등장)를 쓰러뜨려야 다음 페이지로 넘어갈 수 있습니다"
    S.ended = true; S.run = false; bgmSet(null); bank(w)
    uiEnd(w, msg)
}

// ===== 페이지 전환 =====
fun randCrack(sx: Double, sy: Double, a0: Double, n: Int, base: Double, extra: Double): List<DoubleArray> {
    var a = a0; var x = sx; var y = sy
    val p = arrayListOf(doubleArrayOf(x, y))
    for (j in 0 until n) { a += (rnd() - .5) * .9; x += cos(a) * (base + rnd() * extra); y += sin(a) * (base + rnd() * extra); p.add(doubleArrayOf(x, y)) }
    return p
}
fun startGlitch() {
    bgmSet(null)
    S.snap = snapshot()
    S.gl = .001; S.gs = false
    S.cr = List(10) { i -> randCrack(W / 2, H / 2, i * .63 + rnd() * .4, 10, 45.0, 55.0) }
    val n = 14
    S.zz = List(n + 1) { i -> doubleArrayOf(W / 2 + (if (i == 0 || i == n) 0.0 else (rnd() - .5) * 110), H * i / n) }
    S.msg = ""; S.mt = 0.0
}
fun glUpd(dt: Double) {
    if (S.gl <= 0) return
    S.gl += dt
    val t = S.gl; val gi = if (t < 1.2) 0.0 else Math.min(1.0, (t - 1.2) / 1.6)
    if (rnd() < dt * 18 * gi) snd(130 + rnd() * 450, .06, "triangle", .05)
    if (!S.gs && t > 3.4) { S.gs = true; snd(60.0, .6, "sawtooth", .12) }
    if (t > 5.6) nextPage()
}
fun clearField() { S.en.clear(); S.pr.clear(); S.ep.clear(); S.mn.clear(); S.fx.clear(); S.pt.clear(); S.q.clear(); S.gm.clear(); S.os.clear() }
fun nextPage() {
    S.pg = 2; bgmSet("boss"); S.t = 0.0; S.T = 180.0; S.boss = true; S.bg = false; S.dc = 0.0; S.cin = 0.0; S.cl = false
    clearField()
    S.sc = 0.0; S.rn = 0.0; S.nk = 0.0; S.cb = 0; S.lt = -9.0; S.snap = null; S.gl = 0.0; S.pull = null; P.slow = 0.0; P.hp = Math.max(P.hp, P.mh * .6); P.inv = 2.5
    S.ck = List(14) { i ->
        val sx = if (i < 8) W / 2 else rnd() * W
        val sy = if (i < 8) H / 2 else if (rnd() < .5) 0.0 else H
        val a = if (i < 8) i * .785 + rnd() * .5 else atan2(H / 2 - sy, W / 2 - sx) + (rnd() - .5) * .8
        randCrack(sx, sy, a, 14, 50.0, 70.0)
    }
    S.hb = Hbs(2.5)
    S.bo = spawn("hboss", P.x, P.y - 300)
    S.bo!!.inv = true
    S.msg = "PAGE 2 — 오류의 세계 · 버텨라! (3분)" + (if (S.nu.isNotEmpty()) "  ★해금: " + S.nu.joinToString(", ") else ""); S.mt = 4.0
}
fun startP3() {
    S.tf = .001; S.t3 = false
    val c = CHR[(rnd() * CHR.size).toInt()]
    S.rivalId = c.id; S.rcs = HSC[c.id]!![0].n
    snd(80.0, .7, "sawtooth", .12)
}
fun tfUpd(dt: Double) {
    S.tf += dt
    if (!S.t3 && S.tf > 2.4) { S.t3 = true; toRival(); snd(520.0, .3, "triangle", .08) }
    if (S.tf > 4) S.tf = 0.0
}
fun toRival() {
    bank(true)
    val c = CHR.find { it.id == S.rivalId } ?: CHR[0]
    val bs = listOf("pulse", "blade", "rail", "grav", "orbit", "arc", "frost", "mine").shuffled().take(3)
    val fz = WD.values.filter { it.fz }.map { it.id }.shuffled().take(2)
    S.pg = 3; S.t = 0.0; S.T = 1e9; S.boss = true; S.bg = false; S.dc = 0.0; S.cin = 0.0
    clearField()
    S.ck = null; S.hb = null; S.pull = null; P.slow = 0.0; S.hf = 1.0; S.cb = 0; S.nk = 0.0; S.lt = -9.0; P.hp = Math.max(P.hp, P.mh * .6); P.inv = 2.5
    val h = HSC[c.id]!!
    ES["rival"] = Es(h[0].n, h[2].n, h[0].w, h[2].w, h[0].n, h[2].n)
    val e = spawn("rival", P.x, P.y - 330)!!
    e.boss = true; e.hp = P.mh * 50; e.mh = e.hp; e.wl = bs + fz; e.cid = c.id; e.col = "#ff8ad8"; e.cd = 2.0
    S.bo = e; S.msg = "ERROR_CLONE 출현: " + c.n; S.mt = 4.0
}
fun fxStep(dt: Double) {
    if (S.bt > 0) S.bt -= dt
    S.sh *= Math.pow(.01, dt); S.vg = Math.max(0.0, S.vg - dt * 1.6)
    val dr = Math.pow(.04, dt)
    S.pt.each { p -> p.l -= dt; p.x += p.vx * dt; p.y += p.vy * dt; p.vx *= dr; p.vy *= dr }
    S.pt.removeAll { it.l <= 0 }
    S.fx.each { it.l -= dt }
    S.fx.removeAll { it.l <= 0 }
}
fun cinUpd(dt: Double) {
    if (S.cin > 0) {
        S.cin -= dt
        if (S.cin > 1.4) S.sh = Math.max(S.sh, 2.0)
        if (!S.cl && S.cin <= 1.4) {
            S.cl = true
            val b = S.bo!!
            fxR(b.x, b.y, 260.0, "#f84", .5); burst(b.x, b.y, 60.0, "#f84", 450.0, .7, 4.0); shake(14.0); snd(60.0, .5, "sawtooth", .12)
        }
        if (S.cin <= 0) S.bo!!.cd = 3.0
    } else {
        S.dc -= dt; S.ex -= dt
        if (S.ex <= 0) {
            S.ex = .11
            val x = S.bx + (rnd() - .5) * 90; val y = S.by + (rnd() - .5) * 90
            fxR(x, y, 30 + rnd() * 30, if (rnd() < .5) "#ffd24a" else "#f84", .35); burst(x, y, 14.0, "#fa3", 320.0, .6, 4.0); shake(7.0); S.bt = .3; snd(100 + rnd() * 80, .2, "sawtooth", .07)
        }
        if (S.dc <= 0) { S.dc = 0.0; end(true) }
    }
    if (Sn != null) fxStep(dt)
}
/** 한 프레임 진행 (원본의 loop) */
fun tick(dt: Double) {
    val s = Sn ?: return
    if (s.gl > 0) glUpd(dt)
    else if (s.tf > 0) tfUpd(dt)
    else if (s.run && !s.pz) {
        if (s.cin > 0 || s.dc > 0) cinUpd(dt)
        else {
            val sb = if (dt > .034) 2 else 1
            for (i in 0 until s.sp * sb) if (s.run && s.cin <= 0 && s.dc <= 0) upd(dt / sb)
        }
    }
}
