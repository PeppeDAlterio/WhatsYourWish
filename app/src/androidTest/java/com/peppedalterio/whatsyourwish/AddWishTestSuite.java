package com.peppedalterio.whatsyourwish;


import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.rule.GrantPermissionRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.peppedalterio.whatsyourwish.pojo.WishStrings;
import com.peppedalterio.whatsyourwish.utils.ToastMatcher;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.*;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class AddWishTestSuite {

    private static final String TITOLO_E_DESCRIZIONE_TEST_WISH = "TitoloTest\r\nDescrizione prova";

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.READ_CONTACTS",
                    "android.permission.READ_PHONE_STATE");

    @Test
    public void addWishThatAlreadyExists() {

        swipeToMyWishlist();

        addAWish(TITOLO_E_DESCRIZIONE_TEST_WISH);

        onView(withText(TITOLO_E_DESCRIZIONE_TEST_WISH)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

        addAWish(TITOLO_E_DESCRIZIONE_TEST_WISH);

        onView(withText(R.string.toast_wish_exists)).inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));

        //clean wishlist
        pressBack();

        onView(withText(TITOLO_E_DESCRIZIONE_TEST_WISH)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

        removeAWish(TITOLO_E_DESCRIZIONE_TEST_WISH);

        onView(withText(TITOLO_E_DESCRIZIONE_TEST_WISH)).check(doesNotExist());


    }

    @Test
    public void addWishTestAddAndDelete() {

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        swipeToMyWishlist();

        onView(withText(TITOLO_E_DESCRIZIONE_TEST_WISH)).check(doesNotExist());

        addAWish(TITOLO_E_DESCRIZIONE_TEST_WISH);


        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        removeAWish(TITOLO_E_DESCRIZIONE_TEST_WISH);

        onView(withText(TITOLO_E_DESCRIZIONE_TEST_WISH)).check(doesNotExist());

    }

    private void removeAWish(String wishData) {
        onView(withText(wishData)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

        ViewInteraction addedItem = onView(withText(wishData));
        addedItem.perform(longClick());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(android.R.id.button1), withText(R.string.dialog_yes),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.buttonPanel),
                                        0),
                                3)));
        appCompatButton2.perform(scrollTo(), click());
    }

    private void addAWish(String wishData) {

        ViewInteraction floatingActionButton = onView(
                allOf(withId(R.id.floatingActionButton),
                        childAtPosition(
                                withParent(withId(R.id.container)),
                                0),
                        isDisplayed()));
        floatingActionButton.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.wishtitle),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText.perform(click());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.wishtitle),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText2.perform(replaceText(wishData.split(WishStrings.SEPARATOR_TOKEN)[0]), closeSoftKeyboard());

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.wishtitle), withText(wishData.split(WishStrings.SEPARATOR_TOKEN)[0]),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText3.perform(pressImeActionButton());

        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.wishdescription),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                2),
                        isDisplayed()));
        appCompatEditText4.perform(replaceText(wishData.split(WishStrings.SEPARATOR_TOKEN)[1]), closeSoftKeyboard());

        ViewInteraction appCompatEditText5 = onView(
                allOf(withId(R.id.wishdescription), withText(wishData.split(WishStrings.SEPARATOR_TOKEN)[1]),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                2),
                        isDisplayed()));
        appCompatEditText5.perform(pressImeActionButton());

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.addwishbutton), withText(R.string.confirm_button),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                4),
                        isDisplayed()));
        appCompatButton.perform(click());

    }

    private void swipeToMyWishlist() {

        ViewInteraction tabView = onView(
                allOf(withContentDescription(R.string.wishlisttitletab),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.tabs),
                                        0),
                                1),
                        isDisplayed()));
        tabView.perform(click());

        ViewInteraction viewPager = onView(
                allOf(withId(R.id.container),
                        childAtPosition(
                                allOf(withId(R.id.main_content),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                1),
                        isDisplayed()));


        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
