package org.springframework.samples.petclinic.model;

import org.springframework.samples.petclinic.repository.OwnerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.function.Consumer;

@Service
@Validated
public class OwnerService {

    private final OwnerRepository ownerRepository;

    public OwnerService(OwnerRepository ownerRepository) {
        this.ownerRepository = ownerRepository;
    }

    @Transactional
    public Owner addOwner(@NotEmpty String firstName, @NotEmpty String lastName, @NotEmpty String telephone, @NotEmpty String address, @NotEmpty String city) {
        final Owner owner = new Owner();
        owner.setAddress(address);
        owner.setCity(city);
        owner.setTelephone(telephone);
        owner.setFirstName(firstName);
        owner.setLastName(lastName);

        ownerRepository.save(owner);

        return owner;
    }



    @Transactional
    public Owner updateOwner(@NotEmpty int ownerId, String firstName, String lastName, String telephone, String address, String city) {
        Owner owner = ownerRepository.findById(ownerId).orElseThrow();

        setIfGiven(address, owner::setAddress);
        setIfGiven(firstName, owner::setFirstName);
        setIfGiven(lastName, owner::setLastName);
        setIfGiven(telephone, owner::setTelephone);
        setIfGiven(address, owner::setAddress);
        setIfGiven(city, owner::setCity);

        ownerRepository.save(owner);

        return owner;
    }

    /**
     * Find all owner names who have pets of a specific type that have been treated by vets with a particular specialty.
     * 
     * @param petTypeName the name of the pet type to filter by (required, case-insensitive)
     * @param specialtyName the name of the vet specialty to filter by (required, case-insensitive)
     * @return a list of owner full names, distinct and ordered
     * @throws IllegalArgumentException if either parameter is null or empty
     */
    @Transactional(readOnly = true)
    public List<String> findOwnerNamesByPetTypeAndVetSpecialty(
            @NotEmpty String petTypeName, 
            @NotEmpty String specialtyName) {
        // Input validation
        if (petTypeName == null || petTypeName.trim().isEmpty()) {
            throw new IllegalArgumentException("Pet type name is required");
        }
        if (specialtyName == null || specialtyName.trim().isEmpty()) {
            throw new IllegalArgumentException("Specialty name is required");
        }
        
        return ownerRepository.findOwnerNamesByPetTypeAndVetSpecialty(
            petTypeName.trim(), 
            specialtyName.trim()
        );
    }

    private void setIfGiven(String value, Consumer<String> s) {
        if (value != null) {
            s.accept(value);
        }
    }
}
