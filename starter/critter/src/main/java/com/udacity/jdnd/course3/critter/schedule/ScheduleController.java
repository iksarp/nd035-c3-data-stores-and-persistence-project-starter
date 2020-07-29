package com.udacity.jdnd.course3.critter.schedule;

import com.udacity.jdnd.course3.critter.pet.Pet;
import com.udacity.jdnd.course3.critter.pet.PetNotFoundException;
import com.udacity.jdnd.course3.critter.pet.PetService;
import com.udacity.jdnd.course3.critter.user.employee.Employee;
import com.udacity.jdnd.course3.critter.user.employee.EmployeeNotFoundException;
import com.udacity.jdnd.course3.critter.user.employee.EmployeeService;
import com.udacity.jdnd.course3.critter.user.customer.Customer;
import com.udacity.jdnd.course3.critter.user.customer.CustomerNotFoundException;
import com.udacity.jdnd.course3.critter.user.customer.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles web requests related to Schedules.
 */
@RestController
@RequestMapping("/schedule")
public class ScheduleController {

    @Autowired
    EmployeeService employeeService;

    @Autowired
    CustomerService customerService;

    @Autowired
    PetService petService;

    @Autowired
    ScheduleService scheduleService;

    @PostMapping
    public ScheduleDTO createSchedule(@RequestBody ScheduleDTO scheduleDTO) {
        return toDTO(scheduleService.saveSchedule(toSchedule(scheduleDTO)));
    }

    @GetMapping
    public List<ScheduleDTO> getAllSchedules() {
        return toDTOList(scheduleService.getSchedules());
    }

    @GetMapping("/pet/{petId}")
    public List<ScheduleDTO> getScheduleForPet(@PathVariable long petId) {
        Pet pet = petService.getPet(petId).orElseThrow(PetNotFoundException::new);
        return toDTOList(scheduleService.getPetSchedules(pet));
    }

    @GetMapping("/employee/{employeeId}")
    public List<ScheduleDTO> getScheduleForEmployee(@PathVariable long employeeId) {
        Employee employee = employeeService.getEmployee(employeeId).orElseThrow(EmployeeNotFoundException::new);
        return toDTOList(scheduleService.getEmployeeSchedules(employee));
    }

    @GetMapping("/customer/{customerId}")
    public List<ScheduleDTO> getScheduleForCustomer(@PathVariable long customerId) {
        Customer customer = customerService.findById(customerId).orElseThrow(CustomerNotFoundException::new);
        return toDTOList(scheduleService.getCustomerSchedules(customer));
    }

    private List<ScheduleDTO> toDTOList(List<Schedule> schedules) {
        return schedules.stream().map(this::toDTO).collect(Collectors.toList());
    }

    private ScheduleDTO toDTO(Schedule p) {
        ScheduleDTO dto = new ScheduleDTO();
        dto.setId(p.getId());

        List<Employee> employees = p.getEmployees();
        if (employees != null) {
            dto.setEmployeeIds(employees.stream().map(Employee::getId).collect(Collectors.toList()));
        }

        List<Pet> pets = p.getPets();
        if (pets != null) {
            dto.setPetIds(pets.stream().map(Pet::getId).collect(Collectors.toList()));
        }

        dto.setDate(p.getDate());
        dto.setActivities(p.getActivities());
        return dto;
    }

    private Schedule toSchedule(ScheduleDTO dto) {
        Schedule p = new Schedule();
        p.setId(dto.getId());

        List<Long> employeeIds = dto.getEmployeeIds();
        if (employeeIds != null) {
            p.setEmployees(employeeService.getEmployees(employeeIds));
        }

        List<Long> petIds = dto.getPetIds();
        if (petIds != null) {
            p.setPets(petService.getPets(petIds));
        }

        p.setDate(dto.getDate());
        p.setActivities(dto.getActivities());
        return p;
    }
}
