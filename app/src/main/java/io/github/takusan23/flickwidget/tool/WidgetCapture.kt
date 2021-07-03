package io.github.takusan23.flickwidget.tool

import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.children
import androidx.core.view.doOnLayout
import io.github.takusan23.flickwidget.R
import io.github.takusan23.flickwidget.widget.FlickWidget
import kotlinx.coroutines.delay
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.math.max


object WidgetCapture {

    /**
     * ウイジェットをキャプチャーする。[delay]分遅延するのでコルーチンのasync{ }で並列実行したほうがいいよ
     * @param context Context
     * @param flickWidgetId FlickWidgetのID
     * @param widgetIdList ウイジェットのIDが入った配列
     * @param delayMs ウイジェットをキャプチャーするまでの遅延時間。すぐキャプチャするとAPI取得まだで表示されてないとかありそうだし
     * @return [widgetIdList]の数だけキャプチャした画像のパスを入れた配列を返します
     * */
    suspend fun captureFromWidgetIdList(context: Context, flickWidgetId: Int, widgetIdList: List<Int>, delayMs: Long = 1000): List<String> = withContext(Dispatchers.Main) {
        val imgPathList = arrayListOf<String>()
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val appWidgetHost = AppWidgetHost(context, FlickWidget.WIDGET_HOST_ID)
        appWidgetHost.startListening()
        widgetIdList.map { widgetId ->
            // 並列実行
            async {
                // ウイジェット情報
                val appWidgetInfo = appWidgetManager.getAppWidgetInfo(widgetId)
                // ウイジェットのView
                val hostView = appWidgetHost.createView(context, widgetId, appWidgetInfo)
                hostView.setAppWidget(widgetId, appWidgetInfo)

                delay(delayMs / 2)
                val widgetWidth = max(appWidgetInfo.minWidth * 2, 1000)
                val widgetHeight = max(appWidgetInfo.minHeight * 2, 500)
                // Viewの大きさ
                hostView.measure(
                    View.MeasureSpec.makeMeasureSpec(widgetWidth, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(widgetHeight, View.MeasureSpec.EXACTLY)
                )
                hostView.layout(0, 0, hostView.measuredWidth, hostView.measuredHeight)
                delay(delayMs / 2)

                // Canvasを用意
                val bitmap = Bitmap.createBitmap(hostView.width, hostView.height, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)
                // 自分で用意したCanvasに模写する
                hostView.draw(canvas)
                // 画像化する。外部アプリ固有ストレージへ保存
                val folder = File(context.getExternalFilesDir(null), flickWidgetId.toString()).apply { if (!exists()) mkdir() }
                val imageFile = File(folder, "$widgetId.png").apply { createNewFile() }
                val outputStream = imageFile.outputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                outputStream.close()
                imgPathList.add(imageFile.path)
            }
        }.forEach { it.await() } // 全部終わるまで待機
        appWidgetHost.stopListening()
        return@withContext imgPathList
    }

    /**
     * ウイジェットをキャプチャーする。[delay]分遅延するのでコルーチンのasync{ }で並列実行したほうがいいよ
     * @param context Context
     * @param flickWidgetId FlickWidgetのID
     * @param widgetId ウイジェットのID
     * @param delayMs ウイジェットをキャプチャーするまでの遅延時間。すぐキャプチャするとAPI取得まだで表示されてないとかありそうだし
     * */
    suspend fun capture(context: Context, flickWidgetId: Int, widgetId: Int, delayMs: Long = 1000): String = withContext(Dispatchers.Main) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val appWidgetHost = AppWidgetHost(context, FlickWidget.WIDGET_HOST_ID)
        appWidgetHost.startListening()
        // ウイジェット情報
        val appWidgetInfo = appWidgetManager.getAppWidgetInfo(widgetId)
        // ウイジェットのView
        val hostView = appWidgetHost.createView(context, widgetId, appWidgetInfo)
        hostView.setAppWidget(widgetId, appWidgetInfo)

        delay(delayMs / 2)
        val widgetWidth = max(appWidgetInfo.minWidth * 2, 1000)
        val widgetHeight = max(appWidgetInfo.minHeight * 2, 500)
        // Viewの大きさ
        hostView.measure(
            View.MeasureSpec.makeMeasureSpec(widgetWidth, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(widgetHeight, View.MeasureSpec.EXACTLY)
        )
        hostView.layout(0, 0, hostView.measuredWidth, hostView.measuredHeight)
        delay(delayMs / 2)

        // Canvasを用意
        val bitmap = Bitmap.createBitmap(hostView.width, hostView.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        // 自分で用意したCanvasに模写する
        hostView.draw(canvas)
        // 画像化する。外部アプリ固有ストレージへ保存
        val folder = File(context.getExternalFilesDir(null), flickWidgetId.toString()).apply { if (!exists()) mkdir() }
        val imageFile = File(folder, "$widgetId.png").apply { createNewFile() }
        val outputStream = imageFile.outputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        outputStream.close()
        appWidgetHost.stopListening()
        return@withContext imageFile.path
    }

    /** 再帰的にViewを取得してすべてのViewを取得 */
    private fun getAllView(viewGroup: ViewGroup): Int {
        val resultViewList = arrayListOf<View>()

        fun findViewGroup(viewGroup: ViewGroup) {
            viewGroup.children.forEach { view ->
                resultViewList.add(view)
                if (view is ViewGroup) {
                    findViewGroup(view)
                }
            }
        }

        findViewGroup(viewGroup)

        println(resultViewList.filterIsInstance<TextView>().map { it.text })

        return resultViewList.size
    }

}