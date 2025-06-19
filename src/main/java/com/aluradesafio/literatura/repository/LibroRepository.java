package com.aluradesafio.literatura.repository;


import com.aluradesafio.literatura.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LibroRepository extends JpaRepository<Libro,Long> {

    Optional<Libro> findByTitulo(String titulo);

    @Query("SELECT DISTINCT l FROM Libro l LEFT JOIN FETCH l.autor")
    List<Libro> findLibros();

    @Query("SELECT DISTINCT l FROM Libro l JOIN FETCH l.autor")
    List<Libro> findAutores();

    @Query("SELECT DISTINCT l FROM Libro l JOIN l.lenguaje lang WHERE lang.lenguaje = :codigoLenguaje")
    List<Libro> findLibrosPorIdioma(@Param("codigoLenguaje") String codigoLenguaje);


}