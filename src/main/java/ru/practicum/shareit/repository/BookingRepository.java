package ru.practicum.shareit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.model.Booking;
import ru.practicum.shareit.status.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    // Бронирования пользователя
    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status);

    List<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
            Long bookerId, LocalDateTime now1, LocalDateTime now2);

    List<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime now);

    List<Booking> findByBookerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime now);

    // Бронирования вещей пользователя (владелец)
    @Query("SELECT b FROM Booking b WHERE b.item.owner = :ownerId ORDER BY b.start DESC")
    List<Booking> findAllByOwnerId(Long ownerId);

    @Query("SELECT b FROM Booking b WHERE b.item.owner = :ownerId AND b.status = :status ORDER BY b.start DESC")
    List<Booking> findAllByOwnerIdAndStatus(Long ownerId, BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.item.owner = :ownerId AND b.start < :now AND b.end > :now ORDER BY b.start DESC")
    List<Booking> findAllByOwnerIdAndCurrent(Long ownerId, LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.owner = :ownerId AND b.end < :now ORDER BY b.start DESC")
    List<Booking> findAllByOwnerIdAndPast(Long ownerId, LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.owner = :ownerId AND b.start > :now ORDER BY b.start DESC")
    List<Booking> findAllByOwnerIdAndFuture(Long ownerId, LocalDateTime now);

    // Проверка, что пользователь брал вещь в аренду
    boolean existsByBookerIdAndItemIdAndEndBefore(Long bookerId, Long itemId, LocalDateTime now);

    List<Booking> findByItemIdOrderByStartDesc(Long itemId);
}