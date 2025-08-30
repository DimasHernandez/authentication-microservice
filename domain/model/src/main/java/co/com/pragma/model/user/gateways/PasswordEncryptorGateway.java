package co.com.pragma.model.user.gateways;

public interface PasswordEncryptorGateway {

    String encryptPassword(String password);

    Boolean matchesPassword(String plainPassword, String hashedPassword);
}
