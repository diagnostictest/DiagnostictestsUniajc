package com.tecnologiajo.diagnostictestsuniajc.modelos;

import android.graphics.drawable.Drawable;

/**
 * Created by estudiante308 on 09/03/2016.
 */
public class Diagnostico {
    private String id;
    private String nombre;
    private String descripcion;
    private String id_creator;
    private String preguntas;
    private Drawable drawable;

    public Diagnostico() {
    }

    public Diagnostico(String id, String nombre, String descripcion, String id_creator, String preguntas) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.id_creator = id_creator;
        this.preguntas = preguntas;
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

    public String getId_creator() {
        return id_creator;
    }

    public void setId_creator(String id_creator) {
        this.id_creator = id_creator;
    }

    public String getPreguntas() {
        return preguntas;
    }

    public void setPreguntas(String preguntas) {
        this.preguntas = preguntas;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }
}
