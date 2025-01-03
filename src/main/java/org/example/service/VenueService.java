package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.entity.Venue;
import org.example.repository.VenueRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VenueService {
    private final VenueRepository venueRepository;

    public List<Venue> getAllVenues() {
        return venueRepository.findAll();
    }

    public Venue getVenueById(Long id) {
        return venueRepository.findById(id).orElseThrow(() -> new RuntimeException("Venue not found"));
    }

    public Venue createVenue(Venue venue) {
        return venueRepository.save(venue);
    }

    public Venue updateVenue(Long id, Venue venue) {
        Venue existingVenue = getVenueById(id);
        venue.setId(existingVenue.getId());
        return venueRepository.save(venue);
    }

    public void deleteVenue(Long id) {
        venueRepository.deleteById(id);
    }

    public List<Venue> getVenuesByMinCapacity(Integer capacity) {
        return venueRepository.findByCapacityGreaterThanEqual(capacity);
    }
}