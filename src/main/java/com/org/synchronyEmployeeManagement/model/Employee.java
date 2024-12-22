package com.org.synchronyEmployeeManagement.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.math.BigDecimal;

@Entity(name ="employees")
@Data
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private BigDecimal salary;
    private String latestGreeting;
    private Double latestHike;

    public Employee() {
    }

    public Employee(Long id, String name, String email, BigDecimal salary, String latestGreeting, Double latestHike) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.salary = salary;
        this.latestGreeting = latestGreeting;
        this.latestHike = latestHike;
    }
}