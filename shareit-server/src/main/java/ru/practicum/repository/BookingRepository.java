package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.Booking;
import ru.practicum.status.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId);
    List<Booking> findByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status);
    List<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long bookerId, LocalDateTime start, LocalDateTime end);
    List<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime now);
    List<Booking> findByBookerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime now);

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

    boolean existsByBookerIdAndItemIdAndEndBefore(Long bookerId, Long itemId, LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.id = :itemId AND b.start <= :now AND b.status <> 'REJECTED' " +
            "ORDER BY b.start DESC LIMIT 1")
    Optional<Booking> findLastBookingByItemId(@Param("itemId") Long itemId, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.id = :itemId AND b.start > :now AND b.status <> 'REJECTED' " +
            "ORDER BY b.start ASC LIMIT 1")
    Optional<Booking> findNextBookingByItemId(@Param("itemId") Long itemId, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.id IN :itemIds AND b.status <> 'REJECTED' ORDER BY b.start ASC")
    List<Booking> findAllByItemIdInAndNotRejected(@Param("itemIds") List<Long> itemIds);
}