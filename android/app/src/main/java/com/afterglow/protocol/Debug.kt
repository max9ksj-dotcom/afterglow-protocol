package com.afterglow.protocol

import android.os.Handler
import android.os.Looper

// 개발용 빌드에서만 쓰는 자동 시험: adb로 원하는 구간에 바로 들어가 자동으로 플레이한다. (정식 APK에서는 호출되지 않음)
private val dh = Handler(Looper.getMainLooper())
fun debugRun(mode: String) {
    if (mode == "lv") { startGame("pulse"); dh.postDelayed({ lvup() }, 2000); return }
    SV.n.add("u10"); rcb()
    startGame("pulse")
    P.mh = 1e7; P.hp = 1e7; S.sp = 3
    when (mode) {
        "boss" -> { S.t = 535.0; S.T = 900.0 }
        "glitch" -> { S.bg = true; dh.postDelayed({ startGlitch() }, 3000) }
        "p2" -> dh.postDelayed({ nextPage() }, 2000)
        "p3" -> dh.postDelayed({ S.pg = 2; startP3() }, 2000)
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
                if (mode == "boss" && b != null && !b.dead && b.k == "boss") b.hp -= b.mh * .012
                dh.postDelayed(this, 300)
            }
        }
    }
    dh.postDelayed(r, 500)
}
