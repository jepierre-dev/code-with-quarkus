package org.hexarch.user.application.usecase;

import java.util.Optional;
import java.util.UUID;

import org.hexarch.user.application.port.in.UsersUseCase;
import org.hexarch.user.application.port.out.UserRepositoryPort;
import org.hexarch.user.domain.exceptions.UserErrors;
import org.hexarch.user.domain.model.UserModel;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class UsersService implements UsersUseCase {

    private final UserRepositoryPort userRepository;

    public UsersService(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserModel createUser(String username, String email) {
        if (userRepository.existsByUsernameOrEmail(username, email)) {
            throw UserErrors.userAlreadyExists(username, email);
        }
        return userRepository.create(new UserModel(username, email));
    }

    @Override
    public UserModel findById(UUID userId) {
        return userRepository.findById(userId).orElseThrow(() -> UserErrors.userNotFound(userId));
    }

    @Override
    public Optional<UserModel> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional
    public void banUser(UUID userId) {
        userRepository.setBanned(userId, true);
    }

    @Override
    @Transactional
    public void unbanUser(UUID userId) {
        userRepository.setBanned(userId, false);
    }
}
