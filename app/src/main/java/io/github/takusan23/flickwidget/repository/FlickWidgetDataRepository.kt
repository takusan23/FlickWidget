package io.github.takusan23.flickwidget.repository

import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import io.github.takusan23.flickwidget.data.FlickWidgetData
import io.github.takusan23.flickwidget.tool.WidgetCapture
import io.github.takusan23.flickwidget.tool.WidgetInfo
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.json.JSONArray
import java.io.File

/**
 * SharedPreferenceとのやり取りを隠蔽するクラス
 *
 * ウイジェットのデータはPreferenceに保存されるので
 * */
class FlickWidgetDataRepository(private val context: Context) {

    private val prefSetting = PreferenceManager.getDefaultSharedPreferences(context)

    /**
     * FlickWidgetに追加されてるウイジェット情報を返す
     *
     * @param flickWidgetId FlickWidgetのウイジェットID
     * @return ウイジェット情報の配列
     * */
    fun getWidgetInfoList(flickWidgetId: Int): List<WidgetInfo> {
        // JSON配列なのでパース
        val widgetIdJSONArray = prefSetting.getString(flickWidgetId.toString(), "[]") ?: "[]"
        val jsonArray = JSONArray(widgetIdJSONArray)
        // 配列へ
        val widgetIdList = arrayListOf<Int>()
        for (i in 0 until jsonArray.length()) {
            widgetIdList.add(jsonArray.getInt(i))
        }
        // ウイジェットのデータクラスへ
        return widgetIdList.map { id -> WidgetInfo(context, id) }
    }

    /**
     * FlickWidgetデータを返す。この中にキャプチャしたウイジェットの画像なんかがある
     * @param flickWidgetId ウイジェットのID
     * @return ウイジェットの
     * */
    suspend fun getFlickWidgetData(flickWidgetId: Int): List<FlickWidgetData> {
        val widgetInfoList = getWidgetInfoList(flickWidgetId)
        // ウイジェットのキャプチャーをした画像パスをいれたデータクラスを返す
        return widgetInfoList
            .map { widgetInfo ->
                // 並列実行
                GlobalScope.async { Pair(widgetInfo, WidgetCapture.capture(context, flickWidgetId, widgetInfo.widgetId)) }
            }.map { async ->
                // 並列実行したのですべてが終わるまで待機
                async.await()
            }.map { (info, imgPath) ->
                // データクラスを返す
                FlickWidgetData(info.widgetId, info.widgetLabel, imgPath)
            }
    }

    /**
     * FlickWidgetにウイジェットを追加する
     *
     * @param childWidgetId 追加するウイジェットのID
     * @param flickWidgetId ウイジェットID
     * */
    fun addWidgetIdFromFlickWidget(flickWidgetId: Int, childWidgetId: Int) {
        // JSON配列なのでパース
        val widgetIdJSONArray = prefSetting.getString(flickWidgetId.toString(), "[]") ?: "[]"
        val jsonArray = JSONArray(widgetIdJSONArray)
        jsonArray.put(childWidgetId)
        // 保存する。同期処理してくれ
        prefSetting.edit(commit = true) { putString(flickWidgetId.toString(), jsonArray.toString()) }
    }

    /**
     * FlickWidgetに追加したウイジェットを削除する
     *
     * @param childWidgetId 削除するウイジェットのID
     * @param flickWidgetId ウイジェットID
     * */
    fun deleteWidgetIdFromFlickWidget(flickWidgetId: Int, childWidgetId: Int) {
        // JSON配列なのでパース
        val widgetIdJSONArray = prefSetting.getString(flickWidgetId.toString(), "[]") ?: "[]"
        val jsonArray = JSONArray(widgetIdJSONArray)
        // 位置を出す
        var deleteIndex = -1
        for (i in 0 until jsonArray.length()) {
            if (jsonArray.getInt(i) == childWidgetId) {
                deleteIndex = i
                break
            }
        }
        if (deleteIndex != -1) {
            jsonArray.remove(deleteIndex)
        }
        // 保存する
        prefSetting.edit(commit = true) { putString(flickWidgetId.toString(), jsonArray.toString()) }
    }

    /**
     *  FlickWidgetを削除する
     * @param flickWidgetId 削除対象のウイジェットID
     * */
    fun deleteFlickWidget(flickWidgetId: Int) {
        prefSetting.edit(commit = true) { remove(flickWidgetId.toString()) }
        File(context.getExternalFilesDir(null), flickWidgetId.toString()).deleteRecursively()
    }

}