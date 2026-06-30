package ru.practicum.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.ItemRequestCreateDto;
import ru.practicum.dto.ItemRequestDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Item;
import ru.practicum.model.ItemRequest;
import ru.practicum.model.User;
import ru.practicum.repository.ItemRepository;
import ru.practicum.repository.ItemRequestRepository;
import ru.practicum.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ItemRequestServiceImplIntegrationTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User requestor;
    private User otherUser;

    @BeforeEach
    void setUp() {
        requestor = new User();
        requestor.setName("Requestor");
        requestor.setEmail("requestor@example.com");
        requestor = userRepository.save(requestor);

        otherUser = new User();
        otherUser.setName("Other");
        otherUser.setEmail("other@example.com");
        otherUser = userRepository.save(otherUser);
    }

    @Test
    void createRequest_ShouldSaveAndReturnDto() {
        ItemRequestCreateDto createDto = new ItemRequestCreateDto("Нужна дрель");

        ItemRequestDto result = itemRequestService.createRequest(requestor.getId(), createDto);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getDescription()).isEqualTo("Нужна дрель");
        assertThat(result.getItems()).isEmpty();
    }

    @Test
    void createRequest_UserNotFound_ShouldThrowNotFoundException() {
        ItemRequestCreateDto createDto = new ItemRequestCreateDto("Нужна дрель");

        assertThrows(NotFoundException.class,
                () -> itemRequestService.createRequest(999L, createDto));
    }

    @Test
    void getUserRequests_ShouldReturnRequestsWithBatchLoadedItems() {
        ItemRequest request = saveRequest(requestor, "Нужна дрель");

        Item item = new Item();
        item.setName("Дрель");
        item.setDescription("Мощная дрель");
        item.setAvailable(true);
        item.setOwner(otherUser.getId());
        item.setRequest(request);
        itemRepository.save(item);

        List<ItemRequestDto> result = itemRequestService.getUserRequests(requestor.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getItems()).hasSize(1);
        assertThat(result.get(0).getItems().get(0).getName()).isEqualTo("Дрель");
    }

    @Test
    void getUserRequests_NoItems_ShouldReturnEmptyItemsList() {
        saveRequest(requestor, "Нужна лестница");

        List<ItemRequestDto> result = itemRequestService.getUserRequests(requestor.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getItems()).isEmpty();
    }

    @Test
    void getUserRequests_NoRequests_ShouldReturnEmptyList() {
        List<ItemRequestDto> result = itemRequestService.getUserRequests(requestor.getId());

        assertThat(result).isEmpty();
    }

    @Test
    void getAllRequests_ShouldExcludeOwnRequestsAndReturnOthers() {
        saveRequest(requestor, "Запрос требователя");
        ItemRequest otherRequest = saveRequest(otherUser, "Запрос другого пользователя");

        List<ItemRequestDto> result = itemRequestService.getAllRequests(requestor.getId(), 0, 10);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(otherRequest.getId());
    }

    @Test
    void getAllRequests_WithPagination_ShouldRespectFromAndSize() {
        for (int i = 0; i < 5; i++) {
            saveRequest(otherUser, "Запрос " + i);
        }

        List<ItemRequestDto> firstPage = itemRequestService.getAllRequests(requestor.getId(), 0, 2);
        List<ItemRequestDto> secondPage = itemRequestService.getAllRequests(requestor.getId(), 2, 2);

        assertThat(firstPage).hasSize(2);
        assertThat(secondPage).hasSize(2);
    }

    @Test
    void getRequestById_ShouldReturnRequestWithItems() {
        ItemRequest request = saveRequest(requestor, "Нужна дрель");

        Item item = new Item();
        item.setName("Дрель");
        item.setDescription("Мощная дрель");
        item.setAvailable(true);
        item.setOwner(otherUser.getId());
        item.setRequest(request);
        itemRepository.save(item);

        ItemRequestDto result = itemRequestService.getRequestById(otherUser.getId(), request.getId());

        assertThat(result.getId()).isEqualTo(request.getId());
        assertThat(result.getItems()).hasSize(1);
    }

    @Test
    void getRequestById_NotFound_ShouldThrowNotFoundException() {
        assertThrows(NotFoundException.class,
                () -> itemRequestService.getRequestById(requestor.getId(), 999L));
    }

    private ItemRequest saveRequest(User user, String description) {
        ItemRequest request = new ItemRequest();
        request.setDescription(description);
        request.setRequestor(user);
        request.setCreated(LocalDateTime.now());
        return itemRequestRepository.save(request);
    }
}