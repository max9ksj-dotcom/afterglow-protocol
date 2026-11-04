package com.afterglow.protocol

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

// 무기·스킬트리·유물·도트 그림 데이터. tools/export_game_data.js가 게임 원본에서 뽑은 assets/gamedata.json을 읽는다.
class Wd(val id: String, val n: String, val d: Double, val i: Double, val r: Double, val c: String, val t: String,
         val fz: Boolean, val req: List<String>, val gd: String?, val rd: String?)
class Nd(val id: String, val t: Char, val x: Double, val y: Double, val c: Int, val nm: String,
         val e: List<Pair<String, Any>>, val rq: List<String>, val ex: List<String>, val rg: String?)
class Up(val id: String, val n: String, val ic: String, val max: Int, val per: Double, val b: Double, val g: Double, val u: String, val t: String)
class Rel(val id: String, val pm: Boolean, val n: String, val g: Int, val cat: String, val ic: String, val mx: Int,
          val d: String, val tr: String, val sy: String, val lim: String, val df: String)
class Pd(val id: String, val n: String, val mx: Int, val t: String)
class Chr(val id: String, val n: String, val d: String, val m: Map<String, Double>, val pal: Map<Char, String>,
          val ex: List<Triple<Int, Int, String>>, val cd: String)
class Wpx(val x: Int, val y: Double, val c: String, val fc: String)

val WD = LinkedHashMap<String, Wd>()
val NL = ArrayList<Nd>()
val NM = HashMap<String, Nd>()
val NB = HashMap<String, HashSet<String>>()
val LK = ArrayList<Pair<String, String>>()
val UPS = ArrayList<Up>()
val UM = HashMap<String, Up>()
val REL = ArrayList<Rel>()
val RM = HashMap<String, Rel>()
val PD = LinkedHashMap<String, Pd>()
val CHR = ArrayList<Chr>()
val IC = HashMap<String, List<String>>()
val ICP = HashMap<Char, String>()
val WP = ArrayList<Wpx>()
var dataTop: List<String> = emptyList()
var dataLg: List<List<String>> = emptyList()
var dataSpr: Map<String, List<String>> = emptyMap()
var dataGems: Map<String, List<String>> = emptyMap()
val WSH = HashMap<String, List<String>>()
val WV = HashMap<String, Pair<String, Map<Char, String>>>()

private fun strs(a: JSONArray?): List<String> = if (a == null) emptyList() else List(a.length()) { a.getString(it) }
private fun pal(o: JSONObject): Map<Char, String> { val m = HashMap<Char, String>(); for (k in o.keys()) m[k[0]] = o.getString(k); return m }
private fun rowsMap(o: JSONObject): Map<String, List<String>> { val m = HashMap<String, List<String>>(); for (k in o.keys()) m[k] = strs(o.getJSONArray(k)); return m }

