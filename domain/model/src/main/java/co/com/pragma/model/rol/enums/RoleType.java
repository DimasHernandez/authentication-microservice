package co.com.pragma.model.rol.enums;

public enum RoleType {

    APPLICANT("SOLICITANTE"),
    ADMIN("ADMINISTRADOR");

    private final String name;

    RoleType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
