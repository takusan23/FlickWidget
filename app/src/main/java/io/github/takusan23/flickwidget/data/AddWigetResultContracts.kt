package io.github.takusan23.flickwidget.data

import android.app.Activity
import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import io.github.takusan23.flickwidget.R
import io.github.takusan23.flickwidget.widget.FlickWidget

/**
 * WidgetピッカーをActivityResultAPIで利用できるように
 * */
class AddWidgetResultContracts : ActivityResultContract<Unit, Int?>() {

    override fun createIntent(context: Context, input: Unit?): Intent {
        val widgetHost = AppWidgetHost(context.applicationContext, FlickWidget.WIDGET_HOST_ID)
        val widgetId = widgetHost.allocateAppWidgetId()
        return Intent(AppWidgetManager.ACTION_APPWIDGET_PICK).apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
        }
    }

    override fun getSynchronousResult(context: Context, input: Unit?): SynchronousResult<Int?>? {
        return null
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Int? {
        return if (resultCode == Activity.RESULT_OK) {
            intent?.extras?.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1)!!
        } else {
            null
        }
    }
}