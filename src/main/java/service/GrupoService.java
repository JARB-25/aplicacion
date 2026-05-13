package service;

import exception.AccesoDenegadoException;
import model.Grupo;
import model.Materia;
import model.Periodo;
import model.Rol;
import model.Usuario;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GrupoService {

    private Map<String, Grupo> grupos = new HashMap<>();

    private String clave(String codigo, Periodo periodo) {
        return codigo + ":" + periodo.getNombre();
    }

    public Grupo crearGrupo(
            String codigo,
            Materia materia,
            Periodo periodo,
            Usuario profesor) {

        if (profesor.getRol() != Rol.PROFESOR) {
            throw new AccesoDenegadoException("Solo profesores pueden crear grupos");
        }
        String k = clave(codigo, periodo);
        if (grupos.containsKey(k)) {
            throw new RuntimeException("Ya existe un grupo con ese codigo en este periodo");
        }
        Grupo grupo = new Grupo(codigo, materia, periodo, profesor);
        grupos.put(k, grupo);
        return grupo;
    }

    public void inscribirEstudianteEnGrupo(Grupo grupo, Usuario estudiante) {
        grupo.inscribirEstudiante(estudiante);
    }

    public List<Grupo> getGruposActivos(Materia materia) {
        List<Grupo> activos = new ArrayList<>();
        for (Grupo g : grupos.values()) {
            if (g.getMateria().equals(materia) && g.getPeriodo().estaActivo()) {
                activos.add(g);
            }
        }
        return activos;
    }

    public List<Grupo> getGruposDeEstudiante(Usuario estudiante) {
        List<Grupo> resultado = new ArrayList<>();
        for (Grupo g : grupos.values()) {
            if (g.getEstudiantes().contains(estudiante)) {
                resultado.add(g);
            }
        }
        return resultado;
    }

    public Collection<Grupo> getGrupos() {
        return grupos.values();
    }
}
