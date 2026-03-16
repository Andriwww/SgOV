package es.uji.ei1027.clubesportiu.model;

import java.time.LocalDate;

public class RegistroContrato {

    private int idContrato;
    private Integer idAsistente;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String documentoPDF;
    private Estado estado;

    public RegistroContrato() {
    }

    public int getIdContrato() {
        return idContrato;
    }

    public void setIdContrato(int idContrato) {
        this.idContrato = idContrato;
    }

    public Integer getIdAsistente() {
        return idAsistente;
    }

    public void setIdAsistente(Integer idAsistente) {
        this.idAsistente = idAsistente;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getDocumentoPDF() {
        return documentoPDF;
    }

    public void setDocumentoPDF(String documentoPDF) {
        this.documentoPDF = documentoPDF;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "RegistroContrato{" +
                "idContrato=" + idContrato +
                ", idAsistente=" + idAsistente +
                ", fechaInicio=" + fechaInicio +
                ", fechaFin=" + fechaFin +
                ", documentoPDF='" + documentoPDF + '\'' +
                ", estado=" + estado +
                '}';
    }
}