package com.tecnologiajo.diagnostictestsuniajc.modelos;

import android.graphics.drawable.Drawable;

/**
 * Created by estudiante308 on 09/03/2016.
 */
public class Asignature {
    private String id;
    private String nombre;
    private String descripcion;
    private String schema;
    private int cantidadtest;
    private float totalcalificacion;
    private Drawable drawable;

    public Asignature() {
    }

    public Asignature(String id, String nombre, String descripcion,String schema) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.schema = schema;
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

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public int getCantidadtest() {
        return cantidadtest;
    }

    public void setCantidadtest(int cantidadtest) {
        this.cantidadtest = cantidadtest;
    }

    public float getTotalcalificacion() {
        return totalcalificacion;
    }

    public void setTotalcalificacion(float totalcalificacion) {
        this.totalcalificacion = totalcalificacion;
    }
}
