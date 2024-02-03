package com.project.tex.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.PendingDynamicLinkData
import com.project.tex.R
import com.project.tex.settings.ui.InviteFriendsActivity


object IntentUtils {
    fun openWebPage(context: Context, url: String?) {
        var url = url ?: return
        if (!url.startsWith("https://") && !url.startsWith("http://")) {
            url = "https://$url"
        }
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        try {
            context.startActivity(browserIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                context,
                context.getString(R.string.no_application_found),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun getDeeplinkIntent(intent: Intent?): Task<PendingDynamicLinkData> {
        return FirebaseDynamicLinks.getInstance()
            .getDynamicLink(intent)
    }

//    fun openYoutubeVideoInapp(context: Context, url: HelpArticle?) {
//        val intent = Intent(context, YoutubePlayerActivity::class.java)
//        intent.putExtra(Constants.INENT_DATA, url)
//        context.startActivity(intent)
//    }

    fun shareTextUrl(context: Context, text: String?) {
        val sendIntent = Intent()
        sendIntent.setAction(Intent.ACTION_SEND)
        sendIntent.putExtra(Intent.EXTRA_TEXT, text)
        sendIntent.setType("text/plain")
        val shareIntent: Intent = Intent.createChooser(sendIntent, null)
        context.startActivity(shareIntent)
    }

    fun shareTextToWhatsapp(context: Context, text: String?) {
        val sendIntent = Intent()
        sendIntent.setAction(Intent.ACTION_SEND)
        sendIntent.putExtra(Intent.EXTRA_TEXT, text)
        sendIntent.setType("text/plain")
        sendIntent.setPackage("com.whatsapp")
        try {
            context.startActivity(sendIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "WhatsApp not found", Toast.LENGTH_SHORT).show()
        }
    }

    fun shareTextToWhatsapp(context: Context, text: String?, errorMsg: String?) {
        val sendIntent = Intent()
        sendIntent.setAction(Intent.ACTION_SEND)
        sendIntent.putExtra(Intent.EXTRA_TEXT, text)
        sendIntent.setType("text/plain")
        sendIntent.setPackage("com.whatsapp")
        try {
            context.startActivity(sendIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
        }
    }

    fun shareTextToSms(context: Context, text: String?, errorMsg: String?) {
        val sendIntent = Intent(Intent.ACTION_VIEW)
        sendIntent.data = Uri.parse("sms:")
        sendIntent.putExtra("sms_body", text);
        try {
            context.startActivity(sendIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
        }
    }

    fun callCustomer(context: Context, phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.setData(Uri.parse("tel:$phoneNumber"))
        // Try to invoke the intent.
        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
        }
    }

    fun shareEmail(context: Context, msgs: String, err: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        val data = Uri.parse("mailto:?subject=" + "&body=" + msgs)
        intent.data = data
        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
            Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
        }
    }
}