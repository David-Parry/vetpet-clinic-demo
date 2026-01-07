package org.springframework.samples.petclinic.util;

import org.junit.jupiter.api.Test;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for EntityUtils
 */
class EntityUtilsTest {

    @Test
    void shouldGetEntityById() {
        // Given
        Owner owner1 = new Owner();
        owner1.setId(1);
        owner1.setFirstName("John");

        Owner owner2 = new Owner();
        owner2.setId(2);
        owner2.setFirstName("Jane");

        Collection<Owner> owners = Arrays.asList(owner1, owner2);

        // When
        Owner result = EntityUtils.getById(owners, Owner.class, 2);

        // Then
        assertThat(result).isEqualTo(owner2);
        assertThat(result.getFirstName()).isEqualTo("Jane");
    }

    @Test
    void shouldThrowExceptionWhenEntityNotFound() {
        // Given
        Owner owner1 = new Owner();
        owner1.setId(1);
        owner1.setFirstName("John");

        Collection<Owner> owners = Arrays.asList(owner1);

        // When / Then
        assertThatThrownBy(() -> EntityUtils.getById(owners, Owner.class, 999))
            .isInstanceOf(ObjectRetrievalFailureException.class);
    }

    @Test
    void shouldConvertDateToLocalDate() {
        // Given - Using epoch time for 2020-12-31 23:59:59 UTC
        Date date = new Date(1609459199000L); 

        // When
        LocalDate result = EntityUtils.asDateTime(date);

        // Then
        assertThat(result).isNotNull();
        // The result year depends on system timezone, so we just check it's a valid conversion
        assertThat(result.getYear()).isIn(2020, 2021);
    }

    @Test
    void shouldGetEntityByIdWithCorrectType() {
        // Given
        Pet pet1 = new Pet();
        pet1.setId(1);
        pet1.setName("Fluffy");

        Pet pet2 = new Pet();
        pet2.setId(2);
        pet2.setName("Max");

        Collection<Pet> pets = Arrays.asList(pet1, pet2);

        // When
        Pet result = EntityUtils.getById(pets, Pet.class, 1);

        // Then
        assertThat(result).isEqualTo(pet1);
        assertThat(result.getName()).isEqualTo("Fluffy");
    }
}
