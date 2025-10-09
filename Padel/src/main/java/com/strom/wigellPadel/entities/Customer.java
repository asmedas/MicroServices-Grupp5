package com.strom.wigellPadel.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "customers", uniqueConstraints = {
            @UniqueConstraint(name = "uk_customer_username", columnNames = {"username"}),
            @UniqueConstraint(name = "uk_customer_kc_user_id", columnNames = {"keycloak_user_id"})
        },
        indexes = {
            @Index(name = "idx_customer_kc_user_id", columnList = "keycloak_user_id")
        }
)
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @JsonBackReference
    @ManyToMany(mappedBy = "customers", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Address> address = new HashSet<>();

    @Column(name = "email", nullable = false, length = 50)
    private String email;

    @Column(name = "username", nullable = false, length = 50, unique = true)
    private String username;

    @Column(name = "keycloak_user_id", unique = true, length = 36)
    private String keycloakUserId;

    protected Customer() {
    }

    public Customer(String firstName, String lastName, Set<Address> address, String email, String username) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.email = email;
        this.username = username;
    }

    public Customer(String firstName, String lastName, Set<Address> address, String email, String username, String keycloakUserId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.email = email;
        this.username = username;
        this.keycloakUserId = keycloakUserId;
    }

    public Long getId() {
        return id;
    }

    public Customer setId(Long id) {
        this.id = id;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public Customer setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public Customer setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public Set<Address> getAddress() {
        return address;
    }

    public Customer setAddress(Set<Address> address) {
        this.address = address;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public Customer setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public Customer setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getKeycloakUserId() {
        return keycloakUserId;
    }

    public Customer setKeycloakUserId(String keycloakUserId) {
        this.keycloakUserId = keycloakUserId;
        return this;
    }
}
