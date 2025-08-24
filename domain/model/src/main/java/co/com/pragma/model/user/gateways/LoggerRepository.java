package co.com.pragma.model.user.gateways;

public interface LoggerRepository {

    void info(String message, Object... args);
}
