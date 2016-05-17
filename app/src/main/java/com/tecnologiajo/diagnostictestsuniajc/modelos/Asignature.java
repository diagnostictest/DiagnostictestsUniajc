package com.tecnologiajo.diagnostictestsuniajc.modelos;

import android.graphics.drawable.Drawable;

/**
 * Created by estudiante308 on 09/03/2016.
 */
public class Asignature {
    private String id;
    private String nombre;
    private String descripcion;


    private Drawable drawable;

    public Asignature() {
    }

    public Asignature(String id, String nombre, String descripcion) {
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

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }


}
