package com.afterglow.protocol

import android.os.Handler
import android.os.Looper

// 개발용 빌드에서만 쓰는 자동 시험: adb로 원하는 구간에 바로 들어가 자동으로 플레이한다. (정식 APK에서는 호출되지 않음)
private val dh = Handler(Looper.getMainLooper())
fun debugRun(mode: String) {
    if (mode == "lv") { startGame("pulse"); dh.postDelayed({ lvup() }, 2000); return }
    if (mode.startsWith("ui")) {
        val x = SV.x; x.clr = 2; x.ocm = 4; x.fl = 12; x.cr = 25; x.bt = 754; rcb()
        when (mode) { "uioc" -> ocUI(); "uirush" -> rushUI(); "uiawk" -> awkUI(); "uiach" -> achUI(); "uirec" -> recUI(); else -> lab() }
        return
    }
    if (mode.startsWith("fit")) { fitRun(mode); return }
    if (mode == "fuse") { fuseRun(); return }
    SV.n.add("u10"); rcb()
    startGame("pulse")
    P.mh = 1e7; P.hp = 1e7; S.sp = 3
    when (mode) {
        "boss" -> { S.t = 535.0; S.T = 900.0 }
        "glitch" -> { S.bg = true; dh.postDelayed({ startGlitch() }, 3000) }
        "p2" -> dh.postDelayed({ nextPage() }, 2000)
        "p3" -> dh.postDelayed({ S.pg = 2; startP3() }, 2000)
        "deep" -> dh.postDelayed({ deepStart() }, 2000)
        "deep5" -> dh.postDelayed({ deepStart(); S.fl = 3; deepFloor() }, 2000)
        "deep10" -> dh.postDelayed({ deepStart(); S.fl = 8; deepFloor() }, 2000)
        "rush" -> { rushStart("pulse"); P.mh = 1e7; P.hp = 1e7; S.sp = 3 }
    }
    var n = 0
    val r = object : Runnable {
        override fun run() {
            val s = Sn ?: return
            n++
            if (screen == "lvup") pick((rnd() * s.cur.size).toInt())
            else if (screen == "relic") relicTake(0)
            if (!s.ended) {
                val a = n * .4
                joy = Joy(0.0, 0.0, cos(a), sin(a))
                val b = s.bo
                if (mode == "p3" && b != null && !b.dead && s.pg == 3) b.hp -= b.mh * .012
                if ((mode == "boss" || mode == "rush") && b != null && !b.dead && b.k == "boss") b.hp -= b.mh * .012
                if (mode.startsWith("deep") && s.pg >= 4) {
                    if (b != null && !b.dead && s.fl % 5 == 0 && s.cin <= 0) { b.inv = false; hit(b, 1e9) }
                    if (s.T < 1e8 && n % 30 == 0) s.t = s.T
                    P.hp = P.mh
                }
                dh.postDelayed(this, 300)
            }
        }
    }
    dh.postDelayed(r, 500)
}

