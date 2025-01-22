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
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SearchUnitTest {

    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    private PostViewModel postViewModelMock;

    @Before
    public void setUp() {
        // Mock the PostViewModel instance
        postViewModelMock = mock(PostViewModel.class);
    }

    @Test
    public void testSearchByLLMKind() throws InterruptedException {
        String query = "ChatGPT";
        String searchType = "Search By LLM Kind";
        Post post1 = new Post("AI and LLMs", "Content about AI", "User1", "Author Note", "ChatGPT", "2024-01-01", "2024-01-01");

        MutableLiveData<List<Post>> liveData = new MutableLiveData<>();
        when(postViewModelMock.searchPosts(query, searchType)).thenReturn(liveData);

        liveData.setValue(List.of(post1));

        List<Post> searchResults = getOrAwaitValue(postViewModelMock.searchPosts(query, searchType));
        assertNotNull("Search results should not be null", searchResults);
        assertEquals("There should be 1 search result", 1, searchResults.size());
        assertEquals("LLM kind should match the search query", "ChatGPT", searchResults.get(0).getLLMKind());

        System.out.println("\n[PASSED] testSearchByLLMKind");
    }

    @Test
    public void testSearchByTitle() throws InterruptedException {
        String query = "Software Testing";
        String searchType = "Search By Titles";
        Post post1 = new Post("Software Testing", "Content about testing", "User2", "Author Note", "Test", "2024-01-01", "2024-01-01");

        MutableLiveData<List<Post>> liveData = new MutableLiveData<>();
        when(postViewModelMock.searchPosts(query, searchType)).thenReturn(liveData);

        liveData.setValue(List.of(post1));

        List<Post> searchResults = getOrAwaitValue(postViewModelMock.searchPosts(query, searchType));
        assertNotNull("Search results should not be null", searchResults);
        assertEquals("There should be 1 search result", 1, searchResults.size());
        assertEquals("Title should match the search query", "Software Testing", searchResults.get(0).getTitle());
        System.out.println("[PASSED] testSearchByTitle");
    }

    @Test
    public void testFullTextSearch() throws InterruptedException {
        String query = "language model";
        String searchType = "Full Text Search";
        Post post1 = new Post("Understanding LLMs", "A language model overview", "User3", "Note1", "AI", "2024-01-01", "2024-01-01");

        MutableLiveData<List<Post>> liveData = new MutableLiveData<>();
        when(postViewModelMock.searchPosts(query, searchType)).thenReturn(liveData);

        liveData.setValue(List.of(post1));

        List<Post> searchResults = getOrAwaitValue(postViewModelMock.searchPosts(query, searchType));
        assertNotNull("Search results should not be null", searchResults);
        assertEquals("There should be 1 search result", 1, searchResults.size());
        assertEquals("Content should match the search query", "A language model overview", searchResults.get(0).getContent());
        System.out.println("[PASSED] testFullTextSearch");
    }

    @Test
    public void testSearchWithNoResults() throws InterruptedException {
        String query = "Nonexistent";
        String searchType = "Full Text Search";

        MutableLiveData<List<Post>> liveData = new MutableLiveData<>();
        when(postViewModelMock.searchPosts(query, searchType)).thenReturn(liveData);

        liveData.setValue(List.of());

        List<Post> searchResults = getOrAwaitValue(postViewModelMock.searchPosts(query, searchType));
        assertNotNull("Search results should not be null", searchResults);
        assertTrue("Search results should be empty", searchResults.isEmpty());
        System.out.println("[PASSED] testSearchWithNoResults");
    }

    @Test
    public void testDefaultSearchType() throws InterruptedException {
        String query = "xxx";
        String searchType = "all";

        // Create two posts for the test
        Post post1 = new Post("General Post", "General content", "User4", "Note2", "General LLM", "2024-01-01", "2024-01-01");
        Post post2 = new Post("Another Post", "Another content", "User5", "Note3", "Another LLM", "2024-01-02", "2024-01-02");

        // Use real MutableLiveData
        MutableLiveData<List<Post>> liveData = new MutableLiveData<>();
        when(postViewModelMock.searchPosts(query, searchType)).thenReturn(liveData);

        // Set the LiveData value to include two posts
        liveData.setValue(List.of(post1, post2));

        // Retrieve and assert results
        List<Post> searchResults = getOrAwaitValue(postViewModelMock.searchPosts(query, searchType));
        assertNotNull("Search results should not be null", searchResults);
        assertEquals("There should be 2 search results", 2, searchResults.size());
        assertEquals("First post's title should match", "General Post", searchResults.get(0).getTitle());
        assertEquals("Second post's title should match", "Another Post", searchResults.get(1).getTitle());
        System.out.println("[PASSED] testDefaultSearchType\n");
    }


    // Helper method to observe LiveData synchronously
    private <T> T getOrAwaitValue(LiveData<T> liveData) throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<T> data = new AtomicReference<>();

        liveData.observeForever(new Observer<T>() {
            @Override
            public void onChanged(T t) {
                data.set(t);
                latch.countDown();
                liveData.removeObserver(this);
            }
        });

        if (!latch.await(2, TimeUnit.SECONDS)) {
            throw new AssertionError("LiveData value was never set.");
        }

        return data.get();
    }
}
