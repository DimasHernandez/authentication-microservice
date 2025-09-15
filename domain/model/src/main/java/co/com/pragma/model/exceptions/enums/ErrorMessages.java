package co.com.pragma.model.exceptions.enums;

public enum ErrorMessages {

    // 400
    BAD_REQUEST("GEN_002", "Petición malformada"),

    // 404
    USER_NOT_FOUND("USR_004", "Usuario no encontrado"),
    ROLE_NOT_FOUND("ROL_001", "Rol no encontrado"),

    // 422
    INVALID_CREDENTIALS("AUTH_001", "Correo electronico o contraseña incorrectos"),
    EMAIL_ALREADY_REGISTERED("USR_001", "La direccion del correo electronico ya esta registrada."),
    FIELD_EMPTY("USR_002", "Campo obligatorio vacío"),

    // 409
    DOCUMENT_ALREADY_EXISTS("DOC_001", "El número de documento ya existe"),

    //500
    GENERIC_SERVER_ERROR("GEN_500", "Error interno del servidor");

    private final String code;
    private final String message;

    ErrorMessages(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
