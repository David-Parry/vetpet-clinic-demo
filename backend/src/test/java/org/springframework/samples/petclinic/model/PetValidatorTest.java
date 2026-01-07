package org.springframework.samples.petclinic.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for PetValidator
 */
class PetValidatorTest {

    private PetValidator validator;

    @BeforeEach
    void setUp() {
        validator = new PetValidator();
    }

    @Test
    void shouldSupportPetClass() {
        // Then
        assertThat(validator.supports(Pet.class)).isTrue();
    }

    @Test
    void shouldNotSupportOtherClasses() {
        // Then
        assertThat(validator.supports(Owner.class)).isFalse();
    }

    @Test
    void shouldRejectEmptyName() {
        // Given
        Pet pet = new Pet();
        pet.setName("");
        pet.setType(new PetType());
        pet.setBirthDate(LocalDate.now());

        Errors errors = new BeanPropertyBindingResult(pet, "pet");

        // When
        validator.validate(pet, errors);

        // Then
        assertThat(errors.hasErrors()).isTrue();
        assertThat(errors.getFieldError("name")).isNotNull();
        assertThat(errors.getFieldError("name").getCode()).isEqualTo("required");
    }

    @Test
    void shouldRejectNullType() {
        // Given
        Pet pet = new Pet();
        pet.setName("Fluffy");
        pet.setType(null);
        pet.setBirthDate(LocalDate.now());

        Errors errors = new BeanPropertyBindingResult(pet, "pet");

        // When
        validator.validate(pet, errors);

        // Then
        assertThat(errors.hasErrors()).isTrue();
        assertThat(errors.getFieldError("type")).isNotNull();
        assertThat(errors.getFieldError("type").getCode()).isEqualTo("required");
    }

    @Test
    void shouldRejectNullBirthDate() {
        // Given
        Pet pet = new Pet();
        pet.setName("Fluffy");
        pet.setType(new PetType());
        pet.setBirthDate(null);

        Errors errors = new BeanPropertyBindingResult(pet, "pet");

        // When
        validator.validate(pet, errors);

        // Then
        assertThat(errors.hasErrors()).isTrue();
        assertThat(errors.getFieldError("birthDate")).isNotNull();
        assertThat(errors.getFieldError("birthDate").getCode()).isEqualTo("required");
    }

    @Test
    void shouldAcceptValidPet() {
        // Given
        Pet pet = new Pet();
        pet.setName("Fluffy");
        pet.setType(new PetType());
        pet.setBirthDate(LocalDate.of(2020, 5, 15));

        Errors errors = new BeanPropertyBindingResult(pet, "pet");

        // When
        validator.validate(pet, errors);

        // Then
        assertThat(errors.hasErrors()).isFalse();
    }
}
