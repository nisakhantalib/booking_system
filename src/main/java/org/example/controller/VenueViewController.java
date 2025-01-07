package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.entity.Venue;
import org.example.service.EventService;
import org.example.service.VenueService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller // for web view
@RequestMapping("venues") // Note: /venues, not /api/venues
@RequiredArgsConstructor
public class VenueViewController {
    private final VenueService  venueService;

    @GetMapping
    public String listVenues(Model model){
        // Add venues to the model to display in the view
        model.addAttribute("venues", venueService.getAllVenues());
        return "venues/list";  // this will look for templates/venues/list.html
    }

    @GetMapping("/add")
    public String showAddVenueForm(Model model) {
        model.addAttribute("venue", new Venue());
        return "venues/add";
    }

    @PostMapping("/add")
    public String addVenue(@ModelAttribute Venue venue) {
        venueService.createVenue(venue);
        return "redirect:/venues";
    }

    @PostMapping("/delete/{id}")
    public String deleteVenue(@PathVariable Long id) {
        venueService.deleteVenue(id);
        return "redirect:/venues";
    }

    @GetMapping("/edit/{id}")
    public String editVenue(@PathVariable Long id, Model model) {
        Venue venue = venueService.getVenueById(id);
        model.addAttribute("venue", venue);
        return "venues/edit";
    }

    @PostMapping("/update/{id}")
    public String updateVenue(@PathVariable Long id, @ModelAttribute Venue venue) {
        venueService.updateVenue(id, venue);
        return "redirect:/venues";
    }


}
