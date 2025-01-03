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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * test class for the EventController. this uses spring's testing support:
 *
 * @WebMvcTest - sets up a test environment for testing REST controllers
 * MockMvc - lets us simulate HTTP requests
 * @MockBean - creates a spring-managed mock
 */
@WebMvcTest(EventController.class)
@ContextConfiguration(classes = EventApplication.class)
class EventControllerTest {
    /**
     * mockMvc helps us simulate HTTP requests without starting a real server.
     * it's automatically configured by @WebMvcTest
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * create a mock of the service layer.
     * @MockBean is similar to @Mock but works with Spring's dependency injection
     */
    @MockBean
    private EventService eventService;

    /**
     * objectMapper helps convert between Java objects and JSON
     */
    @Autowired
    private ObjectMapper objectMapper;

    // test data
    private Event testEvent;
    private Venue testVenue;

    @BeforeEach
    void setUp() {
        testVenue = new Venue(1L, "Test Venue", "Test Address", 100, null);
        testEvent = new Event(1L, "Test Event", "Test Description",
                LocalDateTime.now(), LocalDateTime.now().plusHours(2), testVenue);
    }

    @Test
    void getAllEvents_ShouldReturnEvents() throws Exception {
        // when: mock the service to return our test event
        when(eventService.getAllEvents()).thenReturn(List.of(testEvent));

        // execute and verify: perform GET request and check response
        mockMvc.perform(get("/api/events"))                    // simulate GET request
                .andExpect(status().isOk())                    // expect 200 OK
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))  // expect JSON
                .andExpect(jsonPath("$[0].name").value("Test Event"));        // check JSON content
    }

    @Test
    void getEventById_ShouldReturnEvent() throws Exception {
        // when: mock the service layer
        when(eventService.getEventById(1L)).thenReturn(testEvent);

        // execute and verify
        mockMvc.perform(get("/api/events/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Test Event"));
    }

    @Test
    void createEvent_ShouldReturnCreatedEvent() throws Exception {
        // when: mock the service layer
        when(eventService.createEvent(any(Event.class))).thenReturn(testEvent);

        // execute and verify
        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testEvent)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Event"));
    }
}