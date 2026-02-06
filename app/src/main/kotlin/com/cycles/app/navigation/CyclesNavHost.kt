package com.cycles.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.cycles.app.ui.screens.CalendarScreen
import com.cycles.app.ui.screens.InsightsScreen
import com.cycles.app.ui.screens.SettingsScreen
import com.cycles.app.ui.screens.TodayScreen
import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable data object Today : Route
    @Serializable data object Calendar : Route
    @Serializable data object Insights : Route
    @Serializable data object Settings : Route
}

@Composable
fun CyclesNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = Route.Today,
        modifier = modifier,
    ) {
        composable<Route.Today> { TodayScreen() }
        composable<Route.Calendar> { CalendarScreen() }
        composable<Route.Insights> { InsightsScreen() }
        composable<Route.Settings> { SettingsScreen() }
    }
}
