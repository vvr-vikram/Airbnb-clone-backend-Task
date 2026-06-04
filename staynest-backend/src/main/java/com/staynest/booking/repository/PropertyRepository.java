package com.staynest.booking.repository;

import com.staynest.booking.entity.Property;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Property p WHERE p.id = :id")
    Optional<Property> findByIdWithWriteLock(@Param("id") Long id);

    @Query("SELECT p FROM Property p WHERE " +
           "(:location IS NULL OR LOWER(p.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
           "(:minPrice IS NULL OR p.pricePerNight >= :minPrice) AND " +
           "(:maxPrice IS NULL OR p.pricePerNight <= :maxPrice) AND " +
           "(:minRating IS NULL OR (SELECT COALESCE(AVG(r.rating), 0.0) FROM Review r WHERE r.property.id = p.id) >= :minRating)")
    Page<Property> searchProperties(@Param("location") String location,
                                    @Param("minPrice") BigDecimal minPrice,
                                    @Param("maxPrice") BigDecimal maxPrice,
                                    @Param("minRating") Double minRating,
                                    Pageable pageable);

    @Query("SELECT p FROM Property p " +
           "ORDER BY (SELECT COALESCE(AVG(r.rating), 0.0) FROM Review r WHERE r.property.id = p.id) DESC, " +
           "(SELECT COUNT(b.id) FROM Booking b WHERE b.property.id = p.id) DESC")
    List<Property> findPopularProperties(Pageable pageable);

    List<Property> findByHostId(Long hostId);
}
