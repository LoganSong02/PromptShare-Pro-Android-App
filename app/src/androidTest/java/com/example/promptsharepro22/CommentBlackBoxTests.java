package com.example.promptsharepro22;

import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.promptsharepro22.ui.LoginActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.Rule;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;

@RunWith(AndroidJUnit4.class)
public class CommentBlackBoxTests {

    @Rule
    public ActivityScenarioRule<LoginActivity> loginActivityScenarioRule = new ActivityScenarioRule<>(LoginActivity.class);

    @Before
    public void resetState() throws InterruptedException {
        loginAsTestUser(); // Log in and navigate to user's posts page
        removeAllPosts(); // Delete all posts
        removeAllComments(); // Delete all comments

        // Create a post for testing comments
        onView(withId(R.id.nav_add_post)).perform(click());
        onView(withId(R.id.titleEditText)).perform(typeText("Test Post for Comments"), closeSoftKeyboard());
        onView(withId(R.id.descriptionEditText)).perform(typeText("This is a post for testing comments"), closeSoftKeyboard());
        onView(withId(R.id.kind1Button)).perform(click());
        onView(withId(R.id.createPostButton)).perform(click());
    }

    @Test
    public void testCreateComment() throws InterruptedException {
        // Step 1: Navigate to the home feed
        onView(withId(R.id.nav_home)).perform(click());

        // Step 2: Locate and click on the specific post in the home feed's RecyclerView
        onView(withId(R.id.postsRecyclerView))
                .perform(RecyclerViewActions.actionOnItem(
                        hasDescendant(withText("Test Post for Comments")),
                        click()
                ));

        // Step 3: Add a comment
        onView(withId(R.id.commentButton)).perform(click());
        onView(withId(R.id.commentEditText)).perform(typeText("This is a test comment"), closeSoftKeyboard());
        onView(withId(R.id.submitCommentButton)).perform(click());

        // Step 4: Verify the comment is displayed
        Thread.sleep(1000);
        onView(withText("This is a test comment")).check(matches(isDisplayed()));
    }

    @Test
    public void testEditComment() throws InterruptedException {
        // Step 1: Create a comment
        onView(withId(R.id.nav_home)).perform(click());
        onView(withId(R.id.postsRecyclerView))
                .perform(RecyclerViewActions.actionOnItem(
                        hasDescendant(withText("Test Post for Comments")),
                        click()
                ));
        onView(withId(R.id.commentButton)).perform(click());
        onView(withId(R.id.commentEditText)).perform(typeText("This is a test comment"), closeSoftKeyboard());
        onView(withId(R.id.submitCommentButton)).perform(click());

        // Step 2: Edit the comment
        onView(withId(R.id.nav_profile)).perform(click());
        onView(withId(R.id.viewAllCommentsButton)).perform(click());
        Thread.sleep(1000);

        onView(withId(R.id.userCommentsRecyclerView))
                .perform(RecyclerViewActions.actionOnItem(
                        hasDescendant(withText("This is a test comment")),
                        click()
                ));
        onView(withId(R.id.editCommentEditText)).perform(clearText(), typeText("This is an edited comment"), closeSoftKeyboard());
        onView(withId(R.id.updateCommentButton)).perform(click());

        // Step 3: Verify the edited comment
        Thread.sleep(1000);
        onView(withText("This is an edited comment")).check(matches(isDisplayed()));
    }

    @Test
    public void testDeleteComment() throws InterruptedException {
        // Step 1: Create a comment
        onView(withId(R.id.nav_home)).perform(click());
        onView(withId(R.id.postsRecyclerView))
                .perform(RecyclerViewActions.actionOnItem(
                        hasDescendant(withText("Test Post for Comments")),
                        click()
                ));
        onView(withId(R.id.commentButton)).perform(click());
        onView(withId(R.id.commentEditText)).perform(typeText("This is a test comment"), closeSoftKeyboard());
        onView(withId(R.id.submitCommentButton)).perform(click());

        // Step 2: Delete the comment
        onView(withId(R.id.nav_profile)).perform(click());
        onView(withId(R.id.viewAllCommentsButton)).perform(click());
        Thread.sleep(1000);

        onView(withId(R.id.userCommentsRecyclerView))
                .perform(RecyclerViewActions.actionOnItem(
                        hasDescendant(withText("This is a test comment")),
                        click()
                ));
        onView(withId(R.id.deleteCommentButton)).perform(click());

        // Step 3: Confirm deletion
        onView(withText("Delete")).perform(click());

        // Step 4: Verify the comment is deleted
        Thread.sleep(1000);
        onView(withText("This is a test comment")).check(doesNotExist());
    }

    private void loginAsTestUser() {
        // Perform login action
        onView(withId(R.id.emailEditText)).perform(typeText("hjiang86@usc.edu"), closeSoftKeyboard());
        onView(withId(R.id.passwordEditText)).perform(typeText(";lkjasdf"), closeSoftKeyboard());
        onView(withId(R.id.loginButton)).perform(click());

        // Wait for login to complete
        onView(withId(R.id.nav_home)).check(matches(isDisplayed()));
    }

    private void removeAllPosts() throws InterruptedException {
        // Delete all posts to ensure a clean state
        onView(withId(R.id.nav_profile)).perform(click());
        onView(withId(R.id.viewAllPostsButton)).perform(click());
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

    private void removeAllComments() throws InterruptedException {
        // Delete all comments to ensure a clean state
        onView(withId(R.id.nav_profile)).perform(click());
        onView(withId(R.id.viewAllCommentsButton)).perform(click());
        Thread.sleep(1000);
        try {
            while (true) {
                onView(withId(R.id.userCommentsRecyclerView))
                        .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
                onView(withId(R.id.deleteCommentButton)).perform(click());
                onView(withText("Delete")).perform(click());
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            // No more items to delete; continue
        }
    }
}
