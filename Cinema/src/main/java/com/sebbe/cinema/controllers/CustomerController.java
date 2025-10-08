package com.sebbe.cinema.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/customers")
@PreAuthorize( "hasRole('ROLE_CUSTOMER')")
public class CustomerController {



}
