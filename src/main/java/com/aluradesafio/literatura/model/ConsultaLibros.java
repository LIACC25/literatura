package com.aluradesafio.literatura.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ConsultaLibros {
    @JsonAlias("results")
    private List<DatosLibro> consultaDatosLibros;

    public List<DatosLibro> getConsultaDatosLibros() {
        return consultaDatosLibros;
    }

    public void setConsultaDatosLibros(List<DatosLibro> consultaDatosLibros) {
        this.consultaDatosLibros = consultaDatosLibros;
    }
}
