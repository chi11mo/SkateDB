package com.chillmo.skatedb.trick_user;


import com.chillmo.skatedb.trick.domain.Trick;
import com.chillmo.skatedb.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserTrickRepository extends JpaRepository<UserTrick, Long> {
    Optional<UserTrick> findByUserAndTrick(User user, Trick trick);

    List<UserTrick> findByUser(User user);
}