package org.springframework.samples.petclinic.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class PetValidatorTest {

    private PetValidator petValidator;

    @BeforeEach
    void setUp() {
        petValidator = new PetValidator();
    }

    @Test
    void supports_shouldReturnTrueForPetClass() {
        // When
        boolean result = petValidator.supports(Pet.class);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void supports_shouldReturnFalseForOtherClasses() {
        // When
        boolean result = petValidator.supports(String.class);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void validate_shouldRejectEmptyName() {
        // Given
        Pet pet = new Pet();
        pet.setName("");
        pet.setBirthDate(LocalDate.now());
        PetType petType = new PetType();
        pet.setType(petType);
        
        Errors errors = new BeanPropertyBindingResult(pet, "pet");

        // When
        petValidator.validate(pet, errors);

        // Then
        assertThat(errors.hasErrors()).isTrue();
        assertThat(errors.getFieldError("name")).isNotNull();
    }

    @Test
    void validate_shouldRejectNullName() {
        // Given
        Pet pet = new Pet();
        pet.setName(null);
        pet.setBirthDate(LocalDate.now());
        PetType petType = new PetType();
        pet.setType(petType);
        
        Errors errors = new BeanPropertyBindingResult(pet, "pet");

        // When
        petValidator.validate(pet, errors);

        // Then
        assertThat(errors.hasErrors()).isTrue();
        assertThat(errors.getFieldError("name")).isNotNull();
    }

    @Test
    void validate_shouldRejectNullTypeForNewPet() {
        // Given
        Pet pet = new Pet();
        pet.setName("Max");
        pet.setBirthDate(LocalDate.now());
        pet.setType(null);
        
        Errors errors = new BeanPropertyBindingResult(pet, "pet");

        // When
        petValidator.validate(pet, errors);

        // Then
        assertThat(errors.hasErrors()).isTrue();
        assertThat(errors.getFieldError("type")).isNotNull();
    }

    @Test
    void validate_shouldRejectNullBirthDate() {
        // Given
        Pet pet = new Pet();
        pet.setName("Max");
        PetType petType = new PetType();
        pet.setType(petType);
        pet.setBirthDate(null);
        
        Errors errors = new BeanPropertyBindingResult(pet, "pet");

        // When
        petValidator.validate(pet, errors);

        // Then
        assertThat(errors.hasErrors()).isTrue();
        assertThat(errors.getFieldError("birthDate")).isNotNull();
    }

    @Test
    void validate_shouldAcceptValidPet() {
        // Given
        Pet pet = new Pet();
        pet.setName("Max");
        pet.setBirthDate(LocalDate.of(2020, 1, 1));
        PetType petType = new PetType();
        pet.setType(petType);
        
        Errors errors = new BeanPropertyBindingResult(pet, "pet");

        // When
        petValidator.validate(pet, errors);

        // Then
        assertThat(errors.hasErrors()).isFalse();
    }

    @Test
    void validate_shouldRejectMultipleInvalidFields() {
        // Given
        Pet pet = new Pet();
        pet.setName("");
        pet.setBirthDate(null);
        pet.setType(null);
        
        Errors errors = new BeanPropertyBindingResult(pet, "pet");

        // When
        petValidator.validate(pet, errors);

        // Then
        assertThat(errors.getErrorCount()).isGreaterThanOrEqualTo(2);
        assertThat(errors.getFieldError("name")).isNotNull();
        assertThat(errors.getFieldError("birthDate")).isNotNull();
    }
}