fun loadData(ctx: Context) {
    if (WD.isNotEmpty()) return
    val j = JSONObject(ctx.assets.open("gamedata.json").bufferedReader(Charsets.UTF_8).use { it.readText() })
    j.getJSONArray("WD").let { a ->
        for (i in 0 until a.length()) {
            val o = a.getJSONObject(i)
            WD[o.getString("id")] = Wd(o.getString("id"), o.getString("n"), o.getDouble("d"), o.getDouble("i"), o.getDouble("r"), o.getString("c"), o.getString("t"),
                o.optInt("fz", 0) != 0, strs(o.optJSONArray("req")), o.optString("gd", "").ifEmpty { null }, o.optString("rd", "").ifEmpty { null })
        }
    }
    j.getJSONArray("NL").let { a ->
        for (i in 0 until a.length()) {
            val o = a.getJSONObject(i)
            val ea = o.getJSONArray("e")
            val e = List(ea.length()) { q -> val p = ea.getJSONArray(q); Pair(p.getString(0), p.get(1)) }
            val n = Nd(o.getString("id"), o.getString("t")[0], o.getDouble("x"), o.getDouble("y"), o.getInt("c"), o.getString("nm"), e,
                strs(o.getJSONArray("rq")), strs(o.getJSONArray("ex")), if (o.isNull("rg")) null else o.getString("rg"))
            NL.add(n); NM[n.id] = n; NB[n.id] = HashSet()
        }
    }
    j.getJSONArray("LK").let { a ->
        for (i in 0 until a.length()) {
            val p = a.getJSONArray(i); val x = p.getString(0); val y = p.getString(1)
            LK.add(Pair(x, y)); NB[x]!!.add(y); NB[y]!!.add(x)
        }
    }
    j.getJSONArray("UPS").let { a ->
        for (i in 0 until a.length()) {
            val o = a.getJSONObject(i)
            val u = Up(o.getString("id"), o.getString("n"), o.getString("ic"), o.getInt("max"), o.getDouble("per"), o.getDouble("b"), o.getDouble("g"), o.getString("u"), o.getString("t"))
            UPS.add(u); UM[u.id] = u
        }
    }
    j.getJSONArray("REL").let { a ->
        for (i in 0 until a.length()) {
            val o = a.getJSONObject(i)
            val r = Rel(o.getString("id"), o.optInt("pm", 0) != 0, o.getString("n"), o.getInt("g"), o.getString("cat"), o.getString("ic"), o.getInt("mx"),
                o.getString("d"), o.getString("tr"), o.getString("sy"), o.getString("lim"), o.getString("df"))
            REL.add(r); RM[r.id] = r
        }
    }
    j.getJSONArray("PD").let { a ->
        for (i in 0 until a.length()) { val p = a.getJSONArray(i); PD[p.getString(0)] = Pd(p.getString(0), p.getString(1), p.getInt(2), p.getString(3)) }
    }
    j.getJSONArray("CHR").let { a ->
        for (i in 0 until a.length()) {
            val o = a.getJSONObject(i)
            val mo = o.getJSONObject("m"); val m = HashMap<String, Double>(); for (k in mo.keys()) m[k] = mo.getDouble(k)
            val ea = o.getJSONArray("ex")
            val ex = List(ea.length()) { q -> val p = ea.getJSONArray(q); Triple(p.getInt(0), p.getInt(1), p.getString(2)) }
            CHR.add(Chr(o.getString("id"), o.getString("n"), o.getString("d"), m, pal(o.getJSONObject("pal")), ex, o.getString("cd")))
        }
    }
    IC.putAll(rowsMap(j.getJSONObject("IC")))
    ICP.putAll(pal(j.getJSONObject("ICP")))
    dataTop = strs(j.getJSONArray("TOP"))
    j.getJSONArray("LG").let { a -> dataLg = List(a.length()) { strs(a.getJSONArray(it)) } }
    dataSpr = rowsMap(j.getJSONObject("SPR"))
    dataGems = rowsMap(j.getJSONObject("GEMS"))
    WSH.putAll(rowsMap(j.getJSONObject("WSH")))
    j.getJSONObject("WV").let { o ->
        for (k in o.keys()) { val p = o.getJSONArray(k); WV[k] = Pair(p.getString(0), pal(p.getJSONObject(1))) }
    }
    j.getJSONArray("WP").let { a ->
        for (i in 0 until a.length()) { val p = a.getJSONArray(i); WP.add(Wpx(p.getInt(0), p.getDouble(1), p.getString(2), p.getString(3))) }
    }
}

// ===== 저장 데이터 (원본의 ap_save2와 같은 구조) =====
class Save {
    var p = 6
    val n = HashSet<String>()
    val u = hashSetOf("kai")
    var stW = 0; var stMk = 0; var stB = 0; var stF = 0; var stG = 0
    var ch = "kai"
    var vol = 2
    var hud = 0
    val up = HashMap<String, Int>()
    var upc = 0
    val rd = HashSet<String>()
    val rk = HashMap<String, Int>()
}
var SV = Save()
private const val KEY = "ap_save2"

