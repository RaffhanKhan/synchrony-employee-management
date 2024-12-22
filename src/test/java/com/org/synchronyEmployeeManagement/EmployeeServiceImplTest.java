package com.org.synchronyEmployeeManagement;

import com.org.synchronyEmployeeManagement.model.Employee;
import com.org.synchronyEmployeeManagement.repo.EmployeeRepository;
import com.org.synchronyEmployeeManagement.service.CacheService;
import com.org.synchronyEmployeeManagement.service.EmployeeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Optional;
public class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private CacheService cacheService;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private Employee employee;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        employee = new Employee(1L, "John Doe", "john.doe@example.com", new BigDecimal(50000.00), "Good Morning", 10.5);
    }

    @Test
    void testCreateEmployee() {
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        Employee savedEmployee = employeeService.createEmployee(employee);

        assertNotNull(savedEmployee);
        assertEquals("John Doe", savedEmployee.getName());
        verify(employeeRepository, times(1)).save(employee);
        verify(cacheService, times(1)).cacheEmployeeById("1", employee);
    }

    @Test
    void testGetEmployeeById() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        Employee foundEmployee = employeeService.getEmployeeById(1L);

        assertNotNull(foundEmployee);
        assertEquals("John Doe", foundEmployee.getName());
        verify(cacheService, times(1)).cacheEmployeeById("1", employee);
    }

    @Test
    void testGetEmployeeById_NotFound() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        Employee foundEmployee = employeeService.getEmployeeById(1L);

        assertNull(foundEmployee);
    }

    @Test
    void testUpdateEmployee() {
        Employee updatedEmployee = new Employee(1L, "John Doe", "john.doe@newdomain.com", new BigDecimal(60000.00), "Good Afternoon", 10.5);

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(updatedEmployee);

        Employee result = employeeService.updateEmployee(updatedEmployee);

        assertNotNull(result);
        assertEquals("john.doe@newdomain.com", result.getEmail());
        verify(employeeRepository, times(1)).save(updatedEmployee);
        verify(cacheService, times(1)).evictEmployeeCache(1L);
        verify(cacheService, times(1)).cacheEmployeeById("1", updatedEmployee);
    }

    @Test
    void testUpdateEmployeeWhenEmployeeDoesNotExist() {
        when(employeeRepository.findById(employee.getId())).thenReturn(Optional.empty());
        Employee result = employeeService.updateEmployee(employee);
        verify(employeeRepository).findById(employee.getId());
        verify(cacheService).evictEmployeeCache(employee.getId());
        assertNull(result);
    }

    @Test
    void testDeleteEmployee() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        employeeService.deleteEmployee(1L);

        verify(employeeRepository, times(1)).deleteById(1L);
        verify(cacheService, times(1)).evictEmployeeCache(1L);
    }
}
