package org.springframework.samples.petclinic.util;

import org.junit.jupiter.api.Test;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.samples.petclinic.model.Owner;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EntityUtilsTest {

    @Test
    void getById_shouldReturnEntityWhenFound() {
        // Given
        Owner owner1 = new Owner();
        owner1.setId(1);
        owner1.setFirstName("John");
        
        Owner owner2 = new Owner();
        owner2.setId(2);
        owner2.setFirstName("Jane");
        
        List<Owner> owners = Arrays.asList(owner1, owner2);

        // When
        Owner result = EntityUtils.getById(owners, Owner.class, 1);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getFirstName()).isEqualTo("John");
    }

    @Test
    void getById_shouldThrowExceptionWhenEntityNotFound() {
        // Given
        Owner owner1 = new Owner();
        owner1.setId(1);
        
        List<Owner> owners = Arrays.asList(owner1);

        // When & Then
        assertThatThrownBy(() -> EntityUtils.getById(owners, Owner.class, 999))
            .isInstanceOf(ObjectRetrievalFailureException.class);
    }

    @Test
    void getById_shouldReturnCorrectEntityFromMultipleEntities() {
        // Given
        Owner owner1 = new Owner();
        owner1.setId(1);
        
        Owner owner2 = new Owner();
        owner2.setId(2);
        
        Owner owner3 = new Owner();
        owner3.setId(3);
        
        List<Owner> owners = Arrays.asList(owner1, owner2, owner3);

        // When
        Owner result = EntityUtils.getById(owners, Owner.class, 2);

        // Then
        assertThat(result.getId()).isEqualTo(2);
    }

    @Test
    void asDateTime_shouldConvertDateToLocalDate() {
        // Given
        Date date = new Date(1609459200000L); // 2021-01-01 00:00:00 UTC
        
        // When
        LocalDate result = EntityUtils.asDateTime(date);

        // Then
        assertThat(result).isNotNull();
        // The exact date depends on system timezone, but it should be close to 2021-01-01
        assertThat(result.getYear()).isEqualTo(2021);
        assertThat(result.getMonthValue()).isEqualTo(1);
    }

    @Test
    void asDateTime_shouldHandleCurrentDate() {
        // Given
        Date now = new Date();
        
        // When
        LocalDate result = EntityUtils.asDateTime(now);

        // Then
        assertThat(result).isNotNull();
        LocalDate expectedDate = LocalDate.ofInstant(now.toInstant(), ZoneId.systemDefault());
        assertThat(result).isEqualTo(expectedDate);
    }
}
