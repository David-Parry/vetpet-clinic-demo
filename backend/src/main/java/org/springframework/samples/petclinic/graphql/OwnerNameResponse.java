/*
 * Copyright 2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.graphql;

/**
 * Response DTO for owner name queries.
 * Contains the full name of an owner (firstName + lastName).
 *
 * @author Agent Implementation
 */
public class OwnerNameResponse {
    private final String fullName;

    /**
     * Constructs an OwnerNameResponse from first and last name components.
     *
     * @param firstName the owner's first name
     * @param lastName the owner's last name
     */
    public OwnerNameResponse(String firstName, String lastName) {
        this.fullName = firstName + " " + lastName;
    }

    /**
     * Gets the full name of the owner.
     *
     * @return the full name (firstName + " " + lastName)
     */
    public String getFullName() {
        return fullName;
    }
}
