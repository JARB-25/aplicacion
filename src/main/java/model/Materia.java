package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Materia implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nombre;
    private Usuario profesor;
    private Set<Usuario>  estudiantes = new HashSet<>();
    private List<Tarea>   tareas      = new LinkedList<>();

    public Materia(String nombre, Usuario profesor) {
        this.nombre = nombre;
        this.profesor = profesor;
    }

    public void agregarTarea(Tarea tarea) {
        tareas.add(tarea);
    }

    public boolean eliminarTarea(String titulo) {
        return tareas.removeIf(t -> t.getTitulo().equals(titulo));
    }

    public List<Tarea> getTareas() { return tareas; }

    public void inscribirEstudiante(Usuario estudiante) {
    	estudiantes.add(estudiante); 
    }

    public String getNombre()              { return nombre; }
    public void   setNombre(String n)      { this.nombre = n; }
    public Usuario getProfesor()           { return profesor; }
    public List<Usuario> getEstudiantes()  { return new ArrayList<>(estudiantes); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Materia)) return false;
        Materia m = (Materia) o;
        return Objects.equals(nombre, m.nombre)
            && Objects.equals(profesor != null ? profesor.getEmail() : null,
                               m.profesor != null ? m.profesor.getEmail() : null);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombre, profesor != null ? profesor.getEmail() : null);
    }

    @Override
    public String toString() { return nombre; }
}
