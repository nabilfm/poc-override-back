package com.example.pocbackwebview

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.example.pocbackwebview.ui.theme.POCBackWebviewTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    var isBackToExitApp: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val coroutineScope = rememberCoroutineScope()
            val webView by remember {
                mutableStateOf(
                    WebView(this).apply {
                        settings.javaScriptEnabled = true
                        addJavascriptInterface(
                            JSBridgeWebInterface(
                                onBack = {
                                    coroutineScope.launch {
                                        if (url?.contains("in-review") == true) {
                                            Log.e("xxx JsBridge", "do nothing")
                                            // do nothing
                                        } else if(canGoBack()) {
                                            Log.e("xxx JsBridge", "going back form url $url")
                                            goBack()
                                        } else {
                                            Log.e("xxx JsBridge", "finish")
                                            finish()
                                        }
                                    }
                                }
                            ),
                            "JsBridge"
                        )
                        setOnKeyListener { view, i, keyEvent ->
                            if (keyEvent.getAction() === KeyEvent.ACTION_DOWN) {
                                when (i) {
                                    KeyEvent.KEYCODE_BACK -> if (canGoBack() && !isBackToExitApp && !url.isInReviewUrl()) {
                                        Log.d("xxx event listener", "going back from url $url")
                                        goBack()
                                        return@setOnKeyListener true
                                    }
                                }
                            }
                            return@setOnKeyListener false
                        }
                        webViewClient = object : WebViewClient() {
                            override fun doUpdateVisitedHistory(
                                view: WebView?,
                                url: String?,
                                isReload: Boolean
                            ) {
                                if (url?.contains("in-review") == true) {
                                    isBackToExitApp = false
                                }
                                super.doUpdateVisitedHistory(view, url, isReload)
                            }
                        }
                    }
                )
            }
            fun onBack() {

            }
            POCBackWebviewTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    it
                    AndroidView(
                        modifier = Modifier.fillMaxSize(),
                        factory = { _ ->
                            webView.apply {
                                loadUrl("http://10.0.2.2:3000")
                            }
                        }
                    )
                }
            }
            onBackPressedDispatcher.addCallback(
                this,
                object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        if (!isBackToExitApp && webView.url.isInReviewUrl()) {
                            Log.e("xxx native", "do nothing")
                            // do nothing
                        } else if (webView.canGoBack()) {
                            Log.e("xxx native", "going back form url ${webView.url}")
                            webView.goBack()
                        } else {
                            Log.e("xxx native", "finish")
                            finish()
                        }
                    }
                }
            )
        }
    }
    private fun String?.isInReviewUrl(): Boolean {
        return this?.contains("in-review") == true
    }
}