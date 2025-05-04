package com.example.wlac_yudo_app;

public class Asistencia {
    private String alumnoId;
    private String fecha;
    private String clase;
    private boolean asistio;

    public Asistencia() {
        // Constructor vacío requerido por Firestore
    }

    public Asistencia(String alumnoId, String fecha, String clase, boolean asistio) {
        this.alumnoId = alumnoId;
        this.fecha = fecha;
        this.clase = clase;
        this.asistio = asistio;
    }

    public String getAlumnoId() {
        return alumnoId;
    }

    public void setAlumnoId(String alumnoId) {
        this.alumnoId = alumnoId;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getClase() {
        return clase;
    }

    public void setClase(String clase) {
        this.clase = clase;
    }

    public boolean isAsistio() {
        return asistio;
    }

    public void setAsistio(boolean asistio) {
        this.asistio = asistio;
    }
}
