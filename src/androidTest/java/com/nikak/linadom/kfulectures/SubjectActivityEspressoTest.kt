package com.nikak.linadom.kfulectures

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.view.View
import android.widget.SearchView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.UiController
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.matcher.IntentMatchers.toPackage
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import com.nikak.linadom.kfulectures.activities.SubjectActivity
import com.nikak.linadom.kfulectures.activities.ThemeActivity
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.ViewAction
import org.hamcrest.CoreMatchers.*
import java.util.regex.Matcher
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.pressImeActionButton
import androidx.test.espresso.action.ViewActions.typeText
import android.widget.EditText


class SubjectActivityEspressoTest {
    private var id: String = "-LwSsq07RycQwnT3LycS"
    private var subjectTeacher: String = "Медведева О.А."
    private var subjectTitle: String = "Спецификация программных систем"

    @get:Rule
    var mActivityRule = ActivityTestRule<SubjectActivity>(SubjectActivity::class.java)


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
    fun checkAboutAppButton() {
        openActionBarOverflowOrOptionsMenu(
            ApplicationProvider.getApplicationContext<Context>()
        )
        onView(withText(R.string.about_app))
            .perform(click())
        onView(withText(R.string.about_app_message)).check(matches(isDisplayed()))
    }


    @Test
    @Throws(Exception::class)
    fun checkOkButtonAlertDialog() {

        openActionBarOverflowOrOptionsMenu(
            ApplicationProvider.getApplicationContext<Context>()
        )

        Espresso.onView(withText(R.string.about_app))
            .perform(click())


        Espresso.onView(withText(R.string.Ok_button)).inRoot(RootMatchers.isDialog())
            .check(matches(isDisplayed())).perform(click())


    }


    @Test
    @Throws(Exception::class)
    fun testClickSearchItem() {
        onView((withId(R.id.app_bar_search)))
            .perform(click())
    }


    @Test
    @Throws(Exception::class)
    fun testOnItemClick() {
        Thread.sleep(5000)
        //нажатие на предмет из списка
        onData(anything()).inAdapterView(withId(R.id.subjectListView)).atPosition(1).perform(click())
        //проеврка, что идет перенаправление на activity с темами лекций
        intended(hasComponent(ThemeActivity::class.java.name))
    }


    @Test
    @Throws(Exception::class)
    fun checkWriteButtonAlertDialog() {

        openActionBarOverflowOrOptionsMenu(
            ApplicationProvider.getApplicationContext<Context>()
        )
        Espresso.onView(withText(R.string.about_app))
            .perform(click())
        Espresso.onView(withText(R.string.about_app_message))
            .check(matches(isDisplayed()))

        Espresso.onView(withText(R.string.write_button)).inRoot(RootMatchers.isDialog())
            .check(matches(isDisplayed())).perform(click())

        intended(toPackage("com.google.android.gm"))

    }


    fun isConnected(context: Context): Boolean {
        var connMan = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var networkinfo = connMan.activeNetworkInfo
        return networkinfo != null && networkinfo.isConnected

    }

    @Test
    @Throws(Exception::class)
    fun checkInternet() {
        assertTrue(isConnected(mActivityRule.activity))
    }



    @Test
    @Throws(Exception::class)
    fun testSearch() {
        onView((withId(R.id.app_bar_search)))
            .perform(click()).perform(typeSearchViewText("cпе"))

    }


    fun typeSearchViewText(text: String): ViewAction {
        return object : ViewAction {
            override fun getDescription(): String {
                return "cпе"
            }

            override fun getConstraints(): org.hamcrest.Matcher<View> {
                return allOf(isDisplayed(), isAssignableFrom(SearchView::class.java))
            }

            override fun perform(uiController: UiController?, view: View?) {
                (view as SearchView).setQuery(text, false)
            }
        }
    }

    @Test
    @Throws(Exception::class)
    fun testItemNotFound() {
        onView(withId(R.id.app_bar_search)).perform(click()).perform(typeSearchViewText("Нет"))
        Thread.sleep(1000)
        onView(withId(R.id.subjectListView)).check(matches(isDisplayed()))
    }


}










