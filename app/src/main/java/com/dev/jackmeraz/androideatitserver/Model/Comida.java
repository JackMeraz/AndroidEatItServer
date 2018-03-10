package com.dev.jackmeraz.androideatitserver.Model;

/**
 * Created by jacobo.meraz on 03/03/2018.
 */

public class Comida {
    private String Name, Image,Descripcion, Precio, Descuento, MenuId;

    public Comida() {
    }

    public Comida(String name, String image, String descripcion, String precio, String descuento, String menuId) {
        Name = name;
        Image = image;
        Descripcion = descripcion;
        Precio = precio;
        Descuento = descuento;
        MenuId = menuId;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public void setDescripcion(String descripcion) {
        Descripcion = descripcion;
    }

    public String getPrecio() {
        return Precio;
    }

    public void setPrecio(String precio) {
        Precio = precio;
    }

    public String getDescuento() {
        return Descuento;
    }

    public void setDescuento(String descuento) {
        Descuento = descuento;
    }

    public String getMenuId() {
        return MenuId;
    }

    public void setMenuId(String menuId) {
        MenuId = menuId;
    }
}
