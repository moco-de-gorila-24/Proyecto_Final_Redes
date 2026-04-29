/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Cliente;

import java.net.InetAddress;

/**
 *
 * @author Central
 */
public class Usuario{
    private String nombre;
    //Clase que representa una dirección ip y la guardamos 
    private InetAddress direccion;
    private int puerto;

    public Usuario(String nombre, InetAddress direccion, int puerto) {
        this.nombre = nombre;
        this.direccion = direccion;
        this.puerto = puerto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public InetAddress getDireccion() {
        return direccion;
    }

    public void setDireccion(InetAddress direccion) {
        this.direccion = direccion;
    }

    public int getPuerto() {
        return puerto;
    }

    public void setPuerto(int puerto) {
        this.puerto = puerto;
    }
    
    
}
