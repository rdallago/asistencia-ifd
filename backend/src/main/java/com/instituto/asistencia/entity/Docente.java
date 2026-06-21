package com.instituto.asistencia.entity;
import java.time.LocalDate;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
@Entity
@Table(name = "docentes")
public class Docente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
@Size(min = 2, max = 100,
       message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombre;
    
    @NotBlank(message = "El apellido es obligatorio")
@Size(min = 2, max = 100,
       message = "El apellido debe tener entre 2 y 100 caracteres")
    private String apellido;
    
    @Pattern(
    regexp = "^[0-9+\\- ]*$",
    message = "El teléfono contiene caracteres inválidos"
)
     private String telefono;

@Email(message = "El correo electrónico no es válido")
@Size(max = 100)
     private String correo;



@Column(name = "fecha_nacimiento")
@NotNull(message = "La fecha de nacimiento es obligatoria")
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

    @Column(
    nullable = false,
    unique = true,
    length = 20
)
    @NotBlank(message = "El DNI es obligatorio")
@Pattern(
    regexp = "^[0-9]{7,8}$",
    message = "El DNI debe contener solo números y tener entre 7 y 8 dígitos"
)
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
@NotBlank(message = "La dirección es obligatoria")
@Size(min = 2, max = 250,
       message = "La dirección debe tener entre 2 y 250 caracteres")
    
    private String direccion;
    
    @NotBlank(message = "El cargo es obligatorio")
    private String cargo;
    
    public Boolean getActivo() {
    return activo;
}

public void setActivo(Boolean activo) {
    this.activo = activo;
}
}