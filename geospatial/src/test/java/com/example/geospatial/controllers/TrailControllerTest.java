package com.example.geospatial.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.example.geospatial.DTO.TrailDTO;
import com.example.geospatial.models.CustomUserDetails;
import com.example.geospatial.models.Trail;
import com.example.geospatial.models.User;
import com.example.geospatial.services.TrailService;
import com.example.geospatial.services.SupabaseStorageService;


@ExtendWith(MockitoExtension.class)
class TrailControllerTest {

    @Mock
    private TrailService trailServiceImpl;

    @Mock
    private CustomUserDetails customUserDetails;

    @Mock
    private SupabaseStorageService supabaseStorageService;

    @InjectMocks
    private TrailController trailController;

    private Trail testTrail;
    private TrailDTO testTrailDTO;
    private User testUser;

    @BeforeEach
void setUp() {
    testUser = new User();
    testUser.setId(1L);
    testUser.setEmail("test@mail.com");

    testTrail = new Trail();
    testTrail.setId(1L);
    testTrail.setName("Mountain Trail");
    testTrail.setLocation("Alps");
    testTrail.setLengthKm(12.5);
    testTrail.setDuration(3.0);
    testTrail.setDifficulty("HARD");
    testTrail.setDescription("A scenic mountain trail");
    testTrail.setUser(testUser);

    testTrailDTO = new TrailDTO(
            1L,
            "Mountain Trail",   
            "Alps",             
            12.5,               
            3.0,                
            "HARD",            
            "A scenic mountain trail", 
            "test@mail.com",    
            "uploads/img.jpg" ,
            true 
    );
}




@Test
void getTrail_shouldReturn500_whenServiceThrows() {
    when(trailServiceImpl.getTrail(1L)).thenThrow(new RuntimeException("DB error"));

    ResponseEntity<?> response = trailController.getTrail(1L);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertErrorMessage(response, "Unable to fetch trail.");
}



