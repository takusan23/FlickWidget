package io.github.takusan23.flickwidget.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * [FlickWidgetConfigureViewModel]へWidgetIdを渡すためだけのクラス
 * */
class FlickWidgetConfigureViewModelFactory(private val application: Application, private val flickWidgetId: Int) : ViewModelProvider.AndroidViewModelFactory(application) {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return FlickWidgetConfigureViewModel(application, flickWidgetId) as T
    }

}