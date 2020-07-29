package com.udacity.jdnd.course3.critter.schedule;

import com.udacity.jdnd.course3.critter.pet.Pet;
import com.udacity.jdnd.course3.critter.user.customer.Customer;
import com.udacity.jdnd.course3.critter.user.employee.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ScheduleService {

    @Autowired
    ScheduleRepository scheduleRepository;

    public Schedule saveSchedule(Schedule schedule) {
        return scheduleRepository.save(schedule);
    }

    public List<Schedule> getSchedules() {
        return scheduleRepository.findAll();
    }

    public Optional<Schedule> getSchedule(long scheduleId) {
        return scheduleRepository.findById(scheduleId);
    }

    public List<Schedule> getPetSchedules(Pet pet) {
        // That could be created with one SQL query for better optimization but I am not an expert...
        return getSchedules().stream()
                .filter(schedule -> schedule.getPets().contains(pet))
                .collect(Collectors.toList());
    }

    public List<Schedule> getEmployeeSchedules(Employee employee) {
        // That could be created with one SQL query for better optimization but I am not an expert...
        return getSchedules().stream()
                .filter(schedule -> schedule.getEmployees().contains(employee))
                .collect(Collectors.toList());
    }

    public List<Schedule> getCustomerSchedules(Customer customer) {
        // That could be created with one SQL query for better optimization but I am not an expert...
        return getSchedules().stream()
                .filter(schedule -> schedule.getPets().stream()
                        .anyMatch(pet -> pet.getCustomer().getId().equals(customer.getId())))
                .collect(Collectors.toList());
    }
}
