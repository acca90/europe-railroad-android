package com.europerailroad.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import com.europerailroad.app.features.commons.Constants
import com.google.firebase.messaging.FirebaseMessaging
import com.railroad.railroad.R
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        subscribeToFirebaseTopic()
        openBrowser()
    }

    private fun openBrowser() {
        val webView: WebView = findViewById(R.id.webview)
        webView.loadUrl(Constants.url)
        webView.webViewClient = WebViewClient()
        webView.settings.javaScriptEnabled = true
    }

    private fun subscribeToFirebaseTopic() {
        val lang = Locale.getDefault().language
        FirebaseMessaging.getInstance().subscribeToTopic("${Constants.firebaseTopic}" + "_" + "$lang")
    }
}