package com.udacity.jdnd.course3.critter.user;

import com.udacity.jdnd.course3.critter.pet.Pet;
import com.udacity.jdnd.course3.critter.pet.PetNotFoundException;
import com.udacity.jdnd.course3.critter.pet.PetService;
import com.udacity.jdnd.course3.critter.user.employee.*;
import com.udacity.jdnd.course3.critter.user.customer.Customer;
import com.udacity.jdnd.course3.critter.user.customer.CustomerDTO;
import com.udacity.jdnd.course3.critter.user.customer.CustomerNotFoundException;
import com.udacity.jdnd.course3.critter.user.customer.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Handles web requests related to Users.
 *
 * Includes requests for both customers and employees. Splitting this into separate user and customer controllers
 * would be fine too, though that is not part of the required scope for this class.
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    CustomerService customerService;

    @Autowired
    PetService petService;

    @Autowired
    EmployeeService employeeService;

    @PostMapping("/customer")
    public CustomerDTO saveCustomer(@RequestBody CustomerDTO customerDTO){
        Customer customer = toCustomer(customerDTO);
        return toDTO(customerService.saveCustomer(customer));
    }

    @GetMapping("/customer")
    public List<CustomerDTO> getAllCustomers(){
        return customerService.getAllCustomers().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @GetMapping("/customer/pet/{petId}")
    public CustomerDTO getOwnerByPet(@PathVariable long petId){
        Pet pet = petService.getPet(petId).orElseThrow(PetNotFoundException::new);
        if (pet.getCustomer() != null) {
            return toDTO(pet.getCustomer());
        } else {
            throw new CustomerNotFoundException("Pet without owner");
        }
    }

    @PostMapping("/employee")
    public EmployeeDTO saveEmployee(@RequestBody EmployeeDTO employeeDTO) {
        return toDTO(employeeService.saveEmployee(toEmployee(employeeDTO)));
    }

    @PostMapping("/employee/{employeeId}")
    public EmployeeDTO getEmployee(@PathVariable long employeeId) {
        Employee employee = employeeService.getEmployee(employeeId).orElseThrow(EmployeeNotFoundException::new);
        return toDTO(employee);
    }

    @PutMapping("/employee/{employeeId}")
    public void setAvailability(@RequestBody Set<DayOfWeek> daysAvailable, @PathVariable long employeeId) {
        Employee employee = employeeService.getEmployee(employeeId).orElseThrow(EmployeeNotFoundException::new);
        employee.setDaysAvailable(daysAvailable);
    }

    @GetMapping("/employee/availability")
    public List<EmployeeDTO> findEmployeesForService(@RequestBody EmployeeRequestDTO employeeDTO) {
        return employeeService.findEmployeesBySkillsAndDayOfWeek(
                employeeDTO.getSkills(), employeeDTO.getDate().getDayOfWeek()
        ).stream().map(this::toDTO).collect(Collectors.toList());
    }

    private EmployeeDTO toDTO(Employee e) {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setId(e.getId());
        dto.setName(e.getName());
        dto.setDaysAvailable(e.getDaysAvailable());
        dto.setSkills(e.getSkills());
        return dto;
    }

    private Employee toEmployee(EmployeeDTO dto) {
        Employee employee = new Employee();
        employee.setId(dto.getId());
        employee.setName(dto.getName());
        employee.setDaysAvailable(dto.getDaysAvailable());
        employee.setSkills(dto.getSkills());
        return employee;
    }

    private CustomerDTO toDTO(Customer c) {
        CustomerDTO dto = new CustomerDTO();
        dto.setId(c.getId());
        dto.setName(c.getName());
        dto.setNotes(c.getNotes());
        dto.setPhoneNumber(c.getPhoneNumber());
        if (c.getPets() != null) {
            dto.setPetIds(c.getPets().stream().map(Pet::getId).collect(Collectors.toList()));
        }
        return dto;
    }

    private Customer toCustomer(CustomerDTO dto) {
        Customer c = new Customer();
        c.setId(dto.getId());
        c.setName(dto.getName());
        c.setNotes(dto.getNotes());
        c.setPhoneNumber(dto.getPhoneNumber());

        List<Long> petIds = dto.getPetIds();
        if (petIds != null) {
            c.setPets(petService.getPets(petIds));
        }
        return c;
    }
}
