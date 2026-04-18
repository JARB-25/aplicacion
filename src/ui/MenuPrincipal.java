package ui;

import service.*;
import model.*;
import java.util.*;

public class MenuPrincipal {

    private Scanner sc = new Scanner(System.in);
    private MateriaService ms = new MateriaService();
    private TareaService ts = new TareaService();

    // 🔥 AHORA RECIBE Usuario
    public void mostrar(Usuario u) {

        // 🔒 PROTECCIÓN DE ROL
        if (!u.getRol().equals("estudiante")) {
            System.out.println("Acceso denegado");
            return;
        }

        String est = u.getNombre();

        while (true) {
            System.out.println("\n=== MENÚ ESTUDIANTE ===");
            System.out.println("1. Ver materias");
            System.out.println("2. Inscribirse");
            System.out.println("3. Ver tareas");
            System.out.println("4. Entregar tarea");
            System.out.println("5. Salir");

            int op = sc.nextInt(); 
            sc.nextLine();

            if (op == 1) {
                List<Materia> lista = ms.obtenerDeEstudiante(est);

                if (lista.isEmpty()) {
                    System.out.println("No tienes materias");
                } else {
                    for (Materia m : lista) {
                        System.out.println(m.getNombre() + " - " + m.getCodigo());
                    }
                }
            }

            if (op == 2) {
                System.out.print("Código de materia: ");
                String cod = sc.nextLine();

                ms.inscribir(cod, est);
                System.out.println("Intento de inscripción realizado");
            }

            if (op == 3) {
                List<Tarea> tareas = ts.obtener(est);

                if (tareas.isEmpty()) {
                    System.out.println("No tienes tareas");
                } else {
                    for (Tarea t : tareas) {
                        System.out.println(t.getTitulo() + " - " + t.getEstado());
                    }
                }
            }

            if (op == 4) {
                System.out.print("Título de la tarea: ");
                String titulo = sc.nextLine();

                ts.entregar(titulo, est);
                System.out.println("Tarea marcada como entregada");
            }

            if (op == 5) return;
        }
    }
}