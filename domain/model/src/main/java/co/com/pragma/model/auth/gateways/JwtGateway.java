package co.com.pragma.model.auth.gateways;

import co.com.pragma.model.user.User;

public interface JwtGateway {

    String generateToken(User user, String userRole);

    Boolean validateToken(String token);

    String extractUserId(String token);

    String extractUserEmail(String token);

    String extractUserDocumentNumber(String token);

    String extractRole(String token);

}
