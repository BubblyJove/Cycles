package com.cycles.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.cycles.app.navigation.CyclesNavHost
import com.cycles.app.navigation.Route
import com.cycles.app.ui.theme.CyclesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CyclesTheme {
                CyclesApp()
            }
        }
    }
}

data class TopLevelRoute(
    val label: String,
    val route: Any,
    val icon: ImageVector,
)

val topLevelRoutes = listOf(
    TopLevelRoute("Today", Route.Today, Icons.Filled.Today),
    TopLevelRoute("Calendar", Route.Calendar, Icons.Filled.CalendarMonth),
    TopLevelRoute("Insights", Route.Insights, Icons.Filled.Insights),
    TopLevelRoute("Settings", Route.Settings, Icons.Filled.Settings),
)

@Composable
fun CyclesApp() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                topLevelRoutes.forEach { topLevelRoute ->
                    NavigationBarItem(
                        icon = { Icon(topLevelRoute.icon, contentDescription = topLevelRoute.label) },
                        label = { Text(topLevelRoute.label) },
                        selected = currentDestination?.hasRoute(topLevelRoute.route::class) == true,
                        onClick = {
                            navController.navigate(topLevelRoute.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        CyclesNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
