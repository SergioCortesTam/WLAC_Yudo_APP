package com.example.wlac_yudo_app;

public class Asistencia {
    private String alumnoId;
    private String nombreAlumno;
    private String fecha;
    private String clase;
    private boolean asistio;
    private long fechaTimestamp;

    public Asistencia() {
        // Constructor vacío requerido por Firestore
    }

    public Asistencia(String alumnoId, String nombreAlumno, String fecha, String clase, boolean asistio, long fechaTimestamp) {
        this.alumnoId = alumnoId;
        this.nombreAlumno = nombreAlumno;
        this.fecha = fecha;
        this.clase = clase;
        this.asistio = asistio;
        this.fechaTimestamp = fechaTimestamp;
    }

    // Getters y Setters
    public String getAlumnoId() { return alumnoId; }
    public void setAlumnoId(String alumnoId) { this.alumnoId = alumnoId; }
    public String getNombreAlumno() { return nombreAlumno; }
    public void setNombreAlumno(String nombreAlumno) { this.nombreAlumno = nombreAlumno; }
    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public String getClase() { return clase; }
    public void setClase(String clase) { this.clase = clase; }
    public boolean isAsistio() { return asistio; }
    public void setAsistio(boolean asistio) { this.asistio = asistio; }
    public long getFechaTimestamp() { return fechaTimestamp; }
    public void setFechaTimestamp(long fechaTimestamp) { this.fechaTimestamp = fechaTimestamp; }
}