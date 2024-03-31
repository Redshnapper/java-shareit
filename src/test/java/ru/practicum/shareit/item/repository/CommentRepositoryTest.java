package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    CommentRepository commentRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    @DirtiesContext
    void find1CommentByItemId() {
        User user = new User();
        userRepository.save(user);
        Item item = new Item();
        item.setOwner(user);
        itemRepository.save(item);
        Comment comment = new Comment();
        comment.setItem(item);
        commentRepository.save(comment);
        List<Comment> comments = commentRepository.findCommentByItemIdOrderById(item.getId());

        assertEquals(1, comments.size());
        assertEquals(comment, comments.get(0));
    }

    @Test
    @DirtiesContext
    void find2CommentsByItemIdOrderById() {
        User user = new User();
        userRepository.save(user);
        Item item = new Item();
        item.setOwner(user);
        itemRepository.save(item);
        Comment comment = new Comment();
        comment.setItem(item);
        commentRepository.save(comment);

        Comment comment2 = new Comment();
        comment2.setItem(item);
        commentRepository.save(comment2);

        List<Comment> comments = commentRepository.findCommentByItemIdOrderById(item.getId());

        assertEquals(2, comments.size());
        assertEquals(comment, comments.get(0));
        assertEquals(comment2, comments.get(1));

    }

    @Test
    @DirtiesContext
    void findCommentByWrongItemIdOrderById() {
        User user = new User();
        userRepository.save(user);
        Item item = new Item();
        item.setOwner(user);
        itemRepository.save(item);
        Comment comment = new Comment();
        comment.setItem(item);
        commentRepository.save(comment);

        List<Comment> comments = commentRepository.findCommentByItemIdOrderById(0L);
        assertEquals(0, comments.size());
        assertNotNull(comments);

        comments = commentRepository.findCommentByItemIdOrderById(-1L);
        assertEquals(0, comments.size());
        assertNotNull(comments);
    }
}