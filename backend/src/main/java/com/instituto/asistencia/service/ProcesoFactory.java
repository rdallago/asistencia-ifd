package com.instituto.asistencia.service;

import java.io.IOException;

public interface ProcesoFactory {

    Process iniciar(ProcessBuilder processBuilder)
            throws IOException;
}