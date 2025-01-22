package com.example.promptsharepro22;

import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.promptsharepro22.ui.LoginActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;

@RunWith(AndroidJUnit4.class)
public class PostBlackBoxTests {

    @Rule
    public ActivityScenarioRule<LoginActivity> loginActivityScenarioRule = new ActivityScenarioRule<>(LoginActivity.class);

    @Before
    public void resetState() throws InterruptedException {
        // Log in and navigate to user's posts page
        loginAsTestUser();
        onView(withId(R.id.nav_profile)).perform(click());
        onView(withId(R.id.viewAllPostsButton)).perform(click());

        // Delete all posts
        Thread.sleep(1000);
        try {
            while (true) {
                onView(withId(R.id.userPostsRecyclerView))
                        .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
                onView(withId(R.id.deletePostButton)).perform(click());
                onView(withText("Delete")).perform(click());
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            // No more items to delete; continue
        }
    }

//    @After
//    public void cleanupState() throws InterruptedException {
//        resetState(); // Ensure state is clean after each test
//    }

    @Test
    public void testCreatePost() throws InterruptedException {
        // Step 1: Navigate to create post
        onView(withId(R.id.nav_add_post)).perform(click());

        // Step 2: Fill post details
        onView(withId(R.id.titleEditText)).perform(typeText("Create Post Test"), closeSoftKeyboard());
        onView(withId(R.id.descriptionEditText)).perform(typeText("This is a test post creation"), closeSoftKeyboard());
        onView(withId(R.id.kind1Button)).perform(click()); // Select LLM Kind

        // Step 3: Submit post
        onView(withId(R.id.createPostButton)).perform(click());

        // Step 4: Verify post is displayed
        onView(withId(R.id.nav_profile)).perform(click());
        onView(withId(R.id.viewAllPostsButton)).perform(click());
        Thread.sleep(1000);
        onView(withText("Create Post Test")).check(matches(isDisplayed()));
    }

    @Test
    public void testEditPost() throws InterruptedException {
        // Step 1: Create a post
        onView(withId(R.id.nav_add_post)).perform(click());
        onView(withId(R.id.titleEditText)).perform(typeText("Create Post Test"), closeSoftKeyboard());
        onView(withId(R.id.descriptionEditText)).perform(typeText("This is a test post creation"), closeSoftKeyboard());
        onView(withId(R.id.kind1Button)).perform(click());
        onView(withId(R.id.createPostButton)).perform(click());

        // Step 2: Navigate to the user posts page
        onView(withId(R.id.nav_profile)).perform(click());
        onView(withId(R.id.viewAllPostsButton)).perform(click());
        Thread.sleep(1000);

        // Step 3: Scroll to and click the last item in the RecyclerView
        onView(withId(R.id.userPostsRecyclerView))
                .perform(RecyclerViewActions.actionOnItem(
                        hasDescendant(withText("Create Post Test")),
                        click()
                ));

        // Step 4: Update post details
        onView(withId(R.id.titleEditText)).perform(clearText(), typeText("Edit Post Test"), closeSoftKeyboard());
        onView(withId(R.id.descriptionEditText)).perform(clearText(), typeText("This is an edited post"), closeSoftKeyboard());

        // Step 5: Submit changes
        onView(withId(R.id.editPostButton)).perform(click());

        // Step 6: Verify updated post
        Thread.sleep(1000);
        onView(withText("Edit Post Test")).check(matches(isDisplayed()));
    }

    @Test
    public void testDeletePost() throws InterruptedException {
        // Step 1: Create a post
        onView(withId(R.id.nav_add_post)).perform(click());
        onView(withId(R.id.titleEditText)).perform(typeText("Temp Post"), closeSoftKeyboard());
        onView(withId(R.id.descriptionEditText)).perform(typeText("Temporary post for delete test"), closeSoftKeyboard());
        onView(withId(R.id.kind1Button)).perform(click());
        onView(withId(R.id.createPostButton)).perform(click());

        // Step 2: Navigate to the user posts page
        onView(withId(R.id.nav_profile)).perform(click());
        onView(withId(R.id.viewAllPostsButton)).perform(click());
        Thread.sleep(1000);

        // Step 3: Scroll to and click the last item in the RecyclerView
        onView(withId(R.id.userPostsRecyclerView))
            .perform(RecyclerViewActions.actionOnItem(
                    hasDescendant(withText("Temp Post")),
                    click()
            ));

        // Step 4: Delete post
        onView(withId(R.id.deletePostButton)).perform(click());

        // Step 5: Confirm deletion
        onView(withText("Delete")).perform(click());

        // Step 6: Verify post is deleted
        Thread.sleep(1000);
        onView(withText("Temp Post")).check(doesNotExist());
    }

    private void loginAsTestUser() {
        // Perform login action
        onView(withId(R.id.emailEditText)).perform(typeText("hjiang86@usc.edu"), closeSoftKeyboard());
        onView(withId(R.id.passwordEditText)).perform(typeText(";lkjasdf"), closeSoftKeyboard());
        onView(withId(R.id.loginButton)).perform(click());

        // Wait for login to complete
        onView(withId(R.id.nav_home)).check(matches(isDisplayed()));
    }
}
