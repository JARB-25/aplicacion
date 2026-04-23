package model;

import java.util.ArrayList;
import java.io.Serializable;
import java.util.List;

public class Grupo implements Serializable{

    private String codigo;          // Ej: "GR-01"
    private Materia materia;
    private Periodo periodo;
    private Usuario profesor;
    private List<Usuario> estudiantes = new ArrayList<>();

    public Grupo(String codigo, Materia materia, Periodo periodo, Usuario profesor) {
        this.codigo = codigo;
        this.materia = materia;
        this.periodo = periodo;
        this.profesor = profesor;
    }

    public void inscribirEstudiante(Usuario estudiante) {
        if (estudiante.getRol() != Rol.ESTUDIANTE) {
            throw new RuntimeException("Solo estudiantes pueden inscribirse en un grupo");
        }
        if (estudiantes.contains(estudiante)) {
            throw new RuntimeException("El estudiante ya está en este grupo");
        }
        estudiantes.add(estudiante);
    }

    public String getCodigo()              { return codigo; }
    public Materia getMateria()            { return materia; }
    public Periodo getPeriodo()            { return periodo; }
    public Usuario getProfesor()           { return profesor; }
    public List<Usuario> getEstudiantes()  { return estudiantes; }
}
