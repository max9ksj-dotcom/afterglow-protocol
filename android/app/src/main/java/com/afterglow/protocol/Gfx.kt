package com.afterglow.protocol

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.text.TextPaint
import android.text.style.MetricAffectingSpan

/** 색 문자열(#abc, #aabbcc, rgba(...))을 안드로이드 색으로 바꾼다. 같은 문자열은 저장해 두고 다시 쓴다. */
object Col {
    private val cache = HashMap<String, Int>()
    fun parse(s: String): Int = cache.getOrPut(s) { try { read(s) } catch (e: Exception) { Color.WHITE } }
    private fun read(s: String): Int =
        if (s.startsWith("#")) {
            val h = s.substring(1)
            val full = if (h.length == 3) "" + h[0] + h[0] + h[1] + h[1] + h[2] + h[2] else h
            (0xFF000000.toInt()) or full.toInt(16)
        } else {
            val p = s.substring(s.indexOf('(') + 1, s.indexOf(')')).split(",").map { it.trim().toDouble() }
            val a = if (p.size > 3) p[3] else 1.0
            Color.argb((a * 255).toInt().coerceIn(0, 255), p[0].toInt(), p[1].toInt(), p[2].toInt())
        }
}

/** 원본 게임의 글꼴 Noto Sans KR. 폰 기본 글꼴과 상관없이 어느 기기에서나 같은 글씨가 나온다. */
object AgFont {
    var regular: Typeface = Typeface.SANS_SERIF
    var bold: Typeface = Typeface.DEFAULT_BOLD
    fun load(c: Context) {
        try {
            regular = Typeface.createFromAsset(c.assets, "fonts/ag_regular.ttf")
            bold = Typeface.createFromAsset(c.assets, "fonts/ag_bold.ttf")
        } catch (e: Exception) { }
    }
}
/** 글 중간의 굵은 부분에 진짜 굵은 글꼴을 쓴다 (안드로이드가 흉내 낸 굵기는 원본과 달라 보인다) */
class BoldSpan : MetricAffectingSpan() {
    override fun updateDrawState(p: TextPaint) { p.typeface = AgFont.bold }
    override fun updateMeasureState(p: TextPaint) { p.typeface = AgFont.bold }
}

/** 도트 그림 한 장. w/h는 게임 좌표 크기, k는 그림 1칸당 실제 픽셀 수 */
class Spr(val b: Bitmap, val w: Double = b.width.toDouble(), val h: Double = b.height.toDouble()) {
    val k: Double get() = b.width / w
}

fun mkS(rows: List<String>, pal: Map<Char, String>, ex: List<Triple<Int, Int, String>> = emptyList()): Spr {
    val b = Bitmap.createBitmap(rows[0].length, rows.size, Bitmap.Config.ARGB_8888)
    rows.forEachIndexed { y, r ->
        for (i in r.indices) { val c = pal[r[i]]; if (c != null && i < b.width) b.setPixel(i, y, Col.parse(c)) }
    }
    for ((i, y, c) in ex) if (i < b.width && y < b.height) b.setPixel(i, y, Col.parse(c))
    return Spr(b)
}

/** 그림의 보이는 부분만 한 색으로 덮는다 */
fun tintC(c: Spr, col: String, al: Double): Spr {
    val b = c.b.copy(Bitmap.Config.ARGB_8888, true)
    val p = Paint()
    p.color = Col.parse(col)
    p.alpha = (al * 255).toInt()
    p.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP)
    Canvas(b).drawRect(0f, 0f, b.width.toFloat(), b.height.toFloat(), p)
    return Spr(b)
}

/** 원본 게임의 캔버스 그리기 명령과 같은 모양으로 쓸 수 있게 감싼 것 */
class G(var c: Canvas) {
    var fillStyle = "#000000"
    var strokeStyle = "#000000"
    var globalAlpha = 1.0
    var lineWidth = 1.0
    var textAlign = "left"
    var lighter = false
    var roundJoin = false
    var roundCap = false
    private var fontSize = 12.0
    private var fontBold = false
    private var dash: FloatArray? = null

    private val paint = Paint().apply { isAntiAlias = true }
    private val tp = Paint().apply { isAntiAlias = true }
    private val bp = Paint().apply { isFilterBitmap = false; isAntiAlias = false }
    private val add = PorterDuffXfermode(PorterDuff.Mode.ADD)
    private val path = Path()
    private val rf = RectF()
    private val rs = Rect()

    private class St(val f: String, val s: String, val a: Double, val l: Double, val t: String, val li: Boolean, val fs: Double, val fb: Boolean)
    private val stack = ArrayList<St>()

    fun save() { stack.add(St(fillStyle, strokeStyle, globalAlpha, lineWidth, textAlign, lighter, fontSize, fontBold)); c.save() }
    fun restore() {
        c.restore()
        if (stack.isNotEmpty()) {
            val s = stack.removeAt(stack.size - 1)
            fillStyle = s.f; strokeStyle = s.s; globalAlpha = s.a; lineWidth = s.l; textAlign = s.t; lighter = s.li; fontSize = s.fs; fontBold = s.fb
        }
    }
    fun resetState() { stack.clear(); globalAlpha = 1.0; lighter = false; textAlign = "left"; dash = null; roundJoin = false; roundCap = false }

    fun translate(x: Double, y: Double) = c.translate(x.toFloat(), y.toFloat())
    fun rotate(a: Double) = c.rotate(Math.toDegrees(a).toFloat())
    fun scale(x: Double, y: Double) = c.scale(x.toFloat(), y.toFloat())
    fun font(size: Double, b: Boolean = false) { fontSize = size; fontBold = b }
    fun setLineDash(on: Double, off: Double) { dash = floatArrayOf(on.toFloat(), off.toFloat()) }
    fun clearLineDash() { dash = null }

