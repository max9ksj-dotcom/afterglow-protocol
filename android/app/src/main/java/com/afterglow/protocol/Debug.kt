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
            "fitpause" -> pauseT()
            "fitend" -> { s.pg = 3; s.kills = 12345; s.na.addAll(ACH.take(5).map { it.n }); s.cr0 = 12; s.bg = true; end(true) }
        }
        P.hp = P.mh
    }, 1500)
}
