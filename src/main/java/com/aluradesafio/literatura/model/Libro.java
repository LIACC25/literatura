package com.aluradesafio.literatura.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "libros")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Libro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String titulo;

    private int descargas;

    @OneToMany(mappedBy = "libro", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Autor> autor = new ArrayList<>();

    @OneToMany(mappedBy = "libro", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Lenguaje> lenguaje = new ArrayList<>();

    public Libro() {
    }


    public Libro(DatosLibro datosLibro) {
        this.titulo = datosLibro.titulo();
        this.descargas = datosLibro.descargas();
    }



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public int getDescargas() {
        return descargas;
    }

    public void setDescargas(int descargas) {
        this.descargas = descargas;
    }

    public List<Autor> getAutor() {
        return autor;
    }

    public void setAutor(List<Autor> autor) {
        this.autor = autor;
    }

    public List<Lenguaje> getLenguaje() {
        return lenguaje;
    }

    public void setLenguaje(List<Lenguaje> lenguaje) {
        this.lenguaje = lenguaje;
    }


    public void agregarAutor(Autor autor) {
        this.autor.add(autor);
        autor.setLibro(this);
    }


    public void agregarLenguaje(Lenguaje lenguaje) {
        this.lenguaje.add(lenguaje);
        lenguaje.setLibro(this);
    }

    @Override
    public String toString() {
        return "Libro{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", descargas=" + descargas +
                ", autor=" + autor +
                ", lenguaje=" + lenguaje +
                '}';
    }
}
