package com.example.promptsharepro22;

import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.promptsharepro22.ui.LoginActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

import android.widget.EditText;

@RunWith(AndroidJUnit4.class)
public class SearchBlackBoxTests {

    @Rule
    public ActivityScenarioRule<LoginActivity> loginActivityScenarioRule = new ActivityScenarioRule<>(LoginActivity.class);

    @Test
    public void testSearchByLLMKind() throws InterruptedException {
        // Step 1: Log in as a test user
        loginAsTestUser();

        // Step 2: Navigate to Search page
        onView(withId(R.id.searchButton)).perform(click());

        // Step 3: Select "LLM Kind" from spinner
        onView(withId(R.id.searchTypeSpinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Search by LLM Kind"))).perform(click());

        // Step 4: Enter query "Claude" in the EditText
        onView(withId(R.id.searchEditText)).perform(typeText("Claude"), closeSoftKeyboard());

        // Step 5: Click the Search button
        onView(withId(R.id.searchButton)).perform(click());

        // Step 6: Verify search results
        Thread.sleep(1000); // Allow time for data to load
        onView(withId(R.id.searchResultsRecyclerView))
                .check(matches(hasDescendant(withText("Best Practices for Writing Clean Code"))));
    }

    @Test
    public void testSearchByTitle() throws InterruptedException {
        // Step 1: Log in as a test user
        loginAsTestUser();

        // Step 2: Navigate to Search page
        onView(withId(R.id.searchButton)).perform(click());

        // Step 3: Select "Title" from spinner
        onView(withId(R.id.searchTypeSpinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Search by Titles"))).perform(click());

        // Step 4: Enter query "Software Testing" in the EditText
        onView(withId(R.id.searchEditText)).perform(typeText("Software Testing"), closeSoftKeyboard());

        // Step 5: Click the Search button
        onView(withId(R.id.searchButton)).perform(click());

        // Step 6: Verify search results
        Thread.sleep(1000); // Allow time for data to load
        onView(withId(R.id.searchResultsRecyclerView))
                .check(matches(hasDescendant(withText("The Future of Software Testing with AI"))));
    }

    @Test
    public void testFullTextSearch() throws InterruptedException {
        // Step 1: Log in as a test user
        loginAsTestUser();

        // Step 2: Navigate to Search page
        onView(withId(R.id.searchButton)).perform(click());

        // Step 3: Select "Full Text" from spinner
        onView(withId(R.id.searchTypeSpinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Full Text Search"))).perform(click());

        // Step 4: Enter query "language model" in the EditText
        onView(withId(R.id.searchEditText)).perform(typeText("resolving merge conflicts"), closeSoftKeyboard());

        // Step 5: Click the Search button
        onView(withId(R.id.searchButton)).perform(click());

        // Step 6: Verify search results
        Thread.sleep(1000); // Allow time for data to load
        onView(withId(R.id.searchResultsRecyclerView))
                .check(matches(hasDescendant(withText("Understanding Git for Version Control"))));
    }

    @Test
    public void testSearchWithNoResults() throws InterruptedException {
        // Step 1: Log in as a test user
        loginAsTestUser();

        // Step 2: Navigate to Search page
        onView(withId(R.id.searchButton)).perform(click());

        // Step 3: Select "Full Text" from spinner
        onView(withId(R.id.searchTypeSpinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Search by Default"))).perform(click());

        // Step 4: Enter query "NonexistentPost" in the EditText
        onView(withId(R.id.searchEditText)).perform(typeText("NonexistentPost"), closeSoftKeyboard());

        // Step 5: Click the Search button
        onView(withId(R.id.searchButton)).perform(click());

        // Step 6: Verify no results
        Thread.sleep(1000); // Allow time for data to load
        onView(withId(R.id.searchResultsRecyclerView)).check(matches(hasChildCount(0)));
    }

    @Test
    public void testDefaultSearchType() throws InterruptedException {
        // Step 1: Log in as a test user
        loginAsTestUser();

        // Step 2: Navigate to Search page
        onView(withId(R.id.searchButton)).perform(click());

        // Step 3: Ensure spinner is set to default ("All")

        // Step 4: Enter query "ChatGPT" in the EditText
        onView(withId(R.id.searchEditText)).perform(typeText("Agile, Waterfall, and DevOps"), closeSoftKeyboard());

        // Step 5: Click the Search button
        onView(withId(R.id.searchButton)).perform(click());

        // Step 6: Verify results contain posts with "ChatGPT" in any field
        Thread.sleep(1000); // Allow time for data to load
        onView(withId(R.id.searchResultsRecyclerView))
                .check(matches(hasDescendant(withText("Choosing the Right Software Development Lifecycle (SDLC)"))));
    }

    private void loginAsTestUser() {
        // Log in as the test user
        onView(withId(R.id.emailEditText)).perform(typeText("logansong@usc.edu"), closeSoftKeyboard());
        onView(withId(R.id.passwordEditText)).perform(typeText("11111111"), closeSoftKeyboard());
        onView(withId(R.id.loginButton)).perform(click());

        // Wait for login to complete
        onView(withId(R.id.nav_home)).check(matches(isDisplayed()));
    }
}
