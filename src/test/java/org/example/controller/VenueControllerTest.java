package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.EventApplication;
import org.example.entity.Venue;
import org.example.service.VenueService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * test class for VenueController
 * we use mockMvc to simulate HTTP requests
 */
@WebMvcTest(VenueController.class)
@ContextConfiguration(classes = EventApplication.class)
class VenueControllerTest {
    // tools we need for testing
    @Autowired
    private MockMvc mockMvc;  // for making fake HTTP requests

    @MockBean
    private VenueService venueService;  // fake service layer

    @Autowired
    private ObjectMapper objectMapper;  // for JSON conversion

    // test data
    private Venue testVenue;

    /**
     * runs before each test to set up fresh test data
     */
    @BeforeEach
    void setUp() {
        // create a test venue with all required fields
        testVenue = new Venue(1L, "Test Venue", "123 Test St", 100, null);
    }

    @Test
    void getAllVenues_ShouldReturnVenues() throws Exception {
        // tell fake service to return our test venue when getAllVenues is called
        when(venueService.getAllVenues()).thenReturn(List.of(testVenue));

        // make GET request and verify response
        mockMvc.perform(get("/api/venues"))
                .andExpect(status().isOk())  // expect 200 OK
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("Test Venue"))  // check first venue's name
                .andExpect(jsonPath("$[0].address").value("123 Test St"))
                .andExpect(jsonPath("$[0].capacity").value(100));
    }

    @Test
    void getVenueById_ShouldReturnVenue() throws Exception {
        // tell fake service what to return for ID 1
        when(venueService.getVenueById(1L)).thenReturn(testVenue);

        // make GET request to /api/venues/1
        mockMvc.perform(get("/api/venues/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Test Venue"));  // checking single venue
    }

    @Test
    void createVenue_ShouldReturnCreatedVenue() throws Exception {
        // tell fake service to return our test venue when creating
        when(venueService.createVenue(any(Venue.class))).thenReturn(testVenue);

        // make POST request with venue data
        mockMvc.perform(post("/api/venues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testVenue)))  // convert venue to JSON
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Venue"));
    }

    @Test
    void updateVenue_ShouldReturnUpdatedVenue() throws Exception {
        // create venue with updated data
        Venue updatedVenue = new Venue(1L, "Updated Venue", "456 New St", 200, null);

        // tell fake service to return updated venue
        when(venueService.updateVenue(eq(1L), any(Venue.class))).thenReturn(updatedVenue);

        // make PUT request with updated data
        mockMvc.perform(put("/api/venues/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedVenue)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Venue"))
                .andExpect(jsonPath("$.capacity").value(200));
    }

    @Test
    void deleteVenue_ShouldReturnNoContent() throws Exception {
        // setup void delete method
        doNothing().when(venueService).deleteVenue(1L);

        // make DELETE request
        mockMvc.perform(delete("/api/venues/1"))
                .andExpect(status().isNoContent());  // expect 204 No Content

        // verify delete was called
        verify(venueService).deleteVenue(1L);
    }

    @Test
    void getVenuesByMinCapacity_ShouldReturnVenues() throws Exception {
        // tell fake service what to return for capacity search
        when(venueService.getVenuesByMinCapacity(100)).thenReturn(List.of(testVenue));

        // make GET request with capacity parameter
        mockMvc.perform(get("/api/venues/search")
                        .param("minCapacity", "100"))  // add query parameter
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("Test Venue"))
                .andExpect(jsonPath("$[0].capacity").value(100));
    }
}