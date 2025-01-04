package org.example.repository;

import org.example.entity.Venue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class VenueRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private VenueRepository venueRepository;

    @Test
    void whenSaveVenue_thenCanFindIt(){
        //1. create a test venue
        Venue venue= new Venue();
        venue.setName("Test Venue");
        venue.setAddress("123 Test Street");
        venue.setCapacity(100);

        //2. save it
        venueRepository.save(venue);

        //3. try to find it
        Venue foundVenue= venueRepository.findById(venue.getId()).orElse(null);

        // 4. Check if what we found matches what we saved
        assertNotNull(foundVenue, "Venue should be found");  // make sure we found something
        assertEquals("Test Venue", foundVenue.getName(), "Venue name should match");
        assertEquals("123 Test Street", foundVenue.getAddress(), "Venue address should match");
        assertEquals(100, foundVenue.getCapacity(), "Venue capacity should match");
    }

    @Test
    void findByCapacityGreaterThanEqual_shouldFindRightVenuea(){
        //1. create test venues
        Venue venue1= new Venue();
        venue1.setName("Test Venue 1");
        venue1.setAddress("123 Test Street");
        venue1.setCapacity(100);

        Venue venue2= new Venue();
        venue2.setName("Test Venue 2");
        venue2.setAddress("456 Test Street");
        venue2.setCapacity(200);

        Venue venue3= new Venue();
        venue3.setName("Test Venue 3");
        venue3.setAddress("789 Test Street");
        venue3.setCapacity(300);

        //2. save it
        venueRepository.save(venue1);
        venueRepository.save(venue2);
        venueRepository.save(venue3);


        //3. search for venues above 250
        List<Venue> foundVenues= venueRepository.findByCapacityGreaterThanEqual(250);

        //4. asserts

        assertNotNull(foundVenues, "Venue should be found");
        assertEquals(1, foundVenues.size(), "Should find exactly 1 venue");
        assertEquals("Test Venue 3", foundVenues.get(0).getName());
        assertEquals(300, foundVenues.get(0).getCapacity(), "Capacity should match");






    }


    @Test
    void findByCapacityGreaterThanEqual_shouldReturnEmptyWhenNoMatches() {
        // 1. create venues with smaller capacities
        Venue venue1 = new Venue();
        venue1.setName("Small Venue 1");
        venue1.setAddress("123 Test Street");
        venue1.setCapacity(50);


        Venue venue2 = new Venue();
        venue2.setName("Small Venue 2");
        venue2.setAddress("456 Test Street");
        venue2.setCapacity(75);

        // 2. save venues
        venueRepository.save(venue1);
        venueRepository.save(venue2);

        // 3. search for venues with larger capacity than any we have
        List<Venue> foundVenues = venueRepository.findByCapacityGreaterThanEqual(500);

        // 4. verify we get empty list, not null
        assertNotNull(foundVenues, "Should return empty list, not null");
        assertTrue(foundVenues.isEmpty(), "Venue list should be empty");
    }

    @Test
    void findAll_shouldReturnAllVenues() {
        // 1. create test venues
        Venue venue1 = new Venue();
        venue1.setName("Venue 1");
        venue1.setAddress("123 Test Street");
        venue1.setCapacity(100);

        Venue venue2 = new Venue();
        venue2.setName("Venue 2");
        venue2.setAddress("456 Test Street");
        venue2.setCapacity(200);

        // 2. save venues
        venueRepository.save(venue1);
        venueRepository.save(venue2);

        // 3. find all venues
        List<Venue> allVenues = venueRepository.findAll();

        // 4. verify we get all venues
        assertNotNull(allVenues, "Venues list should not be null");
        assertEquals(2, allVenues.size(), "Should find all venues");
        //use stream if we don't know the exact order
        assertTrue(allVenues.stream().anyMatch(v -> v.getName().equals("Venue 1")), "Should contain Venue 1");
        assertTrue(allVenues.stream().anyMatch(v -> v.getName().equals("Venue 2")), "Should contain Venue 2");

        //alternative way to do it
//        assertEquals("Venue 1", allVenues.get(0).getName(), "First venue should be Venue 1");
//        assertEquals("Venue 2", allVenues.get(1).getName(), "Second venue should be Venue 2");
//
    }

    @Test
    void deleteVenue_shouldRemoveVenue() {
        // 1. create and save a venue
        Venue venue = new Venue();
        venue.setName("Venue to Delete");
        venue.setAddress("123 Test Street");
        venue.setCapacity(100);

        // 2. save it and get its ID
        Venue savedVenue = venueRepository.save(venue);
        Long venueId = savedVenue.getId();

        // 3. make sure it exists
        assertTrue(venueRepository.findById(venueId).isPresent(), "Venue should exist before deletion");

        // 4. delete it
        venueRepository.deleteById(venueId);

        // 5. verify it's gone
        assertFalse(venueRepository.findById(venueId).isPresent(), "Venue should not exist after deletion");
    }

}
