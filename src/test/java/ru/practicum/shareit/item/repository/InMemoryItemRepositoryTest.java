package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InMemoryItemRepositoryTest {

    @Mock
    UserRepository userRepository;
    private InMemoryItemRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryItemRepository(
                userRepository
        );
    }

    @AfterEach
    void clean() {
        repository.removeAll();
    }


    @Test
    void add() {
        Item item = new Item();
        Item item2 = new Item();
        Item save = repository.add(item);
        Item save2 = repository.add(item2);
        assertNotNull(save);
        assertNotNull(save2);
        assertEquals(1L, save.getId());
        assertEquals(2L, save2.getId());
    }

    @Test
    void update() {
        Item item = new Item();
        User owner = new User(1L);
        item.setOwner(owner);
        Item save = repository.add(item);
        Item updated = Item.builder()
                .name("updated name")
                .description("updated desc")
                .available(true)
                .build();

        when(userRepository.getReferenceById(1L)).thenReturn(owner);

        Item update = repository.update(updated, save.getId(), owner.getId());

        assertNotNull(update);
        assertEquals(1L, update.getId());
        assertEquals(updated.getName(), update.getName());
        assertEquals(updated.getDescription(), update.getDescription());
        assertEquals(updated.getAvailable(), update.getAvailable());
        assertEquals(owner, update.getOwner());
    }

    @Test
    void updateWithNulls() {
        User owner = new User(1L);
        Item item = Item.builder()
                .name("name")
                .description("description")
                .available(true)
                .owner(owner)
                .build();
        Item save = repository.add(item);
        Item updated = Item.builder()
                .name(null)
                .description(null)
                .available(null)
                .build();

        when(userRepository.getReferenceById(1L)).thenReturn(owner);
        Item update = repository.update(updated, save.getId(), owner.getId());

        assertNotNull(update);
        assertEquals(1L, update.getId());
        assertEquals(item.getName(), update.getName());
        assertEquals(item.getDescription(), update.getDescription());
        assertEquals(item.getAvailable(), update.getAvailable());
        assertEquals(owner, update.getOwner());
    }

    @Test
    void updateThrowsNotFoundItemException() {
        Long id = 1L;
        Throwable exception = assertThrows(NotFoundException.class, () -> repository.update(new Item(), id, 1L));
        assertEquals("Предмет с id " + id + " не найден", exception.getMessage());
    }

    @Test
    void updateThrowsNotFoundItemOwnerException() {
        Long id = 1L;
        User owner = new User(1L);
        Item item = Item.builder()
                .owner(owner)
                .build();
        repository.add(item);

        Throwable exception = assertThrows(NotFoundException.class, () -> repository.update(item, id, 2L));
        assertEquals("У данной вещи другой владелец!", exception.getMessage());
    }

    @Test
    void getById() {
        Item item = Item.builder().build();
        repository.add(item);

        Item save = repository.getById(0L, item.getId());

        assertNotNull(save);
        assertEquals(item.getId(), save.getId());
    }

    @Test
    void getByIdNotFoundItemException() {
        Throwable exception = assertThrows(NotFoundException.class, () -> repository.getById(1L, 1L));
        assertEquals("Предмет с id " + 1L + " не найден", exception.getMessage());
    }

    @Test
    void getAllUserItems() {
        User owner = new User(1L);
        User owner2 = new User(2L);
        Item item = Item.builder()
                .owner(owner)
                .build();
        Item item2 = Item.builder()
                .owner(owner)
                .build();
        Item item3 = Item.builder()
                .owner(owner2)
                .build();
        repository.add(item);
        repository.add(item2);
        repository.add(item3);

        List<Item> items = repository.getAllUserItems(1L);
        List<Item> items2 = repository.getAllUserItems(2L);

        assertNotNull(items);
        assertNotNull(items2);
        assertEquals(2, items.size());
        assertEquals(1, items2.size());
    }

    @Test
    void searchByName() {
        Item item = Item.builder()
                .name("name")
                .description("desCRI")
                .available(true)
                .build();
        Item item2 = Item.builder()
                .name("description")
                .description("name2")
                .available(true)
                .build();
        Item item3 = Item.builder()
                .available(false)
                .name("dddesss")
                .description(" ")
                .build();
        repository.add(item);
        repository.add(item2);
        repository.add(item3);

        List<Item> deS = repository.searchByName("DeS");

        assertNotNull(deS);
        assertEquals(2, deS.size());
    }

    @Test
    void searchByNameEmpty() {
        List<Item> empty = repository.searchByName("");
        assertEquals(0, empty.size());
    }

}