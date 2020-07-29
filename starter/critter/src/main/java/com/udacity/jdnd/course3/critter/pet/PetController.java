package com.udacity.jdnd.course3.critter.pet;

import com.udacity.jdnd.course3.critter.user.customer.Customer;
import com.udacity.jdnd.course3.critter.user.customer.CustomerNotFoundException;
import com.udacity.jdnd.course3.critter.user.customer.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Handles web requests related to Pets.
 */
@RestController
@RequestMapping("/pet")
public class PetController {

    @Autowired
    CustomerService customerService;

    @Autowired
    PetService petService;

    @PostMapping
    public PetDTO savePet(@RequestBody PetDTO petDTO) {
        Pet newPet = petService.savePet(toPet(petDTO));
        return toDTO(newPet);
    }

    @GetMapping("/{petId}")
    public PetDTO getPet(@PathVariable long petId) {
        Pet pet = petService.getPet(petId).orElseThrow(PetNotFoundException::new);
        return toDTO(pet);
    }

    @GetMapping
    public List<PetDTO> getPets(){
        return petService.getAllPets().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @GetMapping("/owner/{ownerId}")
    public List<PetDTO> getPetsByOwner(@PathVariable long ownerId) {
        Customer customer = customerService.findById(ownerId).orElseThrow(CustomerNotFoundException::new);
        List<Pet> pets = customer.getPets();
        if (pets != null) {
            return pets.stream().map(this::toDTO).collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }

    private PetDTO toDTO(Pet p) {
        PetDTO dto = new PetDTO();
        dto.setId(p.getId());
        dto.setType(p.getType());
        dto.setName(p.getName());
        dto.setOwnerId(p.getCustomer() != null ? p.getCustomer().getId() : null);
        dto.setBirthDate(p.getBirthDate());
        dto.setNotes(p.getNotes());
        return dto;
    }

    private Pet toPet(PetDTO dto) {
        Pet p = new Pet();
        Long ownerId = dto.getOwnerId();
        if (ownerId != null) {
            Optional<Customer> customer = customerService.findById(ownerId);
            p.setCustomer(customer.orElse(null));
        }
        p.setId(dto.getId());
        p.setType(dto.getType());
        p.setName(dto.getName());
        p.setBirthDate(dto.getBirthDate());
        p.setNotes(dto.getNotes());
        return p;
    }
}
