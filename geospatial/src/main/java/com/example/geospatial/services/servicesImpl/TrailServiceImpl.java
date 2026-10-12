package com.example.geospatial.services.servicesImpl;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.geospatial.DTO.TrailDTO;
import com.example.geospatial.models.Trail;
import com.example.geospatial.models.User;
import com.example.geospatial.repositories.TrailRepository;
import com.example.geospatial.requests.UpdateTrailRequest;
import com.example.geospatial.services.TrailService;

@Service
public class TrailServiceImpl implements TrailService{

    @Autowired
    private TrailRepository trailRepository;

    

    public ResponseEntity<?> createTrail(Trail trail){
        try {
            trailRepository.save(trail);
            return ResponseEntity.ok(trail);
        } catch (Exception e){
           return ResponseEntity.badRequest().body("something went wrong with creation of trail");
        }
    }

    public TrailDTO getTrail(long id) {
    Trail trail = trailRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Trail not found with id: " + id));

    return new TrailDTO(
        trail.getId(),
        trail.getName(),
        trail.getLocation(),
        trail.getLengthKm(),
        trail.getDuration(),
        trail.getDifficulty(),
        trail.getDescription(),
        trail.getUser().getEmail(),
        trail.getImage(),
        trail.getGpxPath() != null
        );
    }

    @Override
    public List<TrailDTO> getAllTrail() {
        List<Trail> trails = trailRepository.findAll();

        return trails.stream().map(r ->
        new TrailDTO(
            r.getId(),
            r.getName(),
            r.getLocation(),
            r.getLengthKm(),
            r.getDuration(),
            r.getDifficulty(),
            r.getDescription(),
            r.getUser().getEmail(),
            r.getImage(),
            r.getGpxPath() != null
        )
    ).toList();
    }

    @Override
    public List<TrailDTO> getUserTrail(User user) {
        List<Trail> trails = trailRepository.findByUserId(user.getId());
        return trails.stream().map(r ->
        new TrailDTO(
            r.getId(),
            r.getName(),
            r.getLocation(),
            r.getLengthKm(),
            r.getDuration(),
            r.getDifficulty(),
            r.getDescription(),
            r.getUser().getEmail(),
            r.getImage(),
            r.getGpxPath() != null
        )
    ).toList();
    }

    @Override
    public ResponseEntity<?> updateTrail(Trail trail) {
        Optional<Trail> existingTrailOpt = trailRepository.findById(trail.getId());

        if (existingTrailOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Trail with id " + trail.getId() + " not found");
        }

        Trail existingTrail = existingTrailOpt.get();

        existingTrail.setName(trail.getName());
        existingTrail.setLocation(trail.getLocation());
        existingTrail.setLengthKm(trail.getLengthKm());
        existingTrail.setDuration(trail.getDuration());
        existingTrail.setDifficulty(trail.getDifficulty());
        existingTrail.setDescription(trail.getDescription());
        existingTrail.setImage(trail.getImage());

        trailRepository.save(existingTrail);

        return ResponseEntity.ok("Trail updated successfully!");
    }

    @Override
    public ResponseEntity<?> deleteTrail(long id) {
        Optional<Trail> existingTrailOpt = trailRepository.findById(id);

        if (existingTrailOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Trail with id " + id + " not found");
        }



        trailRepository.deleteById(id);
        
        return ResponseEntity.ok("Deleted!");
    }

    @Override
public ResponseEntity<?> updateTrailDetails(
        long id,
        UpdateTrailRequest request) {

    Optional<Trail> optionalTrail = trailRepository.findById(id);

    if (optionalTrail.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Trail not found"));
    }

    Trail trail = optionalTrail.get();

    trail.setName(request.name().trim());
    trail.setLocation(request.location().trim());
    trail.setDuration(request.duration());
    trail.setDifficulty(request.difficulty());
    trail.setDescription(
        request.description() == null
            ? ""
            : request.description().trim()
    );

    trailRepository.save(trail);

    return ResponseEntity.ok(
        Map.of("message", "Trail updated successfully")
    );
}

}