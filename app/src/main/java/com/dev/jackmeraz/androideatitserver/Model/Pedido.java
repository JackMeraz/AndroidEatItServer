package com.dev.jackmeraz.androideatitserver.Model;

import java.util.List;

/**
 * Created by jacobo.meraz on 10/03/2018.
 */

public class Pedido {
    private String Telefono, Nombre, Direccion, Total, Status;
    private List<Orden> comida; //lista de comida en la orden

    public Pedido() {
    }

    public Pedido(String telefono, String nombre, String direccion, String total, List<Orden> comida) {
        this.Telefono = telefono;
        this.Nombre = nombre;
        this.Direccion = direccion;
        this.Total = total;
        this.comida = comida;
        this.Status = "0"; //Por default es 0: En Espera, 1: Proceso de Envio, 2: Envio en Transito
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getTelefono() {
        return Telefono;
    }

    public void setTelefono(String telefono) {
        Telefono = telefono;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public String getDireccion() {
        return Direccion;
    }

    public void setDireccion(String direccion) {
        Direccion = direccion;
    }

    public String getTotal() {
        return Total;
    }

    public void setTotal(String total) {
        Total = total;
    }

    public List<Orden> getComida() {
        return comida;
    }

    public void setComida(List<Orden> comida) {
        this.comida = comida;
    }
}
