package com.tecnologiajo.diagnostictestsuniajc.modelos;


public class RequestBody {

    private String deviceName;
    private String deviceId;
    private String registrationId;

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }
}