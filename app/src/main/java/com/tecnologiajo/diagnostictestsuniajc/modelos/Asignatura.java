package com.tecnologiajo.diagnostictestsuniajc.modelos;

/**
 * Created by estudiante308 on 09/03/2016.
 */
public class Asignatura {
    private String id;
    private String nombre;
    private String descripcion;

    public Asignatura() {
    }

    public Asignatura(String id, String nombre, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
