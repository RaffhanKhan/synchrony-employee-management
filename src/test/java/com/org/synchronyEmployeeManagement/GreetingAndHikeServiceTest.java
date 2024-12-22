package com.org.synchronyEmployeeManagement;

import com.org.synchronyEmployeeManagement.dto.ProcessGreetingsAndHikeDTO;
import com.org.synchronyEmployeeManagement.model.Employee;
import com.org.synchronyEmployeeManagement.repo.EmployeeRepository;
import com.org.synchronyEmployeeManagement.service.EmployeeServiceImpl;
import com.org.synchronyEmployeeManagement.service.GreetingAndHikeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;

import static org.mockito.Mockito.*;

public class GreetingAndHikeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private EmployeeServiceImpl employeeService;

    @Mock
    private Executor executor;

    @InjectMocks
    private GreetingAndHikeService greetingAndHikeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSendGreeting() {
        Employee employee = new Employee(1L, "John Doe", "john.doe@example.com", BigDecimal.valueOf(50000), "Hello", 10.5);
        String message = "Good Morning";

        greetingAndHikeService.sendGreeting(employee, message);

        verify(employeeService, times(1)).updateEmployee(employee);
        assert employee.getLatestGreeting().equals(message);
    }

    @Test
    void testProcessSalaryHike() {
        Employee employee = new Employee(1L, "John Doe", "john.doe@example.com", BigDecimal.valueOf(50000), "Hello", 10.5);
        Double hike = 1.1;
        greetingAndHikeService.processSalaryHike(employee, hike);
        verify(employeeService, times(1)).updateEmployee(employee);
        assert employee.getSalary().compareTo(BigDecimal.valueOf(55000)) == 0;
    }

    @Test
    void testProcessGreetingsAndHikes() throws InterruptedException {
        Employee employee1 = new Employee(1L, "John Doe", "john@example.com", new BigDecimal(50000.00), "Hello", 10.5);
        Employee employee2 = new Employee(2L, "Jane Doe", "jane@example.com", new BigDecimal(60000.00), "Hi", 10.5);
        List<Employee> employees = Arrays.asList(employee1, employee2);
        ProcessGreetingsAndHikeDTO processGreetingsAndHikeDTO = new ProcessGreetingsAndHikeDTO();
        processGreetingsAndHikeDTO.setGreetingMessage("Happy New Year!");
        processGreetingsAndHikeDTO.setDefaultHike(1.1);
        when(employeeRepository.findAll()).thenReturn(employees);
        doNothing().when(executor).execute(any(Runnable.class));
        greetingAndHikeService.processGreetingsAndHikes(processGreetingsAndHikeDTO);
        verify(executor, times(4)).execute(any(Runnable.class));

    }

}
