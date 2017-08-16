package com.orengesunshine.notiplay;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * Created by hayatomoritani on 8/10/17.
 */

public class MyViewActionMatcher {
    final int mRecyclerViewId;

    public MyViewActionMatcher(int recyclerViewId) {
        this.mRecyclerViewId = recyclerViewId;
    }

    public Matcher<View> atPosition(final int position) {
        return atPositionOnView(position, -1);
    }

    public Matcher<View> atPositionOnView(final int position, final int targetViewId) {

        return new TypeSafeMatcher<View>() {
            Resources resources = null;
            View childView;

            public void describeTo(Description description) {
                int id = targetViewId == -1 ? mRecyclerViewId : targetViewId;
                String idDescription = Integer.toString(id);
                if (this.resources != null) {
                    try {
                        idDescription = this.resources.getResourceName(id);
                    } catch (Resources.NotFoundException var4) {
                        idDescription = String.format("%s (resource name not found)", id);
                    }
                }

                description.appendText("with id: " + idDescription);
            }

            public boolean matchesSafely(View view) {

                this.resources = view.getResources();

                if (childView == null) {
                    RecyclerView recyclerView =
                            (RecyclerView) view.getRootView().findViewById(mRecyclerViewId);
                    if (recyclerView != null) {

                        childView = recyclerView.findViewHolderForAdapterPosition(position).itemView;
                    }
                    else {
                        return false;
                    }
                }

                if (targetViewId == -1) {
                    return view == childView;
                } else {
                    View targetView = childView.findViewById(targetViewId);
                    return view == targetView;
                }

            }
        };
    }
}
