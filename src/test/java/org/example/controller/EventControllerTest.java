package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.EventApplication;
import org.example.entity.Event;
import org.example.entity.Venue;
import org.example.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

// these static imports are needed for mockMvc and mockito methods
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * test class for EventController. we use @WebMvcTest instead of @SpringBootTest
 * because we only want to test the web layer, not the entire application
 */
@WebMvcTest(EventController.class)
@ContextConfiguration(classes = EventApplication.class)
class EventControllerTest {
    // mockMvc lets us simulate HTTP requests like GET, POST, etc.
    @Autowired
    private MockMvc mockMvc;

    // this creates a fake version of eventService that we can control
    @MockBean
    private EventService eventService;

    // objectMapper helps convert Java objects to JSON and back
    @Autowired
    private ObjectMapper objectMapper;

    // test data we'll use in multiple tests
    private Event testEvent;
    private Venue testVenue;
    private LocalDateTime testTime;

    /**
     * this method runs before each test.
     * we create fresh test data here to avoid test interference
     */
    @BeforeEach
    void setUp() {
        // use a fixed time for predictable testing
        testTime = LocalDateTime.of(2025, 1, 1, 12, 0); // noon on Jan 1, 2025

        // create a test venue first because events need a venue
        testVenue = new Venue(1L, "Test Venue", "Test Address", 100, null);

        // create a test event that uses our venue
        testEvent = new Event(1L, "Test Event", "Test Description",
                testTime, testTime.plusHours(2), testVenue);
    }

    @Test
    void getAllEvents_ShouldReturnEvents() throws Exception {
        // tell the fake service what to return when getAllEvents is called
        when(eventService.getAllEvents()).thenReturn(List.of(testEvent));

        // make a GET request to /api/events and verify:
        mockMvc.perform(get("/api/events"))
                .andExpect(status().isOk())                    // http 200 OK status
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))  // response is JSON
                .andExpect(jsonPath("$[0].name").value("Test Event"))         // first event name
                .andExpect(jsonPath("$[0].description").value("Test Description")); // first event description
    }

    @Test
    void getEventById_ShouldReturnEvent() throws Exception {
        // tell fake service what to return when getEventById(1L) is called
        when(eventService.getEventById(1L)).thenReturn(testEvent);

        // make a GET request to /api/events/1 and verify:
        mockMvc.perform(get("/api/events/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // use $.name instead of $[0].name because we're getting a single object, not an array
                .andExpect(jsonPath("$.name").value("Test Event"));
    }

    @Test
    void createEvent_ShouldReturnCreatedEvent() throws Exception {
        // tell fake service to return our test event when any Event is passed to createEvent
        when(eventService.createEvent(any(Event.class))).thenReturn(testEvent);

        // make a POST request to /api/events with our test event as JSON
        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)  // tell server we're sending JSON
                        .content(objectMapper.writeValueAsString(testEvent)))  // convert event to JSON
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Event"));
    }

    @Test
    void updateEvent_ShouldReturnUpdatedEvent() throws Exception {
        // create an updated event with changes
        Event updatedEvent = new Event(1L, "Updated Event Name", "Updated Description",
                testTime, testTime.plusHours(3), testVenue);  // notice the changes

        // tell fake service what to return when update is called
        when(eventService.updateEvent(eq(1L), any(Event.class))).thenReturn(updatedEvent);

        // make the PUT request with our updated data
        mockMvc.perform(put("/api/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedEvent)))  // send updated data
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Event Name"))  // verify new name
                .andExpect(jsonPath("$.description").value("Updated Description")); // verify new description
    }

    @Test
    void deleteEvent_ShouldReturnNoContent() throws Exception {
        // tell fake service to do nothing when deleteEvent is called (since it returns void)
        doNothing().when(eventService).deleteEvent(1L);

        // make a DELETE request to /api/events/1
        mockMvc.perform(delete("/api/events/1"))
                .andExpect(status().isNoContent());  // expect 204 No Content status
    }

    @Test
    void getEventsByTimeRange_ShouldReturnEvents() throws Exception {
        // setup test times
        LocalDateTime start = testTime;
        LocalDateTime end = testTime.plusHours(2);

        // tell fake service what to return when getEventsByTimeRange is called with any times
        when(eventService.getEventsByTimeRange(any(LocalDateTime.class),
                any(LocalDateTime.class)))
                .thenReturn(List.of(testEvent));

        // make a GET request to /api/events/search with start and end time parameters
        mockMvc.perform(get("/api/events/search")
                        .param("start", start.toString())  // add start time as query parameter
                        .param("end", end.toString()))     // add end time as query parameter
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("Test Event"));
    }
}