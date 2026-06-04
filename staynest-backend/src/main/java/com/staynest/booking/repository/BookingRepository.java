package com.staynest.booking.repository;

import com.staynest.booking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByGuestIdOrderByCreatedAtDesc(Long guestId);

    @Query("SELECT b FROM Booking b WHERE b.property.host.id = :hostId ORDER BY b.createdAt DESC")
    List<Booking> findByHostIdOrderByCreatedAtDesc(@Param("hostId") Long hostId);

    List<Booking> findByPropertyIdOrderByStartDateDesc(Long propertyId);

    boolean existsByPropertyIdAndGuestIdAndStatus(Long propertyId, Long guestId, Booking.BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.property.id = :propertyId " +
           "AND b.status IN :statuses " +
           "AND b.startDate < :endDate AND b.endDate > :startDate")
    List<Booking> findOverlappingBookings(@Param("propertyId") Long propertyId,
                                         @Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate,
                                         @Param("statuses") List<Booking.BookingStatus> statuses);
}
