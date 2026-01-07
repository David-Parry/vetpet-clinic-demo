package org.springframework.samples.petclinic.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for Owner model
 */
class OwnerTest {

    @Test
    void shouldAddPetToOwner() {
        // Given
        Owner owner = new Owner();
        owner.setFirstName("John");
        owner.setLastName("Doe");

        Pet pet = new Pet();
        pet.setName("Fluffy");

        // When
        owner.addPet(pet);

        // Then
        assertThat(owner.getPets()).hasSize(1);
        assertThat(owner.getPets()).contains(pet);
        assertThat(pet.getOwner()).isEqualTo(owner);
    }

    @Test
    void shouldGetPetByName() {
        // Given
        Owner owner = new Owner();
        Pet pet1 = new Pet();
        pet1.setName("Fluffy");
        Pet pet2 = new Pet();
        pet2.setName("Max");

        owner.addPet(pet1);
        owner.addPet(pet2);

        // When
        Pet result = owner.getPet("fluffy"); // case-insensitive

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Fluffy");
    }

    @Test
    void shouldReturnNullWhenPetNotFound() {
        // Given
        Owner owner = new Owner();
        Pet pet = new Pet();
        pet.setName("Fluffy");
        owner.addPet(pet);

        // When
        Pet result = owner.getPet("Unknown");

        // Then
        assertThat(result).isNull();
    }

    @Test
    void shouldSetAndGetAddress() {
        // Given
        Owner owner = new Owner();
        String address = "123 Main St";

        // When
        owner.setAddress(address);

        // Then
        assertThat(owner.getAddress()).isEqualTo(address);
    }

    @Test
    void shouldSetAndGetCity() {
        // Given
        Owner owner = new Owner();
        String city = "Springfield";

        // When
        owner.setCity(city);

        // Then
        assertThat(owner.getCity()).isEqualTo(city);
    }

    @Test
    void shouldSetAndGetTelephone() {
        // Given
        Owner owner = new Owner();
        String telephone = "123-456-7890";

        // When
        owner.setTelephone(telephone);

        // Then
        assertThat(owner.getTelephone()).isEqualTo(telephone);
    }
}
