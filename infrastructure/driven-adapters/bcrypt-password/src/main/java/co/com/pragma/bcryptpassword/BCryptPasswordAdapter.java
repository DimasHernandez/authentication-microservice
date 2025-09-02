package co.com.pragma.bcryptpassword;

import co.com.pragma.model.user.gateways.PasswordEncoderGateway;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BCryptPasswordAdapter implements PasswordEncoderGateway {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public BCryptPasswordAdapter(BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public String hashPassword(String password) {
        return bCryptPasswordEncoder.encode(password);
    }

    @Override
    public Boolean matchesPassword(String plainPassword, String hashedPassword) {
        return bCryptPasswordEncoder.matches(plainPassword, hashedPassword);
    }
}
