package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.entity.Event;
import org.example.service.EventService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 REST Controller for handling Event-related HTTP requests.

  important annotations:
  - @RestController: Combines @Controller and @ResponseBody, making this a REST controller
  - @RequestMapping: Maps URLs starting with "/api/events" to this controller
  - @RequiredArgsConstructor: Generates constructor for final fields

 this controller handles all HTTP requests related to Events (CRUD operations).
 */
@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;


    @GetMapping
    public List<Event> getAllEvents() {
        return eventService.getAllEvents();
    }

    /**
      handles GET requests to fetch a specific event by ID.
      URL: GET /api/events/{id}
     */
    @GetMapping("/{id}")
    public Event getEventById(@PathVariable Long id) {
        return eventService.getEventById(id);
    }

    /**
     handles POST requests to create a new event.
     URL: POST /api/events
     return the created event with generated ID
     */
    @PostMapping
    public Event createEvent(@RequestBody Event event) {
        return eventService.createEvent(event);
    }

    /**
     handles PUT requests to update an existing event.
     URL: PUT /api/events/{id}
     @param id The ID of the event to update
     @param event The updated event data
     return The updated event
     */
    @PutMapping("/{id}")
    public Event updateEvent(@PathVariable Long id, @RequestBody Event event) {
        return eventService.updateEvent(id, event);
    }

    /**
     handles DELETE requests to remove an event.
     URL: DELETE /api/events/{id}
     @param id The ID of the event to delete
     return ResponseEntity with no content (204 status)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    /**
     handles GET requests to search events by time range.
     URL: GET /api/events/search?start=...&end=...

     @param start Start time (ISO format)
     @param end End time (ISO format)
     return List of events within the specified time range
     */
    @GetMapping("/search")
    public List<Event> getEventsByTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return eventService.getEventsByTimeRange(start, end);
    }
}