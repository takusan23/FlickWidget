package io.github.takusan23.flickwidget.data

/**
 * ウイジェットのデータクラス
 *
 * @param widgetId ウイジェットのID
 * @param widgetLabel ウイジェットの名前
 * @param widgetCaptureImagePath ウイジェットをキャプチャした画像のパス
 * */
data class FlickWidgetData(
    val widgetId: Int,
    val widgetLabel: String,
    val widgetCaptureImagePath: String
)