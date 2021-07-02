package io.github.takusan23.flickwidget.tool

import android.appwidget.AppWidgetManager
import android.content.Context
import android.util.DisplayMetrics

/**
 * ウイジェット情報を取得するクラス
 *
 * @param context Context
 * @param widgetId ウイジェットのID
 * */
class WidgetInfo(private val context: Context, val widgetId: Int) {

    private val appWidgetManager = context.getSystemService(Context.APPWIDGET_SERVICE) as AppWidgetManager
    private val packageManager = context.packageManager

    private val widgetInfo = appWidgetManager.getAppWidgetInfo(widgetId)

    /** 最小幅 */
    val minWidth = widgetInfo.minWidth

    /** 最小高さ */
    val minHeight = widgetInfo.minHeight

    /** ウイジェットの名前 */
    val widgetLabel = widgetInfo.loadLabel(packageManager)

    /** ウイジェットのプレビュー画像 */
    val widgetPreviewImage = widgetInfo.loadPreviewImage(context, DisplayMetrics().densityDpi)

}