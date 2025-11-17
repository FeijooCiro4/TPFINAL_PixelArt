package models.enumerators;

public enum RolUsuarios {
    /// Pinta dibujos existentes y creados por otros usuarios normales y, si un admin lo permite, puede crear dibujos
    NORMAL,

    /// Puede procesar los datos de cualquier usuario, dependiendo de los permisos que tenga
    ADMIN;
}
