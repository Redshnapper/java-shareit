package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class RequestRepositoryTest {

    @Autowired
    private RequestRepository repository;
    @Autowired
    private UserRepository userRepository;

    @Test
    @DirtiesContext
    void findAllEmpty() {
        List<Request> all = repository.findAllByUser_Id(1L);
        assertTrue(all.isEmpty());
    }

    @Test
    @DirtiesContext
    void findAllWith1Request() {
        Request request = new Request("", LocalDateTime.now());
        User user = new User();
        request.setUser(user);
        userRepository.save(user);
        repository.save(request);

        List<Request> all = repository.findAllByUser_Id(1L);
        assertEquals(1, all.size());
        assertEquals(request, all.get(0));
    }

    @Test
    @DirtiesContext
    void findAllWithNegativeId() {
        Request request = new Request("", LocalDateTime.now());
        User user = new User();
        request.setUser(user);
        userRepository.save(user);
        repository.save(request);

        List<Request> all = repository.findAllByUser_Id(-1L);
        assertEquals(0, all.size());
    }

    @Test
    @DirtiesContext
    void findAllWithNullId() {
        Request request = new Request("", LocalDateTime.now());
        User user = new User();
        request.setUser(user);
        userRepository.save(user);
        repository.save(request);

        List<Request> all = repository.findAllByUser_Id(null);
        assertEquals(0, all.size());
    }

    @Test
    @DirtiesContext
    void findAllByUserIdNotOrderByCreatedDesc() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime now2 = now.plusHours(1);
        LocalDateTime now3 = now.plusHours(2);


        Request request = new Request("1", now);
        User user1 = new User();
        request.setUser(user1);
        userRepository.save(user1);

        Request request2 = new Request("2", now2);
        User user2 = new User();
        request2.setUser(user2);
        userRepository.save(user2);

        Request request3 = new Request("3", now3);
        User user3 = new User();
        request3.setUser(user3);
        userRepository.save(user3);

        repository.save(request);
        repository.save(request2);
        repository.save(request3);

        Page<Request> all = repository.findAllByUser_IdNotOrderByCreatedDesc(1L, PageRequest.of(0, 2));

        assertEquals(2, all.getContent().size());
        assertEquals(2, all.getSize());
        assertTrue(all.getContent().get(0).getCreated().isAfter(all.getContent().get(1).getCreated()));
    }

    @Test
    @DirtiesContext
    void findAllByUserIdNotOrderByCreatedDescEmpty() {
        Request request = new Request("1", LocalDateTime.now());
        User user1 = new User();
        request.setUser(user1);
        userRepository.save(user1);
        repository.save(request);
        Page<Request> all = repository.findAllByUser_IdNotOrderByCreatedDesc(1L, PageRequest.of(0, 1));

        assertEquals(1, all.getSize());
        assertEquals(0, all.getContent().size());

    }

    @Test
    @DirtiesContext
    void findAllByUserWithZeroSizeThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> repository.findAllByUser_IdNotOrderByCreatedDesc(1L, PageRequest.of(0, 0)));
    }

    @Test
    @DirtiesContext
    void findAllByUserWithNegativePageThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> repository.findAllByUser_IdNotOrderByCreatedDesc(1L, PageRequest.of(-1, 0)));
    }

    @Test
    @DirtiesContext
    void findAllByUserWithNegativeSizeThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> repository.findAllByUser_IdNotOrderByCreatedDesc(1L, PageRequest.of(0, -2)));
    }



}