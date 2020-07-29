package com.udacity.jdnd.course3.critter.pet;

import com.udacity.jdnd.course3.critter.user.customer.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PetService {

    @Autowired
    PetRepository petRepository;

    public Pet savePet(Pet pet) {
        Pet newPet = petRepository.save(pet);
        Customer customer = newPet.getCustomer();
        if (customer != null) {
            if (customer.getPets() == null) {
                customer.setPets(new ArrayList<>());
            }
            customer.getPets().add(pet);
        }
        return newPet;
    }

    public List<Pet> getAllPets() {
        return petRepository.findAll();
    }

    public List<Pet> getPets(List<Long> petIds) {
        return petRepository.findAllById(petIds);
    }

    public Optional<Pet> getPet(Long petId) {
        return petRepository.findById(petId);
    }
}
