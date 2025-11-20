package models.enumerators;

public enum PermisosAdmin {
    /// Tiene todas las capacidades de los solicitantes y puede agregar, borrar, modificar y leer todos los datos de cualquier usuario.
    SUPERADMIN,

    /// Solo puede ver información de usuarios y aseptar solicitudes de crear dibujos dadas por ellos.
    SOLICITARIO,

    /// Solo pued ver la información de los usuarios.
    VISUALIZANTE;
}