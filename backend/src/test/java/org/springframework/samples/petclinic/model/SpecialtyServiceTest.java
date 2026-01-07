package org.springframework.samples.petclinic.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.repository.SpecialtyRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for SpecialtyService
 */
@ExtendWith(MockitoExtension.class)
class SpecialtyServiceTest {

    @Mock
    private SpecialtyRepository specialtyRepository;

    @InjectMocks
    private SpecialtyService specialtyService;

    @Test
    void shouldAddSpecialty() {
        // Given
        String specialtyName = "Cardiology";

        doAnswer(invocation -> {
            Specialty specialty = invocation.getArgument(0);
            specialty.setId(1);
            return specialty;
        }).when(specialtyRepository).save(any(Specialty.class));

        // When
        Specialty result = specialtyService.addSpecialty(specialtyName);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(specialtyName);
        verify(specialtyRepository).save(any(Specialty.class));
    }

    @Test
    void shouldUpdateSpecialty() {
        // Given
        int specialtyId = 1;
        String oldName = "Surgery";
        String newName = "Advanced Surgery";

        Specialty existingSpecialty = new Specialty();
        existingSpecialty.setId(specialtyId);
        existingSpecialty.setName(oldName);

        when(specialtyRepository.findById(specialtyId)).thenReturn(Optional.of(existingSpecialty));
        doAnswer(invocation -> invocation.getArgument(0)).when(specialtyRepository).save(any(Specialty.class));

        // When
        Specialty result = specialtyService.updateSpecialty(specialtyId, newName);

        // Then
        assertThat(result.getName()).isEqualTo(newName);
        verify(specialtyRepository).findById(specialtyId);
        verify(specialtyRepository).save(existingSpecialty);
    }

    @Test
    void shouldDeleteSpecialty() {
        // Given
        int specialtyId = 1;

        Specialty specialty = new Specialty();
        specialty.setId(specialtyId);
        specialty.setName("Dentistry");

        when(specialtyRepository.findById(specialtyId)).thenReturn(Optional.of(specialty));

        // When
        specialtyService.deleteSpecialty(specialtyId);

        // Then
        verify(specialtyRepository).findById(specialtyId);
        verify(specialtyRepository).delete(specialty);
    }
}
