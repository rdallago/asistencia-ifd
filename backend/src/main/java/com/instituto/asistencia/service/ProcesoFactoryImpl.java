package com.instituto.asistencia.service;

import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ProcesoFactoryImpl implements ProcesoFactory {

    @Override
    public Process iniciar(ProcessBuilder processBuilder)
            throws IOException {

        return processBuilder.start();
    }
}