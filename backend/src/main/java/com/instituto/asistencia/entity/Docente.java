package com.instituto.asistencia.entity;
import java.time.LocalDate;
import jakarta.persistence.*;
import java.util.List;
@Entity
@Table(name = "docentes")
public class Docente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String apellido;
     private String telefono;

private String correo;

@Column(name = "fecha_nacimiento")
private LocalDate fechaNacimiento;

@Column(name = "fecha_alta")
private LocalDate fechaAlta;
    public String getTelefono() {
       return telefono;
    }
@ManyToMany
@JoinTable(
    name = "docentes_turnos",
    joinColumns = @JoinColumn(name = "docente_id"),
    inverseJoinColumns = @JoinColumn(name = "turno_id")
)
private List<Turno> turnos;
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public LocalDate getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(LocalDate fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    @Column(nullable = false, unique = true)
    private String dni;
    
    @Column(nullable = false)
    private Boolean activo = true;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    private String direccion;
    private String cargo;
    
    public Boolean getActivo() {
    return activo;
}

public void setActivo(Boolean activo) {
    this.activo = activo;
}
}