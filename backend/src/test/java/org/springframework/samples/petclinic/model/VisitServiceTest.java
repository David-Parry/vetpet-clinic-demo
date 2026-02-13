package org.springframework.samples.petclinic.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.samples.petclinic.repository.PetRepository;
import org.springframework.samples.petclinic.repository.VisitRepository;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VisitServiceTest {

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @Mock
    private PetRepository petRepository;

    @Mock
    private VisitRepository visitRepository;

    @InjectMocks
    private VisitService visitService;

    @Test
    void addVisit_shouldCreateVisitWithAllFields() {
        // Given
        Pet pet = new Pet();
        pet.setId(1);
        pet.setName("Max");
        
        when(petRepository.findById(1)).thenReturn(Optional.of(pet));
        doNothing().when(visitRepository).save(any(Visit.class));
        doNothing().when(applicationEventPublisher).publishEvent(any(VisitCreatedEvent.class));

        // When
        Visit result = visitService.addVisit(1, "Checkup", LocalDate.of(2024, 1, 15), Optional.of(5));

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getDescription()).isEqualTo("Checkup");
        assertThat(result.getDate()).isEqualTo(LocalDate.of(2024, 1, 15));
        assertThat(result.getPet()).isEqualTo(pet);
        verify(visitRepository, times(1)).save(any(Visit.class));
    }

    @Test
    void addVisit_shouldPublishEventOnCreation() {
        // Given
        Pet pet = new Pet();
        pet.setId(1);
        
        when(petRepository.findById(1)).thenReturn(Optional.of(pet));
        doNothing().when(visitRepository).save(any(Visit.class));
        doNothing().when(applicationEventPublisher).publishEvent(any(VisitCreatedEvent.class));

        // When
        visitService.addVisit(1, "Checkup", LocalDate.of(2024, 1, 15), Optional.empty());

        // Then
        verify(applicationEventPublisher, times(1)).publishEvent(any(VisitCreatedEvent.class));
    }

    @Test
    void addVisit_shouldThrowExceptionWhenPetNotFound() {
        // Given
        when(petRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> visitService.addVisit(999, "Checkup", LocalDate.of(2024, 1, 15), Optional.empty()))
            .isInstanceOf(Exception.class);
    }

    @Test
    void addVisit_shouldCreateVisitWithoutVetId() {
        // Given
        Pet pet = new Pet();
        pet.setId(1);
        
        when(petRepository.findById(1)).thenReturn(Optional.of(pet));
        doNothing().when(visitRepository).save(any(Visit.class));
        doNothing().when(applicationEventPublisher).publishEvent(any(VisitCreatedEvent.class));

        // When
        Visit result = visitService.addVisit(1, "Emergency", LocalDate.now(), Optional.empty());

        // Then
        assertThat(result).isNotNull();
        verify(visitRepository, times(1)).save(any(Visit.class));
        verify(applicationEventPublisher, times(1)).publishEvent(any(VisitCreatedEvent.class));
    }
}