fun svLoad(ctx: Context) {
    val s = Save()
    try {
        val t = ctx.getSharedPreferences("afterglow", Context.MODE_PRIVATE).getString(KEY, null)
        if (t != null) {
            val o = JSONObject(t)
            s.p = Math.max(0, o.optInt("p", 6))
            o.optJSONObject("n")?.let { for (k in it.keys()) if (NM.containsKey(k) && k != "core") s.n.add(k) }
            o.optJSONObject("u")?.let { for (k in it.keys()) if (CHR.any { c -> c.id == k }) s.u.add(k) }
            o.optJSONObject("st")?.let {
                s.stW = it.optInt("w"); s.stMk = it.optInt("mk"); s.stB = it.optInt("b"); s.stF = it.optInt("f"); s.stG = it.optInt("g")
            }
            s.ch = o.optString("ch", "kai").let { if (s.u.contains(it)) it else "kai" }
            s.vol = o.optInt("vol", 2).coerceIn(0, 2)
            s.hud = o.optInt("hud", 0).coerceIn(0, 2)
            o.optJSONObject("up")?.let { for (u in UPS) { val l = it.optInt(u.id, 0).coerceIn(0, u.max); if (l > 0) s.up[u.id] = l } }
            o.optJSONObject("rd")?.let { for (k in it.keys()) if (RM.containsKey(k)) s.rd.add(k) }
            o.optJSONObject("rk")?.let { for (r in REL) if (r.pm) { val v = it.optInt(r.id, 0).coerceIn(0, r.mx); if (v > 0) s.rk[r.id] = v } }
        }
    } catch (e: Exception) { }
    SV = s
    SV.upc = SV.up.entries.sumOf { (id, l) -> (0 until l).sumOf { upCost(id, it) } }
    rcb()
}

fun sv() {
    try {
        val o = JSONObject()
        o.put("p", SV.p)
        o.put("n", JSONObject().also { j -> SV.n.forEach { j.put(it, 1) } })
        o.put("u", JSONObject().also { j -> SV.u.forEach { j.put(it, 1) } })
        o.put("st", JSONObject().put("w", SV.stW).put("mk", SV.stMk).put("b", SV.stB).put("f", SV.stF).put("g", SV.stG))
        o.put("ch", SV.ch); o.put("vol", SV.vol); o.put("hud", SV.hud)
        o.put("up", JSONObject().also { j -> SV.up.forEach { (k, v) -> j.put(k, v) } })
        o.put("upc", SV.upc)
        o.put("rd", JSONObject().also { j -> SV.rd.forEach { j.put(it, 1) } })
        o.put("rk", JSONObject().also { j -> SV.rk.forEach { (k, v) -> j.put(k, v) } })
        host?.getSharedPreferences("afterglow", Context.MODE_PRIVATE)?.edit()?.putString(KEY, o.toString())?.apply()
    } catch (e: Exception) { }
}

// 스킬트리 효과 합계
private val TBc = HashMap<String, Double>()
fun rcb() {
    TBc.clear()
    for (n in NL) if (SV.n.contains(n.id)) for ((k, v) in n.e) if (v is Number) TBc[k] = (TBc[k] ?: 0.0) + v.toDouble()
}
fun tb(k: String): Double = TBc[k] ?: 0.0
fun chm(k: String): Double = (CHR.find { it.id == SV.ch } ?: CHR[0]).m[k] ?: 0.0
fun upL(id: String): Int = (SV.up[id] ?: 0).coerceIn(0, UM[id]!!.max)
fun up(id: String): Double = upL(id) * UM[id]!!.per
fun upCost(id: String, l: Int): Int {
    val u = UM[id]!!
    var c = 0
    for (i in 0..l) c = Math.max(c + 1, Math.round(u.b * Math.pow(u.g, i.toDouble())).toInt())
    return c
}
fun invT() = .6 + up("ib")
fun upFmt(id: String, v: Double): String {
    val u = UM[id]!!
    return when (u.u) {
        "p" -> "+" + fmtN(Math.round(v * 1000) / 10.0) + "%"
        "s" -> "+" + String.format(java.util.Locale.US, "%.2f", v) + "초"
        else -> "+" + Math.round(v)
    }
}
fun fmtN(v: Double): String = if (v == Math.floor(v)) v.toLong().toString() else v.toString()
fun upBuy(id: String): Boolean {
    val u = UM[id] ?: return false
    val l = upL(id)
    if (l >= u.max) return false
    val c = upCost(id, l)
    if (SV.p < c) return false
    SV.p -= c; SV.up[id] = l + 1; SV.upc += c; sv()
    return true
}
fun upReset() { SV.p += SV.upc; SV.up.clear(); SV.upc = 0; sv() }
fun UW(k: String): Boolean = k in listOf("pulse", "blade", "rail", "grav") || NL.any { n -> SV.n.contains(n.id) && n.e.any { it.first == "uw" && it.second == k } }
