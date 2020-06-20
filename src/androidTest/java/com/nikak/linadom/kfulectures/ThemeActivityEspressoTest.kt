package com.nikak.linadom.kfulectures

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.rule.ActivityTestRule
import com.nikak.linadom.kfulectures.activities.PDFActivity
import com.nikak.linadom.kfulectures.activities.ThemeActivity
import org.hamcrest.CoreMatchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class ThemeActivityEspressoTest {
    private var id: String = "-LwSsq07RycQwnT3LycS"
    private var subjectTitle: String = "Медведева О.А."
    private var subjectTeacher: String = "Спецификация программных систем"

    @get:Rule
    var mActivityRule = ActivityTestRule<ThemeActivity>(ThemeActivity::class.java, false, false)

    //чтобы заработала проверки открытия activity
    @Rule
    @JvmField
    var intentActivity = ActivityTestRule<PDFActivity>(PDFActivity::class.java, false, false)

    @Before
    @Throws(Exception::class)
    fun setUp() {
        Intents.init()
        //обращаемся к базе данных
        var intent = Intent()
        intent.putExtra("SUBJECT_ID", id)
        intent.putExtra("SUBJECT_TITLE", subjectTitle)
        intent.putExtra("SUBJECT_TEACHER", subjectTeacher)
        mActivityRule.launchActivity(intent)

    }

    @After
    fun cleanUp() {
        Intents.release()
    }

    @Test
    @Throws(Exception::class)
    fun testOnItemClick() {
        Thread.sleep(2000)
        Espresso.onData(CoreMatchers.anything()).
            inAdapterView(ViewMatchers.withId(R.id.themeListView)).atPosition(1)
            .perform(
                ViewActions.click()
            )
        intended(hasComponent(PDFActivity::class.java.name))

    }


    @Test
    @Throws(Exception::class)
    fun checkAboutAppButton() {

        // Open the options menu OR open the overflow menu, depending on whether
        // the device has a hardware or software overflow menu button.
        Espresso.openActionBarOverflowOrOptionsMenu(
            ApplicationProvider.getApplicationContext<Context>()
        )

        // Click the item.
        Espresso.onView(ViewMatchers.withText(R.string.about_app))
            .perform(ViewActions.click())

        // Verify that we have really clicked on the icon by checking
        // the TextView content.
        Espresso.onView(ViewMatchers.withText(R.string.about_app_message))
            .check(ViewAssertions.matches(isDisplayed()))
    }


    @Test
    @Throws(Exception::class)
    fun checkOkButtonAlertDialog() {

        // Open the options menu OR open the overflow menu, depending on whether
        // the device has a hardware or software overflow menu button.
        Espresso.openActionBarOverflowOrOptionsMenu(
            ApplicationProvider.getApplicationContext<Context>()
        )

        // Click the item.
        Espresso.onView(ViewMatchers.withText(R.string.about_app))
            .perform(ViewActions.click())

        // Verify that we have really clicked on the icon by checking
        // the TextView content.
        Espresso.onView(ViewMatchers.withText(R.string.about_app_message))
            .check(ViewAssertions.matches(isDisplayed()))

        Espresso.onView(ViewMatchers.withText(R.string.Ok_button)).inRoot(isDialog())
            .check(ViewAssertions.matches(isDisplayed())).perform(ViewActions.click())


    }

    @Test
    @Throws(Exception::class)
    fun checkWriteButtonAlertDialog() {

        // Open the options menu OR open the overflow menu, depending on whether
        // the device has a hardware or software overflow menu button.
        Espresso.openActionBarOverflowOrOptionsMenu(
            ApplicationProvider.getApplicationContext<Context>()
        )

        // Click the item.
        Espresso.onView(ViewMatchers.withText(R.string.about_app))
            .perform(ViewActions.click())

        // Verify that we have really clicked on the icon by checking
        // the TextView content.
        Espresso.onView(ViewMatchers.withText(R.string.about_app_message))
            .check(ViewAssertions.matches(isDisplayed()))

        Espresso.onView(ViewMatchers.withText(R.string.write_button)).inRoot(isDialog())
            .check(ViewAssertions.matches(isDisplayed())).perform(ViewActions.click())

        intended(IntentMatchers.toPackage("com.google.android.gm"))

    }


    @Test
    @Throws(Exception::class)
    fun testClickSearchIcon() {
        Espresso.onView((ViewMatchers.withId(R.id.app_bar_search)))
            .perform(ViewActions.click())

    }


}