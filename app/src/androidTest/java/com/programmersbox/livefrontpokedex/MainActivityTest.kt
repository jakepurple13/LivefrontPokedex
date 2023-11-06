package com.programmersbox.livefrontpokedex

import android.content.Context
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
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
        composeTestRule.waitUntilAtLeastOneExists(
            hasTestTag(Tags.POKEDEX_ENTRY_ENTRIES),
            5000
        )
    }

    @Test
    fun isDisplayingEntries() {
        composeTestRule.onNodeWithTag(Tags.POKEDEX_LIST_ENTRIES)
            .onChildren()
            .assertCountEquals(3)
    }

    @Test
    fun canClickOnPikachu() {
        composeTestRule.onNodeWithText("pikachu", ignoreCase = true)
            .performClick()
        composeTestRule
            .onAllNodesWithText("Pikachu", ignoreCase = true)
            .assertCountEquals(2)
    }

    @Test
    fun navigateBackFromDetails() {
        composeTestRule.onNodeWithText("Pikachu", ignoreCase = true)
            .assertIsDisplayed()
            .performClick()
        composeTestRule
            .onNodeWithContentDescription(appContext.getString(R.string.back_button))
            .assertIsDisplayed()
            .performClick()
        isDisplayingEntries()
    }

    @Test
    fun canSearchForPikachu() {
        composeTestRule.onNode(
            SemanticsMatcher.expectValue(
                SemanticsProperties.TestTag,
                Tags.SEARCH_BAR_ENTRIES
            ),
            useUnmergedTree = true
        ).performClick()
        composeTestRule.onNodeWithContentDescription(
            appContext.getString(androidx.compose.material3.R.string.search_bar_search),
            useUnmergedTree = true
        ).performTextInput("Pikachu")
        composeTestRule
            .onAllNodesWithTag(Tags.SEARCH_ITEM_ENTRIES)
            .assertCountEquals(2)
        composeTestRule
            .onAllNodesWithTag(Tags.SEARCH_ITEM_ENTRIES)
            .onFirst()
            .performClick()
        composeTestRule.onNodeWithContentDescription(
            appContext.getString(androidx.compose.material3.R.string.search_bar_search),
            useUnmergedTree = true
        ).performImeAction()
        composeTestRule.onNodeWithTag(Tags.POKEDEX_LIST_ENTRIES)
            .onChildren()
            .assertCountEquals(3)
    }
}