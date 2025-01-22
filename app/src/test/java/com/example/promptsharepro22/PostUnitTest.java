package com.example.promptsharepro22;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.promptsharepro22.data.model.Post;
import com.example.promptsharepro22.viewmodel.PostViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class PostUnitTest {

    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    private PostViewModel postViewModelMock;

    @Before
    public void setUp() {
        // Mock the PostViewModel instance
        postViewModelMock = mock(PostViewModel.class);
    }

    @Test
    public void testCreatePost() throws InterruptedException {
        Post post = new Post("Test Title", "Test Content", "User1", "Test Author Note", "Test LLMKind", "2024-01-01", "2024-01-01");

        // Use real MutableLiveData
        MutableLiveData<String> liveData = new MutableLiveData<>();
        when(postViewModelMock.createPost(post)).thenReturn(liveData);

        // Trigger the LiveData value
        liveData.setValue("mockPostId");

        // Assert
        String postId = getOrAwaitValue(postViewModelMock.createPost(post));
        assertNotNull("Post ID should not be null after creation", postId);
        verify(postViewModelMock).createPost(post);
    }

    @Test
    public void testGetPost() throws InterruptedException {
        String postId = "mockPostId";
        Post mockPost = new Post("Test Title", "Test Content", "User1", "Test Author Note", "Test LLMKind", "2024-01-01", "2024-01-01");

        // Use real MutableLiveData
        MutableLiveData<Post> liveData = new MutableLiveData<>();
        when(postViewModelMock.getPost(postId)).thenReturn(liveData);

        // Trigger the LiveData value
        liveData.setValue(mockPost);

        // Assert
        Post fetchedPost = getOrAwaitValue(postViewModelMock.getPost(postId));
        assertNotNull("Fetched post should not be null", fetchedPost);
        assertEquals("Title should match", "Test Title", fetchedPost.getTitle());
    }

    @Test
    public void testUpdatePost() throws InterruptedException {
        String postId = "mockPostId";
        Post updatedPost = new Post("Updated Title", "Updated Content", "User1", "Updated Note", "Updated LLMKind", "2024-01-01", "2024-01-02");

        // Use real MutableLiveData
        MutableLiveData<Boolean> liveData = new MutableLiveData<>();
        when(postViewModelMock.updatePost(postId, updatedPost)).thenReturn(liveData);

        // Trigger the LiveData value
        liveData.setValue(true);

        // Assert
        Boolean updateResult = getOrAwaitValue(postViewModelMock.updatePost(postId, updatedPost));
        assertTrue("Update operation should return true", updateResult);
    }

    @Test
    public void testDeletePost() throws InterruptedException {
        String postId = "mockPostId";

        // Use real MutableLiveData
        MutableLiveData<Boolean> liveData = new MutableLiveData<>();
        when(postViewModelMock.deletePost(postId)).thenReturn(liveData);

        // Trigger the LiveData value
        liveData.setValue(true);

        // Assert
        Boolean deleteResult = getOrAwaitValue(postViewModelMock.deletePost(postId));
        assertTrue("Delete operation should return true", deleteResult);
    }

    @Test
    public void testSearchPosts() throws InterruptedException {
        Post post1 = new Post("AI Title", "AI Content", "User1", "Note1", "LLMKind1", "2024-01-01", "2024-01-01");

        // Use real MutableLiveData
        MutableLiveData<List<Post>> liveData = new MutableLiveData<>();
        when(postViewModelMock.searchPosts("AI", "all")).thenReturn(liveData);

        // Trigger the LiveData value
        liveData.setValue(List.of(post1));

        // Assert
        List<Post> searchResults = getOrAwaitValue(postViewModelMock.searchPosts("AI", "all"));
        assertNotNull("Search results should not be null", searchResults);
        assertEquals("There should be 1 search result", 1, searchResults.size());
        assertEquals("Title should match the search query", "AI Title", searchResults.get(0).getTitle());
    }

    // Helper method to observe LiveData synchronously
    private <T> T getOrAwaitValue(LiveData<T> liveData) throws InterruptedException {
        final Object[] data = new Object[1];
        CountDownLatch latch = new CountDownLatch(1);

        Observer<T> observer = new Observer<T>() {
            @Override
            public void onChanged(T t) {
                data[0] = t;
                latch.countDown();
                liveData.removeObserver(this);
            }
        };

        liveData.observeForever(observer);

        if (!latch.await(2, TimeUnit.SECONDS)) {
            throw new AssertionError("LiveData value was never set.");
        }

        //noinspection unchecked
        return (T) data[0];
    }
}
