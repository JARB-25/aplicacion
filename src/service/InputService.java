package service;

import java.util.Scanner;

public class InputService {

    private static Scanner sc = new Scanner(System.in);

    /** Lee un entero de forma segura (evita crashes con InputMismatchException) */
    public static int leerEntero() {
        while (true) {
            try {
                int valor = Integer.parseInt(sc.nextLine().trim());
                return valor;
            } catch (NumberFormatException e) {
                System.out.print("Ingrese un número válido: ");
            }
        }
    }

    public static String leerLinea() {
        return sc.nextLine();
    }
}