package com.tvandinther.reps.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.tvandinther.reps.ui.exercises.ExercisesScreen
import com.tvandinther.reps.ui.history.HistoryScreen
import com.tvandinther.reps.ui.logging.LoggingScreen
import kotlinx.serialization.Serializable

@Serializable object ExercisesTab
@Serializable object ExercisesList
@Serializable data class SetLogging(val exerciseId: Long)
@Serializable object HistoryTab

@Composable
fun RepsApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val tabs = listOf(
        Tab("Exercises", ExercisesTab),
        Tab("History", HistoryTab),
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
            ) {
                tabs.forEach { tab ->
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any {
                            it.route == tab.route::class.qualifiedName
                        } == true,
                        onClick = {
                            navController.navigate(tab.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {},
                        label = { Text(tab.label) },
                    )
                }
            }
        },
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background,
        ) {
            NavHost(
                navController = navController,
                startDestination = ExercisesTab,
            ) {
                navigation<ExercisesTab>(startDestination = ExercisesList) {
                    composable<ExercisesList> {
                        ExercisesScreen(
                            onExerciseSelected = { exerciseId ->
                                navController.navigate(SetLogging(exerciseId))
                            },
                        )
                    }
                    composable<SetLogging> { backStackEntry ->
                        val route: SetLogging = backStackEntry.toRoute()
                        LoggingScreen(
                            exerciseId = route.exerciseId,
                            onBack = { navController.popBackStack() },
                        )
                    }
                }
                composable<HistoryTab> {
                    HistoryScreen()
                }
            }
        }
    }
}

private data class Tab(val label: String, val route: Any)
