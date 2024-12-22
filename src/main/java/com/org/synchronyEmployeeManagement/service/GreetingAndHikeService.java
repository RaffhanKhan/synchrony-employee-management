package com.org.synchronyEmployeeManagement.service;

import com.org.synchronyEmployeeManagement.dto.ProcessGreetingsAndHikeDTO;
import com.org.synchronyEmployeeManagement.model.Employee;
import com.org.synchronyEmployeeManagement.repo.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.Executor;

@Service
@RequiredArgsConstructor
public class GreetingAndHikeService {

    public static final Logger LOGGER = LoggerFactory.getLogger(GreetingAndHikeService.class);

    private final EmployeeRepository employeeRepository;

    private final EmployeeServiceImpl employeeService;

    private final Executor executor;

    @Async
    public void sendGreeting(Employee employee, String message) {
        employee.setLatestGreeting(message);
        employeeService.updateEmployee(employee);
        LOGGER.info(message);
    }

    @Async
    public void processSalaryHike(Employee employee, Double defaultHike) {
        BigDecimal hikeMultiplier = BigDecimal.valueOf(1 + (defaultHike / 100));
        employee.setSalary(employee.getSalary().multiply(hikeMultiplier));
        employee.setLatestHike(defaultHike);
        employeeService.updateEmployee(employee);
        LOGGER.info("Processed salary hike for: " + employee.getName());
    }

    public void processGreetingsAndHikes(ProcessGreetingsAndHikeDTO processGreetingsAndHikeDTO) {
        List<Employee> employees = employeeRepository.findAll();
        for (Employee employee : employees) {
            executor.execute(() -> sendGreeting(employee, processGreetingsAndHikeDTO.getGreetingMessage()));
            executor.execute(() -> processSalaryHike(employee, processGreetingsAndHikeDTO.getDefaultHike()));
        }
    }
}
