package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.entity.Event;
import org.example.entity.Venue;
import org.example.service.EventService;
import org.example.service.VenueService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller // for web view
@RequestMapping("events") // Note: /events, not /api/events
@RequiredArgsConstructor
public class EventViewController {
    private final EventService eventService;
    private final VenueService venueService;

    @GetMapping
    public String listEvents(Model model){
        // Add events to the model to display in the view
        model.addAttribute("events", eventService.getAllEvents());
        return "events/list";  // this will look for templates/events/list.html
    }

    @GetMapping("/add")
    public String showAddEventForm(Model model) {
        // Create an empty event object for the form
        model.addAttribute("event", new Event());
        // Add list of venues for the dropdown
        model.addAttribute("venues", venueService.getAllVenues());
        return "events/add";  // this will look for templates/events/add.html
    }

    @PostMapping("/add")
    public String addEvent(@ModelAttribute Event event) {
        eventService.createEvent(event);
        return "redirect:/events";  // redirect back to the events list
    }

    @PostMapping("/delete/{id}")
    public String deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return "redirect:/events";
    }

    @GetMapping("/edit/{id}")
    public String editEvent(@PathVariable Long id, Model model){
        Event event = eventService.getEventById(id);
        model.addAttribute("event", event);
        model.addAttribute("venues", venueService.getAllVenues());
        return "events/edit";
    }

    @PostMapping("/update/{id}")
    public String updateEvent(@PathVariable Long id, @ModelAttribute Event event) {
        // Make sure venue relationship is preserved
        Event existingEvent = eventService.getEventById(id);
        event.setId(id);


        // Update the fields
        existingEvent.setName(event.getName());
        existingEvent.setDescription(event.getDescription());
        existingEvent.setStartTime(event.getStartTime());
        existingEvent.setEndTime(event.getEndTime());
        existingEvent.setVenue(event.getVenue());
        eventService.updateEvent(id, event);


        return "redirect:/events";
    }
}


