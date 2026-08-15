package org.hexarch.level.domain.enums;

/** Permisos sobre UN nivel concreto. No se pueden resolver con el JWT: dependen de level_members. */
public enum LevelPermission {

    VIEW_DRAFT,
    EDIT,
    UPLOAD_VERSION,
    PUBLISH,
    MANAGE_MEMBERS,
    DELETE
}
