package es.uji.ei1027.clubesportiu.model;

public class UserDetails {
    private String usuario; // Puede ser el nombre de usuario o el email
    private String password;

    public UserDetails() {}

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}