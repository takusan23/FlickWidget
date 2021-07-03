package io.github.takusan23.flickwidget.widget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import io.github.takusan23.flickwidget.compose.screen.FlickWidgetConfigureScreen
import io.github.takusan23.flickwidget.ui.theme.FlickWidgetTheme
import io.github.takusan23.flickwidget.viewmodel.FlickWidgetConfigureViewModel
import io.github.takusan23.flickwidget.viewmodel.FlickWidgetConfigureViewModelFactory

/**
 * [FlickWidget]の設定画面
 */
class FlickWidgetConfigureActivity : ComponentActivity() {

    /** ウイジェットIDを取得 */
    private val widgetId by lazy { intent?.extras?.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, 0)!! }

    /** ViewModel */
    private val viewModel by lazy { ViewModelProvider(this, FlickWidgetConfigureViewModelFactory(application, widgetId)).get(FlickWidgetConfigureViewModel::class.java) }

    /** 保存したらtrue */
    private var isSaved = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FlickWidgetTheme {
                FlickWidgetConfigureScreen(
                    viewModel = viewModel,
                    onSaveClick = { finishEditor() }
                )
            }
        }
    }

    /** 編集完了 */
    private fun finishEditor() {
        // ウイジェット更新
        FlickWidget.updateAppWidget(this, widgetId)
        // 編集Activity終了
        val resultValue = Intent().apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
        }
        setResult(Activity.RESULT_OK, resultValue)
        isSaved = true
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!isSaved) {
            // セーブしていない場合はウィジェットを削除
            viewModel.allDeleteWidgetIdFromFlickWidget()
        }
    }

}