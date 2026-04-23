package util;

public class PasswordValidator {

    public static boolean esValido(String password) {
        return password != null && password.length() >= 4;
    }
}
