package org.springframework.samples.petclinic.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.repository.OwnerRepository;
import org.springframework.samples.petclinic.repository.PetRepository;
import org.springframework.samples.petclinic.repository.PetTypeRepository;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for PetService
 */
@ExtendWith(MockitoExtension.class)
class PetServiceTest {

    @Mock
    private OwnerRepository ownerRepository;

    @Mock
    private PetRepository petRepository;

    @Mock
    private PetTypeRepository petTypeRepository;

    @InjectMocks
    private PetService petService;

    @Test
    void shouldAddPetWithValidData() {
        // Given
        int ownerId = 1;
        int petTypeId = 1;
        String petName = "Fluffy";
        LocalDate birthDate = LocalDate.of(2020, 5, 15);

        Owner owner = new Owner();
        owner.setId(ownerId);
        owner.setFirstName("John");
        owner.setLastName("Doe");

        PetType petType = new PetType();
        petType.setId(petTypeId);
        petType.setName("cat");

        when(ownerRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(petTypeRepository.findById(petTypeId)).thenReturn(Optional.of(petType));
        doAnswer(invocation -> {
            Pet pet = invocation.getArgument(0);
            pet.setId(1);
            return pet;
        }).when(petRepository).save(any(Pet.class));

        // When
        Pet result = petService.addPet(ownerId, petTypeId, petName, birthDate);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(petName);
        assertThat(result.getBirthDate()).isEqualTo(birthDate);
        assertThat(result.getType()).isEqualTo(petType);
        assertThat(result.getOwner()).isEqualTo(owner);
        verify(ownerRepository).findById(ownerId);
        verify(petTypeRepository).findById(petTypeId);
        verify(petRepository).save(any(Pet.class));
    }

    @Test
    void shouldUpdatePetWithAllFields() {
        // Given
        int petId = 1;
        int newPetTypeId = 2;
        String newName = "Max";
        LocalDate newBirthDate = LocalDate.of(2021, 3, 10);

        Pet existingPet = new Pet();
        existingPet.setId(petId);
        existingPet.setName("Fluffy");
        existingPet.setBirthDate(LocalDate.of(2020, 5, 15));

        PetType oldType = new PetType();
        oldType.setId(1);
        oldType.setName("cat");
        existingPet.setType(oldType);

        PetType newType = new PetType();
        newType.setId(newPetTypeId);
        newType.setName("dog");

        when(petRepository.findById(petId)).thenReturn(Optional.of(existingPet));
        when(petTypeRepository.findById(newPetTypeId)).thenReturn(Optional.of(newType));
        doAnswer(invocation -> invocation.getArgument(0)).when(petRepository).save(any(Pet.class));

        // When
        Pet result = petService.updatePet(petId, Optional.of(newPetTypeId), newName, newBirthDate);

        // Then
        assertThat(result.getName()).isEqualTo(newName);
        assertThat(result.getBirthDate()).isEqualTo(newBirthDate);
        assertThat(result.getType()).isEqualTo(newType);
        verify(petRepository).findById(petId);
        verify(petTypeRepository).findById(newPetTypeId);
        verify(petRepository).save(existingPet);
    }

    @Test
    void shouldUpdatePetWithPartialFields() {
        // Given
        int petId = 1;
        String newName = "Max";

        Pet existingPet = new Pet();
        existingPet.setId(petId);
        existingPet.setName("Fluffy");
        existingPet.setBirthDate(LocalDate.of(2020, 5, 15));

        PetType type = new PetType();
        type.setId(1);
        type.setName("cat");
        existingPet.setType(type);

        when(petRepository.findById(petId)).thenReturn(Optional.of(existingPet));
        doAnswer(invocation -> invocation.getArgument(0)).when(petRepository).save(any(Pet.class));

        // When
        Pet result = petService.updatePet(petId, Optional.empty(), newName, null);

        // Then
        assertThat(result.getName()).isEqualTo(newName);
        assertThat(result.getBirthDate()).isEqualTo(LocalDate.of(2020, 5, 15)); // unchanged
        assertThat(result.getType()).isEqualTo(type); // unchanged
        verify(petRepository).findById(petId);
        verify(petRepository).save(existingPet);
    }
}
