
import android.content.Context
import android.content.pm.PackageManager

import android.content.Intent
import android.net.Uri

import android.text.TextUtils
import com.europerailroad.app.features.commons.Constants
import java.lang.RuntimeException

object CustomTabsHelper {

    private const val STABLE_PACKAGE = "com.android.chrome"
    private const val BETA_PACKAGE = "com.chrome.beta"
    private const val DEV_PACKAGE = "com.chrome.dev"
    private const val LOCAL_PACKAGE = "com.google.android.apps.chrome"

    private const val ACTION_CUSTOM_TABS_CONNECTION = "android.support.customtabs.action.CustomTabsService"

    private var sPackageNameToUse: String? = null

    private var currentIntent: Intent = Intent()

    private val chromeIntent by lazy {
        Intent()
            .setPackage(STABLE_PACKAGE)
            .setAction(Intent.ACTION_VIEW)
            .setData(Uri.parse(Constants.url))
            .addCategory(Intent.CATEGORY_BROWSABLE)
            .addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_REQUIRE_NON_BROWSER
            )
    }

    private val nativeAppIntent by lazy {
        Intent()
            .setAction(Intent.ACTION_VIEW)
            .setData(Uri.parse(Constants.url))
            .addCategory(Intent.CATEGORY_BROWSABLE)
            .addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_REQUIRE_NON_BROWSER
            )
    }

    fun getPackageNameToUse(context: Context): String? {

        val pm = context.packageManager

        var packagesSupportingCustomTabs = getSupportingCustomTabs(context)

        val defaultViewHandlerInfo = pm.resolveActivity(currentIntent, 0)
        var defaultViewHandlerPackageName: String? = null

        if (defaultViewHandlerInfo != null) {
            defaultViewHandlerPackageName = defaultViewHandlerInfo.activityInfo.packageName
        }

        // Now packagesSupportingCustomTabs contains all apps that can handle both VIEW intents
        // and service calls.
        if (packagesSupportingCustomTabs.isEmpty()) {
            sPackageNameToUse = String()
        } else if (packagesSupportingCustomTabs.size == 1) {
            sPackageNameToUse = packagesSupportingCustomTabs[0]
        } else if (!TextUtils.isEmpty(defaultViewHandlerPackageName)
            && !hasSpecializedHandlerIntents(context, nativeAppIntent)
            && packagesSupportingCustomTabs.contains(defaultViewHandlerPackageName)
        ) {
            sPackageNameToUse = defaultViewHandlerPackageName
        } else if (packagesSupportingCustomTabs.contains(STABLE_PACKAGE)) {
            sPackageNameToUse = STABLE_PACKAGE
        } else if (packagesSupportingCustomTabs.contains(BETA_PACKAGE)) {
            sPackageNameToUse = BETA_PACKAGE
        } else if (packagesSupportingCustomTabs.contains(DEV_PACKAGE)) {
            sPackageNameToUse = DEV_PACKAGE
        } else if (packagesSupportingCustomTabs.contains(LOCAL_PACKAGE)) {
            sPackageNameToUse = LOCAL_PACKAGE
        }

        return sPackageNameToUse
    }

    private fun getSupportingCustomTabs(context: Context): ArrayList<String> {

        var packagesSupportingCustomTabs = makeSupportingCustomTabs(context, chromeIntent)

        return when (packagesSupportingCustomTabs.size > 0) {
            true -> {
                currentIntent = chromeIntent
                packagesSupportingCustomTabs
            }
            else -> {
                packagesSupportingCustomTabs = makeSupportingCustomTabs(context, nativeAppIntent)
                currentIntent = nativeAppIntent
                packagesSupportingCustomTabs
            }
        }
    }

    /**
     * Used to make correctly supported browser
     * @param intent The intent to check with.
     * @return Correctly browser
     * */
    private fun makeSupportingCustomTabs(context: Context, intent: Intent): ArrayList<String> {

        val pm = context.packageManager

        // Get all apps that can handle VIEW intents.
        val resolvedActivityList = pm.queryIntentActivities(intent, 0)
        val packagesSupportingCustomTabs: ArrayList<String> = ArrayList()

        for (info in resolvedActivityList) {
            val serviceIntent = Intent()
            serviceIntent.action = ACTION_CUSTOM_TABS_CONNECTION
            serviceIntent.setPackage(info.activityInfo.packageName)
            // Check if this package also resolves the Custom Tabs service.
            if (pm.resolveService(serviceIntent, 0) != null) {
                packagesSupportingCustomTabs.add(info.activityInfo.packageName)
            }
        }

        return packagesSupportingCustomTabs
    }

    /**
     * Used to check whether there is a specialized handler for a given intent.
     * @param intent The intent to check with.
     * @return Whether there is a specialized handler for the given intent.
     */
    private fun hasSpecializedHandlerIntents(context: Context, intent: Intent): Boolean {
        try {
            val pm: PackageManager = context.packageManager
            val handlers = pm.queryIntentActivities(
                intent,
                PackageManager.GET_RESOLVED_FILTER
            )
            if (handlers == null || handlers.size == 0) {
                return false
            }
            for (resolveInfo in handlers) {
                val filter = resolveInfo.filter ?: continue
                if (filter.countDataAuthorities() == 0 || filter.countDataPaths() == 0) continue
                if (resolveInfo.activityInfo == null) continue
                return true
            }
        } catch (e: RuntimeException) {
            //
        }
        return false
    }
}