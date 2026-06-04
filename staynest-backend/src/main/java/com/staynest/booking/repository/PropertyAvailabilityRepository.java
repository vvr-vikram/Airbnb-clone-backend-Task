package com.staynest.booking.repository;

import com.staynest.booking.entity.PropertyAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PropertyAvailabilityRepository extends JpaRepository<PropertyAvailability, Long> {

    List<PropertyAvailability> findByPropertyId(Long propertyId);

    @Query("SELECT pa FROM PropertyAvailability pa WHERE pa.property.id = :propertyId " +
           "AND pa.availableFrom <= :startDate AND pa.availableTo >= :endDate")
    List<PropertyAvailability> findCoveringAvailability(@Param("propertyId") Long propertyId,
                                                        @Param("startDate") LocalDate startDate,
                                                        @Param("endDate") LocalDate endDate);
}
