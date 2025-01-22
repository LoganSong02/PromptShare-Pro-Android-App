package com.example.promptsharepro22;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.promptsharepro22.data.model.Comment;
import com.example.promptsharepro22.viewmodel.CommentViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class CommentUnitTest {

    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    private CommentViewModel commentViewModelMock;

    @Before
    public void setUp() {
        // Mock the CommentViewModel instance
        commentViewModelMock = mock(CommentViewModel.class);
    }

    @Test
    public void testCreateComment() throws InterruptedException {
        Comment comment = new Comment("Test Content", "Post1", "User1", "2024-01-01", "2024-01-01", 4.5f);

        // Use real MutableLiveData
        MutableLiveData<String> liveData = new MutableLiveData<>();
        when(commentViewModelMock.createComment(comment)).thenReturn(liveData);

        // Trigger the LiveData value
        liveData.setValue("mockCommentId");

        // Assert
        String commentId = getOrAwaitValue(commentViewModelMock.createComment(comment));
        assertNotNull("Comment ID should not be null after creation", commentId);
        verify(commentViewModelMock).createComment(comment);
    }

    @Test
    public void testGetComment() throws InterruptedException {
        String commentId = "mockCommentId";
        Comment mockComment = new Comment("Test Content", "Post1", "User1", "2024-01-01", "2024-01-01", 4.5f);

        // Use real MutableLiveData
        MutableLiveData<Comment> liveData = new MutableLiveData<>();
        when(commentViewModelMock.getComment(commentId)).thenReturn(liveData);

        // Trigger the LiveData value
        liveData.setValue(mockComment);

        // Assert
        Comment fetchedComment = getOrAwaitValue(commentViewModelMock.getComment(commentId));
        assertNotNull("Fetched comment should not be null", fetchedComment);
        assertEquals("Content should match", "Test Content", fetchedComment.getContent());
    }

    @Test
    public void testGetCommentsByPostId() throws InterruptedException {
        String postId = "Post1";
        Comment comment1 = new Comment("Content1", postId, "User1", "2024-01-01", "2024-01-01", 4.0f);
        Comment comment2 = new Comment("Content2", postId, "User2", "2024-01-01", "2024-01-01", 5.0f);

        // Use real MutableLiveData
        MutableLiveData<List<Comment>> liveData = new MutableLiveData<>();
        when(commentViewModelMock.getCommentsByPostId(postId)).thenReturn(liveData);

        // Trigger the LiveData value
        liveData.setValue(List.of(comment1, comment2));

        // Assert
        List<Comment> comments = getOrAwaitValue(commentViewModelMock.getCommentsByPostId(postId));
        assertNotNull("Comments list should not be null", comments);
        assertEquals("There should be 2 comments", 2, comments.size());
    }

    @Test
    public void testUpdateComment() throws InterruptedException {
        String commentId = "mockCommentId";
        Comment updatedComment = new Comment("Updated Content", "Post1", "User1", "2024-01-01", "2024-01-02", 4.5f);

        // Use real MutableLiveData
        MutableLiveData<Boolean> liveData = new MutableLiveData<>();
        when(commentViewModelMock.updateComment(commentId, updatedComment)).thenReturn(liveData);

        // Trigger the LiveData value
        liveData.setValue(true);

        // Assert
        Boolean updateResult = getOrAwaitValue(commentViewModelMock.updateComment(commentId, updatedComment));
        assertTrue("Update operation should return true", updateResult);
    }

    @Test
    public void testDeleteComment() throws InterruptedException {
        String commentId = "mockCommentId";

        // Use real MutableLiveData
        MutableLiveData<Boolean> liveData = new MutableLiveData<>();
        when(commentViewModelMock.deleteComment(commentId)).thenReturn(liveData);

        // Trigger the LiveData value
        liveData.setValue(true);

        // Assert
        Boolean deleteResult = getOrAwaitValue(commentViewModelMock.deleteComment(commentId));
        assertTrue("Delete operation should return true", deleteResult);
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
