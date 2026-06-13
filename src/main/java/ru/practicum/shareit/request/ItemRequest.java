package ru.practicum.shareit.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ItemRequest {
    private long id;
    private String requestor;
    private String description;
    private LocalDate created;
}
