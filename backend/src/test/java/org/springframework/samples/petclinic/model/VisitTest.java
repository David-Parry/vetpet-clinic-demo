package org.springframework.samples.petclinic.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for Visit model
 */
class VisitTest {

    @Test
    void shouldSetAndGetDate() {
        // Given
        Visit visit = new Visit();
        LocalDate date = LocalDate.of(2023, 6, 15);

        // When
        visit.setDate(date);

        // Then
        assertThat(visit.getDate()).isEqualTo(date);
    }

    @Test
    void shouldSetAndGetDescription() {
        // Given
        Visit visit = new Visit();
        String description = "Annual checkup";

        // When
        visit.setDescription(description);

        // Then
        assertThat(visit.getDescription()).isEqualTo(description);
    }

    @Test
    void shouldSetAndGetPet() {
        // Given
        Visit visit = new Visit();
        Pet pet = new Pet();
        pet.setName("Fluffy");

        // When
        visit.setPet(pet);

        // Then
        assertThat(visit.getPet()).isEqualTo(pet);
    }

    @Test
    void shouldSetAndGetVetId() {
        // Given
        Visit visit = new Visit();
        Integer vetId = 5;

        // When
        visit.setVetId(vetId);

        // Then
        assertThat(visit.getVetId()).isEqualTo(vetId);
        assertThat(visit.hasVetId()).isTrue();
    }

    @Test
    void shouldReturnFalseWhenNoVetId() {
        // Given
        Visit visit = new Visit();

        // Then
        assertThat(visit.hasVetId()).isFalse();
    }

    @Test
    void shouldInitializeWithCurrentDate() {
        // When
        Visit visit = new Visit();

        // Then
        assertThat(visit.getDate()).isNotNull();
        assertThat(visit.getDate()).isEqualTo(LocalDate.now());
    }
}
