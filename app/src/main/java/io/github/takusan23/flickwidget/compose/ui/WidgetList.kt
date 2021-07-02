package io.github.takusan23.flickwidget.compose.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.VectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import io.github.takusan23.flickwidget.tool.WidgetInfo

/**
 * 登録済みウイジェット一覧
 *
 * @param widgetId ウイジェットのID
 * */
@Composable
fun WidgetList(widgetIdList: List<Int>) {
    // ウイジェット情報を取得
    val context = LocalContext.current
    // 一覧表示
    LazyColumn {
        items(widgetIdList.map { WidgetInfo(context, it) }) { info ->
            Row(
                modifier = Modifier.padding(10.dp).height(100.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    bitmap = info.widgetPreviewImage.toBitmap().asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = info.widgetLabel,
                    modifier = Modifier.weight(2f)
                )
            }
            Divider()
        }
    }
}