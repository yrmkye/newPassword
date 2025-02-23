package com.example.newpassword.page

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.example.newpassword.R
import com.example.newpassword.data.PasswordInfoDao


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldPage(
    context: Context,
    passwordDao: PasswordInfoDao,
    navController: NavController,
    writeString: (String, String) -> Unit,
    readString: (String, String) -> String
) {
    var exitTime: Long = 0
    BackHandler(enabled = true) {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(context, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            context.startActivity(Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_HOME)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            })
            android.os.Process.killProcess(android.os.Process.myPid())
        }
    }
    //需要展示的列表
    val listFlow = passwordDao.getFlowList("").collectAsState(initial = emptyList())
    val list by remember { derivedStateOf { listFlow.value } }

    var selectedItem by remember { mutableIntStateOf(0) }
    val items = listOf(
        Item("首页", R.drawable.outline_vpn_key_24),
        Item("设置", R.drawable.outline_settings_24)
    )
    Scaffold(
        topBar = { TopAppBar(title = { Text(text = items[selectedItem].name) }) },
        bottomBar = {
            NavigationBar {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                painter = painterResource(id = item.icon),
                                contentDescription = item.name
                            )
                        },
                        label = { Text(item.name) },
                        selected = selectedItem == index,
                        onClick = { selectedItem = index }
                    )
                }
            }
        },
        content = { innerPadding ->
            if (selectedItem == 0) {
                MyHomeContent(
                    innerPadding = innerPadding,
                    navController = navController,
                    list = list
                )
            } else {
                MySetContentPage(
                    innerPadding = innerPadding,
                    context = context,
                    passwordDao = passwordDao,
                    navController = navController,
                    writeString = { key, value -> writeString(key, value) },
                    readString = { key, defaultValue -> readString(key, defaultValue) }
                )
            }

        }
    )

}


private data class Item(val name: String, val icon: Int)


