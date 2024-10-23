package com.example.pocbackwebview

import android.webkit.JavascriptInterface

class JSBridgeWebInterface(
    val onBack: () -> Unit
) {
    @JavascriptInterface
    fun handleOnBack() {
        onBack()
    }
}