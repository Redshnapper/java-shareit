package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private ItemRepository repository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RequestRepository requestRepository;

    @Test
    @DirtiesContext
    void findItemsByOwnerIdWith1Item() {
        User user = new User();
        User savedUser = userRepository.save(user);
        Item item = new Item();
        item.setOwner(savedUser);
        Item savedItem = repository.save(item);


        List<Item> items = repository.findItemsByOwnerIdOrderById(savedUser.getId());
        assertEquals(1, items.size());
        assertEquals(savedItem, items.get(0));
    }

    @Test
    @DirtiesContext
    void findItemsByOwnerIdWith2ItemsOrderedById() {
        User user = new User();
        User savedUser = userRepository.save(user);
        Item item = new Item();
        item.setOwner(savedUser);
        Item item2 = new Item();
        item2.setOwner(savedUser);

        Item savedItem = repository.save(item);
        Item savedItem2 = repository.save(item2);

        List<Item> items = repository.findItemsByOwnerIdOrderById(savedUser.getId());
        assertEquals(2, items.size());
        assertEquals(savedItem, items.get(0));
        assertEquals(savedItem2, items.get(1));
    }

    @Test
    @DirtiesContext
    void findAllByUserWithZeroSizeThrowsIllegalArgumentException() {
        List<Item> items = repository.findItemsByOwnerIdOrderById(1L);

        assertEquals(0, items.size());
        assertNotNull(items);
    }

    @Test
    @DirtiesContext
    void findAllByUserWithNegativeIdThrowsIllegalArgumentException() {
        List<Item> items = repository.findItemsByOwnerIdOrderById(-1L);

        assertEquals(0, items.size());
        assertNotNull(items);
    }

    @Test
    @DirtiesContext
    void findItemsByWrongOwnerId() {
        User user = new User();
        User savedUser = userRepository.save(user);
        Item item = new Item();
        item.setOwner(savedUser);
        Item item2 = new Item();
        item2.setOwner(savedUser);
        repository.save(item);
        repository.save(item2);

        List<Item> items = repository.findItemsByOwnerIdOrderById(2L);
        assertEquals(0, items.size());
        assertNotNull(items);
    }

    @Test
    @DirtiesContext
    void findItemsByDescriptionContainsAvailableTrue() {
        Item item = new Item("НазВание", "ОписаНИЕ");
        item.setAvailable(true);
        Item save = repository.save(item);
        List<Item> items = repository.findItemsByNameContainsIgnoreCaseAndAvailableIsTrueOrDescriptionContainsIgnoreCaseAndAvailableIsTrue("", "ОпИсА");

        assertEquals(1, items.size());
        assertEquals(save, items.get(0));
    }

    @Test
    @DirtiesContext
    void findItemsByDescriptionContainsAvailableFalse() {
        Item item = new Item("НазВание", "ОписаНИЕ");
        item.setAvailable(false);
        repository.save(item);

        List<Item> items = repository.findItemsByNameContainsIgnoreCaseAndAvailableIsTrueOrDescriptionContainsIgnoreCaseAndAvailableIsTrue("", "описание");
        assertEquals(0, items.size());
        assertNotNull(items);
    }

    @Test
    @DirtiesContext
    void findItemsByNameContainsAvailableTrue() {
        Item item = new Item("НазВание", "ОписаНИЕ");
        item.setAvailable(true);
        Item save = repository.save(item);
        List<Item> items = repository.findItemsByNameContainsIgnoreCaseAndAvailableIsTrueOrDescriptionContainsIgnoreCaseAndAvailableIsTrue("ВАНИЕ", "");

        assertEquals(1, items.size());
        assertEquals(save, items.get(0));
    }

    @Test
    @DirtiesContext
    void findItemsByNameContainsAvailableFalse() {
        Item item = new Item("НазВание", "ОписаНИЕ");
        item.setAvailable(false);
        repository.save(item);

        List<Item> items = repository.findItemsByNameContainsIgnoreCaseAndAvailableIsTrueOrDescriptionContainsIgnoreCaseAndAvailableIsTrue("вание", "");
        assertEquals(0, items.size());
        assertNotNull(items);
    }

    @Test
    @DirtiesContext
    void findItemsByNameAndDescriptionBlank() {
        Item item = new Item("НазВание", "ОписаНИЕ");
        item.setAvailable(true);
        repository.save(item);

        List<Item> items = repository.findItemsByNameContainsIgnoreCaseAndAvailableIsTrueOrDescriptionContainsIgnoreCaseAndAvailableIsTrue(" ", " ");
        assertEquals(0, items.size());
        assertNotNull(items);
    }

    @Test
    @DirtiesContext
    void find2ItemsByNameAndDescriptionEmpty() {
        Item item = new Item("НазВание", "ОписаНИЕ");
        item.setAvailable(true);
        Item save = repository.save(item);
        Item item1 = new Item("НазВание1", "ОписаНИЕ1");
        item1.setAvailable(true);
        Item save1 = repository.save(item1);


        List<Item> items = repository.findItemsByNameContainsIgnoreCaseAndAvailableIsTrueOrDescriptionContainsIgnoreCaseAndAvailableIsTrue("", "");
        assertEquals(2, items.size());
        assertEquals(save, items.get(0));
        assertEquals(save1, items.get(1));
    }

    @Test
    @DirtiesContext
    void find2ItemsByNameAndDescription() {
        Item item = new Item("НазВание1", "ОписаНИЕ1");
        item.setAvailable(true);
        Item save = repository.save(item);
        Item item1 = new Item("НазВание2", "ОписаНИЕ2");
        item1.setAvailable(true);
        Item save1 = repository.save(item1);


        List<Item> items = repository.findItemsByNameContainsIgnoreCaseAndAvailableIsTrueOrDescriptionContainsIgnoreCaseAndAvailableIsTrue("1", "2");
        assertEquals(2, items.size());
        assertEquals(save, items.get(0));
        assertEquals(save1, items.get(1));
    }

    @Test
    @DirtiesContext
    void findItemsWithNullName() {
        Item item = new Item("НазВание", "ОписаНИЕ");
        item.setAvailable(true);
        Item save = repository.save(item);

        List<Item> items = repository.findItemsByNameContainsIgnoreCaseAndAvailableIsTrueOrDescriptionContainsIgnoreCaseAndAvailableIsTrue(null, "");
        assertEquals(1, items.size());
        assertEquals(save, items.get(0));
    }

    @Test
    @DirtiesContext
    void findItemsWithNullDescription() {
        Item item = new Item("НазВание", "ОписаНИЕ");
        item.setAvailable(true);
        Item save = repository.save(item);

        List<Item> items = repository.findItemsByNameContainsIgnoreCaseAndAvailableIsTrueOrDescriptionContainsIgnoreCaseAndAvailableIsTrue("", null);
        assertEquals(1, items.size());
        assertEquals(save, items.get(0));
    }


    @Test
    @DirtiesContext
    void find2ItemsByRequestIdIWithAll3() {
        Item item = new Item("НазВание", "ОписаНИЕ");
        Item item2 = new Item("НазВание2", "ОписаНИЕ2");
        Request request = new Request();
        requestRepository.save(request);
        Request request2 = new Request();
        requestRepository.save(request2);
        Request request3 = new Request();
        requestRepository.save(request3);
        item.setRequest(request);
        item2.setRequest(request2);
        Item save = repository.save(item);
        Item save1 = repository.save(item2);

        List<Item> items = repository.findItemsByRequestIdIn(List.of(1L, 2L));

        assertEquals(2, items.size());
        assertEquals(save, items.get(0));
        assertEquals(save1, items.get(1));
    }

    @Test
    @DirtiesContext
    void findItemsByRequestIdEmpty() {
        Item item = new Item("НазВание", "ОписаНИЕ");
        Item item2 = new Item("НазВание2", "ОписаНИЕ2");
        Request request = new Request();
        requestRepository.save(request);
        Request request2 = new Request();
        requestRepository.save(request2);
        Request request3 = new Request();
        requestRepository.save(request3);
        item.setRequest(request);
        item2.setRequest(request2);
        repository.save(item);
        repository.save(item2);
        List<Item> items = repository.findItemsByRequestIdIn(new ArrayList<>());

        assertEquals(0, items.size());
        assertNotNull(items);
    }

    @Test
    @DirtiesContext
    void findItemsByRequestIdWithWrongIds() {
        Item item = new Item("НазВание", "ОписаНИЕ");
        Item item2 = new Item("НазВание2", "ОписаНИЕ2");
        Request request = new Request();
        requestRepository.save(request);
        Request request2 = new Request();
        requestRepository.save(request2);
        Request request3 = new Request();
        requestRepository.save(request3);
        item.setRequest(request);
        item2.setRequest(request2);
        repository.save(item);
        repository.save(item2);
        List<Item> items = repository.findItemsByRequestIdIn(List.of(-1L, 0L, -3L));

        assertEquals(0, items.size());
        assertNotNull(items);
    }

    @Test
    @DirtiesContext
    void findItemByRequestId() {
        Item item = new Item("НазВание", "ОписаНИЕ");
        Request request = new Request();
        requestRepository.save(request);
        Request request2 = new Request();
        requestRepository.save(request2);
        item.setRequest(request2);
        Item save = repository.save(item);
        Item find = repository.findItemByRequestId(2L);

        assertEquals(save, find);
        assertEquals(2, request2.getId());
    }

    @Test
    @DirtiesContext
    void findItemByWrongRequestId() {
        Item item = new Item("НазВание", "ОписаНИЕ");
        Request request = new Request();
        requestRepository.save(request);
        Request request2 = new Request();
        requestRepository.save(request2);
        item.setRequest(request2);
        repository.save(item);
        Item save = repository.findItemByRequestId(3L);

        assertEquals(1, request.getId());
        assertEquals(2, request2.getId());
        assertNull(save);
    }
}