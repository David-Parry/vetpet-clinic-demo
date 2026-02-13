package org.springframework.samples.petclinic.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.repository.SpecialtyRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpecialtyServiceTest {

    @Mock
    private SpecialtyRepository specialtyRepository;

    @InjectMocks
    private SpecialtyService specialtyService;

    @Test
    void addSpecialty_shouldCreateSpecialty() {
        // Given
        doNothing().when(specialtyRepository).save(any(Specialty.class));

        // When
        Specialty result = specialtyService.addSpecialty("Radiology");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Radiology");
        verify(specialtyRepository, times(1)).save(any(Specialty.class));
    }

    @Test
    void updateSpecialty_shouldUpdateSpecialtyName() {
        // Given
        Specialty existingSpecialty = new Specialty();
        existingSpecialty.setId(1);
        existingSpecialty.setName("Radiology");
        
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(existingSpecialty));
        doNothing().when(specialtyRepository).save(any(Specialty.class));

        // When
        Specialty result = specialtyService.updateSpecialty(1, "Advanced Radiology");

        // Then
        assertThat(result.getName()).isEqualTo("Advanced Radiology");
        verify(specialtyRepository, times(1)).save(any(Specialty.class));
    }

    @Test
    void updateSpecialty_shouldThrowExceptionWhenSpecialtyNotFound() {
        // Given
        when(specialtyRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> specialtyService.updateSpecialty(999, "New Name"))
            .isInstanceOf(Exception.class);
    }

    @Test
    void deleteSpecialty_shouldDeleteSpecialty() {
        // Given
        Specialty existingSpecialty = new Specialty();
        existingSpecialty.setId(1);
        existingSpecialty.setName("Radiology");
        
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(existingSpecialty));
        doNothing().when(specialtyRepository).delete(any(Specialty.class));

        // When
        specialtyService.deleteSpecialty(1);

        // Then
        verify(specialtyRepository, times(1)).delete(existingSpecialty);
    }

    @Test
    void deleteSpecialty_shouldThrowExceptionWhenSpecialtyNotFound() {
        // Given
        when(specialtyRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> specialtyService.deleteSpecialty(999))
            .isInstanceOf(Exception.class);
    }
}
