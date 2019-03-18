package com.peppedalterio.whatsyourwish;


import android.annotation.SuppressLint;
import android.support.test.espresso.DataInteraction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.peppedalterio.whatsyourwish.R;
import com.peppedalterio.whatsyourwish.activity.MainActivity;
import com.peppedalterio.whatsyourwish.utils.ToastMatcher;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class WishAssignTestSuite {

    private static final String TEST_CONTACT_NUMBER = "01234567890123";
    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    /**
     * Try to assign to a wish already assigned to another person
     */
    @Test
    public void alreadyAssignedTest() {
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.contactssearchbar),
                        childAtPosition(
                                withParent(withId(R.id.container)),
                                1),
                        isDisplayed()));
        appCompatEditText.perform(replaceText("Test contact"), closeSoftKeyboard());

        DataInteraction appCompatTextView = onData(anything())
                .inAdapterView(allOf(withId(R.id.contactlistview),
                        childAtPosition(
                                withClassName(is("android.support.constraint.ConstraintLayout")),
                                0)))
                .atPosition(0);
        appCompatTextView.perform(click());

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction textView = onView(withId(R.id.userwishlistnumber));
        textView.check(matches(withText(TEST_CONTACT_NUMBER)));

        DataInteraction appCompatTextView2 = onData(anything())
                .inAdapterView(allOf(withId(R.id.userwishlist),
                        childAtPosition(
                                withClassName(is("android.support.constraint.ConstraintLayout")),
                                0)))
                .atPosition(1);

        appCompatTextView2.check(matches(withText(containsString("TestItem2"))));
        appCompatTextView2.perform(longClick());

        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withText(R.string.toast_assigned_to_another_one)).inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));

        pressBack();
    }

    /**
     * Assign and de-assign to the 'TestItem3' of a test contact
     */
    @Test
    public void wishAssignTest() {

        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.contactssearchbar),
                        childAtPosition(
                                withParent(withId(R.id.container)),
                                1),
                        isDisplayed()));
        appCompatEditText.perform(replaceText("Test contact"), closeSoftKeyboard());

        DataInteraction appCompatTextView = onData(anything())
                .inAdapterView(allOf(withId(R.id.contactlistview),
                        childAtPosition(
                                withClassName(is("android.support.constraint.ConstraintLayout")),
                                0)))
                .atPosition(0);
        appCompatTextView.perform(click());

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction textView = onView(withId(R.id.userwishlistnumber));
        textView.check(matches(withText(TEST_CONTACT_NUMBER)));

        DataInteraction appCompatTextView2 = onData(anything())
                .inAdapterView(allOf(withId(R.id.userwishlist),
                        childAtPosition(
                                withClassName(is("android.support.constraint.ConstraintLayout")),
                                0)))
                .atPosition(2);

        appCompatTextView2.check(matches(withText("TestItem3")));
        appCompatTextView2.perform(longClick());

        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withText(R.string.toast_self_assign)).inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));

        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }



        Date todayDate = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        String today = formatter.format(todayDate);

        appCompatTextView2.check(matches(withText(containsString(today))));

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        appCompatTextView2.perform(longClick());

        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withText(R.string.toast_remove_assignment)).inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));

        pressBack();
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
