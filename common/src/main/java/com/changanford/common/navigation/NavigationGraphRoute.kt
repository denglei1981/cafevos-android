package com.changanford.common.navigation

import android.content.Context
import androidx.navigation.NavController
import androidx.navigation.NavGraph

interface NavigationGraphRoute {
    val navigation: String
        get() = "navigation"

    var navGraph: NavGraph

    /**
    The .xml name for the nav-graph
     */
    val graphName: String

    /**
    The full package name where the nav-graph is located
     */
    val packageName: String


    var localNavController: NavController

    fun addNavGraphDestination(
        navController: NavController,
        context: Context
    ): NavGraph {
        val navigationId = context.resources.getIdentifier(graphName, navigation, packageName)
        val newGraph = navController.navInflater.inflate(navigationId)
        navController.graph.addDestination(newGraph)
        navGraph = newGraph
        localNavController = navController
        return newGraph
    }

    fun navigationTo() {
        localNavController.navigate(navGraph.id)
    }
}