package es.uji.ei1027.clubesportiu.model;

import java.time.LocalDate;

public class AsistenciaFormacion {

    private int idAsistencia;
    private int idActividad;
    private int idUsuario;
    private int idAsistente;
    private boolean asistio;
    private boolean certificadoGenerado;
    private LocalDate fecha;

    public AsistenciaFormacion() {
    }

    public int getIdAsistencia() {
        return idAsistencia;
    }

    public void setIdAsistencia(int idAsistencia) {
        this.idAsistencia = idAsistencia;
    }

    public int getIdActividad() {
        return idActividad;
    }

    public void setIdActividad(int idActividad) {
        this.idActividad = idActividad;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdAsistente() {
        return idAsistente;
    }

    public void setIdAsistente(int idAsistente) {
        this.idAsistente = idAsistente;
    }

    public boolean isAsistio() {
        return asistio;
    }

    public void setAsistio(boolean asistio) {
        this.asistio = asistio;
    }

    public boolean isCertificadoGenerado() {
        return certificadoGenerado;
    }

    public void setCertificadoGenerado(boolean certificadoGenerado) {
        this.certificadoGenerado = certificadoGenerado;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    @Override
    public String toString() {
        return "AsistenciaFormacion{" +
                "idAsistencia=" + idAsistencia +
                ", idActividad=" + idActividad +
                ", idUsuario=" + idUsuario +
                ", idAsistente=" + idAsistente +
                ", asistio=" + asistio +
                ", certificadoGenerado=" + certificadoGenerado +
                ", fecha=" + fecha +
                '}';
    }
}