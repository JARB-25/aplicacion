package model;

public class Tarea {

    private String titulo;
    private String descripcion;
    private String codigoMateria;
    private String estudiante;
    private String estado;

    public Tarea(String titulo, String descripcion, String codigoMateria, String estudiante, String estado) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.codigoMateria = codigoMateria;
        this.estudiante = estudiante;
        this.estado = estado;
    }

    public String getTitulo() { return titulo; }
    public String getEstudiante() { return estudiante; }
    public String getEstado() { return estado; }

    public void setEstado(String estado) { this.estado = estado; }

    public String toFileString() {
        return titulo + "," + descripcion + "," + codigoMateria + "," + estudiante + "," + estado;
    }

    public static Tarea fromFileString(String linea) {
        if (linea.trim().isEmpty()) return null;
        String[] p = linea.split(",");
        return new Tarea(p[0], p[1], p[2], p[3], p[4]);
    }
}
