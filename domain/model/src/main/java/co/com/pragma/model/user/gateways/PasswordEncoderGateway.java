package co.com.pragma.model.user.gateways;

public interface PasswordEncoderGateway {

    String hashPassword(String password);

    Boolean matchesPassword(String plainPassword, String hashedPassword);
}
