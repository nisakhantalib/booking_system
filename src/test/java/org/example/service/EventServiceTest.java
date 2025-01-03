package org.example.service;

import org.example.entity.Event;
import org.example.entity.Venue;
import org.example.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * test class for EventService. here's what each annotation does:
 *
 * @ExtendWith(MockitoExtension.class) - enables mockito for testing
 * @Mock - creates a fake version of eventRepository
 * @InjectMocks - creates eventService and injects the mock repository
 *
 * mockito is a framework that lets us create fake objects (mocks) for testing
 */
@ExtendWith(MockitoExtension.class)
class EventServiceTest {
    /**
     * creates a fake repository that we can control in our tests.
     * this lets us test eventService without a real database
     */
    @Mock
    private EventRepository eventRepository;

    /**
     * creates our eventService and automatically puts the fake repository into it.
     * this is what we'll be testing
     */
    @InjectMocks
    private EventService eventService;

    // test data we'll use in multiple tests
    private Event testEvent;
    private Venue testVenue;

    /**
     * runs before each test to set up fresh test data.
     * this ensures each test starts with the same clean state
     */
    @BeforeEach
    void setUp() {
        // create test data
        testVenue = new Venue(1L, "Test Venue", "Test Address", 100, null);
        testEvent = new Event(1L, "Test Event", "Test Description",
                LocalDateTime.now(), LocalDateTime.now().plusHours(2), testVenue);
    }

    /**
     * test method naming convention: methodName_expectedBehavior
     * this helps understand what we're testing and what should happen
     */
    @Test
    void getAllEvents_ShouldReturnListOfEvents() {
        // when: tell the mock repository what to return when findAll() is called
        when(eventRepository.findAll()).thenReturn(List.of(testEvent));

        // execute: call the method we're testing
        List<Event> events = eventService.getAllEvents();

        // verify: check the results
        assertNotNull(events, "events list should not be null");
        assertEquals(1, events.size(), "should return one event");
        // verify: check that findAll was called exactly once
        verify(eventRepository).findAll();
    }

    @Test
    void getEventById_ShouldReturnEvent() {
        // when: mock the repository to return our test event
        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));

        // execute: try to get the event
        Event event = eventService.getEventById(1L);

        // verify: check that we got the right event
        assertNotNull(event, "event should not be null");
        assertEquals("Test Event", event.getName(), "event name should match");
        verify(eventRepository).findById(1L);
    }

    @Test
    void createEvent_ShouldReturnCreatedEvent() {
        // when: mock the save operation
        when(eventRepository.save(any(Event.class))).thenReturn(testEvent);

        // execute: try to create an event
        Event created = eventService.createEvent(testEvent);

        // verify: check the created event
        assertNotNull(created, "created event should not be null");
        assertEquals("Test Event", created.getName(), "event name should match");
        verify(eventRepository).save(testEvent);
    }

    @Test
    void updateEvent_ShouldReturnUpdatedEvent() {
        // when: mock both finding and saving the event
        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));
        when(eventRepository.save(any(Event.class))).thenReturn(testEvent);

        // execute: try to update the event
        Event updated = eventService.updateEvent(1L, testEvent);

        // verify: check the updated event
        assertNotNull(updated, "updated event should not be null");
        assertEquals("Test Event", updated.getName(), "event name should match");
        verify(eventRepository).save(testEvent);
    }

    @Test
    void deleteEvent_ShouldDeleteSuccessfully() {
        // when: mock the delete operation (it returns nothing)
        doNothing().when(eventRepository).deleteById(1L);

        // execute: try to delete the event
        eventService.deleteEvent(1L);

        // verify: check that deleteById was called with the right ID
        verify(eventRepository).deleteById(1L);
    }
}
