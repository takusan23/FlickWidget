package io.github.takusan23.flickwidget.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import io.github.takusan23.flickwidget.R
import io.github.takusan23.flickwidget.WidgetHostActivity
import io.github.takusan23.flickwidget.repository.FlickWidgetDataRepository

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in [FlickWidgetConfigureActivity]
 */
class FlickWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetId)
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        val flickWidgetDataRepository = FlickWidgetDataRepository(context)
        for (appWidgetId in appWidgetIds) {
            // Preferenceから削除
            flickWidgetDataRepository.deleteFlickWidget(appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    companion object {

        /**
         * ウイジェットを更新する関数
         * @param appWidgetId 更新するFlickWidgetのId
         * @param context Context
         * */
        fun updateAppWidget(context: Context, appWidgetId: Int) {
            val appWidgetManager = context.getSystemService(Context.APPWIDGET_SERVICE) as AppWidgetManager
            val views = RemoteViews(context.packageName, R.layout.flick_widget)

            // StackViewにデータをセットする
            views.setRemoteAdapter(R.id.flick_widget_stack_view, Intent(context, FlickWidgetRemoteViewService::class.java).apply {
                putExtra("widget_id", appWidgetId)
            })
            // StackViewを押せるように
            val intent = Intent(context, WidgetHostActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(context, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            views.setPendingIntentTemplate(R.id.flick_widget_stack_view, pendingIntent)

            // ウイジェット更新
            appWidgetManager.updateAppWidget(appWidgetId, views)
            // StackView更新。忘れないで
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.flick_widget_stack_view)
            val componentName = ComponentName(context, FlickWidget::class.java)
        }
    }

}

