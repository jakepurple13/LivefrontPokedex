package com.programmersbox.livefrontpokedex

import android.content.Context
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
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
        composeTestRule.onNodeWithText("Pikachu", ignoreCase = true)
            .assertExists()
    }

    @Test
    fun navigateBackFromDetails() {
        composeTestRule.onNodeWithText("pikachu", ignoreCase = true)
            .performClick()
        composeTestRule
            .onNodeWithTag(Tags.BACK_BUTTON)
            .performClick()
        isDisplayingEntries()
    }

    @Test
    fun canSearchForPikachu() {
        composeTestRule.onNode(
            SemanticsMatcher.expectValue(
                SemanticsProperties.ContentDescription,
                listOf(appContext.getString(R.string.search_icon_entries))
            ),
            useUnmergedTree = true
        ).performClick()
        composeTestRule.onNodeWithText(
            appContext.getString(R.string.pokedex_title),
            useUnmergedTree = true
        )
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
    }
}