package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.entity.Venue;
import org.example.service.VenueService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * rest controller for handling venue-related HTTP requests.
 * this class follows the same pattern as EventController
 */
@RestController
@RequestMapping("/api/venues")  // all URLs will start with /api/venues
@RequiredArgsConstructor        // creates constructor for final fields
public class VenueController {
    // final means this cannot be changed after initialization
    private final VenueService venueService;

    /**
     * gets all venues
     * URL: GET /api/venues
     */
    @GetMapping
    public List<Venue> getAllVenues() {
        return venueService.getAllVenues();
    }

    /**
     * gets a specific venue by its ID
     * URL: GET /api/venues/1
     */
    @GetMapping("/{id}")
    public Venue getVenueById(@PathVariable Long id) {
        return venueService.getVenueById(id);
    }

    /**
     * creates a new venue
     * URL: POST /api/venues
     * body: JSON venue data
     */
    @PostMapping
    public Venue createVenue(@RequestBody Venue venue) {
        return venueService.createVenue(venue);
    }

    /**
     * updates an existing venue
     * URL: PUT /api/venues/1
     * body: JSON venue data
     */
    @PutMapping("/{id}")
    public Venue updateVenue(@PathVariable Long id, @RequestBody Venue venue) {
        return venueService.updateVenue(id, venue);
    }

    /**
     * deletes a venue
     * URL: DELETE /api/venues/1
     * returns: 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVenue(@PathVariable Long id) {
        venueService.deleteVenue(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * searches for venues with capacity greater than or equal to given number
     * URL example: GET /api/venues/search?minCapacity=100
     */
    @GetMapping("/search")
    public List<Venue> getVenuesByMinCapacity(@RequestParam Integer minCapacity) {
        return venueService.getVenuesByMinCapacity(minCapacity);
    }
}