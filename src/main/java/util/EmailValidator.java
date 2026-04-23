package util;

public class EmailValidator {

    public static boolean esValido(String email) {
        return email != null && email.endsWith("@poligran.edu.co");
    }
}