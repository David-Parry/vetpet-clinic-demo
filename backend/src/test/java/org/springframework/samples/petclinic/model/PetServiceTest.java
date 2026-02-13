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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
    void addPet_shouldCreatePetWithAllFields() {
        // Given
        Owner owner = new Owner();
        owner.setId(1);
        
        PetType petType = new PetType();
        petType.setId(1);
        petType.setName("Dog");
        
        when(ownerRepository.findById(1)).thenReturn(Optional.of(owner));
        when(petTypeRepository.findById(1)).thenReturn(Optional.of(petType));
        doNothing().when(petRepository).save(any(Pet.class));

        // When
        Pet result = petService.addPet(1, 1, "Max", LocalDate.of(2020, 1, 1));

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Max");
        assertThat(result.getBirthDate()).isEqualTo(LocalDate.of(2020, 1, 1));
        assertThat(result.getOwner()).isEqualTo(owner);
        assertThat(result.getType()).isEqualTo(petType);
        verify(petRepository, times(1)).save(any(Pet.class));
    }

    @Test
    void addPet_shouldThrowExceptionWhenOwnerNotFound() {
        // Given
        when(ownerRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> petService.addPet(999, 1, "Max", LocalDate.of(2020, 1, 1)))
            .isInstanceOf(Exception.class);
    }

    @Test
    void addPet_shouldThrowExceptionWhenPetTypeNotFound() {
        // Given
        Owner owner = new Owner();
        owner.setId(1);
        
        when(ownerRepository.findById(1)).thenReturn(Optional.of(owner));
        when(petTypeRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> petService.addPet(1, 999, "Max", LocalDate.of(2020, 1, 1)))
            .isInstanceOf(Exception.class);
    }

    @Test
    void updatePet_shouldUpdateOnlyProvidedFields() {
        // Given
        Pet existingPet = new Pet();
        existingPet.setId(1);
        existingPet.setName("Max");
        existingPet.setBirthDate(LocalDate.of(2020, 1, 1));
        
        when(petRepository.findById(1)).thenReturn(Optional.of(existingPet));
        doNothing().when(petRepository).save(any(Pet.class));

        // When
        Pet result = petService.updatePet(1, Optional.empty(), "Buddy", null);

        // Then
        assertThat(result.getName()).isEqualTo("Buddy");
        assertThat(result.getBirthDate()).isEqualTo(LocalDate.of(2020, 1, 1)); // unchanged
        verify(petRepository, times(1)).save(any(Pet.class));
    }

    @Test
    void updatePet_shouldUpdatePetTypeWhenProvided() {
        // Given
        Pet existingPet = new Pet();
        existingPet.setId(1);
        existingPet.setName("Max");
        
        PetType newPetType = new PetType();
        newPetType.setId(2);
        newPetType.setName("Cat");
        
        when(petRepository.findById(1)).thenReturn(Optional.of(existingPet));
        when(petTypeRepository.findById(2)).thenReturn(Optional.of(newPetType));
        doNothing().when(petRepository).save(any(Pet.class));

        // When
        Pet result = petService.updatePet(1, Optional.of(2), null, null);

        // Then
        assertThat(result.getType()).isEqualTo(newPetType);
        verify(petRepository, times(1)).save(any(Pet.class));
    }

    @Test
    void updatePet_shouldThrowExceptionWhenPetNotFound() {
        // Given
        when(petRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> petService.updatePet(999, Optional.empty(), "Buddy", null))
            .isInstanceOf(Exception.class);
    }
}
