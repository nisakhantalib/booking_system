package org.example.repository;

import org.example.entity.Event;
import org.example.entity.Venue;
import org.example.repository.EventRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;


/**
 * test class for EventRepository. this tests the actual database operations:
 *
 * @DataJpaTest - sets up an in-memory database for testing
 * TestEntityManager - helps us set up test data
 */
@DataJpaTest
class EventRepositoryTest {
    /**
     * entityManager helps us insert test data into the database.
     * it's similar to repository but gives us more control for testing
     */
    @Autowired
    private TestEntityManager entityManager;

    /**
     * the actual repository we're testing.
     * spring creates a real repository that works with the test database
     */
    @Autowired
    private EventRepository eventRepository;

    @Test
    void findByStartTimeBetween_ShouldReturnEvents() {
        // setup: create and save test data
        Venue venue = new Venue(null, "Test Venue", "Test Address", 100, null);
        entityManager.persist(venue);  // save venue first

        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(2);
        Event event = new Event(null, "Test Event", "Test Description", start, end, venue);
        entityManager.persist(event);  // save event
        entityManager.flush();         // ensure data is written to database

        // execute: test the custom finder method
        List<Event> events = eventRepository.findByStartTimeBetween(
                start.minusHours(1), end.plusHours(1));

        // verify: check the results
        assertFalse(events.isEmpty(), "should find at least one event");
        assertEquals(1, events.size(), "should find exactly one event");
        assertEquals("Test Event", events.get(0).getName(), "event name should match");
    }
}