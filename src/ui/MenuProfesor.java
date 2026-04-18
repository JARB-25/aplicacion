package ui;

import service.*;
import model.*;
import java.util.*;

public class MenuProfesor {

    private Scanner sc = new Scanner(System.in);
    private MateriaService ms = new MateriaService();
    private TareaService ts = new TareaService();

    // 🔥 AHORA RECIBE Usuario
    public void mostrar(Usuario u) {

        // 🔒 PROTECCIÓN DE ROL
        if (!u.getRol().equals("profesor")) {
            System.out.println("Acceso denegado");
            return;
        }

        String prof = u.getNombre();

        while (true) {
            System.out.println("\n=== MENÚ PROFESOR ===");
            System.out.println("1. Crear materia");
            System.out.println("2. Ver materias");
            System.out.println("3. Crear tarea");
            System.out.println("4. Salir");

            int op = sc.nextInt(); 
            sc.nextLine();

            if (op == 1) {
                System.out.print("Nombre materia: ");
                String n = sc.nextLine();

                System.out.print("Código: ");
                String c = sc.nextLine();

                ms.crearMateria(n, c, prof);
                System.out.println("Materia creada");
            }

            if (op == 2) {
                for (Materia m : ms.obtenerTodas()) {
                    System.out.println(m.getNombre() + " - " + m.getCodigo());
                }
            }

            if (op == 3) {
                System.out.print("Título: ");
                String t = sc.nextLine();

                System.out.print("Descripción: ");
                String d = sc.nextLine();

                System.out.print("Código materia: ");
                String c = sc.nextLine();

                List<String> est = new ArrayList<>();

                for (Materia m : ms.obtenerTodas()) {
                    if (m.getCodigo().equals(c)) {
                        est = m.getEstudiantes();
                        break;
                    }
                }

                if (est.isEmpty()) {
                    System.out.println("No hay estudiantes inscritos");
                } else {
                    ts.crearParaMateria(t, d, c, est);
                    System.out.println("Tarea creada y asignada");
                }
            }

            if (op == 4) return;
        }
    }
}
