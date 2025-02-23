package com.example.newpassword.page

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.newpassword.R
import com.example.newpassword.data.AccountNumberInfo
import com.example.newpassword.data.RouteConfig

@Composable
fun MyHomeContent(
    innerPadding: PaddingValues,
    navController: NavController,
    list: List<AccountNumberInfo>
) {
    Column(
        modifier = Modifier
            .padding(innerPadding)
            .padding(horizontal = 10.dp)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(50))
                .clickable { navController.navigate(RouteConfig.ROUTE_SEARCH_PAGE) },
            color = MaterialTheme.colorScheme.onPrimary,
        ) {
            Row(
                modifier = Modifier,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.width(56.dp),
                    painter = painterResource(id = R.drawable.baseline_search_24),
                    contentDescription = "打开搜索界面"
                )
                Text(
                    text = "搜索密码",
                    fontSize = 16.sp,
                    color = Color.Black.copy(alpha = 0.5f)
                )
            }
        }
        ListItem(
            modifier = Modifier.clickable { navController.navigate(RouteConfig.ROUTE_EDIT_PAGE_ID + "-1") },
            headlineContent = { Text(text = "添加密码") },
            leadingContent = {
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .size(48.dp)
                        .background(
                            MaterialTheme.colorScheme.surfaceContainer,
                            RoundedCornerShape(50)
                        )
                ) {
                    Icon(
                        modifier = Modifier
                            .padding(8.dp)
                            .size(32.dp),
                        imageVector = Icons.Default.Add,
                        contentDescription = "添加密码",
                        tint = MaterialTheme.colorScheme.surfaceTint
                    )
                }
            }
        )
        AccountList(navController, list, Modifier)

    }
}