package org.springframework.samples.petclinic.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.repository.SpecialtyRepository;
import org.springframework.samples.petclinic.repository.VetRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VetServiceTest {

    @Mock
    private VetRepository vetRepository;

    @Mock
    private SpecialtyRepository specialtyRepository;

    @InjectMocks
    private VetService vetService;

    @Test
    void createVet_shouldCreateVetWithSpecialties() throws InvalidVetDataException {
        // Given
        Specialty specialty1 = new Specialty();
        specialty1.setId(1);
        specialty1.setName("Radiology");
        
        Specialty specialty2 = new Specialty();
        specialty2.setId(2);
        specialty2.setName("Surgery");
        
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(specialty1));
        when(specialtyRepository.findById(2)).thenReturn(Optional.of(specialty2));
        when(vetRepository.save(any(Vet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Vet result = vetService.createVet("John", "Doe", Arrays.asList(1, 2));

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo("John");
        assertThat(result.getLastName()).isEqualTo("Doe");
        assertThat(result.getSpecialties()).hasSize(2);
        verify(vetRepository).save(any(Vet.class));
    }

    @Test
    void createVet_shouldThrowExceptionWhenSpecialtyNotFound() {
        // Given
        when(specialtyRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> vetService.createVet("John", "Doe", Collections.singletonList(999)))
            .isInstanceOf(InvalidVetDataException.class)
            .hasMessageContaining("Specialty with Id '999' not found");
    }

    @Test
    void createVet_shouldCreateVetWithNoSpecialties() throws InvalidVetDataException {
        // Given
        when(vetRepository.save(any(Vet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Vet result = vetService.createVet("Jane", "Smith", Collections.emptyList());

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo("Jane");
        assertThat(result.getLastName()).isEqualTo("Smith");
        assertThat(result.getSpecialties()).isEmpty();
        verify(vetRepository).save(any(Vet.class));
    }

    @Test
    void createVet_shouldHandleMultipleSpecialtiesCorrectly() throws InvalidVetDataException {
        // Given
        Specialty specialty1 = new Specialty();
        specialty1.setId(1);
        specialty1.setName("Radiology");
        
        Specialty specialty2 = new Specialty();
        specialty2.setId(2);
        specialty2.setName("Surgery");
        
        Specialty specialty3 = new Specialty();
        specialty3.setId(3);
        specialty3.setName("Dentistry");
        
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(specialty1));
        when(specialtyRepository.findById(2)).thenReturn(Optional.of(specialty2));
        when(specialtyRepository.findById(3)).thenReturn(Optional.of(specialty3));
        when(vetRepository.save(any(Vet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Vet result = vetService.createVet("Dr.", "Multi", Arrays.asList(1, 2, 3));

        // Then
        assertThat(result.getSpecialties()).hasSize(3);
        verify(specialtyRepository, times(3)).findById(any());
    }
}
