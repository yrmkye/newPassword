package com.example.newpassword.page

import android.content.Context
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.newpassword.data.PasswordInfoDao
import com.example.newpassword.data.RouteConfig

@Composable
fun NavHost(
    context: Context,
    passwordDao: PasswordInfoDao,
    writeString: (String, String) -> Unit,
    readString: (String, String) -> String
) {


    val navController = rememberNavController()

    androidx.navigation.compose.NavHost(
        navController = navController,
        startDestination = if(readString("firstTime", "true") == "true")RouteConfig.ROUTE_BOOT_PAGE else RouteConfig.ROUTE_HOMW_PAGE,
        enterTransition = {
            fadeIn(animationSpec = tween(200, easing = FastOutSlowInEasing))
        },
        exitTransition = {
            fadeOut(animationSpec = tween(200, easing = FastOutSlowInEasing))
        }
    ) {

        // 引导页
        composable(route = RouteConfig.ROUTE_BOOT_PAGE) {
            MyBoot() {
                writeString("firstTime", "false")
                navController.navigate(RouteConfig.ROUTE_HOMW_PAGE)
            }
        }
        // 关于页
        composable(route = RouteConfig.ROUTE_ABOUT_PAGE) {
            MyAbout()
        }
        // 主页
        composable(route = RouteConfig.ROUTE_HOMW_PAGE) {
            ScaffoldPage(
                context = context,
                navController = navController,
                passwordDao = passwordDao,
                writeString = { key, value -> writeString(key, value) },
                readString = { key, defaultValue -> readString(key, defaultValue) }
            )
        }
        // 搜索页
        composable(route = RouteConfig.ROUTE_SEARCH_PAGE) {
            SearchPage(
                navController = navController,
                passwordDao = passwordDao
            )
        }
        // 详细页
        composable(RouteConfig.ROUTE_DETAILS_PAGE) { navBackStackEntry ->
            val name = (navBackStackEntry.arguments?.getString("name") ?: "")
            DetailsPage(
                passwordDao = passwordDao,
                navController = navController,
                name = name,
            )
        }
        // 修改页
        composable(RouteConfig.ROUTE_EDIT_PAGE) { navBackStackEntry ->
            val id = navBackStackEntry.arguments?.getString("id") ?: "-1"
            EditPage(
                passwordDao = passwordDao,
                navController = navController,
                id = id.toInt()
            )

        }
    }
}