package dataaccess;

import java.util.HashMap;
import model.*;

import javax.xml.crypto.Data;


public class MemoryUserDAO implements UserDAO{
    private int nextId = 1;
    final private HashMap<String, UserData> users = new HashMap<>();

//    public Pet addPet(Pet pet) {
//        pet = new Pet(nextId++, pet.name(), pet.type());
//
//        pets.put(pet.id(), pet);
//        return pet;
//    }



    public UserData getUser(String username) throws DataAccessException {
        if (users.containsKey(username)){
            return users.get(username);
        }
        else {
            throw new DataAccessException("Username is already taken.");
        }
    }

//    public PetList listPets() {
//        return new PetList(pets.values());
//    }
//
//    public void deletePet(Integer id) {
//        pets.remove(id);
//    }
//
//    public void deleteAllPets() {
//        pets.clear();
//    }
}
