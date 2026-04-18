package model;

public class Evento {

    private String titulo;
    private String descripcion;
    private String fecha;

    public Evento(String titulo, String descripcion, String fecha) {
        this.titulo      = titulo;
        this.descripcion = descripcion;
        this.fecha       = fecha;
    }

    public String getTitulo()      { return titulo; }
    public String getDescripcion() { return descripcion; }
    public String getFecha()       { return fecha; }

    public String toFileString() {
        return titulo + "," + descripcion + "," + fecha;
    }

    public static Evento fromFileString(String linea) {
        String[] p = linea.split(",", -1);
        return new Evento(p[0], p[1], p[2]);
    }
}
