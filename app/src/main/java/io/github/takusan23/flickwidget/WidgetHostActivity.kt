package io.github.takusan23.flickwidget

import android.app.Activity
import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.github.takusan23.flickwidget.databinding.ActivityWidgetHostBinding
import io.github.takusan23.flickwidget.tool.WidgetCapture
import io.github.takusan23.flickwidget.widget.FlickWidget
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * FlickWidgetを押したときにウイジェットを表示するのでそのためのActivity
 *
 * Intentに「widget_id」でウイジェットのId、「flick_widget_id」でFlickWidgetのIdを渡してください
 *
 * */
class WidgetHostActivity : Activity() {

    private val appWidgetManager by lazy { AppWidgetManager.getInstance(this.applicationContext) }
    private val appWidgetHost by lazy { AppWidgetHost(this.applicationContext, FlickWidget.WIDGET_HOST_ID) }
    private val widgetId by lazy { intent.extras?.getInt("widget_id")!! }
    private val flickWidgetId by lazy { intent.extras?.getInt("flick_widget_id")!! }
    private val activityWidgetHostBinding by lazy { ActivityWidgetHostBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 表示
        setContentView(activityWidgetHostBinding.root)

        // ウイジェットのViewを取得
        val appWidgetInfo = appWidgetManager.getAppWidgetInfo(widgetId)
        val hostView = appWidgetHost.createView(this.applicationContext, widgetId, appWidgetInfo)
        hostView.setAppWidget(widgetId, appWidgetInfo)

        activityWidgetHostBinding.root.addView(hostView)

        // ウイジェットの外を押したらActivity終了
        activityWidgetHostBinding.root.setOnClickListener {
            finishAndRemoveTask()
        }

    }

    override fun onStart() {
        super.onStart()
        appWidgetHost.startListening()
    }

    override fun onStop() {
        super.onStop()
        appWidgetHost.stopListening()
        // 更新
        FlickWidget.updateAppWidget(this@WidgetHostActivity, flickWidgetId)
    }

}