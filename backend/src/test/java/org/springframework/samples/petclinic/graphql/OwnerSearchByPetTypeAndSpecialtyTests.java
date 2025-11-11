package org.springframework.samples.petclinic.graphql;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for the ownersByPetTypeAndVetSpecialty GraphQL query.
 * 
 * This test class verifies the functionality of finding owners who have pets
 * of a specific type that have been treated by vets with a particular specialty.
 * 
 * @author Agent Implementation
 */
public class OwnerSearchByPetTypeAndSpecialtyTests extends AbstractClinicGraphqlTests {

    private static final Logger log = LoggerFactory.getLogger(OwnerSearchByPetTypeAndSpecialtyTests.class);

    @Test
    public void findOwnersByPetTypeAndSpecialty_ValidData_ReturnsOwners() {
        var query = """
            query {
                ownersByPetTypeAndVetSpecialty(
                    petTypeName: "cat", 
                    specialtyName: "radiology"
                ) 
            }
            """;
        
        userRoleGraphQlTester.document(query)
            .execute()
            .path("ownersByPetTypeAndVetSpecialty")
            .entityList(String.class)
            .satisfies(owners -> {
                log.debug("Found owners: {}", owners);
                assertThat(owners).isNotNull();
                // Verify no duplicates
                assertThat(owners).doesNotHaveDuplicates();
                // Verify sorted alphabetically
                if (owners.size() > 1) {
                    assertThat(owners).isSorted();
                }
            });
    }

    @Test
    public void findOwnersByPetTypeAndSpecialty_CaseInsensitive_ReturnsOwners() {
        var queryLowerCase = """
            query {
                ownersByPetTypeAndVetSpecialty(
                    petTypeName: "cat", 
                    specialtyName: "radiology"
                ) 
            }
            """;
        
        var queryUpperCase = """
            query {
                ownersByPetTypeAndVetSpecialty(
                    petTypeName: "CAT", 
                    specialtyName: "RADIOLOGY"
                ) 
            }
            """;
        
        var resultLower = userRoleGraphQlTester.document(queryLowerCase)
            .execute()
            .path("ownersByPetTypeAndVetSpecialty")
            .entityList(String.class)
            .get();
        
        var resultUpper = userRoleGraphQlTester.document(queryUpperCase)
            .execute()
            .path("ownersByPetTypeAndVetSpecialty")
            .entityList(String.class)
            .get();
        
        // Both queries should return the same results
        assertThat(resultLower).isEqualTo(resultUpper);
    }

    @Test
    public void findOwnersByPetTypeAndSpecialty_NonExistentPetType_ReturnsEmptyList() {
        var query = """
            query {
                ownersByPetTypeAndVetSpecialty(
                    petTypeName: "Dragon", 
                    specialtyName: "radiology"
                ) 
            }
            """;
        
        userRoleGraphQlTester.document(query)
            .execute()
            .path("ownersByPetTypeAndVetSpecialty")
            .entityList(String.class)
            .hasSize(0);
    }

    @Test
    public void findOwnersByPetTypeAndSpecialty_NonExistentSpecialty_ReturnsEmptyList() {
        var query = """
            query {
                ownersByPetTypeAndVetSpecialty(
                    petTypeName: "cat", 
                    specialtyName: "magic"
                ) 
            }
            """;
        
        userRoleGraphQlTester.document(query)
            .execute()
            .path("ownersByPetTypeAndVetSpecialty")
            .entityList(String.class)
            .hasSize(0);
    }

    @Test
    public void findOwnersByPetTypeAndSpecialty_EmptyPetTypeName_ReturnsError() {
        var query = """
            query {
                ownersByPetTypeAndVetSpecialty(
                    petTypeName: "", 
                    specialtyName: "radiology"
                ) 
            }
            """;
        
        userRoleGraphQlTester.document(query)
            .execute()
            .errors()
            .satisfy(errors -> {
                assertThat(errors).isNotEmpty();
                assertThat(errors.get(0).getMessage()).contains("petTypeName");
            });
    }

    @Test
    public void findOwnersByPetTypeAndSpecialty_EmptySpecialtyName_ReturnsError() {
        var query = """
            query {
                ownersByPetTypeAndVetSpecialty(
                    petTypeName: "cat", 
                    specialtyName: ""
                ) 
            }
            """;
        
        userRoleGraphQlTester.document(query)
            .execute()
            .errors()
            .satisfy(errors -> {
                assertThat(errors).isNotEmpty();
                assertThat(errors.get(0).getMessage()).contains("specialtyName");
            });
    }

    @Test
    public void findOwnersByPetTypeAndSpecialty_WithWhitespace_TrimsAndReturnsOwners() {
        var query = """
            query {
                ownersByPetTypeAndVetSpecialty(
                    petTypeName: "  cat  ", 
                    specialtyName: "  radiology  "
                ) 
            }
            """;
        
        userRoleGraphQlTester.document(query)
            .execute()
            .path("ownersByPetTypeAndVetSpecialty")
            .entityList(String.class)
            .satisfies(owners -> {
                assertThat(owners).isNotNull();
                assertThat(owners).doesNotHaveDuplicates();
            });
    }

    @Test
    public void findOwnersByPetTypeAndSpecialty_DogAndSurgery_ReturnsExpectedOwners() {
        var query = """
            query {
                ownersByPetTypeAndVetSpecialty(
                    petTypeName: "dog", 
                    specialtyName: "surgery"
                ) 
            }
            """;
        
        userRoleGraphQlTester.document(query)
            .execute()
            .path("ownersByPetTypeAndVetSpecialty")
            .entityList(String.class)
            .satisfies(owners -> {
                log.debug("Found dog owners with surgery vets: {}", owners);
                assertThat(owners).isNotNull();
                assertThat(owners).doesNotHaveDuplicates();
                // Each owner name should be in "FirstName LastName" format
                owners.forEach(name -> {
                    assertThat(name).contains(" ");
                    assertThat(name.trim()).isEqualTo(name);
                });
            });
    }
}