/** 합체 무기가 재료 무기의 증강을 받는지 점검한다. 결과는 logcat의 AGFUSE에 나온다 */
private fun fuseRun() {
    SV.n.remove("u10"); rcb()
    startGame("pulse"); P.mh = 1e9; P.hp = 1e9
    dh.postDelayed({
        val s = S
        fun line(): List<En> {
            s.en.clear(); s.pr.clear(); s.q.clear(); s.ws.clear()
            return (0 until 10).map { i -> spawn("tank", P.x + 30 + i * 20, P.y)!!.also { it.hp = 1e9; it.mh = 1e9 } }
        }
        fun cnt(l: List<En>) = l.count { it.hp < 1e9 }
        fun ps(k: String, v: Int) { s.ps.clear(); if (v > 0) s.ps[k] = v }
        fun aimAt(a: Double) { aimJ = Joy(0.0, 0.0, cos(a), sin(a)) }
        aimAt(0.0)
        fun rail(v: Int): Int { ps("pierce", v); val l = line(); fire("fz_rail", Wp(1, 0.0)); return cnt(l) }
        fun rico(v: Int): Int { ps("pierce", v); val l = line(); applyR(l[0], 10.0, "ricochet", "#fff"); return cnt(l.drop(1)) }
        fun chain(v: Int): Int { ps("chain", v); val l = line(); applyR(l[0], 10.0, "chain", "#fff"); return cnt(l.drop(1)) }
        fun tn(v: Int): Double { ps("aim", v); line(); fire("fz_storm", Wp(1, 0.0)); return s.pr.last().tn }
        fun storm(v: Int): Int { ps("chain", v); val l = line(); fire("fz_storm", Wp(1, 0.0)); repeat(40) { P.hp = 1e9; upd(.016) }; return cnt(l) }
        fun lock(v: Int): Double { aimAt(.5); ps("aim", v); line(); fire("fz_rail", Wp(1, 0.0)); aimAt(0.0); return s.la }
        android.util.Log.i("AGFUSE", "rail " + rail(0) + "->" + rail(3) + " rico " + rico(0) + "->" + rico(3) + " chain " + chain(0) + "->" + chain(3) +
            " tn " + tn(0) + "->" + tn(2) + " storm " + storm(0) + "->" + storm(3) + " lock " + lock(0) + "->" + lock(1))
        var n = 0
        for ((k, D) in WD) if (D.fz) {
            s.ps.clear(); s.ps["pierce"] = 3; s.ps["chain"] = 3; s.ps["aim"] = 3
            line(); s.ws[k] = Wp(1, 0.0); fire(k, s.ws[k]!!)
            repeat(30) { P.hp = 1e9; upd(.016) }
            n++
        }
        android.util.Log.i("AGFUSE", "smoke ok " + n)
        fun set(p: Map<String, Int>) { s.ps.clear(); s.ps.putAll(p) }
        fun step(n: Int) = repeat(n) { P.hp = 1e9; upd(.016) }
        fun cyc(k: String, p: Map<String, Int>): Double { val l = line(); l.size; set(p); s.ws[k] = Wp(1, 0.0); upd(.016); return s.ws[k]!!.t }
        fun frost(v: Int): Int { val l = line(); set(mapOf("prj" to v)); val w = Wp(1, 99.0); s.ws["frost"] = w; fire("frost", w); step(30); return cnt(l) }
        fun far(k: String, v: Int): Boolean { line(); set(emptyMap()); val d = WD[k]!!.r * stat().ar + 30; s.en.clear(); set(mapOf("rng" to v)); spawn("tank", P.x + d, P.y)!!.also { it.hp = 1e9; it.mh = 1e9 }; return fire(k, Wp(1, 99.0)) }
        fun stn(v: Int): Double { val l = line(); set(mapOf("dur" to v)); fire("arc", Wp(1, 99.0)); return l[0].stn }
        fun orb(p: Map<String, Int>): String { line(); set(p); s.ws["orbit"] = Wp(1, 0.0); val o = OB()!!; return "" + o.n + "/" + o.r + "/" + o.iv }
        fun offer(k: String): String { line(); s.ps.clear(); s.ws[k] = Wp(1, 0.0); return AGW.keys.filter { p -> opts().any { it.a.startsWith(PD[p]!!.n) } }.joinToString(",") }
        android.util.Log.i("AGFUSE", "reload rail " + cyc("rail", emptyMap()) + "->" + cyc("rail", mapOf("rld" to 3)) + " grav " + cyc("grav", emptyMap()) + "->" + cyc("grav", mapOf("rld" to 3)))
        android.util.Log.i("AGFUSE", "prj frost " + frost(0) + "->" + frost(2) + " range rail " + far("rail", 0) + "->" + far("rail", 3) + " pulse " + far("pulse", 3) + " stun " + stn(0) + "->" + stn(3))
        val bs = spawn("boss", P.x + 50, P.y)!!; stun(bs)
        android.util.Log.i("AGFUSE", "orbit " + orb(emptyMap()) + " -> " + orb(mapOf("prj" to 2, "rng" to 3, "rld" to 3)) + " bossStun " + bs.stn)
        android.util.Log.i("AGFUSE", "offer grav[" + offer("grav") + "] arc[" + offer("arc") + "] frost[" + offer("frost") + "] fz_core[" + offer("fz_core") + "] fz_storm[" + offer("fz_storm") + "]")
        var c = 0
        for ((k, _) in WD) {
            line(); set(mapOf("rld" to 3, "dur" to 3, "prj" to 2, "rng" to 3, "pierce" to 3, "chain" to 3, "aim" to 3)); s.ws[k] = Wp(1, 0.0)
            step(90); c++
        }
        android.util.Log.i("AGFUSE", "allmax ok " + c)
    }, 1500)
}

