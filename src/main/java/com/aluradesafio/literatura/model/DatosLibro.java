package com.aluradesafio.literatura.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DatosLibro(
        @JsonAlias("title") String titulo,

        @JsonAlias("authors") List<DatosAutor> autores,

        @JsonAlias("languages") List<String> lenguajes,

        @JsonAlias("download_count") int descargas
        ) {
}
