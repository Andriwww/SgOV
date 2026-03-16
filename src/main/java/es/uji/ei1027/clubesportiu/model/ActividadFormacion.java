package es.uji.ei1027.clubesportiu.model;

import java.time.LocalDate;

public class ActividadFormacion {

    private int idActividad;
    private int idFormador;
    private String titulo;
    private LocalDate fecha;
    private String descripcion;
    private int aforoMaximo;

    public ActividadFormacion() {
    }

    public int getIdActividad() {
        return idActividad;
    }

    public void setIdActividad(int idActividad) {
        this.idActividad = idActividad;
    }

    public int getIdFormador() {
        return idFormador;
    }

    public void setIdFormador(int idFormador) {
        this.idFormador = idFormador;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getAforoMaximo() {
        return aforoMaximo;
    }

    public void setAforoMaximo(int aforoMaximo) {
        this.aforoMaximo = aforoMaximo;
    }

    @Override
    public String toString() {
        return "ActividadFormacion{" +
                "idActividad=" + idActividad +
                ", idFormador=" + idFormador +
                ", titulo='" + titulo + '\'' +
                ", fecha=" + fecha +
                ", descripcion='" + descripcion + '\'' +
                ", aforoMaximo=" + aforoMaximo +
                '}';
    }
}