/** 화면 크기 점검용: 글씨와 버튼이 가장 많이 나오는 상태를 만든다 */
private fun fitRun(mode: String) {
    val x = SV.x; x.clr = 2; x.ocm = 4; x.fl = 12; x.cr = 25; x.bt = 754
    SV.hud = 1; rcb()
    when (mode) {
        "fitmenu" -> { menu(); return }
        "fittree" -> { tree(); return }
        "fitinv" -> { invest(); return }
        "fitcodex" -> { relicCodex(); return }
        "fitlab" -> { ocUI(); return }
    }
    startGame("pulse")
    S.ws["blade"] = Wp(5, .2); S.ws["rail"] = Wp(5, .2); S.ws["grav"] = Wp(5, .2)
    for (k in PD.keys.take(6)) S.ps[k] = 2
    P.mh = 1e7; P.hp = 1e7
    dh.postDelayed({
        val s = Sn ?: return@postDelayed
        when (mode) {
            "fithud" -> { deepStart(); s.fl = 2; deepFloor(); s.mut = MUT[3]; s.msg = "PAGE 2 — 오류의 세계 · 버텨라! (3분)"; s.mt = 1e5; s.cb = 12; s.ct = 1e5 }
            "fitboss" -> { s.boss = true; val b = spawn("boss", P.x + 250, P.y); s.bo = b; s.msg = "심연의 새끼들!"; s.mt = 1e5; s.cb = 12; s.ct = 1e5 }
            "fitp2" -> nextPage()
            "fitp3" -> { s.pg = 2; s.rivalId = CHR[0].id; toRival(); s.cb = 12; s.ct = 1e5 }
            "fitlv" -> lvup()
            "fitaug" -> {
                s.ws.clear(); for (k in WD.keys.filter { WD[it]!!.fz && wOk(it, "prj") && wOk(it, "rng") && wOk(it, "dur") }.take(4)) s.ws[k] = Wp(5, 99.0)
                s.ps.clear(); for ((k, d) in PD) s.ps[k] = if (AGW.containsKey(k)) 1 else d.mx
                lvup()
            }
            "fitpause" -> pauseT()
            "fitelite" -> { s.elt = 1e9; s.sc = 1e9; s.rn = 1e9; s.en.clear(); for ((i, k) in listOf("crawl", "split", "charge", "spit", "tank", "bat", "bomb").withIndex()) { val e = spawn(k, P.x - 120 + (i % 3) * 120, P.y - 220 + (i / 3) * 220) ?: continue; e.sp = 0.0; e.el = true; e.hp = 1e9; e.mh = 1e9; e.sc += 1; e.r = Math.round(e.r * 1.3).toDouble(); e.ec = 2.5; ELD[k]?.let { d -> if (d.r.isNotEmpty()) e.c = "E_$k"; e.en = d.n; e.col = d.p[1] }; e.ec = 99.0 }; val b = spawn("boss", P.x, P.y + 300); if (b != null) { b.sp = 0.0; b.cd = 99.0; b.hp = 1e9; b.mh = 1e9 } }
            "fitcap" -> { s.cp.add(Cap(P.x + 90, P.y - 60, 'h', 30.0)); s.cp.add(Cap(P.x - 100, P.y + 40, 'm', 30.0)); s.cp.add(Cap(P.x + 50, P.y + 110, 'b', 30.0)) }
            "fitend" -> { s.pg = 3; s.kills = 12345; s.na.addAll(ACH.take(5).map { it.n }); s.cr0 = 12; s.bg = true; end(true) }
        }
        P.hp = P.mh
    }, 1500)
}
