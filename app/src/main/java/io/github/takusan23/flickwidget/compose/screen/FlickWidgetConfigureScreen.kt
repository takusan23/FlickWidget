package io.github.takusan23.flickwidget.compose.screen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.res.painterResource
import io.github.takusan23.flickwidget.R
import io.github.takusan23.flickwidget.compose.ui.WidgetList
import io.github.takusan23.flickwidget.data.AddWidgetResultContracts
import io.github.takusan23.flickwidget.viewmodel.FlickWidgetConfigureViewModel

/**
 * [io.github.takusan23.flickwidget.widget.FlickWidget]の設定画面。ウイジェット追加など
 *
 * @param viewModel ViewModel
 * */
@Composable
fun FlickWidgetConfigureScreen(viewModel: FlickWidgetConfigureViewModel, onSaveClick: () -> Unit) {
    // FlickWidgetの中のウイジェットの配列をLiveDataで受け取る
    val widgetList = viewModel.widgetIdList.observeAsState(initial = listOf())
    // ウイジェットピッカーコールバック
    val widgetPickerCallBack = rememberLauncherForActivityResult(contract = AddWidgetResultContracts()) { widgetId ->
        viewModel.addWidgetIdFromFlickWidget(widgetId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                actions = {
                    IconButton(onClick = { onSaveClick() }) {
                        Icon(painter = painterResource(id = R.drawable.ic_outline_save_24), contentDescription = "save")
                    }
                },
                title = {
                    Text(text = "追加するウイジェット選択画面")
                }
            )
        },
        content = {
            WidgetList(widgetIdList = widgetList.value.map { it.widgetId })
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { widgetPickerCallBack.launch(Unit) },
                icon = { Icon(painter = painterResource(id = R.drawable.ic_baseline_add_24), contentDescription = null) },
                text = { Text(text = "ウイジェット追加") }
            )
        }
    )
}