package model;

import java.util.*;

public class Materia {

    private String nombre;
    private String codigo;
    private String profesor;
    private List<String> estudiantes;

    public Materia(String nombre, String codigo, String profesor, List<String> estudiantes) {
        this.nombre = nombre;
        this.codigo = codigo;
        this.profesor = profesor;
        this.estudiantes = estudiantes;
    }

    public String getNombre() { return nombre; }
    public String getCodigo() { return codigo; }
    public String getProfesor() { return profesor; }
    public List<String> getEstudiantes() { return estudiantes; }

    public void agregarEstudiante(String estudiante) {
        if (!estudiantes.contains(estudiante)) {
            estudiantes.add(estudiante);
        }
    }

    public String toFileString() {
        return nombre + "," + codigo + "," + profesor + "," + String.join(";", estudiantes);
    }

    public static Materia fromFileString(String linea) {
        if (linea.trim().isEmpty()) return null;

        String[] p = linea.split(",");
        List<String> est = new ArrayList<>();

        if (p.length > 3 && !p[3].isEmpty()) {
            est = Arrays.asList(p[3].split(";"));
        }

        return new Materia(p[0], p[1], p[2], new ArrayList<>(est));
    }
}