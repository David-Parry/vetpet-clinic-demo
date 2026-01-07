package org.springframework.samples.petclinic.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.samples.petclinic.repository.PetRepository;
import org.springframework.samples.petclinic.repository.VisitRepository;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for VisitService
 */
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
    void shouldAddVisitWithVet() {
        // Given
        int petId = 1;
        String description = "Annual checkup";
        LocalDate date = LocalDate.now();
        int vetId = 5;

        Pet pet = new Pet();
        pet.setId(petId);
        pet.setName("Fluffy");

        when(petRepository.findById(petId)).thenReturn(Optional.of(pet));
        doAnswer(invocation -> {
            Visit visit = invocation.getArgument(0);
            visit.setId(1);
            return visit;
        }).when(visitRepository).save(any(Visit.class));

        // When
        Visit result = visitService.addVisit(petId, description, date, Optional.of(vetId));

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getDescription()).isEqualTo(description);
        assertThat(result.getDate()).isEqualTo(date);
        assertThat(result.getPet()).isEqualTo(pet);
        assertThat(result.getVetId()).isEqualTo(vetId);
        verify(petRepository).findById(petId);
        verify(visitRepository).save(any(Visit.class));
        verify(applicationEventPublisher).publishEvent(any(VisitCreatedEvent.class));
    }

    @Test
    void shouldAddVisitWithoutVet() {
        // Given
        int petId = 1;
        String description = "Emergency visit";
        LocalDate date = LocalDate.now();

        Pet pet = new Pet();
        pet.setId(petId);
        pet.setName("Max");

        when(petRepository.findById(petId)).thenReturn(Optional.of(pet));
        doAnswer(invocation -> {
            Visit visit = invocation.getArgument(0);
            visit.setId(2);
            return visit;
        }).when(visitRepository).save(any(Visit.class));

        // When
        Visit result = visitService.addVisit(petId, description, date, Optional.empty());

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getDescription()).isEqualTo(description);
        assertThat(result.getDate()).isEqualTo(date);
        assertThat(result.getPet()).isEqualTo(pet);
        assertThat(result.getVetId()).isNull();
        verify(petRepository).findById(petId);
        verify(visitRepository).save(any(Visit.class));
        verify(applicationEventPublisher).publishEvent(any(VisitCreatedEvent.class));
    }

    @Test
    void shouldPublishEventAfterVisitCreation() {
        // Given
        int petId = 1;
        String description = "Vaccination";
        LocalDate date = LocalDate.now();

        Pet pet = new Pet();
        pet.setId(petId);

        when(petRepository.findById(petId)).thenReturn(Optional.of(pet));
        doAnswer(invocation -> {
            Visit visit = invocation.getArgument(0);
            visit.setId(3);
            return visit;
        }).when(visitRepository).save(any(Visit.class));

        // When
        visitService.addVisit(petId, description, date, Optional.empty());

        // Then
        verify(applicationEventPublisher).publishEvent(any(VisitCreatedEvent.class));
    }
}
