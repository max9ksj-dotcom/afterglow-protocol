package com.afterglow.protocol

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.webkit.WebViewAssetLoader

/**
 * 잔광 프로토콜 안드로이드 앱.
 * 게임(index.html)은 앱 안(assets)에 들어 있어서 인터넷 없이 바로 실행된다.
 * 고정 주소(https://appassets.androidplatform.net/)로 열어서 저장 데이터(localStorage)가 앱 업데이트 후에도 유지된다.
 */
class MainActivity : Activity() {
    private lateinit var web: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val loader = WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(this))
            .build()

        web = WebView(this)
        web.setBackgroundColor(0xFF0D1020.toInt())
        web.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true                  // 저장(localStorage)
            mediaPlaybackRequiresUserGesture = false  // 배경 음악
            allowFileAccess = false
            allowContentAccess = false
            setSupportZoom(false)
            builtInZoomControls = false
            textZoom = 100                            // 휴대폰 글자 크기 설정에 영향받지 않게
        }
        web.overScrollMode = View.OVER_SCROLL_NEVER
        web.webViewClient = object : WebViewClient() {
            override fun shouldInterceptRequest(
                view: WebView,
                request: WebResourceRequest
            ): WebResourceResponse? = loader.shouldInterceptRequest(request.url)
        }
        setContentView(web)
        hideBars()
        if (savedInstanceState != null) {
            web.restoreState(savedInstanceState)
        } else {
            web.loadUrl("https://appassets.androidplatform.net/assets/index.html")
        }
    }

    /** 상태 표시줄·내비게이션 바를 숨긴 전체 화면 */
    @Suppress("DEPRECATION")
    private fun hideBars() {
        if (Build.VERSION.SDK_INT >= 30) {
            window.insetsController?.let {
                it.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                )
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideBars()
    }

    /** 뒤로 가기: 게임 중이면 일시정지, 아니면 앱 종료 */
    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        web.evaluateJavascript(
            "(function(){try{if(typeof S!=='undefined'&&S&&S.run&&!S.ended&&!S.pz){pauseT();return 'paused'}}catch(e){}return 'menu'})()"
        ) { v -> if (v == null || !v.contains("paused")) finish() }
    }

    override fun onPause() {
        super.onPause()
        // 앱을 내리면 게임을 일시정지하고 음악을 멈춘다
        web.evaluateJavascript(
            "try{if(typeof S!=='undefined'&&S&&S.run&&!S.ended&&!S.pz)pauseT();bgmPause()}catch(e){}",
            null
        )
        web.onPause()
    }

    override fun onResume() {
        super.onResume()
        web.onResume()
        hideBars()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        web.saveState(outState)
    }

    override fun onDestroy() {
        if (::web.isInitialized) web.destroy()
        super.onDestroy()
    }
}
