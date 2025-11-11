package org.springframework.samples.petclinic.model;

import org.springframework.samples.petclinic.repository.OwnerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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
     * Find all owner names who have pets of a specific type and have used veterinary services
     * from vets with a particular specialty.
     *
     * @param petTypeName the name of the pet type (case-insensitive)
     * @param specialtyName the name of the vet specialty (case-insensitive)
     * @return a list of owner full names (firstName + lastName), sorted and distinct
     */
    @Transactional(readOnly = true)
    public List<String> findOwnerNamesByPetTypeAndVetSpecialty(
            String petTypeName, String specialtyName) {
        List<Owner> owners = ownerRepository.findOwnersByPetTypeAndVetSpecialty(
            petTypeName, specialtyName);
        
        return owners.stream()
            .map(owner -> owner.getFirstName() + " " + owner.getLastName())
            .distinct()
            .sorted()
            .collect(Collectors.toList());
    }

    private void setIfGiven(String value, Consumer<String> s) {
        if (value != null) {
            s.accept(value);
        }
    }
}
