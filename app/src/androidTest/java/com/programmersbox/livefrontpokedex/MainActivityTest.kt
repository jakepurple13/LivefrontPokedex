package com.programmersbox.livefrontpokedex

import android.content.Context
import androidx.activity.compose.setContent
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.test.platform.app.InstrumentationRegistry
import com.programmersbox.livefrontpokedex.di.MockPokemonRepository
import com.programmersbox.livefrontpokedex.screens.detail.PokedexDetail
import com.programmersbox.livefrontpokedex.screens.entries.PokedexEntries
import com.programmersbox.livefrontpokedex.ui.theme.LivefrontPokedexTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@HiltAndroidTest
@RunWith(JUnit4::class)
class MainActivityTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val appContext: Context = InstrumentationRegistry.getInstrumentation().targetContext

    @OptIn(ExperimentalTestApi::class)
    @Before
    fun setup() {
        hiltRule.inject()
        composeTestRule.activity.setContent {
            val navController = rememberNavController()
            LivefrontPokedexTheme {
                NavHost(
                    navController = navController,
                    startDestination = PokedexScreen.PokedexEntries.route,
                ) {
                    composable(
                        route = PokedexScreen.PokedexEntries.route
                    ) {
                        PokedexEntries(
                            onDetailNavigation = { navController.navigate(PokedexScreen.PokedexDetail(it)) },
                            isHorizontalOrientation = false
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
        composeTestRule.waitUntilAtLeastOneExists(
            hasTestTag(Tags.POKEDEX_ENTRY_ENTRIES),
            5000
        )
    }

    @Test
    fun isDisplayingEntries() {
        composeTestRule.onNodeWithTag(Tags.POKEDEX_LIST_ENTRIES)
            .onChildren()
            .assertCountEquals(MockPokemonRepository.pokemonList.size)
    }

    @Test
    fun canClickOnPikachu() {
        composeTestRule.onNodeWithText(
            "Pikachu",
            ignoreCase = true,
            useUnmergedTree = true
        ).performClick()
        composeTestRule.onAllNodesWithText(
            "Pikachu",
            ignoreCase = true
        ).assertCountEquals(9)
    }

    @Test
    fun navigateBackFromDetails() {
        canClickOnPikachu()
        composeTestRule
            .onNodeWithTag(Tags.BACK_BUTTON)
            .performClick()
        isDisplayingEntries()
    }

    /*@Test
    fun canSearchForPikachu() {
        composeTestRule
            .onNodeWithTag(Tags.SEARCH_BAR_ENTRIES)
            .performClick()
        composeTestRule.onNodeWithTag(Tags.SEARCH_BAR_ENTRIES)
            .performTextInput("Pikachu")
        composeTestRule
            .onAllNodesWithTag(Tags.SEARCH_ITEM_ENTRIES)
            .assertCountEquals(2)
        composeTestRule
            .onAllNodesWithTag(Tags.SEARCH_ITEM_ENTRIES)
            .onFirst()
            .performClick()
        composeTestRule.onNodeWithTag(Tags.POKEDEX_LIST_ENTRIES)
            .onChildren()
            .assertCountEquals(2)
    }*/
}