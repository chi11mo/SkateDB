package com.chillmo.skatedb.user;

import com.chillmo.skatedb.trick.library.repository.TrickLibraryRepository;
import com.chillmo.skatedb.trick_user.TrickStatus;
import com.chillmo.skatedb.trick_user.UserTrick;
import com.chillmo.skatedb.trick_user.UserTrickRepository;
import com.chillmo.skatedb.user.domain.ExperienceLevel;
import com.chillmo.skatedb.user.domain.Role;
import com.chillmo.skatedb.user.domain.Stand;
import com.chillmo.skatedb.user.domain.User;
import com.chillmo.skatedb.user.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@DependsOn("trickDataLoader")
public class TemplateUserDataLoader implements CommandLineRunner {

    private record TemplateUser(
            String username,
            String email,
            String password,
            ExperienceLevel level,
            Stand stand,
            List<String> masteredTricks
    ) {}

    private final UserRepository userRepository;
    private final TrickLibraryRepository trickLibraryRepository;
    private final UserTrickRepository userTrickRepository;
    private final PasswordEncoder passwordEncoder;

    public TemplateUserDataLoader(UserRepository userRepository,
                                  TrickLibraryRepository trickLibraryRepository,
                                  UserTrickRepository userTrickRepository,
                                  PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.trickLibraryRepository = trickLibraryRepository;
        this.userTrickRepository = userTrickRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        List<TemplateUser> templates = List.of(
                new TemplateUser(
                        "admin",
                        "admin@example.com",
                        "adminpass",
                        ExperienceLevel.PRO,
                        Stand.Regular,
                        List.of()
                ),
                new TemplateUser(
                        "tonyhawk",
                        "tony@example.com",
                        "password123",
                        ExperienceLevel.PRO,
                        Stand.Regular,
                        List.of("Kickflip", "Laser Flip", "Ollie")
                ),
                new TemplateUser(
                        "janedoe",
                        "jane@example.com",
                        "password123",
                        ExperienceLevel.SKILLED,
                        Stand.Goofy,
                        List.of("Heelflip", "Shove-it", "Ollie")
                ),
                new TemplateUser(
                        "bobsmith",
                        "bob@example.com",
                        "password123",
                        ExperienceLevel.NOVICE,
                        Stand.Regular,
                        List.of("Ollie")
                )
        );

        for (TemplateUser t : templates) {
            if (userRepository.existsByUsername(t.username())) {
                continue;
            }
            User user = User.builder()
                    .username(t.username())
                    .email(t.email())
                    .password(passwordEncoder.encode(t.password()))
                    .experienceLevel(t.level())
                    .stand(t.stand())
                    .enabled(true)
                    .roles(t.username().equals("admin") ?
                            Set.of(Role.ROLE_USER, Role.ROLE_ADMIN) :
                            Set.of(Role.ROLE_USER))
                    .build();
            User savedUser = userRepository.save(user);

            for (String trickName : t.masteredTricks()) {
                trickLibraryRepository.findByNameContaining(trickName)
                        .stream()
                        .findFirst()
                        .ifPresent(trick -> {
                            UserTrick ut = UserTrick.builder()
                                    .user(savedUser)
                                    .trick(trick)
                                    .status(TrickStatus.MASTERED)
                                    .build();
                            userTrickRepository.save(ut);
                        });
            }
        }
    }
}
