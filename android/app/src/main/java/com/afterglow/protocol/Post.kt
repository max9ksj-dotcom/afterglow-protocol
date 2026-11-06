package com.afterglow.protocol

// 생존 이후 콘텐츠: 과부하 등급·심층·보스 러시·각성·도전 과제. 웹판의 같은 이름 함수와 수치를 맞춘다.

fun ocN(): Int = Sn?.oc ?: 0
fun ocP(): Double = 1 + .25 * ocN()
fun dmgMul(): Double = (1 + .12 * ocN()) * (1 + .06 * (Sn?.fl ?: 0))
fun eHp(): Double = (1 + .35 * ocN()) * (if (S.pg >= 4) Math.pow(1.22, S.fl.toDouble()) else 1.0) * (if (S.mut?.id == "swarm") .7 else 1.0)
fun eSp(): Double = (1 + .03 * ocN()) * (if (S.mut?.id == "fast") 1.3 else 1.0)

fun xv(k: String): Int {
    val x = SV.x
    return when (k) {
        "clr" -> x.clr; "fl" -> x.fl; "occ" -> if (x.clr > 0) x.occ else 0; "br" -> x.br; "tk" -> x.tk
        "mk" -> SV.stMk; "fz" -> SV.stF; "ch" -> SV.u.size; "rl" -> SV.rd.size; "uv" -> SV.up.values.sum()
        "nd" -> SV.n.size; "aw" -> x.aw.values.sum()
        else -> 0
    }
}
fun achChk(): List<Ach> {
    val r = ArrayList<Ach>()
    for (a in ACH) if (!SV.x.ac.contains(a.id) && xv(a.k) >= a.v) {
        SV.x.ac.add(a.id); SV.p += a.r; r.add(a)
        Sn?.let { it.pr0 += a.r }
    }
    if (r.isNotEmpty()) { sv(); Sn?.na?.addAll(r.map { it.n }) }
    return r
}
fun xClear() {
    val x = SV.x; val o = ocN()
    x.clr++; x.occ = Math.max(x.occ, o); x.ocm = Math.max(x.ocm, Math.min(OCN, o + 1))
    val t = Math.floor(S.tt).toInt()
    if (t > 0 && (x.bt == 0 || t < x.bt)) x.bt = t
    if (S.rush) { x.br++; SV.p += 15; S.pr0 += 15 }
    sv()
}

