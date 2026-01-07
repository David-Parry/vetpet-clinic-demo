package org.springframework.samples.petclinic.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for Vet model
 */
class VetTest {

    @Test
    void shouldAddSpecialtyToVet() {
        // Given
        Vet vet = new Vet();
        vet.setFirstName("James");
        vet.setLastName("Carter");

        Specialty specialty = new Specialty();
        specialty.setName("Surgery");

        // When
        vet.addSpecialty(specialty);

        // Then
        assertThat(vet.getSpecialties()).hasSize(1);
        assertThat(vet.getSpecialties()).contains(specialty);
        assertThat(vet.getNrOfSpecialties()).isEqualTo(1);
    }

    @Test
    void shouldAddMultipleSpecialties() {
        // Given
        Vet vet = new Vet();
        vet.setFirstName("Helen");
        vet.setLastName("Leary");

        Specialty specialty1 = new Specialty();
        specialty1.setName("Radiology");

        Specialty specialty2 = new Specialty();
        specialty2.setName("Surgery");

        // When
        vet.addSpecialty(specialty1);
        vet.addSpecialty(specialty2);

        // Then
        assertThat(vet.getNrOfSpecialties()).isEqualTo(2);
        assertThat(vet.getSpecialties()).contains(specialty1, specialty2);
    }

    @Test
    void shouldReturnZeroSpecialtiesForNewVet() {
        // Given
        Vet vet = new Vet();

        // Then
        assertThat(vet.getNrOfSpecialties()).isEqualTo(0);
        assertThat(vet.getSpecialties()).isEmpty();
    }
}
