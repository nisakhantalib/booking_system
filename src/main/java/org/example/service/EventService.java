package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.entity.Event;
import org.example.repository.EventRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

/**
 * service class that handles business logic for events.
 *
 * important concepts:
 * - @Service: marks this as a Spring service component
 * - @RequiredArgsConstructor: lombok annotation that generates a constructor for final fields
 *
 * this class acts as a middle layer between the controller and repository,
 * handling business logic and data validation.
 */
@Service
@RequiredArgsConstructor
public class EventService {

    /**
     * the eventRepository instance will be automatically injected by Spring.
     * 'final' keyword ensures the repository cannot be changed after initialization.
     */
    private final EventRepository eventRepository;

    /**
     * retrieves all events from the database.
     *
     * @return list of all events
     */
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    /**
     * finds a specific event by its ID.
     *
     * @param id the ID of the event to find
     * @return the found event
     * @throws RuntimeException if the event is not found
     */
    public Event getEventById(Long id) {
        // orElseThrow is a better practice than get() as it handles the null case explicitly
        return eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("event not found with id: " + id));
    }

    /**
     * creates a new event in the database.
     *
     * @param event the event object to be created
     * @return the saved event with generated ID
     */
    public Event createEvent(Event event) {
        // add any validation logic here before saving
        return eventRepository.save(event);
    }

    /**
     * updates an existing event.
     *
     * @param id the ID of the event to update
     * @param event the updated event data
     * @return the updated event
     * @throws RuntimeException if the event is not found
     */
    public Event updateEvent(Long id, Event event) {
        // first check if the event exists
        Event existingEvent = getEventById(id);

        // set the ID to ensure we update the existing event
        event.setId(existingEvent.getId());

        // save the updated event
        return eventRepository.save(event);
    }

    /**
     * deletes an event from the database.
     *
     * @param id the ID of the event to delete
     */
    public void deleteEvent(Long id) {
        // add any deletion validation logic here
        eventRepository.deleteById(id);
    }

    /**
     * finds events within a specific time range.
     *
     * @param start start time of the range
     * @param end end time of the range
     * @return list of events within the specified time range
     */
    public List<Event> getEventsByTimeRange(LocalDateTime start, LocalDateTime end) {
        return eventRepository.findByStartTimeBetween(start, end);
    }
}