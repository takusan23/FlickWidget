package io.github.takusan23.flickwidget.data

import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import io.github.takusan23.flickwidget.R

/**
 * WidgetピッカーをActivityResultAPIで利用できるように
 * */
class AddWidgetResultContracts : ActivityResultContract<Unit, Int>() {

    override fun createIntent(context: Context, input: Unit?): Intent {
        val widgetHost = AppWidgetHost(context.applicationContext, R.string.app_name)
        val widgetId = widgetHost.allocateAppWidgetId()
        return Intent(AppWidgetManager.ACTION_APPWIDGET_PICK).apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Int {
        return intent?.extras?.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1)!!
    }
}