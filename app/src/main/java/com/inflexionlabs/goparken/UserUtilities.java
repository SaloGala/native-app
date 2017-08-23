package com.inflexionlabs.goparken;

/**
 * Created by odalysmarronsanchez on 22/08/17.
 */

class UserUtilities {
    private static final UserUtilities ourInstance = new UserUtilities();

    String nombre;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    String apellido;

    static UserUtilities getInstance() {
        return ourInstance;
    }

    private UserUtilities() {
    }
}
