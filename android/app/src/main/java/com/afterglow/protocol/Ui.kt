package com.afterglow.protocol

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StyleSpan
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

// 메뉴·선택 화면. 원본의 HTML 화면을 안드로이드 화면으로 옮겼다.
val RGN = listOf("일반", "희귀", "영웅", "전설")
val RGC = listOf("#b8c0e0", "#5fa8ff", "#c77dff", "#ffd24a")
val RGW = intArrayOf(50, 30, 15, 5)
const val AMB = "#ffb347"
const val DIM = "#8a92b8"
const val INK = "#e8ecff"
private val mainH = Handler(Looper.getMainLooper())

/** 뒤로 가기를 눌렀을 때 할 일. null이면 앱 종료(메인 메뉴) */
var backAct: (() -> Unit)? = null
var screen = ""

private fun ctx(): Context = host!!
private fun dp(v: Double): Int = (v * ctx().resources.displayMetrics.density).toInt()
private fun hx(c: String) = if (c.length == 4) "#" + c[1] + c[1] + c[2] + c[2] + c[3] + c[3] else c
private fun fc(c: String, t: String) = "<font color='" + hx(c) + "'>" + t + "</font>"
private fun html(s: String): CharSequence {
    val t = SpannableStringBuilder(Html.fromHtml(s, Html.FROM_HTML_MODE_LEGACY))
    for (o in t.getSpans(0, t.length, StyleSpan::class.java)) if (o.style == Typeface.BOLD) {
        val a = t.getSpanStart(o); val b = t.getSpanEnd(o)
        t.removeSpan(o); t.setSpan(BoldSpan(), a, b, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
    return t
}
private fun rTag(r: Rel) = if (r.pm) fc("#ffd24a", "영구") else fc("#8a93c8", "이번 판")

private fun tv(s: String, size: Double, col: String = INK, center: Boolean = false): TextView = TextView(ctx()).apply {
    typeface = AgFont.regular; text = html(s); setTextSize(TypedValue.COMPLEX_UNIT_DIP, size.toFloat()); setTextColor(Col.parse(col))
    if (center) gravity = Gravity.CENTER
    setLineSpacing(0f, 1.25f)
}
private fun LinearLayout.h1(s: String) {
    val m = ctx().resources.displayMetrics
    addView(tv("<b>$s</b>", Math.min(34.0, (m.widthPixels / m.density - 24.0) / s.length), AMB, true))
}
private fun LinearLayout.h2(s: String, c: String = INK) = addView(tv("<b>$s</b>", 22.0, c, true), lp(b = 10.0))
private fun LinearLayout.p(s: String, c: String = DIM, center: Boolean = true) = addView(tv(s, 13.0, c, center), lp(t = 8.0, b = 8.0))
private fun lp(t: Double = 0.0, b: Double = 0.0) = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(t); bottomMargin = dp(b) }

private fun bg(fill: String, stroke: String, w: Double = 2.0) = GradientDrawable().apply {
    setColor(Col.parse(fill)); setStroke(dp(w), Col.parse(stroke)); cornerRadius = dp(6.0).toFloat()
}
private fun btn(title: String, small: String? = null, border: String = "#2c335c", fill: String = "#1a1f3a", dis: Boolean = false, center: Boolean = false, click: (() -> Unit)?): TextView {
    val b = tv("<b>$title</b>" + (if (small != null) "<br><small>" + fc(DIM, small) + "</small>" else ""), 13.0, INK, center)
    b.background = bg(fill, border)
    b.setPadding(dp(10.0), dp(11.0), dp(10.0), dp(11.0))
    if (dis) b.alpha = .45f
    if (click != null) b.setOnClickListener { click() }
    return b
}
private fun LinearLayout.btnRow(b: View) = addView(b, lp(t = 8.0))
private fun LinearLayout.grid(items: List<View>) {
    var i = 0
    while (i < items.size) {
        val row = LinearLayout(ctx()).apply { orientation = LinearLayout.HORIZONTAL; isBaselineAligned = false; weightSum = 2f }
        for (j in 0 until 2) {
            val l = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f)
            if (j == 1) l.leftMargin = dp(8.0)
            if (i + j < items.size) row.addView(items[i + j], if (i + 1 == items.size) LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f) else l)
        }
        addView(row, lp(t = 8.0))
        i += 2
    }
}
private fun icon(k: String, size: Double): ImageView = ImageView(ctx()).apply {
    val d = BitmapDrawable(resources, ric(k).b); d.isFilterBitmap = false
    setImageDrawable(d); scaleType = ImageView.ScaleType.FIT_CENTER
    background = GradientDrawable().apply { setColor(Col.parse("#10152a")); cornerRadius = dp(6.0).toFloat() }
    layoutParams = LinearLayout.LayoutParams(dp(size), dp(size)).apply { rightMargin = dp(8.0) }
}
private fun card(border: String = "#2c335c"): LinearLayout = LinearLayout(ctx()).apply {
    orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL
    background = bg("#1a1f3a", border, 1.0); setPadding(dp(8.0), dp(8.0), dp(8.0), dp(8.0))
}
private fun page(name: String, back: (() -> Unit)?, maxW: Double = 560.0, build: LinearLayout.() -> Unit) {
    val h = host!!
    screen = name; backAct = back
    h.ov.removeAllViews(); h.ov.visibility = View.VISIBLE
    h.ov.setBackgroundColor(Col.parse(if (name == "menu") "rgba(8,10,22,.55)" else "rgba(8,10,22,.88)"))
    val box = LinearLayout(h).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(12.0), dp(12.0), dp(12.0), dp(12.0)); build() }
    val sc = ScrollView(h).apply { isFillViewport = true; overScrollMode = View.OVER_SCROLL_NEVER; isVerticalScrollBarEnabled = false }
    val wrap = FrameLayout(h)
    val w = Math.min(h.resources.displayMetrics.widthPixels, dp(maxW))
    wrap.addView(box, FrameLayout.LayoutParams(w, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER))
    pageBox = box; pageMax = maxW
    pageFit()
    sc.addView(wrap, FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
    h.ov.addView(sc, FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
}
private var pageBox: View? = null
private var pageMax = 560.0
/** 화면을 돌리거나 접었다 폈을 때 메뉴 폭을 다시 맞춘다 */
fun pageFit() {
    val b = pageBox ?: return; val h = host ?: return
    val w = Math.min(h.resources.displayMetrics.widthPixels - h.ov.paddingLeft - h.ov.paddingRight, dp(pageMax))
    if (b.layoutParams.width != w) { b.layoutParams.width = w; b.requestLayout() }
}
private fun hideOv() { val h = host!!; h.ov.visibility = View.GONE; h.ov.removeAllViews(); screen = ""; backAct = null }
private fun setBar(on: Boolean) { host!!.bar.visibility = if (on) View.VISIBLE else View.GONE }

// ===== 메인 메뉴 =====
/** 버튼 왼쪽에 무기 도트 그림을 붙인다 */
private fun withW(b: TextView, k: String, size: Double = 32.0): TextView {
    val d = BitmapDrawable(b.resources, wsp(k).b); d.isFilterBitmap = false
    val sw = wsp(k).w; val sh = wsp(k).h; val s = dp(size) / Math.max(sw, sh)
    d.setBounds(0, 0, (sw * s).toInt(), (sh * s).toInt())
    b.setCompoundDrawables(d, null, null, null); b.compoundDrawablePadding = dp(10.0); b.gravity = Gravity.CENTER_VERTICAL
    return b
}
fun menu() {
    MENU = true
    setBar(false)
    page("menu", null) {
        h1("잔광 프로토콜")
        if (SV.stG < 3) p("초보자 보호: 앞으로 " + (3 - SV.stG) + "판 동안 받는 피해 -20%, 판이 끝나면 보너스 3포인트", "#88ff88")
        p("캐릭터", INK)
        grid(CHR.map { c ->
            val u = SV.u.contains(c.id)
            btn(fc(c.pal['a']!!, "■ " + c.n), if (u) c.d else "🔒 해금: " + c.cd, if (SV.ch == c.id) "#5fd8ff" else "#2c335c", dis = !u, click = if (u) ({ selC(c.id) }) else null)
        })
        p("시작 무기를 고르세요", INK)
        grid(WD.keys.filter(::UW).map { k -> val D = WD[k]!!; withW(btn(fc(D.c, D.n), D.t) { startGame(k) }, k) })
        btnRow(btn("게임 설명") { help("menu") })
        btnRow(btn("강화", "스킬트리 · 능력치 투자 · 유물 도감 — 특성 포인트 " + SV.p + " 보유") { upg() })
        btnRow(btn("심층 연구소", if (SV.x.clr > 0) "과부하 등급 · 보스 러시 · 각성 · 도전 과제 · 기록" else "🔒 3페이지를 클리어하면 열립니다 · 도전 과제와 기록은 볼 수 있습니다") { lab() })
    }
}
fun selC(id: String) { if (SV.u.contains(id)) { SV.ch = id; sv(); menu() } }

fun help(bk: String) {
    val back: () -> Unit = if (bk == "pause") ({ pzR() }) else ({ menu() })
    page("help", back, 1000.0) {
        h2("게임 설명")
        fun H(t: String, b: String) { addView(tv("<b>$t</b>", 15.0, AMB), lp(t = 12.0, b = 4.0)); addView(tv(b, 13.0, INK), lp()) }
        H("목표", "자동 공격 생존 로그라이크 체험판입니다. 5분을 버티면 승리합니다.<br>9분에 나타나는 보스는 스킬트리에서 게임 시간을 늘려야 만날 수 있고, 보스를 쓰러뜨리면 2페이지 → 3페이지로 이어집니다.")
        H("조작", "화면 아래 왼쪽 스틱으로 이동, 오른쪽 스틱으로 조준합니다. 스킬트리(해금)에서 \"자동 조준\"을 얻으면 조준이 자동으로 바뀝니다.")
        H("성장", "경험치 구슬을 모으면 레벨업하고 강화를 하나 고릅니다. 무기 2개를 Lv5로 만들면 ★합체할 수 있습니다.<br>판이 끝나면 받는 특성 포인트로 스킬트리와 능력치 투자에서 영구 강화합니다.")
        H("유물", "엘리트 몬스터(금색 테두리)를 처치하면 유물 3개 중 하나를 고릅니다. 영구 유물 5종은 다음 판에도 남고, 나머지 10종은 한 판 동안만 유지됩니다.")
        H("몬스터", "기생충(처음부터) · 지렁이(1분) · 분열체(2분) · 돌격수(4분) · 폭탄귀(5분 30초) · 독침 사수(7분) · 갑각 거신(8분)")
        H("생존 이후", "3페이지를 클리어하면 같은 빌드로 심층(PAGE 4)에 잠수할 수 있습니다. 층마다 규칙이 바뀌고 5층마다 수호자가 나타납니다.<br>심층 연구소에서 과부하 등급, 보스 러시, 각성(잔광 결정으로 영구 강화), 도전 과제, 기록을 볼 수 있습니다.")
        H("보스", "빨간 원과 선은 곧 공격이 온다는 경고입니다. 보스 체력은 화면 위에 표시되고, 보스가 화면 밖에 있으면 가장자리 화살표가 방향을 알려 줍니다.")
        btnRow(btn("← 돌아가기", click = back))
    }
}
fun pzR() { S.pz = false; pauseT() }
fun upg() {
    setBar(false)
    page("upg", { menu() }) {
        h2("강화")
        p("보유 특성 포인트 " + SV.p, AMB)
        btnRow(btn("스킬트리", "노드를 찍어 영구 강화 · 잠긴 무기 해금 가능") { tree() })
        btnRow(btn("능력치 투자", "능력치 10종을 레벨 단위로 영구 강화 (레벨업 강화·유물과 별개)") { invest() })
        btnRow(btn("유물 도감", "엘리트 처치 보상으로 한 판 동안 얻는 특수 효과 15종") { relicCodex() })
        btnRow(btn("← 메인 메뉴") { menu() })
    }
}

// ===== 능력치 투자 =====
private var UR = false
fun invest(m: String = "") {
    page("invest", { upg() }, 1000.0) {
        h2("능력치 투자 (영구)")
        p("보유 특성 포인트 " + SV.p + " · 누적 투자 " + SV.upc + "p" + (if (m.isNotEmpty()) " · $m" else ""), AMB)
        p("여러 판에 걸쳐 유지되는 영구 강화입니다. 레벨업 때 고르는 강화(한 판)와 엘리트가 주는 유물(한 판)과는 별개입니다.")
        for (u in UPS) {
            val l = upL(u.id); val mx = l >= u.max; val c = if (mx) 0 else upCost(u.id, l); val poor = !mx && SV.p < c
            val row = card()
            row.addView(icon(u.ic, 40.0))
            row.addView(tv("<b>" + u.n + "</b> " + fc(DIM, "Lv $l/" + u.max) + "<br>" + fc(DIM, u.t + " " + upFmt(u.id, up(u.id))) +
                (if (mx) "" else fc(DIM, " → ") + "<b>" + fc("#88ff88", upFmt(u.id, u.per * (l + 1))) + "</b>") +
                "<br>" + fc(DIM, "레벨당 " + upFmt(u.id, u.per) + " · " + (if (mx) "최대 레벨" else "비용 " + c + "p")), 13.0),
                LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
            row.addView(btn(if (mx) "최대" else if (poor) "포인트 부족" else "투자 " + c + "p", dis = mx || poor, center = true) { invest(if (upBuy(u.id)) "" else "투자할 수 없습니다") },
                LinearLayout.LayoutParams(dp(96.0), ViewGroup.LayoutParams.WRAP_CONTENT).apply { leftMargin = dp(8.0) })
            addView(row, lp(t = 8.0))
        }
        btnRow(btn(if (UR) "정말 초기화? (투자 포인트 전액 환급)" else "투자 초기화") {
            if (!UR) { UR = true; invest(); mainH.postDelayed({ UR = false; if (screen == "invest") invest() }, 3000) }
            else { upReset(); UR = false; invest("초기화 완료 — 포인트가 환급되었습니다") }
        })
        btnRow(btn("← 강화로 돌아가기") { upg() })
    }
}

// ===== 유물 =====
fun relicPick() {
    val pool = ArrayList(REL.filter { rlc(it.id) < it.mx })
    val o = ArrayList<Rel>()
    while (o.size < 3 && pool.isNotEmpty()) {
        val tot = pool.sumOf { RGW[it.g] }
        var x = rnd() * tot; var i = 0
        while (i < pool.size) { x -= RGW[pool[i].g]; if (x <= 0) break; i++ }
        o.add(pool.removeAt(Math.min(i, pool.size - 1)))
    }
    if (o.isEmpty()) { P.hp = Math.min(P.mh, P.hp + P.mh * .3); return }
    S.run = false; S.rco = o
    page("relic", {}) {
        h2("유물 선택 — 엘리트 처치 보상", "#c77dff")
        addView(tv("하나를 고르세요. " + fc("#ffd24a", "영구") + " 유물은 다음 판에도 남고, " + fc("#8a93c8", "이번 판") + " 유물은 판이 끝나면 사라집니다.", 13.0, DIM, true), lp(b = 4.0))
        o.forEachIndexed { i, r ->
            val row = card(RGC[r.g])
            row.addView(icon(r.ic, 40.0))
            row.addView(tv("<b>" + fc(RGC[r.g], r.n) + "</b> " + rTag(r) + fc(DIM, " · " + RGN[r.g] + " · " + r.cat + " · " + (if (rlc(r.id) > 0) "보유 " + rlc(r.id) + "/" + r.mx else "신규")) +
                "<br>" + r.d + "<br>" + fc(DIM, "시너지: " + r.sy), 13.0), LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
            row.setOnClickListener { relicTake(i) }
            addView(row, lp(t = 8.0))
        }
    }
}
fun relicTake(i: Int) {
    val r = S.rco?.getOrNull(i) ?: return
    if (S.run) return
    S.rl[r.id] = rlc(r.id) + 1; relApply(r.id); SV.rd.add(r.id)
    if (r.pm) SV.rk[r.id] = Math.min(r.mx, (SV.rk[r.id] ?: 0) + 1)
    sv(); S.rco = null; S.run = true; hideOv(); S.msg = "유물 획득: " + r.n; S.mt = 2.5
}
private fun relList(): String {
    val ks = S.rl.entries.filter { it.value > 0 }
    if (ks.isEmpty()) return fc(DIM, "보유 유물 없음 (엘리트를 처치하면 유물 선택지가 나옵니다)")
    return ks.joinToString("<br>") { (id, n) -> val r = RM[id]!!; fc(RGC[r.g], r.n + (if (n > 1) " ×$n" else "")) + " [" + rTag(r) + "] — " + fc(INK, r.d) }
}
fun relicCodex() {
    page("codex", { upg() }, 1000.0) {
        h2("유물 도감")
        addView(tv("엘리트 몬스터(금색 테두리)를 처치하면 3개 중 하나를 고르는 유물 보상을 얻습니다. " + fc("#ffd24a", "영구") + " 유물 5종은 한 번 얻으면 다음 판에도 계속 남고(최대 중첩까지), " + fc("#8a93c8", "이번 판") + " 유물 10종은 판이 끝나면 사라집니다. ✔ 표시는 한 번이라도 획득한 유물입니다.", 13.0, DIM, true), lp(b = 4.0))
        for (r in REL) {
            val row = card(RGC[r.g])
            row.addView(icon(r.ic, 40.0))
            row.addView(tv("<b>" + fc(RGC[r.g], r.n) + "</b> " + rTag(r) + fc(DIM, (if (r.pm) " " + (SV.rk[r.id] ?: 0) + "/" + r.mx else "") + " · " + RGN[r.g] + " · " + r.cat + (if (SV.rd.contains(r.id)) " · ✔" else "")) +
                "<br>" + r.d + "<br>" + fc(DIM, "발동: " + r.tr + " · 최대 " + r.mx + "중첩 · 구현 " + r.df) + "<br>" + fc(DIM, "시너지: " + r.sy) + "<br>" + fc(DIM, "제한: " + r.lim), 13.0),
                LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
            addView(row, lp(t = 8.0))
        }
        btnRow(btn("← 강화로 돌아가기") { upg() })
    }
}

// ===== 레벨업 =====
fun lvup() {
    S.run = false
    var o: List<Opt> = opts().shuffled()
    o = (o.filter { it.ev } + o.filter { !it.ev }).take(3 + tb("ch").toInt())
    if (o.isEmpty()) o = listOf(Opt("체력 회복", "체력 +30") { P.hp = Math.min(P.mh, P.hp + 30) })
    S.cur = o
    page("lvup", {}) {
        h2("레벨 " + P.lv + " — 하나를 고르세요")
        o.forEachIndexed { i, x -> btnRow(optCard(x, i)) }
        if (S.rr > 0) btnRow(btn("다시 뽑기 (남은 횟수 " + S.rr + ")") { S.rr--; lvup() })
    }
}
/** 레벨업 카드: 왼쪽 그림(무기 도트/능력 아이콘) + 종류 표시 + 레벨 눈금. 차례로 살짝 떠오르며 나타난다 */
private fun optCard(x: Opt, i: Int): View {
    val row = LinearLayout(ctx()).apply {
        orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL
        background = android.graphics.drawable.LayerDrawable(arrayOf(bg(if (x.ev) "#2d2616" else "#1a1f3a", if (x.ev) AMB else "#2c335c"),
            GradientDrawable().apply { setColor(Col.parse(x.tc)); cornerRadii = floatArrayOf(dp(6.0).toFloat(), dp(6.0).toFloat(), 0f, 0f, 0f, 0f, dp(6.0).toFloat(), dp(6.0).toFloat()) })).apply {
            setLayerSize(1, dp(4.0), -1); setLayerGravity(1, Gravity.START or Gravity.FILL_VERTICAL)
        }
        setPadding(dp(12.0), dp(9.0), dp(10.0), dp(9.0))
        setOnClickListener { pick(i) }
    }
    val spr = if (x.wk != null) wsp(x.wk) else if (x.ic != null) ric(x.ic) else null
    if (spr != null) row.addView(ImageView(ctx()).apply {
        val d = BitmapDrawable(resources, spr.b); d.isFilterBitmap = false
        setImageDrawable(d); scaleType = ImageView.ScaleType.FIT_CENTER; setPadding(dp(4.0), dp(4.0), dp(4.0), dp(4.0))
        background = GradientDrawable().apply { setColor(Col.parse("#10152a")); cornerRadius = dp(6.0).toFloat() }
        layoutParams = LinearLayout.LayoutParams(dp(40.0), dp(40.0)).apply { rightMargin = dp(10.0) }
    })
    val pips = if (x.mx > 0) "<br>" + (0 until x.mx).joinToString("") { j -> fc(if (j < x.lv) x.tc else if (j == x.lv) "#7c86c0" else "#2c335c", "■") } else ""
    val tag = if (x.tg.isNotEmpty() && !x.a.contains(x.tg)) fc(x.tc, "[" + x.tg + "]") + " " else ""
    row.addView(tv(tag + "<b>" + x.a + "</b>" + pips + "<br><small>" + fc(DIM, x.b) + "</small>", 13.0), LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
    row.alpha = 0f; row.translationY = dp(8.0).toFloat()
    row.animate().alpha(1f).translationY(0f).setStartDelay(60L * i).setDuration(220L).start()
    return row
}
fun pick(i: Int) {
    if (i !in S.cur.indices) return
    S.cur[i].f()
    hideOv(); S.run = true
    if (S.sl > 0) { S.sl--; P.lv++; lvup() }
}

// ===== 게임 시작·끝·일시정지 =====
fun uiGameStart() { hideOv(); setBar(true); tbLab(); host!!.spBtn.text = "배속 x1" }
fun uiEnd(w: Boolean, msg: String?) {
    setBar(false)
    val t = Math.floor(S.t).toInt()
    page("end", { toMenu() }) {
        h1(if (w) (if (S.pg == 3) "생존 성공 — 버그 제거 완료" else "생존 성공") else "작동 정지")
        if (msg != null) p(msg, "#ff8888")
        if (S.pg > 1) p("PAGE " + S.pg + (if (w) " 클리어" else " 도달"), "#ff8ad8")
        val sc = LinearLayout(ctx()).apply { orientation = LinearLayout.HORIZONTAL }
        listOf(Triple("생존", "" + t / 60 + ":" + String.format(java.util.Locale.US, "%02d", t % 60), "#5fd8ff"), Triple("처치", "" + S.kills, "#ff8a8a"), Triple("레벨", "" + P.lv, "#88ff88"), Triple("포인트", "+" + S.pr0, AMB)).forEachIndexed { i, (a, v, c) ->
            val cell = LinearLayout(ctx()).apply {
                orientation = LinearLayout.VERTICAL; gravity = Gravity.CENTER
                background = android.graphics.drawable.LayerDrawable(arrayOf(bg("#1a1f3a", "#2c335c", 1.0), GradientDrawable().apply { setColor(Col.parse(c)) })).apply { setLayerSize(1, -1, dp(3.0)); setLayerGravity(1, Gravity.TOP or Gravity.FILL_HORIZONTAL) }
                setPadding(dp(4.0), dp(8.0), dp(4.0), dp(8.0))
                addView(tv(a, 11.0, DIM, true)); addView(tv("<b>$v</b>", 20.0, c, true))
                alpha = 0f; translationY = dp(8.0).toFloat(); animate().alpha(1f).translationY(0f).setStartDelay(80L * i).setDuration(300L).start()
            }
            sc.addView(cell, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f).apply { if (i > 0) leftMargin = dp(8.0) })
        }
        addView(sc, lp(t = 14.0, b = 6.0))
        p("획득 특성 포인트 +" + S.pr0 + (if (S.bonus) " (초보자 보너스 +3 포함)" else "") + " (보유 " + SV.p + ")", AMB)
        if (S.nu.isNotEmpty()) p("★ 새 캐릭터 해금: " + S.nu.joinToString(", "), "#ffd24a")
        val x = SV.x
        if (S.pg >= 4) p("심층 " + S.fl + "층 도달 (최고 " + x.fl + "층)", "#5fd8ff")
        if (S.cr0 > 0) p("잔광 결정 +" + S.cr0 + " (보유 " + x.cr + ")", "#5fd8ff")
        if (ocN() > 0) p("과부하 " + ocN() + "등급 · 포인트 ×" + String.format(java.util.Locale.US, "%.2f", ocP()), "#ff8866")
        if (S.rush) p("보스 러시", "#ffd24a")
        if (S.na.isNotEmpty()) p("★ 도전 과제 달성: " + S.na.joinToString(", "), "#ffd24a")
        p("빌드")
        S.ws.entries.chunked(2).forEach { row ->
            val r = LinearLayout(ctx()).apply { orientation = LinearLayout.HORIZONTAL }
            row.forEachIndexed { i, e -> val D = WD[e.key]!!
                r.addView(withW(tv(fc(D.c, D.n) + " <b>" + fc(D.c, "Lv" + e.value.l) + "</b>", 12.0).apply { background = bg("#1a1f3a", D.c, 1.0); setPadding(dp(6.0), dp(4.0), dp(8.0), dp(4.0)) }, e.key, 24.0),
                    LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f).apply { if (i > 0) leftMargin = dp(6.0) })
            }
            if (row.size == 1) r.addView(View(ctx()), LinearLayout.LayoutParams(0, 1, 1f).apply { leftMargin = dp(6.0) })
            addView(r, lp(t = 6.0))
        }
        if (w && S.pg == 3) btnRow(btn("심층으로 잠수 — PAGE 4", "같은 빌드로 끝없는 층에 도전합니다", AMB, "#2d2616") { deepStart() })
        btnRow(btn("같은 무기로 다시 시작") { restart() })
        btnRow(btn("메인 메뉴") { toMenu() })
    }
}
fun uiDeep() { hideOv(); setBar(true); tbLab() }
fun floorPick(pt: Int, cr: Int) {
    S.run = false
    val o = floorOpts().shuffled().take(3).map { q -> Opt(q.a, q.b) { q.f(); deepFloor() } }
    S.cur = o
    page("lvup", {}) {
        h2("심층 " + S.fl + "층 돌파")
        p("특성 포인트 +$pt · 잔광 결정 +$cr", AMB)
        p("다음 층으로 가져갈 것을 고르세요")
        o.forEachIndexed { i, x -> btnRow(optCard(x, i)) }
    }
}

// ===== 심층 연구소 =====
private fun LinearLayout.backLab() = btnRow(btn("← 연구소로 돌아가기") { lab() })
private fun nf(v: Int) = String.format(java.util.Locale.US, "%,d", v)
fun lab() {
    setBar(false)
    val x = SV.x; val lk = x.clr == 0; val n = ACH.count { x.ac.contains(it.id) }; val lt = "🔒 3페이지 클리어 시 해금"
    page("lab", { menu() }) {
        h2("심층 연구소")
        p("특성 포인트 " + SV.p + " · 잔광 결정 " + x.cr, AMB)
        btnRow(btn("과부하 등급", if (lk) lt else "현재 " + x.oc + "등급 · 최대 " + x.ocm + "등급", dis = lk, click = if (lk) null else ({ ocUI() })))
        btnRow(btn("보스 러시", if (lk) lt else "강화된 상태로 시작해 보스전만 치릅니다", dis = lk, click = if (lk) null else ({ rushUI() })))
        btnRow(btn("각성", if (lk) lt else "잔광 결정으로 영구 강화", dis = lk, click = if (lk) null else ({ awkUI() })))
        btnRow(btn("도전 과제", n.toString() + " / " + ACH.size + " 달성") { achUI() })
        btnRow(btn("기록실", "최고 기록과 복구 기록") { recUI() })
        btnRow(btn("← 메인 메뉴") { menu() })
    }
}
fun ocSet(v: Int) { if (v <= SV.x.ocm) { SV.x.oc = v; sv(); ocUI() } }
fun ocUI() {
    val x = SV.x
    page("oc", { lab() }) {
        h2("과부하 등급")
        p("등급마다 적 체력 +35%, 적 속도 +3%, 받는 피해 +12%, 획득 포인트 +25%가 쌓입니다. 3페이지를 클리어하면 다음 등급이 열립니다.")
        grid((0..OCN).map { i ->
            val lk = i > x.ocm
            btn(if (i > 0) i.toString() + "등급" else "끔", if (lk) "🔒 이전 등급 클리어" else if (i > 0) "포인트 ×" + String.format(java.util.Locale.US, "%.2f", 1 + .25 * i) else "기본 난이도",
                if (x.oc == i) "#5fd8ff" else "#2c335c", dis = lk, click = if (lk) null else ({ ocSet(i) }))
        })
        backLab()
    }
}
fun rushUI() {
    page("rush", { lab() }) {
        h2("보스 러시")
        p("고른 무기와 다른 무기 2개를 Lv5로 들고 레벨 25로 시작합니다. 수호자 → 2페이지 → 3페이지를 이어서 치르고, 클리어하면 특성 포인트 15를 더 받습니다.")
        grid(WD.keys.filter { UW(it) && !WD[it]!!.fz }.map { k -> val D = WD[k]!!; btn(fc(D.c, D.n), D.t) { rushStart(k) } })
        backLab()
    }
}
fun awkBuy(id: String) {
    val a = AWK.find { it.id == id } ?: return
    val x = SV.x; val l = x.aw[id] ?: 0
    if (l >= a.max || x.cr < awkCost(a, l)) return
    x.cr -= awkCost(a, l); x.aw[id] = l + 1; rcb(); sv(); awkUI()
}
fun awkUI() {
    val x = SV.x
    page("awk", { lab() }, 1000.0) {
        h2("각성 (영구)")
        p("잔광 결정 " + x.cr, "#5fd8ff")
        p("심층 층을 돌파하면 잔광 결정을 얻습니다. 수호자 층은 3개, 보물 층은 2배입니다.")
        for (a in AWK) {
            val l = x.aw[a.id] ?: 0; val mx = l >= a.max; val c = awkCost(a, l); val po = !mx && x.cr < c
            btnRow(btn(a.n + " Lv " + l + "/" + a.max, a.t + " " + awkFmt(a, a.per * l) + " · " + (if (mx) "최대 레벨" else "비용 결정 " + c + "개"), dis = mx || po, click = if (mx || po) null else ({ awkBuy(a.id) })))
        }
        backLab()
    }
}
fun achUI() {
    val x = SV.x
    page("ach", { lab() }, 1000.0) {
        h2("도전 과제")
        for (a in ACH) {
            val d = x.ac.contains(a.id)
            btnRow(btn((if (d) "✔ " else "") + a.n, a.d + " · 보상 " + a.r + "p" + (if (d) "" else " · 진행 " + nf(Math.min(a.v, xv(a.k))) + "/" + nf(a.v)), if (d) "#5fd8ff" else "#2c335c", click = null))
        }
        backLab()
    }
}
fun recUI() {
    val x = SV.x
    page("rec", { lab() }, 1000.0) {
        h2("기록실")
        p("3페이지 클리어 " + x.clr + "회 · 가장 빠른 클리어 " + (if (x.bt > 0) "" + x.bt / 60 + "분 " + x.bt % 60 + "초" else "없음"))
        p("심층 최고 " + x.fl + "층 · 과부하 최고 " + x.occ + "등급 · 보스 러시 " + x.br + "회")
        p("누적 처치 " + nf(x.tk) + " · 한 판 최다 처치 " + nf(SV.stMk) + " · 무기 합체 " + SV.stF + "회")
        for (l in LOG) btnRow(if (x.fl >= l.f) btn(l.n, l.t, click = null) else btn("🔒 " + l.n, "심층 " + l.f + "층에 도달하면 복구됩니다", dis = true, click = null))
        backLab()
    }
}
fun bgmPg() = if ((Sn?.pg ?: 1) >= 2) "boss" else "game"
fun pauseT() {
    val s = Sn ?: return
    if (s.ended || !s.run || s.gl > 0 || s.tf > 0) return
    if (s.pz) { s.pz = false; hideOv(); bgmSet(bgmPg()); return }
    s.pz = true; bgmPause()
    page("pause", { pauseT() }) {
        h2("일시정지")
        addView(tv(relList(), 13.0, DIM), lp(b = 4.0))
        btnRow(btn("재개") { pauseT() })
        btnRow(btn("게임 설명") { help("pause") })
        btnRow(btn("재시작") { restart() })
        btnRow(btn("메인 메뉴") { toMenu() })
    }
}
fun restart() {
    val s = Sn
    val w = s?.w0
    if (s != null) { s.pz = false; s.ended = true }
    if (w != null && UW(w)) { if (s.rush) rushStart(w) else startGame(w) } else toMenu()
}
fun toMenu() {
    Sn?.let { it.pz = false; it.ended = true; it.run = false }
    bgmSet(null); menu()
}
fun hudT() { SV.hud = (SV.hud + 1) % 3; sv(); tbLab() }
fun volT() {
    SV.vol = (SV.vol + 1) % 3; sv(); tbLab()
    val s = Sn
    if (BGC != null && s != null && s.run && !s.pz) bgmSet(BGC)
    snd(440.0, .12, "sine", .05)
}
fun tbLab() {
    val h = host ?: return
    h.hudBtn.text = "능력치 " + listOf("간단", "자세히", "최소")[SV.hud]
    h.volBtn.text = listOf("소리 끔", "소리 낮음", "소리 보통")[SV.vol]
    h.fitBar()
}

// ===== 스킬트리 =====
val REGA = mapOf("a" to 270.0, "c" to 330.0, "s" to 30.0, "g" to 90.0, "t" to 150.0, "u" to 210.0)
val RCOL = mapOf("a" to "#e55", "c" to "#fa3", "s" to "#4c6", "g" to "#48f", "t" to "#ec4", "u" to "#a6f")
val RNM = mapOf("a" to "공격", "c" to "치명", "s" to "생존", "g" to "성장", "t" to "시간", "u" to "해금")
private val TRD = mapOf('n' to 13.0, 'e' to 18.0, 's' to 20.0, 'k' to 28.0, 'b' to 20.0, 'c' to 32.0)
private val TSZ = mapOf('n' to 20.0, 'e' to 30.0, 's' to 30.0, 'b' to 30.0, 'k' to 40.0, 'c' to 40.0)
private val TN = mapOf('n' to "일반 노드", 'e' to "강화 노드", 's' to "특수 노드", 'k' to "핵심 노드", 'b' to "분기 노드", 'c' to "시작 노드")
private var SEL: String? = null
private var CF: String? = null
private var RF = false
private var skView: TreeView? = null
private var skPanel: LinearLayout? = null
private var skPts: TextView? = null
private var skReset: TextView? = null

private fun isL(id: String) = id == "core" || SV.n.contains(id)
private fun chk(n: Nd): Char {
    if (isL(n.id)) return 'L'
    if (n.ex.any(::isL)) return 'X'
    if (n.rq.any { !isL(it) }) return 'R'
    if (!NB[n.id]!!.any(::isL)) return 'A'
    if (SV.p < n.c) return 'P'
    return 'O'
}
private fun nodeCol(n: Nd) = if (n.t == 'c') "#ffd24a" else RCOL[n.rg]!!
private fun iconOf(n: Nd): String { val k = n.e.firstOrNull()?.first; return if (n.t == 'c') "st" else if (k != null && IC.containsKey(k)) k else "dm" }

private fun skRender() {
    skPts?.text = "보유 포인트 " + SV.p
    skReset?.text = html("<b>" + (if (RF) "정말 초기화?" else "초기화") + "</b>")
    skView?.invalidate()
    val pn = skPanel ?: return
    pn.removeAllViews()
    val n = SEL?.let { NM[it] }
    if (n == null) {
        pn.addView(tv((if (SV.n.isEmpty()) "<b>" + fc("#ffd24a", "처음이라면:") + "</b> 가운데 ★와 이어진 노드부터 습득하세요. " else "") + "노드를 눌러 조건 확인 · 드래그 이동 · 핀치 확대 · 미니맵 터치 이동", 12.0, DIM))
        pn.addView(tv("원 일반 · 이중원 강화 · ◆ 특수 · 점선 큰 원 핵심(선행 노드 필요) · 점선 ◆ 분기(둘 중 하나만)", 12.0, DIM), lp(t = 4.0))
        return
    }
    val c = chk(n)
    fun L(ok: Boolean, t: String) = fc(if (ok) "#88ff88" else "#ff8888", (if (ok) "✔ " else "✖ ") + t)
    val row = LinearLayout(ctx()).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
    row.addView(icon(iconOf(n), 30.0))
    row.addView(tv("<b>" + fc(nodeCol(n), n.nm) + "</b> " + fc(DIM, TN[n.t]!! + (if (n.t == 'c') "" else " · " + RNM[n.rg])), 13.0), LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
    pn.addView(row)
    val lines = ArrayList<String>()
    if (c == 'L') lines.add(fc("#3fd8c8", "✔ 습득 완료"))
    else {
        lines.add(L(NB[n.id]!!.any(::isL), "연결된 노드 중 하나를 먼저 습득"))
        if (n.rq.isNotEmpty()) lines.add(L(n.rq.all(::isL), "필수 노드(노란 점선): " + n.rq.joinToString(", ") { NM[it]!!.nm + (if (isL(it)) " ✔" else " ✖") }))
        if (n.ex.isNotEmpty()) lines.add(L(!n.ex.any(::isL), "배타 노드(빨간 점선, 동시 습득 불가): " + n.ex.joinToString(", ") { NM[it]!!.nm }))
        lines.add(L(SV.p >= n.c, "포인트 " + SV.p + " / 필요 " + n.c))
    }
    pn.addView(tv(lines.joinToString("<br>"), 13.0), lp(t = 4.0))
    if (c == 'O') pn.addView(btn(if (CF == n.id) "확인 — " + n.c + "p 사용" else "습득하기 (" + n.c + "p)") { learn() }, lp(t = 6.0))
}
private fun learn() {
    val n = SEL?.let { NM[it] } ?: return
    if (chk(n) != 'O') return
    if (CF != n.id) { CF = n.id; skRender(); return }
    SV.p -= n.c; SV.n.add(n.id); CF = null; rcb(); sv(); skRender()
}
private fun skRS() {
    if (!RF) { RF = true; skRender(); mainH.postDelayed({ RF = false; if (screen == "tree") skRender() }, 3000); return }
    var t = 0
    for (n in NL) if (SV.n.contains(n.id)) t += n.c
    SV.p += t; SV.n.clear(); rcb(); sv(); RF = false; SEL = null; CF = null; skRender()
}

class TreeView(c: Context) : View(c) {
    private val den = resources.displayMetrics.density.toDouble()
    private var vx = 0.0; private var vy = 0.0; private var vs = 1.0
    private val mm = 112.0
    private val ptr = HashMap<Int, DoubleArray>()
    private var moved = 0.0; private var pd = 0.0; private var inMini = false
    private var g: G? = null
    private val vw get() = width / den
    private val vh get() = height / den

    fun center() { vs = Math.max(.35, Math.min(1.1, Math.min(vw, vh) / 700)); vx = vw / 2; vy = vh / 2; invalidate() }
    fun fit() { vs = Math.max(.08, Math.min(vw, vh) / 3500); vx = vw / 2; vy = vh / 2; invalidate() }
    override fun onSizeChanged(w: Int, h: Int, ow: Int, oh: Int) { if (ow == 0) center() }

    private fun zoomAt(x: Double, y: Double, f: Double) {
        val s = Math.max(.07, Math.min(2.4, vs * f)); val k = s / vs
        vx = x - (x - vx) * k; vy = y - (y - vy) * k; vs = s; invalidate()
    }
    private fun tapAt(cx: Double, cy: Double) {
        val wx = (cx - vx) / vs; val wy = (cy - vy) / vs
        var b: Nd? = null; var bd = 1e9
        for (n in NL) { val d = hyp(n.x - wx, n.y - wy); val lim = Math.max(TRD[n.t]!! + 8, 24 / vs); if (d < lim && d < bd) { bd = d; b = n } }
        SEL = b?.id; CF = null; skRender()
    }
    private fun miniAt(x: Double, y: Double) {
        val wx = (x - (vw - mm - 8)) / mm * 3400 - 1700; val wy = (y - 8) / mm * 3400 - 1700
        vx = vw / 2 - wx * vs; vy = vh / 2 - wy * vs; invalidate()
    }
    override fun onTouchEvent(e: MotionEvent): Boolean {
        val i = e.actionIndex; val id = e.getPointerId(i)
        val x = e.getX(i) / den; val y = e.getY(i) / den
        when (e.actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                inMini = ptr.isEmpty() && x > vw - mm - 8 && y < mm + 8
                ptr[id] = doubleArrayOf(x, y); moved = 0.0
                if (ptr.size == 2) { val a = ptr.values.toList(); pd = hyp(a[0][0] - a[1][0], a[0][1] - a[1][1]) }
            }
            MotionEvent.ACTION_MOVE -> {
                if (ptr.size == 1 && !inMini) {
                    val pid = e.getPointerId(0); val o = ptr[pid]
                    if (o != null) {
                        val nx = e.getX(0) / den; val ny = e.getY(0) / den
                        vx += nx - o[0]; vy += ny - o[1]; moved += Math.abs(nx - o[0]) + Math.abs(ny - o[1]); ptr[pid] = doubleArrayOf(nx, ny); invalidate()
                    }
                } else if (ptr.size >= 2) {
                    for (q in 0 until e.pointerCount) ptr[e.getPointerId(q)] = doubleArrayOf(e.getX(q) / den, e.getY(q) / den)
                    val a = ptr.values.toList(); val d = hyp(a[0][0] - a[1][0], a[0][1] - a[1][1])
                    if (pd > 0) zoomAt((a[0][0] + a[1][0]) / 2, (a[0][1] + a[1][1]) / 2, d / pd)
                    pd = d; moved += 10
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                if (ptr.size == 1 && moved < 8 && e.actionMasked == MotionEvent.ACTION_UP) { if (inMini) miniAt(x, y) else tapAt(x, y) }
                ptr.remove(id); pd = 0.0
            }
            MotionEvent.ACTION_CANCEL -> { ptr.clear(); pd = 0.0 }
        }
        return true
    }
    override fun onDraw(c: Canvas) {
        c.drawColor(Col.parse("#0a0d1c"))
        val g0 = g ?: G(c).also { g = it }
        g0.c = c; g0.resetState()
        c.save(); c.scale(den.toFloat(), den.toFloat())
        c.save(); c.translate(vx.toFloat(), vy.toFloat()); c.scale(vs.toFloat(), vs.toFloat())
        for (r in doubleArrayOf(180.0, 360.0, 540.0, 760.0, 1000.0, 1250.0, 1500.0)) { g0.strokeStyle = "#12183a"; g0.lineWidth = 2.0; g0.circleStroke(0.0, 0.0, r) }
        for ((k, deg) in REGA) {
            val a = deg * Math.PI / 180
            g0.strokeStyle = "#0f1530"; g0.lineWidth = 2.0; g0.line(0.0, 0.0, cos(a) * 1620, sin(a) * 1620)
            g0.globalAlpha = .5; g0.font(46.0, true); g0.textAlign = "center"; g0.fillStyle = RCOL[k]!!; g0.fillText(RNM[k]!!, cos(a) * 1710, sin(a) * 1710 + 16); g0.globalAlpha = 1.0
        }
        for ((p, q) in LK) {
            val a = NM[p]!!; val b = NM[q]!!; val la = isL(p); val lb = isL(q); val on = la && lb; val ad = la || lb
            g0.strokeStyle = if (on) "#3fd8c8" else if (ad) "#6a7399" else "#262c4a"; g0.lineWidth = if (on) 5.0 else if (ad) 3.0 else 2.5; g0.line(a.x, a.y, b.x, b.y)
        }
        for (n in NL) {
            val st = chk(n); val r = TRD[n.t]!!; val col = nodeCol(n); val ln0 = st == 'L'; val av = st == 'O' || st == 'P'; val sel = n.id == SEL
            val x = n.x; val y = n.y; val fill = if (ln0) col else "#10152a"; val stk = if (sel || ln0) "#fff" else if (av) col else "#3a4166"
            val sw = if (sel) 5.0 else if (n.t == 'k' || n.t == 'c') 5.0 else 3.0
            g0.globalAlpha = if (ln0 || av) 1.0 else .5
            val ga = g0.globalAlpha
            if (ln0) { g0.fillStyle = col; g0.globalAlpha = .22; g0.circleFill(x, y, r + 8); g0.globalAlpha = ga }
            if (n.t == 's' || n.t == 'b') {
                fun dia(q: Double) { g0.beginPath(); g0.moveTo(x, y - q); g0.lineTo(x + q, y); g0.lineTo(x, y + q); g0.lineTo(x - q, y); g0.closePath() }
                dia(r * 1.13); g0.fillStyle = fill; g0.fill(); g0.strokeStyle = stk; g0.lineWidth = sw
                if (n.t == 'b') g0.setLineDash(5.0, 3.0)
                g0.stroke(); g0.clearLineDash()
                if (ln0) { dia(r * .78); g0.fillStyle = "#0d1226"; g0.fill() }
            } else {
                g0.fillStyle = fill; g0.circleFill(x, y, r); g0.strokeStyle = stk; g0.lineWidth = sw; g0.circleStroke(x, y, r)
                if (ln0) { g0.fillStyle = "#0d1226"; g0.circleFill(x, y, r - 5) }
                if (n.t == 'e' || n.t == 'k') { g0.strokeStyle = if (ln0) col else stk; g0.lineWidth = 2.0; g0.circleStroke(x, y, r - 6) }
                if (n.t == 'k') { g0.strokeStyle = if (ln0) col else "#4a5280"; g0.lineWidth = 2.0; g0.setLineDash(3.0, 4.0); g0.circleStroke(x, y, r + 6); g0.clearLineDash() }
            }
            val ik = if (st == 'R' || st == 'X') "lk" else iconOf(n); val sz = TSZ[n.t]!!
            g0.drawImage(ric(ik), x - sz / 2, y - sz / 2, sz, sz)
            if (!ln0 && n.t != 'c') { g0.font(12.0); g0.textAlign = "center"; g0.fillStyle = if (st == 'P') "#f66" else "#aab2e0"; g0.fillText(n.c.toString() + "p", x, y + r + 14) }
            g0.globalAlpha = 1.0
        }
        SEL?.let { NM[it] }?.let { n ->
            g0.lineWidth = 3.0; g0.setLineDash(6.0, 4.0)
            g0.strokeStyle = "#ffd24a"; for (r in n.rq) { val q = NM[r]!!; g0.circleStroke(q.x, q.y, TRD[q.t]!! + 10) }
            g0.strokeStyle = "#ff5050"; for (r in n.ex) { val q = NM[r]!!; g0.circleStroke(q.x, q.y, TRD[q.t]!! + 10) }
            g0.clearLineDash()
        }
        c.restore()
        // 미니맵
        val mx = vw - mm - 8; val my = 8.0; val k = mm / 3400
        g0.fillStyle = "rgba(10,13,28,.82)"; g0.fillRect(mx, my, mm, mm); g0.strokeStyle = "#2c335c"; g0.lineWidth = 1.0; g0.strokeRect(mx, my, mm, mm)
        for (n in NL) {
            g0.fillStyle = if (isL(n.id)) nodeCol(n) else "#2c335c"
            g0.circleFill(mx + (n.x + 1700) * k, my + (n.y + 1700) * k, (if (n.t == 'k') 40.0 else if (n.t == 'c') 50.0 else 22.0) * k)
        }
        g0.strokeStyle = "#ffffff"; g0.lineWidth = 1.2
        g0.strokeRect(mx + (-vx / vs + 1700) * k, my + (-vy / vs + 1700) * k, vw / vs * k, vh / vs * k)
        c.restore()
    }
}
private class MaxScroll(c: Context, val maxH: Int) : ScrollView(c) {
    override fun onMeasure(w: Int, h: Int) = super.onMeasure(w, MeasureSpec.makeMeasureSpec(maxH, MeasureSpec.AT_MOST))
}
fun tree() {
    val h = host!!
    screen = "tree"; backAct = { upg() }
    h.ov.removeAllViews(); h.ov.visibility = View.VISIBLE; h.ov.setBackgroundColor(Col.parse("#0a0d1c"))
    val root = LinearLayout(h).apply { orientation = LinearLayout.VERTICAL }
    val head = LinearLayout(h).apply { orientation = LinearLayout.VERTICAL; setBackgroundColor(Col.parse("#10152a")); setPadding(dp(8.0), dp(6.0), dp(8.0), dp(6.0)) }
    val t1 = LinearLayout(h).apply { orientation = LinearLayout.HORIZONTAL }
    t1.addView(tv("<b>스킬 네트워크</b>", 14.0, AMB), LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
    skPts = tv("", 13.0, "#ffd24a"); t1.addView(skPts)
    head.addView(t1)
    val tv0 = TreeView(h)
    skView = tv0
    val t2 = LinearLayout(h).apply { orientation = LinearLayout.HORIZONTAL }
    fun sb(s: String, f: () -> Unit): TextView {
        val b = btn(s, center = true, click = f); b.setPadding(dp(4.0), dp(8.0), dp(4.0), dp(8.0))
        t2.addView(b, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f).apply { leftMargin = dp(3.0); rightMargin = dp(3.0); topMargin = dp(4.0) })
        return b
    }
    sb("◎ 중앙") { tv0.center() }; sb("⤢ 전체") { tv0.fit() }
    skReset = sb("초기화") { skRS() }
    sb("← 강화") { upg() }
    head.addView(t2)
    root.addView(head)
    root.addView(tv0, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f))
    val pn = LinearLayout(h).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(10.0), dp(10.0), dp(10.0), dp(10.0)) }
    skPanel = pn
    val sc = MaxScroll(h, (h.resources.displayMetrics.heightPixels * .38).toInt()).apply { setBackgroundColor(Col.parse("#10152a")); addView(pn) }
    root.addView(sc, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
    h.ov.addView(root, FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
    SEL = null; CF = null; RF = false
    skRender()
}
