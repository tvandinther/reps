package com.tvandinther.reps.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.tvandinther.reps.ui.exercises.EditExerciseScreen
import com.tvandinther.reps.ui.exercises.ExercisesScreen
import com.tvandinther.reps.ui.history.HistoryScreen
import com.tvandinther.reps.ui.logging.LoggingScreen
import com.tvandinther.reps.ui.settings.SettingsScreen
import com.tvandinther.reps.ui.theme.BarlowCondensedFamily
import com.tvandinther.reps.ui.theme.ColorBorder
import com.tvandinther.reps.ui.theme.ColorDivider
import com.tvandinther.reps.ui.theme.ColorInk4
import com.tvandinther.reps.ui.theme.ColorSignal
import com.tvandinther.reps.ui.theme.StyleTab
import kotlinx.serialization.Serializable

@Serializable object ExercisesTab
@Serializable object ExercisesList
@Serializable data class SetLogging(val exerciseId: Long)
@Serializable data class ExerciseEdit(val exerciseId: Long)
@Serializable object HistoryTab
@Serializable object SettingsTab

@Composable
fun RepsApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val tabs = listOf(
        Tab("Exercises", ExercisesTab),
        Tab("History",   HistoryTab),
        Tab("Settings",  SettingsTab),
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            RepsTabBar(
                tabs = tabs,
                isTabSelected = { tab ->
                    currentDestination?.hierarchy?.any {
                        it.route == tab.route::class.qualifiedName
                    } == true
                },
                onTabSelected = { tab ->
                    navController.navigate(tab.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
            )
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
                            onEditExercise = { exerciseId ->
                                navController.navigate(ExerciseEdit(exerciseId))
                            },
                        )
                    }
                    composable<ExerciseEdit> { backStackEntry ->
                        val route: ExerciseEdit = backStackEntry.toRoute()
                        EditExerciseScreen(
                            exerciseId = route.exerciseId,
                            onBack = { navController.popBackStack() },
                            onDeleted = { navController.popBackStack() },
                        )
                    }
                    composable<SetLogging> { backStackEntry ->
                        val route: SetLogging = backStackEntry.toRoute()
                        LoggingScreen(
                            exerciseId = route.exerciseId,
                            onBack = { navController.popBackStack() },
                            onEditExercise = { navController.navigate(ExerciseEdit(route.exerciseId)) },
                        )
                    }
                }
                composable<HistoryTab> {
                    HistoryScreen()
                }
                composable<SettingsTab> {
                    SettingsScreen()
                }
            }
        }
    }
}

@Composable
private fun RepsTabBar(
    tabs: List<Tab>,
    isTabSelected: (Tab) -> Boolean,
    onTabSelected: (Tab) -> Unit,
) {
    val signal = ColorSignal
    val divider = ColorDivider

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF060606))
            .navigationBarsPadding(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .drawBehind {
                    // 1px top border
                    drawLine(
                        color = divider,
                        start = Offset(0f, 0f),
                        end = Offset(size.width, 0f),
                        strokeWidth = 1.dp.toPx(),
                    )
                },
        ) {
            tabs.forEach { tab ->
                val selected = isTabSelected(tab)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable { onTabSelected(tab) }
                        .then(
                            if (selected) Modifier.drawBehind {
                                // 3px signal-orange top border on active tab
                                drawLine(
                                    color = signal,
                                    start = Offset(0f, 0f),
                                    end = Offset(size.width, 0f),
                                    strokeWidth = 3.dp.toPx(),
                                )
                            } else Modifier
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = tab.label.uppercase(),
                        style = StyleTab,
                        color = if (selected) ColorSignal else ColorInk4,
                    )
                }
            }
        }
    }
}

private data class Tab(val label: String, val route: Any)
