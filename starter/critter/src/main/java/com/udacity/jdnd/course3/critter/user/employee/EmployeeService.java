package com.udacity.jdnd.course3.critter.user.employee;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    @Autowired
    EmployeeRepository repository;

    public Employee saveEmployee(Employee employee) {
        return repository.save(employee);
    }

    public Optional<Employee> getEmployee(long id) {
        return repository.findById(id);
    }

    public List<Employee> getEmployees(List<Long> employeeIds) {
        return repository.findAllById(employeeIds);
    }

    public List<Employee> getEmployees() {
        return repository.findAll();
    }

    public List<Employee> findEmployeesBySkillsAndDayOfWeek(Set<EmployeeSkill> skills, DayOfWeek dayOfWeek) {
        // That could be created with one SQL query for better optimization but I am not an expert...
        return getEmployees().stream()
                .filter(e -> e.getSkills().containsAll(skills) && e.getDaysAvailable().contains(dayOfWeek))
                .collect(Collectors.toList());
    }
}
