package org.example.service;

import org.example.entity.Venue;
import org.example.repository.VenueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * test class for VenueService. we use @ExtendWith(MockitoExtension.class)
 * to enable mocking in our tests
 */
@ExtendWith(MockitoExtension.class)
class VenueServiceTest {

    /**
     * @Mock creates a fake repository we can control
     * this lets us test the service without a real database
     */
    @Mock
    private VenueRepository venueRepository;

    /**
     * @InjectMocks creates our service and puts the fake repository into it
     * this is what we'll be testing
     */
    @InjectMocks
    private VenueService venueService;

    // test data we'll use in multiple tests
    private Venue testVenue;

    @BeforeEach
    void setUp() {
        // create fresh test data before each test
        testVenue = new Venue(1L, "Test Venue", "123 Test St", 100, null);
    }

    @Test
    void getAllVenues_ShouldReturnListOfVenues() {
        // tell the fake repository what to return
        when(venueRepository.findAll()).thenReturn(List.of(testVenue));

        // call the service method
        List<Venue> venues = venueService.getAllVenues();

        // verify the results
        assertNotNull(venues, "venues list should not be null");
        assertEquals(1, venues.size(), "should return one venue");
        assertEquals("Test Venue", venues.get(0).getName(), "venue name should match");

        // verify the repository method was called
        verify(venueRepository).findAll();
    }

    @Test
    void getVenueById_ShouldReturnVenue() {
        // tell repository to return our test venue when asked
        when(venueRepository.findById(1L)).thenReturn(Optional.of(testVenue));

        // try to get the venue
        Venue found = venueService.getVenueById(1L);

        // verify what we got
        assertNotNull(found, "should find a venue");
        assertEquals("Test Venue", found.getName(), "venue name should match");
        verify(venueRepository).findById(1L);
    }

    @Test
    void getVenueById_ShouldThrowException_WhenVenueNotFound() {
        // tell repository to return empty (no venue found)
        when(venueRepository.findById(1L)).thenReturn(Optional.empty());

        // try to get non-existent venue and expect exception
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            venueService.getVenueById(1L);
        });

        // verify exception message
        assertEquals("Venue not found", exception.getMessage());
        verify(venueRepository).findById(1L);
    }

    @Test
    void createVenue_ShouldReturnCreatedVenue() {
        // tell repository what to return when saving
        when(venueRepository.save(any(Venue.class))).thenReturn(testVenue);

        // try to create a venue
        Venue created = venueService.createVenue(testVenue);

        // verify the created venue
        assertNotNull(created, "created venue should not be null");
        assertEquals("Test Venue", created.getName(), "venue name should match");
        verify(venueRepository).save(testVenue);
    }

    @Test
    void updateVenue_ShouldReturnUpdatedVenue() {
        // create venue with updated data
        Venue updatedVenue = new Venue(1L, "Updated Venue", "456 New St", 200, null);

        // tell repository what to return for find and save
        when(venueRepository.findById(1L)).thenReturn(Optional.of(testVenue));
        when(venueRepository.save(any(Venue.class))).thenReturn(updatedVenue);

        // try to update the venue
        Venue updated = venueService.updateVenue(1L, updatedVenue);

        // verify the update
        assertNotNull(updated, "updated venue should not be null");
        assertEquals("Updated Venue", updated.getName(), "venue name should be updated");
        assertEquals(200, updated.getCapacity(), "capacity should be updated");
        verify(venueRepository).findById(1L);
        verify(venueRepository).save(any(Venue.class));
    }

    @Test
    void deleteVenue_ShouldCallRepository() {
        // tell repository to do nothing when delete is called (it's void)
        doNothing().when(venueRepository).deleteById(1L);

        // try to delete
        venueService.deleteVenue(1L);

        // verify delete was called with right ID
        verify(venueRepository).deleteById(1L);
    }

    @Test
    void getVenuesByMinCapacity_ShouldReturnVenues() {
        // tell repository what to return for capacity search
        when(venueRepository.findByCapacityGreaterThanEqual(100))
                .thenReturn(List.of(testVenue));

        // search for venues
        List<Venue> venues = venueService.getVenuesByMinCapacity(100);

        // verify results
        assertNotNull(venues, "venues list should not be null");
        assertEquals(1, venues.size(), "should find one venue");
        assertEquals(100, venues.get(0).getCapacity(), "venue capacity should match");
        verify(venueRepository).findByCapacityGreaterThanEqual(100);
    }
}