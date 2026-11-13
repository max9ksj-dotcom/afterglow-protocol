package com.afterglow.protocol

// ===== 보급 캡슐: 몬스터 처치 시 가끔(엘리트는 반드시) 떨어지는 특수 아이템 (웹판 capDrop/capTake/capUpd/drawCaps와 같음) =====
// h 수리(체력 25% 회복) · m 자석(맵의 경험치 구슬 전부 끌어옴) · b 정화(주변 적에게 피해+밀쳐내기, 보스는 약하게)
class Cap(var x: Double, var y: Double, val k: Char, var l: Double) { var g = false }
const val CPC = .006
const val CPL = 30.0
val CPD = mapOf('h' to Pair("수리 캡슐", "#5dff8a"), 'm' to Pair("자석 캡슐", "#5fd8ff"), 'b' to Pair("정화 캡슐", "#ff5a5a"))

fun capDrop(x: Double, y: Double) {
    if (S.cp.size >= 4) return
    val r = rnd()
    var k = if (r < .4) 'h' else if (r < .75) 'm' else 'b'
    if (k == 'h' && P.hp >= P.mh * .9) k = if (rnd() < .5) 'm' else 'b'
    S.cp.add(Cap(x, y, k, CPL)); fxR(x, y, 26.0, CPD[k]!!.second, .4)
}

fun capTake(c: Cap) {
    val (nm, col) = CPD[c.k]!!
    S.cpn++
    when (c.k) {
        'h' -> { P.hp = Math.min(P.mh, P.hp + Math.round(P.mh * .25)); burst(P.x, P.y, 24.0, col, 260.0, .6, 4.0); snd(660.0, .25, "triangle", .07) }
        'm' -> { for (q in S.gm) q.vac = true; fxR(P.x, P.y, Math.max(W, H) * .6, col, .5); snd(880.0, .3, "sine", .07) }
        else -> {
            val R = 420.0; val d = 50.0 + 8 * P.lv
            for (e in ArrayList(S.en)) if (!e.dead && hyp(e.x - P.x, e.y - P.y) < R + e.r) hit(e, if (e.boss) d * .3 else d, col)
            fxR(P.x, P.y, R, col, .45)
            S.fx.add(Fx('o', .35, .35).also { it.x = P.x; it.y = P.y; it.r = R; it.c = col; it.k = true })
            shake(8.0); stop(.06); snd(90.0, .4, "sawtooth", .1)
        }
    }
    S.fx.add(Fx('n', 1.0, 1.0).also { it.x = P.x + 8 - nm.length * 5; it.y = P.y - 30; it.vs = nm; it.cr = true })
}

fun capUpd(dt: Double) {
    for (c in ArrayList(S.cp)) {
        c.l -= dt
        val dx = P.x - c.x; val dy = P.y - c.y; val d = Math.max(.001, hyp(dx, dy))
        if (d < 70) { val mv = Math.min(d, 260 * dt); c.x += dx / d * mv; c.y += dy / d * mv }
        if (d < 24 && !c.g) { c.g = true; capTake(c) }
    }
    S.cp.removeAll { it.g || it.l <= 0 }
}

fun drawCaps(g: G) {
    for (c in S.cp) {
        if (c.l < 5 && Math.floor(c.l * 8).toInt() % 2 == 1) continue
        val col = CPD[c.k]!!.second; val x = Math.round(c.x).toDouble(); val y = Math.round(c.y + Math.sin(S.t * 4 + c.x) * 3).toDouble()
        g.lighter = true; g.globalAlpha = .25 + .15 * Math.sin(S.t * 6); g.fillStyle = col; g.circleFill(x, y, 16.0); g.lighter = false; g.globalAlpha = 1.0
        g.fillStyle = "rgba(0,0,0,.3)"; g.fillRect(x - 7, y + 12, 14.0, 3.0)
        g.fillStyle = "#14102a"; g.fillRect(x - 7, y - 10, 14.0, 20.0)
        g.fillStyle = col; g.fillRect(x - 5, y - 8, 10.0, 16.0)
        g.fillStyle = "#ffffff"; g.fillRect(x - 5, y - 1, 10.0, 2.0); g.fillRect(x - 3, y - 6, 2.0, 3.0)
    }
}
