package com.europerailroad.app

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Browser
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import com.europerailroad.app.features.commons.Constants
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.messaging.FirebaseMessaging
import com.railroad.railroad.BuildConfig
import com.railroad.railroad.R
import java.util.*
import androidx.browser.customtabs.CustomTabsClient

import android.content.ComponentName
import android.view.View
import android.widget.EditText
import android.widget.TextView

import androidx.browser.customtabs.CustomTabsServiceConnection
import androidx.browser.customtabs.CustomTabsCallback

import androidx.browser.customtabs.CustomTabsSession
import com.europerailroad.app.features.commons.openExternalBrowser
import java.net.URI


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(!BuildConfig.DEBUG)
        setContentView(R.layout.activity_main)
        subscribeToFirebaseTopic()
        openBrowser()
    }

    private fun openBrowser() {

        val builder = CustomTabsIntent.Builder()

        val pkg = CustomTabsHelper.getPackageNameToUse(this)

        val params = CustomTabColorSchemeParams.Builder()
        params.setToolbarColor(ContextCompat.getColor(this@MainActivity, R.color.colorPrimary))
        builder.setDefaultColorSchemeParams(params.build())

        builder.setShowTitle(true)
        builder.setShareState(CustomTabsIntent.SHARE_STATE_ON)
        builder.setInstantAppsEnabled(true)
        builder.setUrlBarHidingEnabled(false)

        val customBuilder = builder.build()

        if (this.isPackageInstalled("$pkg")) {
            // if chrome is available use chrome custom tabs
            customBuilder.intent.setPackage(pkg)
            customBuilder.launchUrl(this, Uri.parse(Constants.url))
       } else {
            findViewById<TextView>(R.id.text_info).visibility = View.VISIBLE
            //val webView: WebView = findViewById(R.id.webview)
            //webView.loadUrl(Constants.url)
            //webView.webViewClient = WebViewClient()
            //webView.settings.javaScriptEnabled = true
            openExternalBrowser(Constants.url)
        }
    }

    private fun Context.isPackageInstalled(packageName: String): Boolean {
        // check if chrome is installed or not
        return try {
            packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    private fun subscribeToFirebaseTopic() {
        val lang = Locale.getDefault().language
        FirebaseMessaging.getInstance().subscribeToTopic("${Constants.firebaseTopic}" + "_" + "$lang")
    }
}