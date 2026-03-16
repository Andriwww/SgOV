package es.uji.ei1027.clubesportiu.model;

public class Formador {

    private int idFormador;
    private String nombre;
    private String apellidos;
    private String email;
    private String especialidad;

    public Formador() {
    }

    public int getIdFormador() {
        return idFormador;
    }

    public void setIdFormador(int idFormador) {
        this.idFormador = idFormador;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    @Override
    public String toString() {
        return "Formador{" +
                "idFormador=" + idFormador +
                ", nombre='" + nombre + '\'' +
                ", apellidos='" + apellidos + '\'' +
                ", email='" + email + '\'' +
                ", especialidad='" + especialidad + '\'' +
                '}';
    }
}