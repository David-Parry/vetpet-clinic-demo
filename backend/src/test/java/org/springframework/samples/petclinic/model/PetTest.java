package org.springframework.samples.petclinic.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for Pet model
 */
class PetTest {

    @Test
    void shouldAddVisitToPet() {
        // Given
        Pet pet = new Pet();
        pet.setName("Fluffy");

        Visit visit = new Visit();
        visit.setDescription("Annual checkup");
        visit.setDate(LocalDate.now());

        // When
        pet.addVisit(visit);

        // Then
        assertThat(pet.getVisits()).hasSize(1);
        assertThat(pet.getVisits()).contains(visit);
        assertThat(visit.getPet()).isEqualTo(pet);
    }

    @Test
    void shouldSetAndGetBirthDate() {
        // Given
        Pet pet = new Pet();
        LocalDate birthDate = LocalDate.of(2020, 5, 15);

        // When
        pet.setBirthDate(birthDate);

        // Then
        assertThat(pet.getBirthDate()).isEqualTo(birthDate);
    }

    @Test
    void shouldSetAndGetType() {
        // Given
        Pet pet = new Pet();
        PetType type = new PetType();
        type.setName("cat");

        // When
        pet.setType(type);

        // Then
        assertThat(pet.getType()).isEqualTo(type);
    }

    @Test
    void shouldSetAndGetOwner() {
        // Given
        Pet pet = new Pet();
        Owner owner = new Owner();
        owner.setFirstName("John");
        owner.setLastName("Doe");

        // When
        pet.setOwner(owner);

        // Then
        assertThat(pet.getOwner()).isEqualTo(owner);
    }
}
