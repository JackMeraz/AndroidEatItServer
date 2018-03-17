package com.dev.jackmeraz.androideatitserver.Model;

/**
 * Created by jacobo.meraz on 10/03/2018.
 */

public class Orden {
    private String Id_Producto;
    private String Nom_Producto;
    private String Cantidad;
    private String Precio;
    private String Descuento;

    public Orden() {
    }

    public Orden(String id_Producto, String nom_Producto, String cantidad, String precio, String descuento) {
        Id_Producto = id_Producto;
        Nom_Producto = nom_Producto;
        Cantidad = cantidad;
        Precio = precio;
        Descuento = descuento;
    }

    public String getId_Producto() {
        return Id_Producto;
    }

    public void setId_Producto(String id_Producto) {
        Id_Producto = id_Producto;
    }

    public String getNom_Producto() {
        return Nom_Producto;
    }

    public void setNom_Producto(String nom_Producto) {
        Nom_Producto = nom_Producto;
    }

    public String getCantidad() {
        return Cantidad;
    }

    public void setCantidad(String cantidad) {
        Cantidad = cantidad;
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
}
