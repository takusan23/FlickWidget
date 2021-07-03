package io.github.takusan23.flickwidget.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.github.takusan23.flickwidget.repository.FlickWidgetDataRepository
import io.github.takusan23.flickwidget.tool.WidgetInfo

/**
 * ViewModel
 *
 * @param flickWidgetId ウイジェットのID
 * */
class FlickWidgetConfigureViewModel(application: Application, private val flickWidgetId: Int) : AndroidViewModel(application) {

    private val _widgetIdList = MutableLiveData<List<WidgetInfo>>()

    /** 登録するウイジェット配列 */
    val widgetIdList = _widgetIdList as LiveData<List<WidgetInfo>>

    /** FlickWidgetデータ取得クラス */
    private val flickWidgetDataRepository = FlickWidgetDataRepository(application.applicationContext)

    init {
        sendWidgetList()
    }

    /** FlickWidgetに追加したウイジェットの配列をLiveDataへ送信 */
    private fun sendWidgetList() = _widgetIdList.postValue(flickWidgetDataRepository.getWidgetInfoList(flickWidgetId))

    /**
     * FlickWidgetへウイジェットを追加する
     *
     * @param widgetId ウイジェットのId
     * */
    fun addWidgetIdFromFlickWidget(widgetId: Int) {
        flickWidgetDataRepository.addWidgetIdFromFlickWidget(flickWidgetId, widgetId)
        sendWidgetList()
    }

    /**
     * FlickWidgetに登録したウィジェットを削除
     * */
    fun allDeleteWidgetIdFromFlickWidget() {
        flickWidgetDataRepository.deleteFlickWidget(flickWidgetId)
    }

}