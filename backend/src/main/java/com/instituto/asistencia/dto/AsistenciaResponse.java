/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.instituto.asistencia.dto;

import java.time.LocalTime;
import java.time.LocalDate;

/**
 *
 * @author Informatica
 */
public class AsistenciaResponse {

    private String docente;

    private String turno;

    private LocalDate fecha;

    private LocalTime horaIngreso;

    private LocalTime horaEgreso;

    private String estado;
}