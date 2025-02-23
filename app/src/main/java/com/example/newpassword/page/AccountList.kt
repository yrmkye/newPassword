package com.example.newpassword.page

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.newpassword.R
import com.example.newpassword.data.AccountNumberInfo
import com.example.newpassword.data.RouteConfig

@Composable
fun AccountList(
    navController: NavController,
    list: List<AccountNumberInfo>,
    modifier: Modifier
) {
    if (list.isEmpty()) {
        Text(text = "暂无账号信息", style = MaterialTheme.typography.bodyLarge)
    } else {
        LazyColumn(
            modifier = modifier
        ) {
            itemsIndexed(list) { index, item ->
                if (index in list.indices) {
                    val safeAppName = item.appName.replace("<", "&lt;").replace(">", "&gt;")
                    ListItem(
                        modifier = Modifier
                            .clickable { navController.navigate(RouteConfig.ROUTE_DETAILS_PAGE_NAME + item.appName) },
                        headlineContent = { Text(text = safeAppName) },
                        leadingContent = {
                            Surface(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surface),
                                color = MaterialTheme.colorScheme.surface,
                                shape = CircleShape,
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                            ) {
                                Icon(
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .size(32.dp),
                                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                                    contentDescription = "点击查看 $safeAppName",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        },
                        supportingContent = {
                            Text(
                                text = "${item.number} 个账号",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    )
                }
            }
        }
    }
}