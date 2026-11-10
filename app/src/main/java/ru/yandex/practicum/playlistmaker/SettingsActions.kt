package ru.yandex.practicum.playlistmaker

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast

internal class SettingsActions(private val activity: Activity) {
    fun share() {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, activity.getString(R.string.share_text))
        }
        launch(Intent.createChooser(intent, activity.getString(R.string.share)))
    }

    fun contactDeveloper() {
        val email = Uri.encode(activity.getString(R.string.developer_email))
        val subject = Uri.encode(activity.getString(R.string.support_subject))
        val body = Uri.encode(activity.getString(R.string.support_body))
        launch(Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$email?subject=$subject&body=$body")))
    }

    fun openAgreement() {
        launch(Intent(Intent.ACTION_VIEW, Uri.parse(activity.getString(R.string.agreement_url))))
    }

    private fun launch(intent: Intent) {
        try {
            activity.startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(activity, R.string.no_handler, Toast.LENGTH_LONG).show()
        }
    }
}
