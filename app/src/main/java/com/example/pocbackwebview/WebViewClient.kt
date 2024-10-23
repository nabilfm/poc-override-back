package com.example.pocbackwebview

import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient

class WebClient(): WebViewClient() {
    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {

        return super.shouldOverrideUrlLoading(view, request)
    }
}