    @Test
    void getAllTrail_shouldReturnOkWithEmptyList() {
        when(trailServiceImpl.getAllTrail()).thenReturn(Collections.emptyList());

        ResponseEntity<?> response = trailController.getAllTrail();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Collections.emptyList(), response.getBody());
    }

    @Test
    void getAllTrail_shouldReturn500_whenServiceThrows() {
        when(trailServiceImpl.getAllTrail()).thenThrow(new RuntimeException("DB error"));

        ResponseEntity<?> response = trailController.getAllTrail();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertErrorMessage(response, "Unable to fetch trails.");
    }



    @Test
    void getUserTrail_shouldReturnOkWithUserTrails() {
        List<TrailDTO> trails = List.of(testTrailDTO);
        when(customUserDetails.getUser()).thenReturn(testUser);
        when(trailServiceImpl.getUserTrail(testUser)).thenReturn(trails);

        ResponseEntity<?> response = trailController.getUserTrail(customUserDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(trails, response.getBody());
        verify(trailServiceImpl).getUserTrail(testUser);
    }

    @Test
    void getUserTrail_shouldReturn500_whenServiceThrows() {
        when(customUserDetails.getUser()).thenReturn(testUser);
        when(trailServiceImpl.getUserTrail(testUser)).thenThrow(new RuntimeException("DB error"));

        ResponseEntity<?> response = trailController.getUserTrail(customUserDetails);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertErrorMessage(response, "Unable to fetch user trails.");
    }

    @Test
    void getUserTrail_shouldReturn500_whenCurrentUserIsNull() {
        ResponseEntity<?> response = trailController.getUserTrail(null);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertErrorMessage(response, "Unable to fetch user trails.");
    }



    @Test
void createTrail_shouldReturnOk_whenValidRequest() throws Exception {
    MockMultipartFile image = new MockMultipartFile(
            "image", "trail.jpg", "image/jpeg", "fake-image-bytes".getBytes()
    );
    when(supabaseStorageService.uploadImage(any(MultipartFile.class)))
        .thenReturn("https://example.com/trail.jpg");
    when(customUserDetails.getUser()).thenReturn(testUser);
    doReturn(ResponseEntity.ok("Trail created successfully"))
            .when(trailServiceImpl).createTrail(any(Trail.class));

    ResponseEntity<?> response = trailController.createTrail(
            "Mountain Trail", "Alps", 12.5, 3.0, "HARD",
            "A scenic trail", image, customUserDetails
    );

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertSuccessMessage(response, "Trail created successfully");
    verify(trailServiceImpl, times(1)).createTrail(any(Trail.class));
}

@Test
void createTrail_shouldReturn500_whenServiceThrows() throws Exception {
    MockMultipartFile image = new MockMultipartFile(
            "image", "trail.jpg", "image/jpeg", "fake-image-bytes".getBytes()
    );
    when(supabaseStorageService.uploadImage(any(MultipartFile.class)))
        .thenReturn("https://example.com/trail.jpg");
    when(customUserDetails.getUser()).thenReturn(testUser);
    doThrow(new RuntimeException("Storage error"))
            .when(trailServiceImpl).createTrail(any(Trail.class));

    ResponseEntity<?> response = trailController.createTrail(
            "Mountain Trail", "Alps", 12.5, 3.0, "HARD",
            "A scenic trail", image, customUserDetails
    );

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertErrorMessage(response, "Unable to create trail.");
}

    @Test
void createTrail_shouldMapFieldsCorrectly() throws Exception {
    MockMultipartFile image = new MockMultipartFile(
            "image", "trail.jpg", "image/jpeg", "fake-image-bytes".getBytes()
    );
    when(supabaseStorageService.uploadImage(any(MultipartFile.class)))
        .thenReturn("https://example.com/trail.jpg");
    when(customUserDetails.getUser()).thenReturn(testUser);
    doReturn(ResponseEntity.ok("Trail created successfully"))
            .when(trailServiceImpl).createTrail(any(Trail.class));

    trailController.createTrail(
            "Mountain Trail", "Alps", 12.5, 3.0, "HARD",
            "A scenic trail", image, customUserDetails
    );

    verify(trailServiceImpl).createTrail(argThat(trail ->
            trail.getName().equals("Mountain Trail") &&
            trail.getLocation().equals("Alps") &&
            trail.getLengthKm().equals(12.5) &&
            trail.getDuration().equals(3.0) &&
            trail.getDifficulty().equals("HARD") &&
            trail.getUser().equals(testUser) &&
            "https://example.com/trail.jpg".equals(trail.getImage())
    ));
}



    @Test
    void updateTrail_shouldReturnOk_whenServiceSucceeds() {
        doReturn(ResponseEntity.ok("Updated")).when(trailServiceImpl).updateTrail(testTrail);

        ResponseEntity<?> response = trailController.updateTrail(testTrail);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(trailServiceImpl, times(1)).updateTrail(testTrail);
    }

    @Test
    void updateTrail_shouldReturn500_whenServiceThrows() {
        when(trailServiceImpl.updateTrail(testTrail)).thenThrow(new RuntimeException("DB error"));

        ResponseEntity<?> response = trailController.updateTrail(testTrail);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertErrorMessage(response, "Unable to update trail.");
    }



    @Test
    void deleteTrail_shouldReturnOk_whenServiceSucceeds() {
        doReturn(ResponseEntity.ok("Deleted")).when(trailServiceImpl).deleteTrail(1L);

        ResponseEntity<?> response = trailController.deleteTrail(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(trailServiceImpl, times(1)).deleteTrail(1L);
    }

    @Test
    void deleteTrail_shouldReturn500_whenServiceThrows() {
        when(trailServiceImpl.deleteTrail(1L)).thenThrow(new RuntimeException("DB error"));

        ResponseEntity<?> response = trailController.deleteTrail(1L);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertErrorMessage(response, "Unable to delete trail.");
    }

    @Test
    void deleteTrail_shouldCallServiceWithCorrectId() {
        doReturn(ResponseEntity.ok("Deleted")).when(trailServiceImpl).deleteTrail(99L);

        trailController.deleteTrail(99L);

        verify(trailServiceImpl).deleteTrail(99L);
        verify(trailServiceImpl, never()).deleteTrail(1L);
    }


    @SuppressWarnings("unchecked")
    private void assertErrorMessage(ResponseEntity<?> response, String expectedMessage) {
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertNotNull(body);
        assertEquals(expectedMessage, body.get("error"));
    }

    @SuppressWarnings("unchecked")
    private void assertSuccessMessage(ResponseEntity<?> response, String expectedMessage) {
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertNotNull(body);
        assertEquals(expectedMessage, body.get("message"));
    }
}