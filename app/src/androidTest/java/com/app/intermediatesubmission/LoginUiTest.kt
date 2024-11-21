package com.app.intermediatesubmission

import android.view.WindowManager
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.Root
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.app.intermediatesubmission.ui.login.LoginActivity
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class LoginActivityTest {

    private val idlingResource = SimpleIdlingResource()

    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(idlingResource)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(idlingResource)
    }

    @get:Rule
    val activityRule = ActivityScenarioRule(LoginActivity::class.java)

    @Test
    fun testEmptyEmailShowsError() {

        onView(withId(R.id.edEmail))
            .perform(clearText(), closeSoftKeyboard())

        onView(withId(R.id.edPassword))
            .perform(typeText("password123"), closeSoftKeyboard())

        onView(withId(R.id.loginButton))
            .perform(click())

        Thread.sleep(250)

        onView(withText("Email Masih Kosong"))
            .inRoot(ToastMatcher())
            .check(matches(isDisplayed()))
    }

    @Test
    fun testEmptyPasswordShowsError() {

        onView(withId(R.id.edEmail))
            .perform(typeText("test@email.com"), closeSoftKeyboard())

        onView(withId(R.id.edPassword))
            .perform(clearText(), closeSoftKeyboard())

        onView(withId(R.id.loginButton))
            .perform(click())

        Thread.sleep(250)

        onView(withText("Password masih kosong"))
            .inRoot(ToastMatcher())
            .check(matches(isDisplayed()))
    }

    @Test
    fun testLoginFailedShowsDialog() {
        onView(withId(R.id.edEmail))
            .perform(typeText("wrong@email.com"), closeSoftKeyboard())
        onView(withId(R.id.edPassword))
            .perform(typeText("wrongpassword"), closeSoftKeyboard())

        onView(withId(R.id.loginButton))
            .perform(click())

        // Add waiting mechanism for the dialog
        onView(withText("Failed"))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
        onView(withText("Login Gagal"))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))

        onView(withText("OK"))
            .inRoot(isDialog())
            .perform(click())
    }

}

class ToastMatcher : TypeSafeMatcher<Root>() {

    override fun matchesSafely(root: Root): Boolean {
        val type = root.windowLayoutParams.get().type
        return type == WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
    }


    override fun describeTo(description: Description?) {
        description?.appendText("is toast")
    }
}