package net.salim.api_motus.service;



import lombok.RequiredArgsConstructor;
import net.salim.api_motus.model.User;
import net.salim.api_motus.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // Créer un utilisateur
    public User createUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        return userRepository.save(user);
    }

    // Obtenir tous les utilisateurs
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Obtenir un utilisateur par ID
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // Obtenir un utilisateur par nom d'utilisateur
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // Obtenir un utilisateur par email
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Mettre à jour un utilisateur
    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setUsername(userDetails.getUsername());
        user.setEmail(userDetails.getEmail());
        return userRepository.save(user);
    }

    // Supprimer un utilisateur
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(id);
    }

    // Vérifier si username existe
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    // Vérifier si email existe
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}