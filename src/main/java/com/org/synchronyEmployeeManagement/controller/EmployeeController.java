package com.org.synchronyEmployeeManagement.controller;

import com.org.synchronyEmployeeManagement.dto.ProcessGreetingsAndHikeDTO;
import com.org.synchronyEmployeeManagement.model.Employee;
import com.org.synchronyEmployeeManagement.service.EmployeeService;
import com.org.synchronyEmployeeManagement.service.GreetingAndHikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class EmployeeController {

    private final GreetingAndHikeService greetingAndHikeService;
    private final EmployeeService employeeService;

    private static final String PAYLOAD = "payload";
    private static final String STATUS = "status";
    private static final String ERROR = "error";
    private static final String SUCCESS = "success";


    @PostMapping("/createEmployee")
    public ResponseEntity<Map<String , Object>> createEmployee(@RequestBody Employee employee){
        Map<String, Object> resp = new HashMap<>();
        resp.put(PAYLOAD, employeeService.createEmployee(employee));
        return new ResponseEntity<>(resp, HttpStatus.CREATED);
    }

    @GetMapping("/getEmployee")
    public ResponseEntity<Map<String, Object>> getEmployee(@RequestParam Long id) {

        Map<String, Object> resp = new HashMap<>();
        resp.put(PAYLOAD, employeeService.getEmployeeById(id));
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    @PutMapping("/updateEmployee")
    public ResponseEntity<Map<String, Object>> updateEmployee(@RequestBody Employee employee) {
        Map<String, Object> resp = new HashMap<>();
        Employee updatedEmployee = employeeService.updateEmployee(employee);
        if (updatedEmployee != null) {
            resp.put(PAYLOAD, updatedEmployee);
            return new ResponseEntity<>(resp, HttpStatus.OK);
        } else {
            resp.put(ERROR, "Employee not found");
            return new ResponseEntity<>(resp, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/deleteEmployee")
    public ResponseEntity<Map<String, Object>> deleteEmployee(@RequestParam Long id) {
        employeeService.deleteEmployee(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/getAllEmployees")
    public ResponseEntity<Map<String, Object>> getAllEmployees() {
        Map<String, Object> resp = new HashMap<>();
        List<Employee> employees = employeeService.getAllEmployees();
        resp.put(PAYLOAD, employees);
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    @PostMapping("/initiate-hikes")
    public String initiateHikes(@RequestBody ProcessGreetingsAndHikeDTO processGreetingsAndHikeDTO) {
        greetingAndHikeService.processGreetingsAndHikes(processGreetingsAndHikeDTO);
        return "Employee greetings and hikes are being processed asynchronously!";
    }
}
