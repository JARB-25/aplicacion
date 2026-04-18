package service;

import model.*;
import java.io.*;
import java.util.*;

public class FileService {

    private static final String DATA_DIR = "data/";
    private static final String USUARIOS = DATA_DIR + "usuarios.txt";
    private static final String MATERIAS = DATA_DIR + "materias.txt";
    private static final String TAREAS = DATA_DIR + "tareas.txt";

    private static void crearCarpeta() {
        new File(DATA_DIR).mkdirs();
    }

    // USUARIOS
    public static List<Usuario> cargarUsuarios() {
        List<Usuario> lista = new ArrayList<>();
        File f = new File(USUARIOS);
        if (!f.exists()) return lista;

        try (Scanner sc = new Scanner(f)) {
            while (sc.hasNextLine()) {
                Usuario u = Usuario.fromFileString(sc.nextLine());
                if (u != null) lista.add(u);
            }
        } catch (Exception e) {}
        return lista;
    }

    public static void guardarUsuarios(List<Usuario> lista) {
        crearCarpeta();
        try (PrintWriter pw = new PrintWriter(USUARIOS)) {
            for (Usuario u : lista) pw.println(u.toFileString());
        } catch (Exception e) {}
    }

    // MATERIAS
    public static List<Materia> cargarMaterias() {
        List<Materia> lista = new ArrayList<>();
        File f = new File(MATERIAS);
        if (!f.exists()) return lista;

        try (Scanner sc = new Scanner(f)) {
            while (sc.hasNextLine()) {
                Materia m = Materia.fromFileString(sc.nextLine());
                if (m != null) lista.add(m);
            }
        } catch (Exception e) {}
        return lista;
    }

    public static void guardarMaterias(List<Materia> lista) {
        crearCarpeta();
        try (PrintWriter pw = new PrintWriter(MATERIAS)) {
            for (Materia m : lista) pw.println(m.toFileString());
        } catch (Exception e) {}
    }

    // TAREAS
    public static List<Tarea> cargarTareas() {
        List<Tarea> lista = new ArrayList<>();
        File f = new File(TAREAS);
        if (!f.exists()) return lista;

        try (Scanner sc = new Scanner(f)) {
            while (sc.hasNextLine()) {
                Tarea t = Tarea.fromFileString(sc.nextLine());
                if (t != null) lista.add(t);
            }
        } catch (Exception e) {}
        return lista;
    }

    public static void guardarTareas(List<Tarea> lista) {
        crearCarpeta();
        try (PrintWriter pw = new PrintWriter(TAREAS)) {
            for (Tarea t : lista) pw.println(t.toFileString());
        } catch (Exception e) {}
    }
}