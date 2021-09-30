package com.changanford.evos.utils

import android.os.Bundle
import androidx.annotation.NonNull
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import me.majiajie.pagerbottomtabstrip.NavigationController
import me.majiajie.pagerbottomtabstrip.listener.OnTabItemSelectedListener


/**
 *  文件名：BottomNavigationUtils
 *  创建者: zcy
 *  创建日期：2021/9/2 14:57
 *  描述: TODO
 *  修改描述：TODO
 */
object BottomNavigationUtils {

    fun setupWithNavController(
        @NonNull pageIds: IntArray,
        @NonNull navigationController: NavigationController,
        @NonNull navController: NavController
    ) {
        navigationController.addTabItemSelectedListener(object : OnTabItemSelectedListener {
            override fun onSelected(index: Int, old: Int) {
                onNavDestinationSelected(pageIds[index], navController)
            }

            override fun onRepeat(index: Int) {}
        })
        navController.addOnDestinationChangedListener { controller: NavController?, destination: NavDestination, arguments: Bundle? ->
            val destinationId = destination.id
            for (i in pageIds.indices) {
                if (destinationId == pageIds[i]) {
                    if (navigationController.selected != i) {
                        navigationController.setSelect(i, false)
                    }
                    break
                }
            }
        }
    }

    private fun onNavDestinationSelected(id: Int, @NonNull navController: NavController) {
        val options = NavOptions.Builder()
            .setLaunchSingleTop(true)
//            .setEnterAnim(R.anim.nav_default_enter_anim)
//            .setExitAnim(R.anim.nav_default_exit_anim)
//            .setPopEnterAnim(R.anim.nav_default_pop_enter_anim)
//            .setPopExitAnim(R.anim.nav_default_pop_exit_anim)
            .setPopUpTo(navController.graph.startDestination, false)
            .build()
        try {
            navController.navigate(id, null, options)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }
}