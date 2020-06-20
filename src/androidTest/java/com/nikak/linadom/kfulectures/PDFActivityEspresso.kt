package com.nikak.linadom.kfulectures

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.rule.ActivityTestRule
import com.nikak.linadom.kfulectures.activities.PDFActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PDFActivityEspresso {
    private var id: String = "-LwSsq07RycQwnT3LycS"
    private var subjectTitle: String = "Медведева О.А."
    private var subjectTeacher: String = "Спецификация программных систем"
    private var idTheme: String = "-LzlS9v6MFLiqPt6DzqF"
    private var themeTitle: String = "Спецификация\nФункциональные и нефункциональные требования"
    private var themeNumber: String = "2"

    @get:Rule
    var mActivityRule = ActivityTestRule<PDFActivity>(PDFActivity::class.java,false,false)



    @Before
    @Throws(Exception::class)
    fun setUp() {
        Intents.init()
        //обращаемся к базе данных
        var intent = Intent()
        intent.putExtra("SUBJECT_ID", id)
        intent.putExtra("SUBJECT_TITLE", subjectTitle)
        intent.putExtra("SUBJECT_TEACHER", subjectTeacher)
        intent.putExtra("THEME_ID", idTheme)
        intent.putExtra("THEME_TITLE", themeTitle)
        intent.putExtra("THEME_NUMBER", themeNumber)
        mActivityRule.launchActivity(intent)

    }

    @After
    fun cleanUp() {
        Intents.release()
    }

    @Test
    @Throws(Exception::class)
    fun testDownloadIcon() {
        Thread.sleep(5000)
        Espresso.onView((ViewMatchers.withId(R.id.saveLecture)))
            .perform(ViewActions.click())

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
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
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
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withText(R.string.Ok_button)).inRoot(RootMatchers.isDialog())
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed())).perform(ViewActions.click())


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
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withText(R.string.write_button)).inRoot(RootMatchers.isDialog())
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed())).perform(ViewActions.click())

        Intents.intended(IntentMatchers.toPackage("com.google.android.gm"))

    }
}