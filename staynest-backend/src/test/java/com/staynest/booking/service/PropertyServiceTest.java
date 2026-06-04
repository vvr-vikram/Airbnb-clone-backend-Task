package com.staynest.booking.service;

import com.staynest.booking.dto.PropertyDetailsResponse;
import com.staynest.booking.dto.PropertyRequest;
import com.staynest.booking.dto.PropertyResponse;
import com.staynest.booking.entity.Property;
import com.staynest.booking.entity.User;
import com.staynest.booking.entity.User.Role;
import com.staynest.booking.exception.BadRequestException;
import com.staynest.booking.repository.BookingRepository;
import com.staynest.booking.repository.PropertyAvailabilityRepository;
import com.staynest.booking.repository.PropertyRepository;
import com.staynest.booking.repository.ReviewRepository;
import com.staynest.booking.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PropertyServiceTest {

    @Mock
    private PropertyRepository propertyRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PropertyAvailabilityRepository availabilityRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private PropertyService propertyService;

    private User host;
    private User guest;
    private Property property;

    @BeforeEach
    void setUp() {
        host = User.builder()
                .id(2L)
                .name("Aarav Sharma")
                .email("aarav@staynest.in")
                .role(Role.HOST)
                .createdAt(LocalDateTime.now())
                .build();

        guest = User.builder()
                .id(1L)
                .name("Ananya Iyer")
                .email("ananya@staynest.in")
                .role(Role.GUEST)
                .createdAt(LocalDateTime.now())
                .build();

        property = Property.builder()
                .id(1L)
                .title("Taj Luxury Villa")
                .description("Beachfront villa in Goa")
                .location("Goa")
                .pricePerNight(BigDecimal.valueOf(15000.00))
                .host(host)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void createProperty_Success() {
        PropertyRequest request = new PropertyRequest();
        request.setTitle("Taj Luxury Villa");
        request.setDescription("Beachfront villa in Goa");
        request.setLocation("Goa");
        request.setPricePerNight(BigDecimal.valueOf(15000.00));
        request.setHostId(2L); // Aarav (Host)

        when(userRepository.findById(2L)).thenReturn(Optional.of(host));
        when(propertyRepository.save(any(Property.class))).thenReturn(property);

        PropertyResponse response = propertyService.createProperty(request);

        assertNotNull(response);
        assertEquals("Taj Luxury Villa", response.getTitle());
        assertEquals("Goa", response.getLocation());
        assertEquals(BigDecimal.valueOf(15000.00), response.getPricePerNight());
        assertEquals(2L, response.getHostId());
        assertEquals("Aarav Sharma", response.getHostName());

        verify(propertyRepository, times(1)).save(any(Property.class));
    }

    @Test
    void createProperty_Fail_UserNotHost() {
        PropertyRequest request = new PropertyRequest();
        request.setTitle("Taj Luxury Villa");
        request.setDescription("Beachfront villa in Goa");
        request.setLocation("Goa");
        request.setPricePerNight(BigDecimal.valueOf(15000.00));
        request.setHostId(1L); // Ananya (Guest)

        when(userRepository.findById(1L)).thenReturn(Optional.of(guest)); // User is GUEST

        assertThrows(BadRequestException.class, () -> propertyService.createProperty(request));
        verify(propertyRepository, never()).save(any());
    }

    @Test
    void getPropertyDetails_Success() {
        when(propertyRepository.findById(1L)).thenReturn(Optional.of(property));
        when(availabilityRepository.findByPropertyId(1L)).thenReturn(new ArrayList<>());
        when(reviewRepository.findByPropertyIdOrderByCreatedAtDesc(1L)).thenReturn(new ArrayList<>());

        PropertyDetailsResponse response = propertyService.getPropertyDetails(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Taj Luxury Villa", response.getTitle());
        assertEquals("Aarav Sharma", response.getHost().getName());
        assertEquals(0, response.getAvailabilities().size());
        assertEquals(0, response.getReviews().size());
        assertEquals(0.0, response.getAverageRating());
    }
}
