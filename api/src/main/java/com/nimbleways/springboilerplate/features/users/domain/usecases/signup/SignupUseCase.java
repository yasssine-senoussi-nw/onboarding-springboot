package com.nimbleways.springboilerplate.features.users.domain.usecases.signup;

import com.nimbleways.springboilerplate.common.domain.ports.EmploymentDatePort;
import com.nimbleways.springboilerplate.common.domain.ports.PasswordEncoderPort;
import com.nimbleways.springboilerplate.common.domain.ports.TimeProviderPort;
import com.nimbleways.springboilerplate.common.domain.valueobjects.EncodedPassword;
import com.nimbleways.springboilerplate.features.users.domain.entities.User;
import com.nimbleways.springboilerplate.features.users.domain.ports.UserRepositoryPort;
import com.nimbleways.springboilerplate.features.users.domain.valueobjects.NewUser;
import java.time.Instant;

public class SignupUseCase {
      private final UserRepositoryPort userRepository;
      private final PasswordEncoderPort passwordEncoder;
      private final TimeProviderPort timeProvider;
      private final EmploymentDatePort employmentDateProvider;

      public SignupUseCase(
              final UserRepositoryPort userRepository,
              final PasswordEncoderPort passwordEncoder,
              final TimeProviderPort timeProvider,
              final EmploymentDatePort employmentDateProvider
      ) {
          this.userRepository = userRepository;
          this.passwordEncoder = passwordEncoder;
          this.timeProvider = timeProvider;
          this.employmentDateProvider = employmentDateProvider;
      }

      public User handle(final SignupCommand signupCommand) {
          EncodedPassword encodedPassword = passwordEncoder.encode(signupCommand.plainPassword());
          Instant creationDateTime = timeProvider.instant();
          Instant employmentDate = employmentDateProvider.getEmploymentDate(signupCommand.email());
          // TODO: validate role and fail if it doesn't exist in the repository
          final NewUser newUser = signupCommand.toNewUser(encodedPassword, creationDateTime, employmentDate);
          return userRepository.create(newUser);
      }
}