fun deepStart() {
    val s = S
    s.ended = false; s.run = true; s.pz = false; s.pg = 4; s.fl = 0; s.boss = true; s.ck = null; s.hb = null; s.gl = 0.0; s.tf = 0.0
    uiDeep(); bgmSet("boss"); deepFloor()
}
fun deepFloor() {
    S.fl++
    val bf = S.fl % 5 == 0
    S.t = 0.0; S.T = if (bf) 1e9 else 45.0; S.bg = false; S.dc = 0.0; S.cin = 0.0
    clearField()
    S.pull = null; P.slow = 0.0; P.inv = Math.max(P.inv, 2.0); S.sc = .5; S.mk2 = 2.0; S.elt = 6.0; S.lt = -9.0; S.sw = 0.0; S.cb = 0
    S.mut = if (bf) null else MUT[(rnd() * MUT.size).toInt()]
    if (bf) deepBoss() else { S.msg = S.mut!!.n; S.mt = 2.5 }
    S.bt = .4; shake(6.0); fxR(P.x, P.y, 240.0, "#5fd8ff", .5); snd(140.0, .4, "sawtooth", .08)
}
fun deepBoss() {
    val a = rnd() * TAU
    if (S.fl % 10 == 0) {
        val c = CHR[(rnd() * CHR.size).toInt()]; val h = HSC[c.id]!!
        val bs = listOf("pulse", "blade", "rail", "grav", "orbit", "arc", "frost", "mine").shuffled().take(3)
        val fz = WD.values.filter { it.fz }.map { it.id }.shuffled().take(2)
        S.rivalId = c.id; S.rcs = h[0].n; S.hf = 1.0
        ES["rival"] = Es(h[0].n, h[2].n, h[0].w, h[2].w, h[0].n, h[2].n)
        val e = spawn("rival", P.x, P.y - 330)!!
        e.boss = true; e.hp = P.mh * 30 * eHp(); e.mh = e.hp; e.wl = bs + fz; e.cid = c.id; e.col = "#ff8ad8"; e.cd = 2.0
        S.bo = e; S.msg = "거울 층 — " + c.n; S.mt = 3.0
    } else {
        val e = spawn("boss", P.x + cos(a) * 300, P.y + sin(a) * 300)!!
        e.hp *= 1.5; e.mh = e.hp
        S.bo = e; S.cin = 2.6; S.cl = false; S.msg = "수호자 출현"; S.mt = 3.0
    }
}
fun deepUpd(dt: Double) {
    if (S.pg < 4) return
    val M = S.mut?.id; val bf = S.fl % 5 == 0
    if (S.sc <= 0) {
        val n = Math.min(if (M == "swarm") 8 else if (bf) 2 else 5, CAP - aliveN())
        for (i in 0 until n) spawn(pickE())
        S.sc = if (M == "swarm") .35 else if (bf) 1.6 else .55
    }
    if (M == "drain" && P.hp > 0) P.hp = Math.max(1.0, P.hp - P.mh * .012 * dt)
    S.mk2 -= dt
    if (S.mk2 <= 0) {
        if (M == "hail") {
            S.mk2 = 2.6
            val b = rnd() * TAU
            for (i in 0 until 6) { val a = b + i * 1.047; val x = P.x + cos(a) * 320; val y = P.y + sin(a) * 320; hb(x, y, atan2(P.y - y, P.x - x), 175.0, 9.0, 0.0, null) }
        } else if (M == "field") {
            S.mk2 = 2.2
            for (i in 0 until 3) harea(P.x + (rnd() - .5) * 260, P.y + (rnd() - .5) * 260, 62.0, 12.0, .95, "#ff8844")
        } else S.mk2 = 9.0
    }
}
fun deepClear() {
    if (S.ended || !S.run) return
    val x = SV.x; val bf = S.fl % 5 == 0; val gd = S.mut?.id == "gold"
    val pt = Math.round((2 + S.fl * .5) * (if (bf) 3 else 1) * (if (gd) 2 else 1) * ocP()).toInt()
    val cr = (if (bf) 3 else 1) * (if (gd) 2 else 1)
    var v = 0.0
    S.gm.each { v += it.v }
    if (v > 0) gainXP(v)
    SV.p += pt; x.cr += cr; S.pr0 += pt; S.cr0 += cr; x.fl = Math.max(x.fl, S.fl)
    clearField(); achChk(); sv()
    floorPick(pt, cr)
}
fun floorOpts(): List<Opt> {
    val c = S.fl
    return listOf(
        Opt("수리", "체력 50% 회복") { P.hp = Math.min(P.mh, P.hp + P.mh * .5) },
        Opt("공격 회로", "이번 판 피해 +12%") { S.db += .12 },
        Opt("방어 회로", "이번 판 받는 피해 -5%") { S.da = Math.min(.4, S.da + .05) },
        Opt("기동 회로", "이번 판 이동 속도 +6%") { S.dv += .06 },
        Opt("체력 회로", "최대 체력 +15%") { val v = Math.round(P.mh * .15).toDouble(); P.mh += v; P.hp += v },
        Opt("유물 탐색", "유물 3개 중 하나를 고릅니다") { S.rpend = true },
        Opt("결정 추출", "잔광 결정 +2") { SV.x.cr += 2; S.cr0 += 2; sv() },
        Opt("포인트 추출", "특성 포인트 +" + (3 + c)) { SV.p += 3 + c; S.pr0 += 3 + c; sv() })
}
fun rushStart(w: String) {
    startGame(w)
    S.rush = true
    val ks = WD.keys.filter { UW(it) && it != w && !WD[it]!!.fz }.shuffled().take(2)
    S.ws[w]!!.l = 5
    for (k in ks) S.ws[k] = Wp(5, .2)
    for (k in PD.keys.shuffled().take(3)) S.ps[k] = Math.min(3, PD[k]!!.mx)
    P.lv = 25; P.nx = 106.0; S.t = 530.0; S.T = 900.0; S.elt = 999.0; S.msg = "곧 수호자가 나타납니다"; S.mt = 3.0
}
fun awkCost(a: Awk, l: Int) = a.c * (l + 1)
fun awkFmt(a: Awk, v: Double): String = if (a.u == "p") "+" + fmtN(Math.round(v * 1000) / 10.0) + "%" else "+" + Math.round(v)
