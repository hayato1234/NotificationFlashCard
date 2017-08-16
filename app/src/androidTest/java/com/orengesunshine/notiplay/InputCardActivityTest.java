package com.orengesunshine.notiplay;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.ViewAssertion;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.action.ViewActions;
import android.support.test.rule.ActivityTestRule;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.InstrumentationRegistry.getContext;
import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.action.ViewActions.typeTextIntoFocusedView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;
import static org.junit.Assert.assertThat;

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

/**
 * Created by hayatomoritani on 8/3/17.
 */
public class InputCardActivityTest {

    @Rule
    public ActivityTestRule<InputCardActivity> testRule = new ActivityTestRule<InputCardActivity>(InputCardActivity.class) {
        @Override
        protected Intent getActivityIntent() {
            Context targetContext = getInstrumentation().getTargetContext();
            Intent result = new Intent(targetContext, InputCardActivity.class);
            result.putExtra(InputCardActivity.FOLDER_NAME, "testing folder");
            return result;
        }
    };

    private String testText = "some text";

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testEditText() {
        //make sure keyboard language is English!
        Espresso.onView(withId(R.id.edit_recycler_view)).perform(RecyclerViewActions.scrollToPosition(0));
        Espresso.onView(withRecyclerView(R.id.edit_recycler_view).atPositionOnView(0, R.id.input_front_text)).perform(typeText(testText));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.edit_recycler_view)).check(matches(hasDescendant(withText(testText))));
    }

    @Test
    public void saveCards(){
        Espresso.onView(withRecyclerView(R.id.edit_recycler_view).atPositionOnView(0, R.id.input_front_text)).perform(typeText(testText));
        Espresso.openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        Espresso.onView(withText(R.string.save)).perform(click());
        Espresso.onView(allOf(withId(android.support.design.R.id.snackbar_text),withText(R.string.saved))).check(matches(isDisplayed()));
    }

    @Test
    public void addCard(){
        RecyclerView recyclerView = (RecyclerView) testRule.getActivity().findViewById(R.id.edit_recycler_view);
        int originalChildCount = recyclerView.getChildCount();
        Espresso.onView(withId(R.id.input_add_button)).perform(click());
        Espresso.onView(withId(R.id.edit_recycler_view)).check(new RecyclerViewItemCountAssertion(originalChildCount));
        Espresso.onView(withRecyclerView(R.id.edit_recycler_view).atPositionOnView(originalChildCount,R.id.input_front_text)).check(matches(withText("")));
    }

    @Test
    public void editFolderTitle(){
        Espresso.openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        Espresso.onView(withText(R.string.menu_edit_folder_name)).perform(click());
        Espresso.onView(withId(R.id.change_folder_title)).perform(typeText(testText));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withText(R.string.change)).perform(click());
        matchToolbarTitle(testText);
    }

    private static ViewInteraction matchToolbarTitle(String title){
        return Espresso.onView(allOf(isAssignableFrom(TextView.class),
                withParent(isAssignableFrom(Toolbar.class))
                )).check(matches(withText(title)));
    }

    private static MyViewActionMatcher withRecyclerView(final int recyclerViewId) {
        return new MyViewActionMatcher(recyclerViewId);
    }

    public class RecyclerViewItemCountAssertion implements ViewAssertion {
        private final int expectedCount;

        public RecyclerViewItemCountAssertion(int expectedCount) {
            this.expectedCount = expectedCount+1;
        }

        @Override
        public void check(View view, NoMatchingViewException noViewFoundException) {
            if (noViewFoundException != null) {
                throw noViewFoundException;
            }

            RecyclerView recyclerView = (RecyclerView) view;
            RecyclerView.Adapter adapter = recyclerView.getAdapter();
            assertThat(adapter.getItemCount(), is(expectedCount));
        }
    }

    @After
    public void tearDown() throws Exception {

    }
}