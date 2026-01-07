package org.springframework.samples.petclinic.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.repository.OwnerRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for OwnerService
 */
@ExtendWith(MockitoExtension.class)
class OwnerServiceTest {

    @Mock
    private OwnerRepository ownerRepository;

    @InjectMocks
    private OwnerService ownerService;

    @Test
    void shouldAddOwnerWithValidData() {
        // Given
        String firstName = "John";
        String lastName = "Doe";
        String telephone = "123-456-7890";
        String address = "123 Main St";
        String city = "Springfield";

        doAnswer(invocation -> {
            Owner owner = invocation.getArgument(0);
            owner.setId(1);
            return owner;
        }).when(ownerRepository).save(any(Owner.class));

        // When
        Owner result = ownerService.addOwner(firstName, lastName, telephone, address, city);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo(firstName);
        assertThat(result.getLastName()).isEqualTo(lastName);
        assertThat(result.getTelephone()).isEqualTo(telephone);
        assertThat(result.getAddress()).isEqualTo(address);
        assertThat(result.getCity()).isEqualTo(city);
        verify(ownerRepository).save(any(Owner.class));
    }

    @Test
    void shouldUpdateOwnerWithAllFields() {
        // Given
        int ownerId = 1;
        String newFirstName = "Jane";
        String newLastName = "Smith";
        String newTelephone = "987-654-3210";
        String newAddress = "456 Oak Ave";
        String newCity = "Shelbyville";

        Owner existingOwner = new Owner();
        existingOwner.setId(ownerId);
        existingOwner.setFirstName("John");
        existingOwner.setLastName("Doe");
        existingOwner.setTelephone("123-456-7890");
        existingOwner.setAddress("123 Main St");
        existingOwner.setCity("Springfield");

        when(ownerRepository.findById(ownerId)).thenReturn(Optional.of(existingOwner));
        doAnswer(invocation -> invocation.getArgument(0)).when(ownerRepository).save(any(Owner.class));

        // When
        Owner result = ownerService.updateOwner(ownerId, newFirstName, newLastName, newTelephone, newAddress, newCity);

        // Then
        assertThat(result.getFirstName()).isEqualTo(newFirstName);
        assertThat(result.getLastName()).isEqualTo(newLastName);
        assertThat(result.getTelephone()).isEqualTo(newTelephone);
        assertThat(result.getAddress()).isEqualTo(newAddress);
        assertThat(result.getCity()).isEqualTo(newCity);
        verify(ownerRepository).findById(ownerId);
        verify(ownerRepository).save(existingOwner);
    }

    @Test
    void shouldUpdateOwnerWithPartialFields() {
        // Given
        int ownerId = 1;
        String newFirstName = "Jane";

        Owner existingOwner = new Owner();
        existingOwner.setId(ownerId);
        existingOwner.setFirstName("John");
        existingOwner.setLastName("Doe");
        existingOwner.setTelephone("123-456-7890");
        existingOwner.setAddress("123 Main St");
        existingOwner.setCity("Springfield");

        when(ownerRepository.findById(ownerId)).thenReturn(Optional.of(existingOwner));
        doAnswer(invocation -> invocation.getArgument(0)).when(ownerRepository).save(any(Owner.class));

        // When
        Owner result = ownerService.updateOwner(ownerId, newFirstName, null, null, null, null);

        // Then
        assertThat(result.getFirstName()).isEqualTo(newFirstName);
        assertThat(result.getLastName()).isEqualTo("Doe"); // unchanged
        assertThat(result.getTelephone()).isEqualTo("123-456-7890"); // unchanged
        verify(ownerRepository).findById(ownerId);
        verify(ownerRepository).save(existingOwner);
    }
}
