package com.strom.wigellPadel.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

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

    @Column(name = "username", nullable = false, length = 50, unique = true)
    private String username;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;

    @Column(name = "keycloak_user_id", unique = true, length = 36)
    private String keycloakUserId;

    protected Customer() {
    }

    public Customer(String username, String firstName, String lastName, Address address) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
    }

    public Customer(String username, String firstName, String lastName) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Customer(String username, String firstName, String lastName, Address address, String keycloakUserId) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.keycloakUserId = keycloakUserId;
    }

    public Long getId() {
        return id;
    }

    public Customer setId(Long id) {
        this.id = id;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public Customer setUsername(String username) {
        this.username = username;
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

    public Address getAddress() {
        return address;
    }

    public Customer setAddress(Address address) {
        this.address = address;
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
