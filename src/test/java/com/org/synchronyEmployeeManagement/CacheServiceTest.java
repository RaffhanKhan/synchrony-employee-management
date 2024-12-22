package com.org.synchronyEmployeeManagement;
import com.org.synchronyEmployeeManagement.model.Employee;
import com.org.synchronyEmployeeManagement.service.CacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.mockito.Mockito.*;

public class CacheServiceTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private CacheService cacheService;
    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void testCacheEmployeeById() {
        
        Employee employee = new Employee(1L, "John Doe", "john.doe@example.com", null, "Hello", 10.5);
        String employeeId = "1";
        
        cacheService.cacheEmployeeById(employeeId, employee);
        verify(valueOperations, times(1)).set("employee:" + employeeId, employee);
    }

    @Test
    void testEvictEmployeeCache() {
        Long employeeId = 1L;
        cacheService.evictEmployeeCache(employeeId);
        verify(redisTemplate, times(1)).delete("employee:" + employeeId);
    }

    @Test
    void testGetEmployeeFromCache() {
        Long employeeId = 1L;
        Employee employee = new Employee(1L, "John Doe", "john.doe@example.com", null, "Hello", 10.5);
        when(valueOperations.get("employee:" + employeeId)).thenReturn(employee);
        
        Object result = cacheService.getEmployeeFromCache(employeeId);
        
        assert(result instanceof Employee);
        assert(((Employee) result).getName().equals("John Doe"));
    }

    @Test
    void testGetEmployeeFromCacheWhenNoEmployeeInCache() {
        Long employeeId = 1L;
        when(valueOperations.get("employee:" + employeeId)).thenReturn(null);
        Object result = cacheService.getEmployeeFromCache(employeeId);
        assert(result == null);
    }
}
