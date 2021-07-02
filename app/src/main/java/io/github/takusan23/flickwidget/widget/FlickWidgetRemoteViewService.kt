package io.github.takusan23.flickwidget.widget

import android.content.Intent
import android.graphics.BitmapFactory
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.preference.PreferenceManager
import io.github.takusan23.flickwidget.R
import io.github.takusan23.flickwidget.WidgetHostActivity
import io.github.takusan23.flickwidget.data.FlickWidgetData
import io.github.takusan23.flickwidget.repository.FlickWidgetDataRepository
import kotlinx.coroutines.runBlocking

/**
 * StackViewのAdapter。StackViewに情報を表示するクラス
 * */
class FlickWidgetRemoteViewService : RemoteViewsService() {

    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        val context = applicationContext
        val prefSetting = PreferenceManager.getDefaultSharedPreferences(context)

        /** FlickWidgetのId */
        val flickWidgetId = intent?.extras?.getInt("widget_id")!!

        /** ウイジェットを画像として保存配列 */
        val flickWidgetDataList = arrayListOf<FlickWidgetData>()

        /** ウイジェットの情報取得クラス */
        val flickWidgetDataRepository = FlickWidgetDataRepository(applicationContext)

        return object : RemoteViewsFactory {
            override fun onCreate() {

            }

            /** データ取得 */
            override fun onDataSetChanged() {
                // ドキュメントに同期処理していいよって言われたのでお言葉に甘えて同期処理。無礼講
                runBlocking {
                    flickWidgetDataList.addAll(flickWidgetDataRepository.getFlickWidgetData(flickWidgetId))
                }
            }

            override fun onDestroy() {

            }

            override fun getCount(): Int {
                return flickWidgetDataList.size
            }

            /** 一個一個のビューを返す */
            override fun getViewAt(position: Int): RemoteViews {
                return if (position < flickWidgetDataList.size) {
                    val flickWidgetData = flickWidgetDataList[position]
                    RemoteViews(context.packageName, R.layout.flick_widget_item).apply {
                        val bitmap = BitmapFactory.decodeFile(flickWidgetData.widgetCaptureImagePath)
                        setTextViewText(R.id.flick_widget_item_label_text_view, flickWidgetData.widgetLabel)
                        setImageViewBitmap(R.id.flick_widget_item_image_view, bitmap)
                        // おしたらウイジェット表示Activityを出す
                        val intent = Intent(context, WidgetHostActivity::class.java).apply {
                            putExtra("widget_id", flickWidgetData.widgetId)
                            putExtra("flick_widget_id", flickWidgetId)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        setOnClickFillInIntent(R.id.flick_widget_item_image_view, intent)
                    }
                } else RemoteViews(context.packageName, R.layout.flick_widget_item)
            }

            override fun getLoadingView(): RemoteViews? {
                return null
            }

            override fun getViewTypeCount(): Int {
                return 1
            }

            override fun getItemId(position: Int): Long {
                return position.toLong()
            }

            override fun hasStableIds(): Boolean {
                return true
            }

        }
    }
}