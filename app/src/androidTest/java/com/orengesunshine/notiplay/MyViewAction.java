package com.orengesunshine.notiplay;

import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import android.support.test.espresso.action.ViewActions;
import android.view.View;
import android.widget.EditText;

import org.hamcrest.Matcher;

/**
 * Created by hayatomoritani on 8/3/17.
 */

class MyViewAction {

    static ViewAction clickChildViewWithId(final int id) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return null;
            }

            @Override
            public String getDescription() {
                return "Click on a child view with specified id.";
            }

            @Override
            public void perform(UiController uiController, View view) {
                View v = view.findViewById(id);
                v.performClick();
            }
        };
    }

}