package ru.practicum.shareit.booking;

import lombok.Data;
import java.time.LocalDate;

@Data
public class Booking {
    private long id;
    private LocalDate start;
    private LocalDate end;
    private String item;
    private String status;
    private String booker;
}
