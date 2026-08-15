package org.hexarch.user.infrastructure.adapters.out.persistence.mapper;

import org.hexarch.user.domain.model.UserModel;
import org.hexarch.user.infrastructure.adapters.out.persistence.entity.UserEntity;

public final class UserMapper {

    private UserMapper() {
    }

    public static UserEntity toEntity(UserModel user) {
        UserEntity entity = new UserEntity();
        entity.id = user.id();
        entity.name = user.username();
        entity.email = user.email();
        entity.banned = user.banned();
        entity.role = user.role();
        return entity;
    }

    public static UserModel toDomain(UserEntity entity) {
        return new UserModel(entity.id, entity.name, entity.email, entity.banned, entity.role);
    }
}
