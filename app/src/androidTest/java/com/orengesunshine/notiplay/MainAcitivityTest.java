package com.orengesunshine.notiplay;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.intent.matcher.ComponentNameMatchers.hasClassName;
import static android.support.test.espresso.intent.matcher.ComponentNameMatchers.hasMyPackageName;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.espresso.intent.Intents.intended;

import static org.hamcrest.Matchers.allOf;

/**
 * Created by hayatomoritani on 8/10/17.
 */

public class MainAcitivityTest {

    @Rule
    public IntentsTestRule testRule = new IntentsTestRule<>(MainActivity.class);

    @Test
    public void makeNewFolderAndStartInputWithExtra(){
        String testFolderName = "test folder2";
        Espresso.onView(withId(R.id.fab)).perform(click());
        Espresso.onView(withId(R.id.make_new_folder_input)).perform(typeText(testFolderName));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withText(R.string.ok)).check(matches(isDisplayed())).perform(click());

        intended(allOf(hasComponent(hasMyPackageName()),
                hasComponent(hasClassName("com.orengesunshine.notiplay.InputCardActivity")),
                hasExtra(InputCardActivity.FOLDER_NAME,testFolderName)));
    }

    @Test
    public void editIntent(){
        Espresso.onView(withRecyclerView(R.id.main_recycler_view).atPositionOnView(0,R.id.edit_button)).perform(click());
        intended(allOf(hasComponent(hasMyPackageName()),hasComponent(hasClassName("com.orengesunshine.notiplay.InputCardActivity"))));
    }

    private static MyViewActionMatcher withRecyclerView(final int recyclerViewId) {
        return new MyViewActionMatcher(recyclerViewId);
    }
}
