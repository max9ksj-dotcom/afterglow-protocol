package com.afterglow.protocol

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.Choreographer
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView

var host: MainActivity? = null

/** 잔광 프로토콜 앱 본체. 웹뷰 없이 Kotlin으로 직접 그린다. */
class MainActivity : Activity() {
    lateinit var ov: FrameLayout
    lateinit var bar: FlowBar
    lateinit var hudBtn: TextView
    lateinit var volBtn: TextView
    lateinit var spBtn: TextView
    private lateinit var fxBtn: TextView
    private lateinit var gv: GameView

    private fun barBtn(s: String, click: (TextView) -> Unit): TextView {
        val d = resources.displayMetrics.density
        return TextView(this).apply {
            typeface = AgFont.regular; text = s; setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11f); setTextColor(Col.parse(INK)); gravity = Gravity.CENTER
            minHeight = (34 * d).toInt(); minWidth = (34 * d).toInt(); setPadding((8 * d).toInt(), (6 * d).toInt(), (8 * d).toInt(), (6 * d).toInt())
            background = GradientDrawable().apply { setColor(Col.parse("#1a1f3a")); setStroke(d.toInt().coerceAtLeast(1), Col.parse("#2c335c")); cornerRadius = 4 * d }
            setOnClickListener { click(this) }
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply { leftMargin = (4 * d).toInt(); topMargin = (4 * d).toInt() }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        host = this
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        AgFont.load(this)
        loadData(this)
        svLoad(this)
        initSprites()
        bgmInit(this)

        val root = FrameLayout(this)
        gv = GameView(this)
        root.addView(gv, FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        ov = FrameLayout(this)
        root.addView(ov, FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        // 오버레이가 켜져 있을 때 터치가 게임으로 내려가지 않게 막는다
        ov.isClickable = true

        val d = resources.displayMetrics.density
        bar = FlowBar(this).apply { visibility = View.GONE }
        bar.addView(barBtn("⏸") { pauseT() })
        hudBtn = barBtn("능력치") { hudT() }; bar.addView(hudBtn)
        volBtn = barBtn("소리") { volT() }; bar.addView(volBtn)
        spBtn = barBtn("배속 x1") { b -> Sn?.let { it.sp = it.sp % 3 + 1; b.text = "배속 x" + it.sp } }; bar.addView(spBtn)
        fxBtn = barBtn("이펙트 표준") { b -> FX = (FX + 1) % 4; b.text = "이펙트 " + listOf("끔", "약", "표준", "강")[FX] }; bar.addView(fxBtn)
        root.addView(bar, FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.TOP or Gravity.END).apply { rightMargin = (6 * d).toInt(); topMargin = (2 * d).toInt() })
        // 카메라 구멍·노치를 피한다
        root.setOnApplyWindowInsetsListener { _, ins ->
            if (Build.VERSION.SDK_INT >= 28) {
                val c = ins.displayCutout
                val l = c?.safeInsetLeft ?: 0; val t = c?.safeInsetTop ?: 0; val r = c?.safeInsetRight ?: 0; val b = c?.safeInsetBottom ?: 0
                SAL = l / d.toDouble(); SAT = t / d.toDouble(); SAR = r / d.toDouble(); SAB = b / d.toDouble()
                ov.setPadding(l, t, r, b)
                (bar.layoutParams as FrameLayout.LayoutParams).apply { topMargin = t + (2 * d).toInt(); rightMargin = r + (6 * d).toInt() }
                fitBar(); pageFit()
            }
            ins
        }
        setContentView(root)
        hideBars()
        menu()
        if ((applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0) intent.getStringExtra("jump")?.let { debugRun(it) }
    }

    /** 왼쪽 위 정보창을 피해 버튼을 줄바꿈한다 */
    fun fitBar() {
        val d = resources.displayMetrics.density
        bar.maxW = ((W - hudBox() - 14 - SAL - SAR) * d).toInt()
        bar.requestLayout()
    }

    /** 상태 표시줄·내비게이션 바를 숨긴 전체 화면 */
    @Suppress("DEPRECATION")
    private fun hideBars() {
        if (Build.VERSION.SDK_INT >= 28) window.attributes = window.attributes.apply { layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES }
        if (Build.VERSION.SDK_INT >= 30) {
            window.insetsController?.let {
                it.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        }
    }
    override fun onWindowFocusChanged(hasFocus: Boolean) { super.onWindowFocusChanged(hasFocus); if (hasFocus) hideBars() }

    /** 뒤로 가기: 게임 중이면 일시정지, 선택 화면에서는 무시, 하위 화면은 이전 화면으로, 메인 메뉴에서만 앱 종료 */
    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        if (ov.visibility != View.VISIBLE) { pauseT(); return }
        val b = backAct
        if (b != null) b() else finish()
    }

    override fun onPause() {
        super.onPause()
        Sn?.let { if (it.run && !it.pz && !it.ended) pauseT() }
        bgmPause(); Synth.stop(); gv.stopLoop(); sv()
    }
    override fun onResume() {
        super.onResume()
        host = this
        hideBars(); Synth.start(); gv.startLoop()
        val s = Sn
        if (BGC != null && s != null && !s.pz && !s.ended) bgmSet(BGC)
    }
    override fun onDestroy() { if (host === this) host = null; super.onDestroy() }
}

/** 폭이 모자라면 다음 줄로 넘기고 오른쪽에 붙이는 버튼 묶음 */
class FlowBar(c: Context) : ViewGroup(c) {
    var maxW = 0
    private val gap = (4 * resources.displayMetrics.density).toInt()
    private fun limit(ws: Int): Int { val a = MeasureSpec.getSize(ws); return if (maxW > 0) Math.min(maxW, a) else a }
    private var aw = 0
    override fun onMeasure(ws: Int, hs: Int) {
        aw = limit(ws)
        val u = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        var x = 0; var y = gap; var rh = 0; var mw = 0
        for (i in 0 until childCount) {
            val v = getChildAt(i); v.measure(u, u)
            val w = v.measuredWidth + gap
            if (x > 0 && x + w > aw) { y += rh + gap; x = 0; rh = 0 }
            x += w; rh = Math.max(rh, v.measuredHeight); mw = Math.max(mw, x)
        }
        setMeasuredDimension(mw, y + rh)
    }
    override fun onLayout(ch: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var i = 0; var y = gap
        while (i < childCount) {
            var j = i; var x = 0; var rh = 0
            while (j < childCount) {
                val w = getChildAt(j).measuredWidth + gap
                if (x > 0 && x + w > aw) break
                x += w; rh = Math.max(rh, getChildAt(j).measuredHeight); j++
            }
            var px = (r - l) - x + gap
            for (k in i until j) { val v = getChildAt(k); v.layout(px, y, px + v.measuredWidth, y + v.measuredHeight); px += v.measuredWidth + gap }
            y += rh + gap; i = j
        }
        val den = resources.displayMetrics.density
        TBW = (r - l) / den.toDouble(); TBH = (b - t) / den.toDouble()
    }
}

/** 게임 화면. 원본 웹판처럼 화면 크기의 작은 버퍼에 그린 뒤 각지게 늘려 도트 느낌을 낸다(글씨 포함). */
class GameView(c: Context) : View(c), Choreographer.FrameCallback {
    private val den = resources.displayMetrics.density.toDouble()
    private var running = false
    private var last = 0L
    private var g: G? = null
    private var cv: Canvas? = null
    private val dst = Rect()
    private val bp = Paint().apply { isFilterBitmap = false }
    private var pm = -1
    private var pa = -1

    fun startLoop() { if (!running) { running = true; last = 0; Choreographer.getInstance().postFrameCallback(this) } }
    fun stopLoop() { running = false; Choreographer.getInstance().removeFrameCallback(this) }

    private fun setup(w: Int, h: Int) {
        if (w <= 0 || h <= 0) return
        W = w / den; H = h / den
        val b = Bitmap.createBitmap(Math.ceil(W * RS).toInt(), Math.ceil(H * RS).toInt(), Bitmap.Config.ARGB_8888)
        frame = b
        val c = Canvas(b)
        c.scale(RS.toFloat(), RS.toFloat())
        cv = c
        g = G(c)
        dst.set(0, 0, w, h)
        post { host?.fitBar(); pageFit() }
    }
    override fun onSizeChanged(w: Int, h: Int, ow: Int, oh: Int) { setup(w, h) }

    override fun doFrame(ns: Long) {
        if (!running) return
        val dt = if (last == 0L) 0.0 else Math.min(.05, (ns - last) / 1e9)
        last = ns
        try {
            tick(dt)
            val c = cv; val gg = g
            if (c != null && gg != null) {
                c.save()
                draw(gg)
                c.restoreToCount(1)
            }
        } catch (e: Exception) { android.util.Log.e("afterglow", "frame", e) }
        invalidate()
        Choreographer.getInstance().postFrameCallback(this)
    }
    override fun onDraw(c: Canvas) { frame?.let { c.drawBitmap(it, null, dst, bp) } }

    private fun stk(j: Joy, x: Double, y: Double) {
        var dx = x - j.ox; var dy = y - j.oy
        val d = hyp(dx, dy)
        if (d > SR) { dx = dx / d * SR; dy = dy / d * SR }
        j.x = dx / SR; j.y = dy / SR
    }
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(e: MotionEvent): Boolean {
        if (Sn == null) return true
        when (e.actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                val i = e.actionIndex; val id = e.getPointerId(i); val x = e.getX(i) / den; val y = e.getY(i) / den
                if (tb("aa") > 0) {
                    if (pm < 0) { joy = Joy(x, y); pm = id }
                } else {
                    val b = stickBase()
                    if (x < W * .5) { if (pm < 0) { joy = Joy(b.lx, b.ly).also { stk(it, x, y) }; pm = id } }
                    else if (pa < 0) { aimJ = Joy(b.rx, b.ry).also { stk(it, x, y) }; pa = id }
                }
            }
            MotionEvent.ACTION_MOVE -> for (i in 0 until e.pointerCount) {
                val id = e.getPointerId(i); val x = e.getX(i) / den; val y = e.getY(i) / den
                val j = joy; val a = aimJ
                if (j != null && id == pm) stk(j, x, y) else if (a != null && id == pa) stk(a, x, y)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                val id = e.getPointerId(e.actionIndex)
                if (id == pm) { joy = null; pm = -1 }
                if (id == pa) { aimJ = null; pa = -1 }
            }
            MotionEvent.ACTION_CANCEL -> { joy = null; aimJ = null; pm = -1; pa = -1 }
        }
        return true
    }
}
