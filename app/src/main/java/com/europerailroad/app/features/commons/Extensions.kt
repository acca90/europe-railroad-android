package com.europerailroad.app.features.commons

import android.app.Activity
import androidx.core.content.ContextCompat.startActivity

import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat


fun Activity.openExternalBrowser(urlStr: String) {
    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(urlStr))
    ContextCompat.startActivity(this, browserIntent, null)
}