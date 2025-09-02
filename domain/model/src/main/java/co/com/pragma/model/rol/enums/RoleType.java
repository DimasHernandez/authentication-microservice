package co.com.pragma.model.rol.enums;

public enum RoleType {

    APPLICANT("SOLICITANTE", "APPLICANT"),
    ADMIN("ADMINISTRADOR", "ADMIN");

    private final String name;

    private final String englishName;

    RoleType(String name, String englishName) {
        this.name = name;
        this.englishName = englishName;
    }

    public String getName() {
        return name;
    }

    public String getEnglishName() {
        return englishName;
    }
}
