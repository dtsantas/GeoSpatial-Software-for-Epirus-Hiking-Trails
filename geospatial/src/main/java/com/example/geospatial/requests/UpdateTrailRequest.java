package com.example.geospatial.requests;

import jakarta.validation.constraints.*;

public record UpdateTrailRequest(

    @NotBlank
    String name,

    @NotBlank
    String location,

    @NotNull
    @DecimalMin("0.01")
    Double duration,

    @NotBlank
    @Pattern(regexp = "Easy|Mid|Hard")
    String difficulty,

    @Size(max = 2000)
    String description

) {}