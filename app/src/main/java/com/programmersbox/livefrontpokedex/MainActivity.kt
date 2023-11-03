package com.programmersbox.livefrontpokedex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LivefrontPokedexTheme {
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
                            onDetailNavigation = { navController.navigate(PokedexScreen.PokedexDetail(it)) }
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