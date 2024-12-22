package com.org.synchronyEmployeeManagement.service;

import com.org.synchronyEmployeeManagement.model.Employee;
import com.org.synchronyEmployeeManagement.repo.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    public static final Logger LOGGER = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    private final EmployeeRepository employeeRepository;

    private final CacheService cacheService;

    @Cacheable(value = "employee", key ="#employee.id")
    public Employee createEmployee(Employee employee) {
        Employee savedEmployee = employeeRepository.save(employee);
        cacheService.cacheEmployeeById(String.valueOf(employee.getId()), savedEmployee);
        return savedEmployee;
    }


    @Cacheable(value = "employee", key ="#employee.id")
    public Employee getEmployeeById(Long id) {
        Employee cachedEmployee = (Employee) cacheService.getEmployeeFromCache(id);
        if (cachedEmployee != null) {
            LOGGER.info("Returned-from-CACHE");
            return cachedEmployee;
        }
        Optional<Employee> employee = employeeRepository.findById(id);
        if (employee.isPresent()) {
            cacheService.cacheEmployeeById(String.valueOf(id), employee.get());
            LOGGER.info("Returned-from-DATABASE");
            return employee.get();
        }
        return null;
    }

    @Cacheable(value = "employee", key ="#employee")
    public List<Employee> getAllEmployees() {
        List<Employee> employees = employeeRepository.findAll();
        for (Employee employee : employees) {
            cacheService.cacheEmployeeById(String.valueOf(employee.getId()), employee);
        }
        return employees;
    }



    @CacheEvict(value = "employee", key = "#employee.id")
    public void deleteEmployee(Long employeeId) {
        Optional<Employee> employee = employeeRepository.findById(employeeId);
        if(employee.isPresent()){
            cacheService.evictEmployeeCache(employeeId);
            employeeRepository.deleteById(employeeId);
        }
    }

    @CachePut(value = "employee", key = "#employee.id")
    public Employee updateEmployee(Employee employee) {
        Optional<Employee> existingEmployeeOpt = employeeRepository.findById(employee.getId());
        cacheService.evictEmployeeCache(employee.getId());
        if (existingEmployeeOpt.isPresent()) {
            Employee existingEmployee = existingEmployeeOpt.get();
            if (employee.getName() != null) {
                existingEmployee.setName(employee.getName());
            }
            if (employee.getEmail() != null) {
                existingEmployee.setEmail(employee.getEmail());
            }
            if (employee.getSalary() != null) {
                existingEmployee.setSalary(employee.getSalary());
            }
            if (employee.getLatestHike() != null) {
                existingEmployee.setLatestHike(employee.getLatestHike());
            }
            if(employee.getLatestGreeting() != null) {
                existingEmployee.setLatestGreeting(employee.getLatestGreeting());
            }
            Employee updatedEmployee = employeeRepository.save(existingEmployee);
            cacheService.cacheEmployeeById(String.valueOf(employee.getId()), updatedEmployee);
            return updatedEmployee;
        } else {
            return null;
        }
    }

}
