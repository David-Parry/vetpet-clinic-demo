package org.springframework.samples.petclinic.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.repository.SpecialtyRepository;
import org.springframework.samples.petclinic.repository.VetRepository;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for VetService
 */
@ExtendWith(MockitoExtension.class)
class VetServiceTest {

    @Mock
    private VetRepository vetRepository;

    @Mock
    private SpecialtyRepository specialtyRepository;

    @InjectMocks
    private VetService vetService;

    @Test
    void shouldCreateVetWithSpecialties() throws InvalidVetDataException {
        // Given
        String firstName = "James";
        String lastName = "Carter";
        Integer specialty1Id = 1;
        Integer specialty2Id = 2;

        Specialty specialty1 = new Specialty();
        specialty1.setId(specialty1Id);
        specialty1.setName("Surgery");

        Specialty specialty2 = new Specialty();
        specialty2.setId(specialty2Id);
        specialty2.setName("Dentistry");

        when(specialtyRepository.findById(specialty1Id)).thenReturn(Optional.of(specialty1));
        when(specialtyRepository.findById(specialty2Id)).thenReturn(Optional.of(specialty2));
        when(vetRepository.save(any(Vet.class))).thenAnswer(invocation -> {
            Vet vet = invocation.getArgument(0);
            vet.setId(1);
            return vet;
        });

        // When
        Vet result = vetService.createVet(firstName, lastName, Arrays.asList(specialty1Id, specialty2Id));

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo(firstName);
        assertThat(result.getLastName()).isEqualTo(lastName);
        assertThat(result.getNrOfSpecialties()).isEqualTo(2);
        verify(specialtyRepository).findById(specialty1Id);
        verify(specialtyRepository).findById(specialty2Id);
        verify(vetRepository).save(any(Vet.class));
    }

    @Test
    void shouldThrowExceptionWhenSpecialtyNotFound() {
        // Given
        String firstName = "James";
        String lastName = "Carter";
        Integer invalidSpecialtyId = 999;

        when(specialtyRepository.findById(invalidSpecialtyId)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> vetService.createVet(firstName, lastName, Arrays.asList(invalidSpecialtyId)))
            .isInstanceOf(InvalidVetDataException.class)
            .hasMessageContaining("Specialty with Id '999' not found");

        verify(specialtyRepository).findById(invalidSpecialtyId);
        verify(vetRepository, never()).save(any(Vet.class));
    }

    @Test
    void shouldCreateVetWithNoSpecialties() throws InvalidVetDataException {
        // Given
        String firstName = "Helen";
        String lastName = "Leary";

        when(vetRepository.save(any(Vet.class))).thenAnswer(invocation -> {
            Vet vet = invocation.getArgument(0);
            vet.setId(2);
            return vet;
        });

        // When
        Vet result = vetService.createVet(firstName, lastName, Arrays.asList());

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo(firstName);
        assertThat(result.getLastName()).isEqualTo(lastName);
        assertThat(result.getNrOfSpecialties()).isEqualTo(0);
        verify(vetRepository).save(any(Vet.class));
    }
}