    private fun ap(p: Paint, col: String) {
        val v = Col.parse(col)
        val a = (Color.alpha(v) * globalAlpha.coerceIn(0.0, 1.0)).toInt()
        p.color = (v and 0x00FFFFFF) or (a shl 24)
        p.xfermode = if (lighter) add else null
    }
    private fun fillP(): Paint { ap(paint, fillStyle); paint.style = Paint.Style.FILL; paint.shader = null; paint.pathEffect = null; return paint }
    private fun strokeP(): Paint {
        ap(paint, strokeStyle); paint.style = Paint.Style.STROKE; paint.shader = null
        paint.strokeWidth = lineWidth.toFloat()
        paint.strokeJoin = if (roundJoin) Paint.Join.ROUND else Paint.Join.MITER
        paint.strokeCap = if (roundCap) Paint.Cap.ROUND else Paint.Cap.BUTT
        paint.pathEffect = dash?.let { DashPathEffect(it, 0f) }
        return paint
    }

    fun fillRect(x: Double, y: Double, w: Double, h: Double) = c.drawRect(x.toFloat(), y.toFloat(), (x + w).toFloat(), (y + h).toFloat(), fillP())
    fun strokeRect(x: Double, y: Double, w: Double, h: Double) = c.drawRect(x.toFloat(), y.toFloat(), (x + w).toFloat(), (y + h).toFloat(), strokeP())
    fun fillShader(s: Shader, x: Double, y: Double, w: Double, h: Double) {
        paint.reset(); paint.shader = s; paint.style = Paint.Style.FILL
        c.drawRect(x.toFloat(), y.toFloat(), (x + w).toFloat(), (y + h).toFloat(), paint)
        paint.shader = null
    }

    fun beginPath() = path.reset()
    fun moveTo(x: Double, y: Double) = path.moveTo(x.toFloat(), y.toFloat())
    fun lineTo(x: Double, y: Double) = path.lineTo(x.toFloat(), y.toFloat())
    fun closePath() = path.close()
    fun arc(x: Double, y: Double, r: Double, a0: Double, a1: Double, ccw: Boolean = false) {
        var sw = Math.toDegrees(a1 - a0)
        if (ccw && sw > 0) sw -= 360.0
        rf.set((x - r).toFloat(), (y - r).toFloat(), (x + r).toFloat(), (y + r).toFloat())
        if (Math.abs(sw) >= 359.9) path.addOval(rf, Path.Direction.CW)
        else path.arcTo(rf, Math.toDegrees(a0).toFloat(), sw.toFloat(), false)
    }
    fun fill() = c.drawPath(path, fillP())
    fun stroke() = c.drawPath(path, strokeP())
    fun clip() { c.clipPath(path) }
    fun circleFill(x: Double, y: Double, r: Double) = c.drawCircle(x.toFloat(), y.toFloat(), r.toFloat(), fillP())
    fun circleStroke(x: Double, y: Double, r: Double) = c.drawCircle(x.toFloat(), y.toFloat(), r.toFloat(), strokeP())
    fun line(x1: Double, y1: Double, x2: Double, y2: Double) = c.drawLine(x1.toFloat(), y1.toFloat(), x2.toFloat(), y2.toFloat(), strokeP())

    private fun textP(style: Paint.Style, col: String): Paint {
        ap(tp, col); tp.style = style; tp.textSize = fontSize.toFloat(); tp.typeface = if (fontBold) AgFont.bold else AgFont.regular
        tp.textAlign = when (textAlign) { "center" -> Paint.Align.CENTER; "right" -> Paint.Align.RIGHT; else -> Paint.Align.LEFT }
        return tp
    }
    fun fillText(s: String, x: Double, y: Double) = c.drawText(s, x.toFloat(), y.toFloat(), textP(Paint.Style.FILL, fillStyle))
    fun measure(s: String): Double = textP(Paint.Style.FILL, fillStyle).measureText(s).toDouble()
    /** 글이 폭을 넘으면 글꼴 크기를 줄인다 */
    fun fit(s: String, mx: Double, size: Double, b: Boolean = false, min: Double = 9.0) {
        font(size, b)
        val w = measure(s)
        if (w > mx) font(Math.max(min, Math.floor(size * mx / w)), b)
    }
    fun strokeText(s: String, x: Double, y: Double) {
        val p = textP(Paint.Style.STROKE, strokeStyle)
        p.strokeWidth = lineWidth.toFloat(); p.strokeJoin = Paint.Join.ROUND
        c.drawText(s, x.toFloat(), y.toFloat(), p)
    }

    private fun bpSet() { bp.alpha = (globalAlpha.coerceIn(0.0, 1.0) * 255).toInt(); bp.xfermode = if (lighter) add else null }
    fun drawImage(s: Spr, x: Double, y: Double) = drawImage(s, x, y, s.w, s.h)
    fun drawImage(s: Spr, x: Double, y: Double, w: Double, h: Double) {
        bpSet(); rf.set(x.toFloat(), y.toFloat(), (x + w).toFloat(), (y + h).toFloat()); c.drawBitmap(s.b, null, rf, bp)
    }
    fun drawImage(s: Spr, sx: Double, sy: Double, sw: Double, sh: Double, x: Double, y: Double, w: Double, h: Double) {
        bpSet()
        val k = s.k
        rs.set((sx * k).toInt(), (sy * k).toInt(), ((sx + sw) * k).toInt(), ((sy + sh) * k).toInt())
        rf.set(x.toFloat(), y.toFloat(), (x + w).toFloat(), (y + h).toFloat())
        c.drawBitmap(s.b, rs, rf, bp)
    }
}
