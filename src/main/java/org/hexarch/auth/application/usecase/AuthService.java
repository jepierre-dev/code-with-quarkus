package org.hexarch.auth.application.usecase;

import java.util.UUID;

import org.hexarch.auth.application.port.in.AuthUseCase;
import org.hexarch.auth.application.port.out.CredentialRepositoryPort;
import org.hexarch.auth.application.port.out.PasswordHasherPort;
import org.hexarch.auth.application.port.out.TokenProviderPort;
import org.hexarch.auth.domain.exceptions.AuthErrors;
import org.hexarch.auth.domain.model.AuthToken;
import org.hexarch.auth.domain.model.RawPassword;
import org.hexarch.user.application.port.in.UsersUseCase;
import org.hexarch.user.domain.model.UserModel;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class AuthService implements AuthUseCase {

    private final UsersUseCase usersUseCase;
    private final CredentialRepositoryPort credentialRepository;
    private final PasswordHasherPort passwordHasher;
    private final TokenProviderPort tokenProvider;

    public AuthService(UsersUseCase usersUseCase, CredentialRepositoryPort credentialRepository,
            PasswordHasherPort passwordHasher, TokenProviderPort tokenProvider) {
        this.usersUseCase = usersUseCase;
        this.credentialRepository = credentialRepository;
        this.passwordHasher = passwordHasher;
        this.tokenProvider = tokenProvider;
    }

    // La contrasena se valida antes de crear el usuario para no dejar cuentas sin credencial.
    @Override
    @Transactional
    public AuthToken register(String username, String email, String password) {
        String passHash = passwordHasher.hash(new RawPassword(password));

        UserModel user = usersUseCase.createUser(username, email);
        credentialRepository.save(user.id(), passHash);
        return tokenProvider.issue(user.id(), user.email());
    }

    @Override
    public AuthToken login(String email, String password) {
        UserModel user = usersUseCase.findByEmail(email).orElseThrow(AuthErrors::invalidCredentials);
        String passHash = credentialRepository.findHashByUserId(user.id())
                .orElseThrow(AuthErrors::invalidCredentials);

        if (!passwordHasher.matches(password, passHash)) {
            throw AuthErrors.invalidCredentials();
        }
        // El baneo se comprueba despues del hash: adelantarlo revelaria que la cuenta existe.
        if (user.banned()) {
            throw AuthErrors.accountBanned();
        }
        return tokenProvider.issue(user.id(), user.email());
    }

    @Override
    @Transactional
    public void changePassword(UUID userId, String currentPassword, String newPassword) {
        String passHash = credentialRepository.findHashByUserId(userId)
                .orElseThrow(() -> AuthErrors.credentialNotFound(userId));

        if (!passwordHasher.matches(currentPassword, passHash)) {
            throw AuthErrors.invalidCredentials();
        }
        credentialRepository.updateHash(userId, passwordHasher.hash(new RawPassword(newPassword)));
    }
}
