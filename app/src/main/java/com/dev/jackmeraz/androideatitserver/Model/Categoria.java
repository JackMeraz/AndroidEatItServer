package com.dev.jackmeraz.androideatitserver.Model;

/**
 * Created by jacobo.meraz on 27/01/2018.
 */

public class Categoria {

    private String Nombre;
    private String Imagen;

    public Categoria() {
    }

    public Categoria(String nombre, String imagen) {
        Nombre = nombre;
        Imagen = imagen;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public String getImagen() {
        return Imagen;
    }

    public void setImagen(String imagen) {
        Imagen = imagen;
    }
}
