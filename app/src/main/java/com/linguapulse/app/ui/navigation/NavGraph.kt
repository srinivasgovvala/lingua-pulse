package com.linguapulse.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.linguapulse.app.domain.repository.LessonRepository
import com.linguapulse.app.domain.repository.OfflineQueueRepository
import com.linguapulse.app.domain.repository.UserProgressRepository
import com.linguapulse.app.domain.usecase.ProcessLessonCompletionUseCase
import com.linguapulse.app.ui.screens.home.HomeScreen
import com.linguapulse.app.ui.screens.home.HomeViewModel
import com.linguapulse.app.ui.screens.profile.ProfileScreen
import com.linguapulse.app.ui.screens.profile.ProfileViewModel
import com.linguapulse.app.ui.screens.review.ReviewScreen
import com.linguapulse.app.ui.screens.review.ReviewViewModel
import com.linguapulse.app.ui.screens.session.SessionScreen
import com.linguapulse.app.ui.screens.session.SessionViewModel
import com.linguapulse.app.ui.theme.PulseGreen
import com.linguapulse.app.ui.theme.PulseSurface
import com.linguapulse.app.ui.theme.PulseText

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    data object Home : Screen("home", "Learn", Icons.Default.Home)
    data object Review : Screen("review", "Review", Icons.Default.FitnessCenter)
    data object Profile : Screen("profile", "Profile", Icons.Default.Person)
    data object Session : Screen("session/{lessonId}?isPractice={isPractice}", "Session") {
        fun createRoute(lessonId: String, isPractice: Boolean = false) = "session/$lessonId?isPractice=$isPractice"
    }
}

@Composable
fun LinguaPulseNavGraph(
    lessonRepository: LessonRepository,
    userProgressRepository: UserProgressRepository,
    offlineQueueRepository: OfflineQueueRepository,
    processLessonCompletionUseCase: ProcessLessonCompletionUseCase,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomBarScreens = listOf(Screen.Home, Screen.Review, Screen.Profile)
    val showBottomBar = currentRoute in bottomBarScreens.map { it.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(containerColor = PulseSurface) {
                    bottomBarScreens.forEach { screen ->
                        val isSelected = currentRoute == screen.route
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { screen.icon?.let { Icon(it, contentDescription = null) } },
                            label = { Text(screen.title) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = PulseGreen,
                                selectedTextColor = PulseGreen,
                                indicatorColor = PulseSurface
                            )
                        )
                    }
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                val homeViewModel = remember {
                    HomeViewModel(
                        lessonRepository = lessonRepository,
                        userProgressRepository = userProgressRepository,
                        offlineQueueRepository = offlineQueueRepository
                    )
                }
                HomeScreen(
                    viewModel = homeViewModel,
                    onStartLesson = { lessonId ->
                        navController.navigate(Screen.Session.createRoute(lessonId, false))
                    },
                    onStartReview = {
                        navController.navigate(Screen.Session.createRoute("practice_review", true))
                    },
                    onNavigateToProfile = {
                        navController.navigate(Screen.Profile.route)
                    }
                )
            }

            composable(Screen.Review.route) {
                val reviewViewModel = remember {
                    ReviewViewModel(userProgressRepository = userProgressRepository)
                }
                ReviewScreen(
                    viewModel = reviewViewModel,
                    onStartPracticeSession = {
                        navController.navigate(Screen.Session.createRoute("practice_review", true))
                    }
                )
            }

            composable(Screen.Profile.route) {
                val profileViewModel = remember {
                    ProfileViewModel(userProgressRepository = userProgressRepository)
                }
                ProfileScreen(viewModel = profileViewModel)
            }

            composable(
                route = Screen.Session.route,
                arguments = listOf(
                    navArgument("lessonId") { type = NavType.StringType },
                    navArgument("isPractice") {
                        type = NavType.BoolType
                        defaultValue = false
                    }
                )
            ) { backStackEntry ->
                val lessonId = backStackEntry.arguments?.getString("lessonId") ?: "lesson_es_1_1"
                val isPractice = backStackEntry.arguments?.getBoolean("isPractice") ?: false

                val sessionViewModel = remember(lessonId, isPractice) {
                    SessionViewModel(
                        lessonId = lessonId,
                        isPracticeMode = isPractice,
                        lessonRepository = lessonRepository,
                        userProgressRepository = userProgressRepository,
                        processLessonCompletionUseCase = processLessonCompletionUseCase
                    )
                }

                SessionScreen(
                    viewModel = sessionViewModel,
                    onFinishSession = { navController.popBackStack() },
                    onNavigateToReview = {
                        navController.popBackStack()
                        navController.navigate(Screen.Session.createRoute("practice_review", true))
                    }
                )
            }
        }
    }
}
