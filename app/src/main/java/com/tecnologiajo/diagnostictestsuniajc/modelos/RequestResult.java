package com.tecnologiajo.diagnostictestsuniajc.modelos;

import android.graphics.drawable.Drawable;

/**
 * Created by ADMIN on 13/04/2016.
 */
public class RequestResult {
    public String Descripcion;
    public Boolean Estado; /** true=ganado, false= perdido */
    private String groupTest;
    private String device;


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

    public String getGroupTest() {
        return groupTest;
    }

    public void setGroupTest(String groupTest) {
        this.groupTest = groupTest;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }
}
