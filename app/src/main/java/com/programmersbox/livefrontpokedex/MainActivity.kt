package com.programmersbox.livefrontpokedex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.programmersbox.livefrontpokedex.screens.detail.PokedexDetail
import com.programmersbox.livefrontpokedex.screens.entries.PokedexEntries
import com.programmersbox.livefrontpokedex.ui.theme.LivefrontPokedexTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LivefrontPokedexTheme {
                val windowSize = calculateWindowSizeClass(activity = this)
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = PokedexScreen.PokedexEntries.route,
                    enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start) },
                    exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start) },
                    popEnterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End) },
                    popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End) },
                ) {

                    composable(
                        route = PokedexScreen.PokedexEntries.route
                    ) {
                        PokedexEntries(
                            onDetailNavigation = { navController.navigate(PokedexScreen.PokedexDetail(it)) },
                            isHorizontalOrientation = windowSize.widthSizeClass == WindowWidthSizeClass.Expanded
                        )
                    }

                    composable(
                        route = PokedexScreen.PokedexDetail.route,
                        arguments = listOf(
                            navArgument("id") {
                                type = NavType.StringType
                            }
                        )
                    ) {
                        PokedexDetail(
                            onBackPress = { navController.popBackStack() },
                        )
                    }
                }
            }
        }
    }
}