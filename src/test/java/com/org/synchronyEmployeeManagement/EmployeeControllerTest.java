package com.org.synchronyEmployeeManagement;

import com.org.synchronyEmployeeManagement.controller.EmployeeController;
import com.org.synchronyEmployeeManagement.model.Employee;
import com.org.synchronyEmployeeManagement.dto.ProcessGreetingsAndHikeDTO;
import com.org.synchronyEmployeeManagement.service.EmployeeServiceImpl;
import com.org.synchronyEmployeeManagement.service.GreetingAndHikeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.math.BigDecimal;
import java.util.List;

public class EmployeeControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EmployeeServiceImpl employeeService;

    @Mock
    private GreetingAndHikeService greetingAndHikeService;

    @InjectMocks
    private EmployeeController employeeController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(employeeController).build();
    }

    @Test
    void testCreateEmployee() throws Exception {
        Employee employee = new Employee(1L, "test", "test@test.com", new BigDecimal("50000.00"), "Good Morning", 10.5);

        when(employeeService.createEmployee(any(Employee.class))).thenReturn(employee);

        mockMvc.perform(post("/createEmployee")
                        .contentType(MediaType.valueOf("application/json"))
                        .content("{\"id\":1,\"name\":\"test\",\"email\":\"test@test.com\",\"salary\":50000,\"latestGreeting\":\"Good Morning\"}")
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.payload.id").value(1L))
                .andExpect(jsonPath("$.payload.name").value("test"))
                .andExpect(jsonPath("$.payload.email").value("test@test.com"));
    }

    @Test
    void testGetEmployee() throws Exception {
        Employee employee = new Employee(1L, "test", "test@test.com", new BigDecimal(50000.00), "Good Morning", 10.5);

        when(employeeService.getEmployeeById(1L)).thenReturn(employee);

        mockMvc.perform(get("/getEmployee")
                        .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.id").value(1L))
                .andExpect(jsonPath("$.payload.name").value("test"));
    }

    @Test
    void testUpdateEmployee() throws Exception {
        Employee existingEmployee = new Employee(1L, "test", "test@test.com", new BigDecimal(50000.00), "Good Morning", 10.5);
        Employee updatedEmployee = new Employee(1L, "test", "john.doe@newdomain.com", new BigDecimal(60000.00), "Good Afternoon", 10.5);

        when(employeeService.updateEmployee(any(Employee.class))).thenReturn(updatedEmployee);

        mockMvc.perform(put("/updateEmployee")
                        .contentType("application/json")
                        .content("{\"id\":1,\"name\":\"test\",\"email\":\"john.doe@newdomain.com\",\"salary\":60000,\"latestGreeting\":\"Good Afternoon\"}")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.id").value(1L))
                .andExpect(jsonPath("$.payload.email").value("john.doe@newdomain.com"));
    }

    @Test
    void testDeleteEmployee() throws Exception {
        Employee employee = new Employee(1L, "test", "test@test.com", new BigDecimal(50000.00), "Good Morning", 10.5);
        employeeService.createEmployee(employee);
        doNothing().when(employeeService).deleteEmployee(1L);

        mockMvc.perform(get("/deleteEmployee")
                        .contentType("application/json")
                        .param("id", "1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetAllEmployees() throws Exception {
        List<Employee> employees = List.of(
                new Employee(1L, "test", "test@test.com", new BigDecimal("50000.00"), "Good Morning", 10.5),
                new Employee(2L, "Jane Smith", "jane.smith@example.com", new BigDecimal(60000.00), "Hello", 10.5)
        );

        when(employeeService.getAllEmployees()).thenReturn(employees);

        mockMvc.perform(get("/getAllEmployees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload[0].id").value(1L))
                .andExpect(jsonPath("$.payload[1].id").value(2L));
    }

    @Test
    void testInitiateHikes() throws Exception {
        ProcessGreetingsAndHikeDTO dto = new ProcessGreetingsAndHikeDTO();
        dto.setGreetingMessage("Hike Processed");

        mockMvc.perform(post("/initiate-hikes")
                        .contentType("application/json")
                        .content("{\"message\":\"Hike Processed\"}")
                )
                .andExpect(status().isOk())
                .andExpect(content().string("Employee greetings and hikes are being processed asynchronously!"));
    }
}
