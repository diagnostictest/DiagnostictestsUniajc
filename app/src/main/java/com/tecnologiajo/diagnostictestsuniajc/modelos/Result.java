package com.tecnologiajo.diagnostictestsuniajc.modelos;

import android.graphics.drawable.Drawable;

/**
 * Created by ADMIN on 13/04/2016.
 */
public class Result {
    public String Descripcion;
    public Boolean Estado; /** true=ganado, false= perdido */
    private Drawable drawable;

    public Result() {
    }

    public Result(String descripcion, Boolean estado) {
        Descripcion = descripcion;
        Estado = estado;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public void setDescripcion(String descripcion) {
        Descripcion = descripcion;
    }

    public Boolean getEstado() {
        return Estado;
    }

    public void setEstado(Boolean estado) {
        Estado = estado;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }
}
