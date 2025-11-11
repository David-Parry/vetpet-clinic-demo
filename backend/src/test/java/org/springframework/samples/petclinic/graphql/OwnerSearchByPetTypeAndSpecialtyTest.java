package org.springframework.samples.petclinic.graphql;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureHttpGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.samples.petclinic.PetClinicTestDbConfiguration;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for the ownersByPetTypeAndVetSpecialty GraphQL query.
 * 
 * This test verifies the functionality of finding owners who have pets of a specific type
 * and have used veterinary services from vets with a particular specialty.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureHttpGraphQlTester
@Import(PetClinicTestDbConfiguration.class)
public class OwnerSearchByPetTypeAndSpecialtyTest extends GraphQlTokenProvider {

    private static final Logger log = LoggerFactory.getLogger(OwnerSearchByPetTypeAndSpecialtyTest.class);

    @Autowired
    private HttpGraphQlTester graphQlTester;

    @Test
    void testOwnersByPetTypeAndVetSpecialty_WithValidParameters_ReturnsDistinctOwners() {
        // Create a GraphQL tester with user authentication
        var userRoleGraphQlTester = graphQlTester.mutate()
            .headers(headers -> headers.setBearerAuth(createUserToken()))
            .build();

        var query = """
            query {
                ownersByPetTypeAndVetSpecialty(
                    petTypeName: "cat"
                    specialtyName: "radiology"
                )
            }
            """;

        userRoleGraphQlTester.document(query)
            .execute()
            .path("ownersByPetTypeAndVetSpecialty")
            .entityList(String.class)
            .satisfies(owners -> {
                log.info("Found {} owners with cats and radiology visits", owners.size());
                assertThat(owners).doesNotHaveDuplicates();
                assertThat(owners).allMatch(name -> 
                    name.contains(" ") && !name.trim().isEmpty()
                );
            });
    }

    @Test
    void testOwnersByPetTypeAndVetSpecialty_CaseInsensitive_ReturnsResults() {
        var userRoleGraphQlTester = graphQlTester.mutate()
            .headers(headers -> headers.setBearerAuth(createUserToken()))
            .build();

        var query = """
            query {
                ownersByPetTypeAndVetSpecialty(
                    petTypeName: "CAT"
                    specialtyName: "RADIOLOGY"
                )
            }
            """;

        userRoleGraphQlTester.document(query)
            .execute()
            .path("ownersByPetTypeAndVetSpecialty")
            .entityList(String.class)
            .satisfies(owners -> {
                log.info("Case-insensitive search found {} owners", owners.size());
                assertThat(owners).isNotNull();
            });
    }

    @Test
    void testOwnersByPetTypeAndVetSpecialty_WithDogAndSurgery_ReturnsResults() {
        var userRoleGraphQlTester = graphQlTester.mutate()
            .headers(headers -> headers.setBearerAuth(createUserToken()))
            .build();

        var query = """
            query {
                ownersByPetTypeAndVetSpecialty(
                    petTypeName: "dog"
                    specialtyName: "surgery"
                )
            }
            """;

        userRoleGraphQlTester.document(query)
            .execute()
            .path("ownersByPetTypeAndVetSpecialty")
            .entityList(String.class)
            .satisfies(owners -> {
                log.info("Found {} owners with dogs and surgery visits", owners.size());
                assertThat(owners).isNotNull();
                assertThat(owners).doesNotHaveDuplicates();
            });
    }

    @Test
    void testOwnersByPetTypeAndVetSpecialty_WithNonExistentPetType_ReturnsEmptyList() {
        var userRoleGraphQlTester = graphQlTester.mutate()
            .headers(headers -> headers.setBearerAuth(createUserToken()))
            .build();

        var query = """
            query {
                ownersByPetTypeAndVetSpecialty(
                    petTypeName: "elephant"
                    specialtyName: "surgery"
                )
            }
            """;

        userRoleGraphQlTester.document(query)
            .execute()
            .path("ownersByPetTypeAndVetSpecialty")
            .entityList(String.class)
            .satisfies(owners -> {
                log.info("Non-existent pet type returned {} owners", owners.size());
                assertThat(owners).isEmpty();
            });
    }

    @Test
    void testOwnersByPetTypeAndVetSpecialty_WithNonExistentSpecialty_ReturnsEmptyList() {
        var userRoleGraphQlTester = graphQlTester.mutate()
            .headers(headers -> headers.setBearerAuth(createUserToken()))
            .build();

        var query = """
            query {
                ownersByPetTypeAndVetSpecialty(
                    petTypeName: "cat"
                    specialtyName: "neurosurgery"
                )
            }
            """;

        userRoleGraphQlTester.document(query)
            .execute()
            .path("ownersByPetTypeAndVetSpecialty")
            .entityList(String.class)
            .satisfies(owners -> {
                log.info("Non-existent specialty returned {} owners", owners.size());
                assertThat(owners).isEmpty();
            });
    }

    @Test
    void testOwnersByPetTypeAndVetSpecialty_WithEmptyPetTypeName_ReturnsError() {
        var userRoleGraphQlTester = graphQlTester.mutate()
            .headers(headers -> headers.setBearerAuth(createUserToken()))
            .build();

        var query = """
            query {
                ownersByPetTypeAndVetSpecialty(
                    petTypeName: ""
                    specialtyName: "surgery"
                )
            }
            """;

        userRoleGraphQlTester.document(query)
            .execute()
            .errors()
            .satisfy(errors -> {
                assertThat(errors).isNotEmpty();
                log.info("Empty petTypeName correctly returned error");
            });
    }

    @Test
    void testOwnersByPetTypeAndVetSpecialty_WithEmptySpecialtyName_ReturnsError() {
        var userRoleGraphQlTester = graphQlTester.mutate()
            .headers(headers -> headers.setBearerAuth(createUserToken()))
            .build();

        var query = """
            query {
                ownersByPetTypeAndVetSpecialty(
                    petTypeName: "cat"
                    specialtyName: ""
                )
            }
            """;

        userRoleGraphQlTester.document(query)
            .execute()
            .errors()
            .satisfy(errors -> {
                assertThat(errors).isNotEmpty();
                log.info("Empty specialtyName correctly returned error");
            });
    }

    @Test
    void testOwnersByPetTypeAndVetSpecialty_WithWhitespacePetTypeName_ReturnsError() {
        var userRoleGraphQlTester = graphQlTester.mutate()
            .headers(headers -> headers.setBearerAuth(createUserToken()))
            .build();

        var query = """
            query {
                ownersByPetTypeAndVetSpecialty(
                    petTypeName: "   "
                    specialtyName: "surgery"
                )
            }
            """;

        userRoleGraphQlTester.document(query)
            .execute()
            .errors()
            .satisfy(errors -> {
                assertThat(errors).isNotEmpty();
                log.info("Whitespace-only petTypeName correctly returned error");
            });
    }
}
