package main;

import ui.MenuPrincipal;
import service.AuthService;
import service.InputService;
import model.Usuario;

public class Main {
    public static void main(String[] args) {

        AuthService auth = new AuthService();

        while (true) {
            System.out.println("\n=== SISTEMA ===");
            System.out.println("1. Iniciar sesión");
            System.out.println("2. Registrarse");
            System.out.println("3. Salir");

            int op = InputService.leerEntero();

            if (op == 1) {
                System.out.print("Usuario: ");
                String nombre = InputService.leerLinea();

                System.out.print("Contraseña: ");
                String pass = InputService.leerLinea();

                Usuario u = auth.login(nombre, pass);

                if (u == null) {
                    System.out.println("Credenciales incorrectas");
                } else {
                    new MenuPrincipal().mostrar(u);
                }
            }

            if (op == 2) {
                System.out.print("Nuevo usuario: ");
                String nombre = InputService.leerLinea();

                System.out.print("Contraseña: ");
                String pass = InputService.leerLinea();

                System.out.print("Rol (estudiante/admin): ");
                String rol = InputService.leerLinea();

                auth.registrar(nombre, pass, rol);
                System.out.println("Usuario registrado con éxito");
            }

            if (op == 3) {
                System.out.println("Adiós 👋");
                return;
            }
        }
    }
}