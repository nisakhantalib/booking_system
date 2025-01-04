package org.example.repository;

import org.example.entity.Event;
import org.example.entity.Venue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class EventRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private VenueRepository venueRepository;

    private Venue testVenue;
    private Event testEvent;
    private LocalDateTime baseTime;

    @BeforeEach
    void setUp() {
        // setup common test data
        baseTime = LocalDateTime.now();

        // create test venue
        testVenue = new Venue();
        testVenue.setName("Test Venue");
        testVenue.setAddress("123 Test St");
        testVenue.setCapacity(100);

        // Using repository.save() instead of entityManager.persist()
        testVenue = venueRepository.save(testVenue);
    }

    @Test
    void findByStartTimeBetween_ShouldReturnEvents() {
        // create test event
        Event event = new Event();
        event.setName("Test Event");
        event.setDescription("Test Description");
        event.setStartTime(baseTime);
        event.setEndTime(baseTime.plusHours(2));
        event.setVenue(testVenue);

        // save using repository instead of entityManager
        eventRepository.save(event);

        // search for events
        List<Event> events = eventRepository.findByStartTimeBetween(
                baseTime.minusHours(1),
                baseTime.plusHours(3)
        );

        // verify results
        assertNotNull(events, "events list should not be null");
        assertEquals(1, events.size(), "should find one event");
        assertEquals("Test Event", events.get(0).getName(), "event name should match");
    }

    @Test
    void findByStartTimeBetween_ShouldNotReturnEventsOutsideTimeRange() {
        // create an event outside our search range
        Event event = new Event();
        event.setName("Future Event");
        event.setDescription("Future Description");
        event.setStartTime(baseTime.plusDays(1));  // tomorrow
        event.setEndTime(baseTime.plusDays(1).plusHours(2));
        event.setVenue(testVenue);

        eventRepository.save(event);

        // search for events in current time range
        List<Event> events = eventRepository.findByStartTimeBetween(
                baseTime,
                baseTime.plusHours(3)
        );

        // verify no events found
        assertTrue(events.isEmpty(), "should not find events outside time range");
    }

    @Test
    void findByVenueId_ShouldReturnEvents() {
        // create multiple events for the same venue
        Event event1 = new Event();
        event1.setName("Event 1");
        event1.setStartTime(baseTime);
        event1.setEndTime(baseTime.plusHours(2));
        event1.setVenue(testVenue);

        Event event2 = new Event();
        event2.setName("Event 2");
        event2.setStartTime(baseTime.plusHours(3));
        event2.setEndTime(baseTime.plusHours(5));
        event2.setVenue(testVenue);

        eventRepository.save(event1);
        eventRepository.save(event2);

        // find events by venue
        List<Event> events = eventRepository.findByVenueId(testVenue.getId());

        // verify results
        assertEquals(2, events.size(), "should find two events");
        assertTrue(events.stream().anyMatch(e -> e.getName().equals("Event 1")),
                "should contain Event 1");
        assertTrue(events.stream().anyMatch(e -> e.getName().equals("Event 2")),
                "should contain Event 2");
    }

    @Test
    void findUpcomingEvents_ShouldReturnFutureEvents() {
        // Use fixed time for predictable testing
        LocalDateTime baseTime = LocalDateTime.of(2025, 1, 1, 12, 0); // noon on Jan 1, 2025

        Event pastEvent = createEvent("Past Event", baseTime.minusHours(2));    // 10:00
        Event currentEvent = createEvent("Current Event", baseTime);            // 12:00
        Event futureEvent = createEvent("Future Event", baseTime.plusHours(2)); // 14:00

        eventRepository.save(pastEvent);
        eventRepository.save(currentEvent);
        eventRepository.save(futureEvent);

        // Find events after 12:00
        List<Event> upcomingEvents = eventRepository.findByStartTimeAfter(baseTime);

        assertEquals(1, upcomingEvents.size(), "should find only the future event");
        assertEquals("Future Event", upcomingEvents.get(0).getName());}

    // helper method to create events
    private Event createEvent(String name, LocalDateTime startTime) {
        Event event = new Event();
        event.setName(name);
        event.setDescription(name + " Description");
        event.setStartTime(startTime);
        event.setEndTime(startTime.plusHours(2));
        event.setVenue(testVenue);
        return event;
    }
}