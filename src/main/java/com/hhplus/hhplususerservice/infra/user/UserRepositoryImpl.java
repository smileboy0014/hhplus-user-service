package com.hhplus.hhplususerservice.infra.user;

import com.hhplus.hhplususerservice.domain.user.User;
import com.hhplus.hhplususerservice.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    @Override
    public Optional<User> getUser(Long userId) {
        Optional<UserEntity> userEntity = userJpaRepository.findById(userId);
        if (userEntity.isPresent()) {
            return userEntity.map(UserEntity::toDomain);
        }

        return Optional.empty();
    }

    @Override
    public Optional<User> saveUser(User user) {
        UserEntity userEntity = userJpaRepository.save(UserEntity.from(user));
        return Optional.of(userEntity.toDomain());
    }

    @Override
    public List<User> getUsers() {
        return userJpaRepository.findAll()
                .stream()
                .map(UserEntity::toDomain)
                .toList();
    }

    @Override
    public void deleteAll() {
        userJpaRepository.deleteAllInBatch();
    }


}
