package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Grupo implements Serializable {

    private String codigo;
    private Materia materia;
    private Periodo periodo;
    private Usuario profesor;
    private Set<Usuario> estudiantes = new HashSet<>();

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
        boolean agregado = estudiantes.add(estudiante);
        if (!agregado) {
            throw new RuntimeException("El estudiante ya esta en este grupo");
        }
    }

    public String getCodigo() {
        return codigo;
    }

    public Materia getMateria() {
        return materia;
    }

    public Periodo getPeriodo() {
        return periodo;
    }

    public Usuario getProfesor() {
        return profesor;
    }

    public List<Usuario> getEstudiantes() {
        return new ArrayList<>(estudiantes);
    }
}
