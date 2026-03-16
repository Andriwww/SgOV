package es.uji.ei1027.clubesportiu.model;

import java.util.Date;

public class Seleccion {
    private int idSeleccion;
    private Date fechaSeleccion;
    private Estado estado;

    public Seleccion() {
    }

    public int getIdSeleccion() {
        return idSeleccion;
    }

    public void setIdSeleccion(int idSeleccion) {
        this.idSeleccion = idSeleccion;
    }

    public Date getFechaSeleccion() {
        return fechaSeleccion;
    }

    public void setFechaSeleccion(Date fechaSeleccion) {
        this.fechaSeleccion = fechaSeleccion;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Seleccion{" +
                "idSeleccion=" + idSeleccion +
                ", fechaSeleccion='" + fechaSeleccion + '\'' +
                ", estado='" + estado + '\'' +
                '}';
    }
}