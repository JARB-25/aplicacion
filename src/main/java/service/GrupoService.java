package service;

import model.*;
import exception.AccesoDenegadoException;
import java.util.ArrayList;
import java.util.List;

public class GrupoService {

    private List<Grupo> grupos = new ArrayList<>();

    /** Solo un PROFESOR puede crear un grupo */
    public Grupo crearGrupo(
            String codigo,
            Materia materia,
            Periodo periodo,
            Usuario profesor) {

        if (profesor.getRol() != Rol.PROFESOR) {
            throw new AccesoDenegadoException("Solo profesores pueden crear grupos");
        }
        // Evitar código duplicado en el mismo período
        for (Grupo g : grupos) {
            if (g.getCodigo().equals(codigo) && g.getPeriodo().equals(periodo)) {
                throw new RuntimeException("Ya existe un grupo con ese código en este período");
            }
        }
        Grupo grupo = new Grupo(codigo, materia, periodo, profesor);
        grupos.add(grupo);
        return grupo;
    }

    public void inscribirEstudianteEnGrupo(Grupo grupo, Usuario estudiante) {
        grupo.inscribirEstudiante(estudiante);
    }

    /** Retorna los grupos activos (período vigente) de una materia */
    public List<Grupo> getGruposActivos(Materia materia) {
        List<Grupo> activos = new ArrayList<>();
        for (Grupo g : grupos) {
            if (g.getMateria().equals(materia) && g.getPeriodo().estaActivo()) {
                activos.add(g);
            }
        }
        return activos;
    }

    public List<Grupo> getGruposDeEstudiante(Usuario estudiante) {
        List<Grupo> resultado = new ArrayList<>();
        for (Grupo g : grupos) {
            if (g.getEstudiantes().contains(estudiante)) {
                resultado.add(g);
            }
        }
        return resultado;
    }

    public List<Grupo> getGrupos() { return grupos; }
}
