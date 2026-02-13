package org.springframework.samples.petclinic.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.repository.OwnerRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OwnerServiceTest {

    @Mock
    private OwnerRepository ownerRepository;

    @InjectMocks
    private OwnerService ownerService;

    @Test
    void addOwner_shouldCreateOwnerWithAllFields() {
        // Given
        doNothing().when(ownerRepository).save(any(Owner.class));

        // When
        Owner result = ownerService.addOwner("John", "Doe", "123-456-7890", "123 Main St", "Springfield");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo("John");
        assertThat(result.getLastName()).isEqualTo("Doe");
        assertThat(result.getTelephone()).isEqualTo("123-456-7890");
        assertThat(result.getAddress()).isEqualTo("123 Main St");
        assertThat(result.getCity()).isEqualTo("Springfield");
        verify(ownerRepository, times(1)).save(any(Owner.class));
    }

    @Test
    void updateOwner_shouldUpdateOnlyProvidedFields() {
        // Given
        Owner existingOwner = new Owner();
        existingOwner.setId(1);
        existingOwner.setFirstName("John");
        existingOwner.setLastName("Doe");
        existingOwner.setTelephone("123-456-7890");
        existingOwner.setAddress("123 Main St");
        existingOwner.setCity("Springfield");
        
        when(ownerRepository.findById(1)).thenReturn(Optional.of(existingOwner));
        doNothing().when(ownerRepository).save(any(Owner.class));

        // When
        Owner result = ownerService.updateOwner(1, "Jane", null, null, null, null);

        // Then
        assertThat(result.getFirstName()).isEqualTo("Jane");
        assertThat(result.getLastName()).isEqualTo("Doe"); // unchanged
        assertThat(result.getTelephone()).isEqualTo("123-456-7890"); // unchanged
        verify(ownerRepository, times(1)).save(any(Owner.class));
    }

    @Test
    void updateOwner_shouldThrowExceptionWhenOwnerNotFound() {
        // Given
        when(ownerRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> ownerService.updateOwner(999, "Jane", null, null, null, null))
            .isInstanceOf(Exception.class);
    }

    @Test
    void updateOwner_shouldUpdateAllFieldsWhenProvided() {
        // Given
        Owner existingOwner = new Owner();
        existingOwner.setId(1);
        existingOwner.setFirstName("John");
        existingOwner.setLastName("Doe");
        existingOwner.setTelephone("123-456-7890");
        existingOwner.setAddress("123 Main St");
        existingOwner.setCity("Springfield");
        
        when(ownerRepository.findById(1)).thenReturn(Optional.of(existingOwner));
        doNothing().when(ownerRepository).save(any(Owner.class));

        // When
        Owner result = ownerService.updateOwner(1, "Jane", "Smith", "555-1234", "456 Oak Ave", "Shelbyville");

        // Then
        assertThat(result.getFirstName()).isEqualTo("Jane");
        assertThat(result.getLastName()).isEqualTo("Smith");
        assertThat(result.getTelephone()).isEqualTo("555-1234");
        assertThat(result.getAddress()).isEqualTo("456 Oak Ave");
        assertThat(result.getCity()).isEqualTo("Shelbyville");
        verify(ownerRepository, times(1)).save(any(Owner.class));
    }
}
