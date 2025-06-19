package com.aluradesafio.literatura.principal;
import com.aluradesafio.literatura.model.*;
import com.aluradesafio.literatura.repository.LibroRepository;
import com.aluradesafio.literatura.service.ConsumoAPI;
import com.aluradesafio.literatura.service.ConvierteDatos;
import org.springframework.transaction.annotation.Transactional;


import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class Principal {

    private Scanner teclado = new Scanner(System.in);
    private ConsumoAPI consumoApi = new ConsumoAPI();
    private final String URL_BASE = "https://gutendex.com/books/?";
    private ConvierteDatos conversor = new ConvierteDatos();
    private List<DatosLibro> datosLibros = new ArrayList<>();
    private LibroRepository repositorio;
    private List<Libro> libros;

    public Principal(LibroRepository repository) {
        this.repositorio = repository;
    }

    public void muestraElMenu() {
        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    1 - Buscar libro por título 
                    2 - Listar libros registrados
                    3 - Listar autores registrados
                    4 - Listar autores vivos en un determinado año
                    5 - Listar libros por idioma
                    0 - Salir
                    """;
            System.out.println(menu);
            opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion) {
                case 1:
                    consultaLibros();
                    break;
                case 2:
                    listarLibrosRegistrados();
                    break;
                case 3:
                    listarAutoresRegistrados();
                    break;
                case 4:
                    listarAutoresVivosEnAnio();
                    break;
                case 5:
                    listarLibrosPorLenguaje();
                    break;
                case 0:
                    System.out.println("Cerrando la aplicación...");
                    break;
                default:
                    System.out.println("Opción inválida");
            }
        }

    }

    @Transactional
    public void consultaLibros() {
        System.out.println("Escribe el título del libro a buscar:");
        String nombreLibro = teclado.nextLine();

        if (nombreLibro == null || nombreLibro.trim().isEmpty()) {
            System.out.println("Título inválido. Intenta de nuevo.");
            return;
        }

        String url = "https://gutendex.com/books/?search=" + URLEncoder.encode(nombreLibro, StandardCharsets.UTF_8);
        String json = consumoApi.obtenerDatos(url);

        /*String json = """
                    {
                      "results": [
                        {
                          "title": "Libro de Prueba con Varios Autores e Idiomas",
                          "authors": [
                            { "name": "Autor Uno", "birth_year": 1900, "death_year": 1950 },
                            { "name": "Autor Dos", "birth_year": 1920, "death_year": 1980 }
                          ],
                          "languages": ["en", "fr"],
                          "download_count": 1234
                        }
                      ]
                    }
                    """;*/




        ConsultaLibros consulta = conversor.obtenerDatos(json, ConsultaLibros.class);
        List<DatosLibro> datos = consulta.getConsultaDatosLibros();

        if (datos == null || datos.isEmpty()) {
            System.out.println("No se encontraron libros con ese título.");
            return;
        }


        DatosLibro libro = datos.get(0);


        System.out.println("----- Resultado encontrado -----");
        System.out.println("Título: " + libro.titulo());

        if (libro.autores() != null && !libro.autores().isEmpty()) {

            String nombresAutores = libro.autores().stream()
                    .map(DatosAutor::nombre)
                    .collect(Collectors.joining(", "));
            System.out.println("Autor(es): " + (nombresAutores.isEmpty() ? "No disponible" : nombresAutores));
        } else {
            System.out.println("Autor: No disponible");
        }

        if (libro.lenguajes() != null && !libro.lenguajes().isEmpty()) {

            String idiomas = String.join(", ", libro.lenguajes());
            System.out.println("Idioma(s): " + (idiomas.isEmpty() ? "No disponible" : idiomas));
        } else {
            System.out.println("Idioma: No disponible");
        }

        System.out.println("Número de descargas: " + libro.descargas());
        System.out.println("--------------------------------");


        Optional<Libro> libroExistente = repositorio.findByTitulo(libro.titulo());

        if (libroExistente.isPresent()) {
            System.out.println("El libro '" + libro.titulo() + "' \nya existe en la base de datos.\n\n");
            return;
        }


        Libro libroEntidad = new Libro(libro);


        List<Autor> autores = libro.autores().stream()
                .map(datoAutor -> {
                    Autor autor = new Autor(datoAutor);
                    autor.setLibro(libroEntidad);
                    return autor;
                }).collect(Collectors.toList());
        libroEntidad.setAutor(autores);


        List<Lenguaje> lenguajes = libro.lenguajes().stream()
                .map(codigo -> {
                    Lenguaje lenguaje = new Lenguaje(codigo);
                    lenguaje.setLibro(libroEntidad);
                    return lenguaje;
                }).collect(Collectors.toList());
        libroEntidad.setLenguaje(lenguajes);


        repositorio.save(libroEntidad);
        System.out.println("Libro guardado exitosamente en la base de datos.");
    }



    public void listarLibrosRegistrados() {
        List<Libro> libros = repositorio.findLibros();

        for (Libro libro : libros) {
            System.out.println("----- Libro -----");
            System.out.println("Título: " + libro.getTitulo());

            String nombresAutores = libro.getAutor().stream()
                    .map(Autor::getNombre)
                    .collect(Collectors.joining(", "));
            System.out.println("Autor(es): " + nombresAutores);

            String idiomas = libro.getLenguaje().stream()
                    .map(Lenguaje::getLenguaje)
                    .collect(Collectors.joining(", "));
            System.out.println("Idioma(s): " + idiomas);

            System.out.println("Descargas: " + libro.getDescargas());
            System.out.println("-----------------");
        }
    }





    public void listarAutoresRegistrados() {
        List<Libro> libros = repositorio.findAutores();

        class AutorInfo {
            Autor autor;
            List<String> libros = new ArrayList<>();
            AutorInfo(Autor autor) { this.autor = autor; }
        }

        Map<String, AutorInfo> autoresMap = new LinkedHashMap<>();

        for (Libro libro : libros) {
            for (Autor autor : libro.getAutor()) {
                AutorInfo info = autoresMap.computeIfAbsent(autor.getNombre(), k -> new AutorInfo(autor));
                info.libros.add(libro.getTitulo());
            }
        }

        if (autoresMap.isEmpty()) {
            System.out.println("No hay autores registrados.");
            return;
        }

        for (AutorInfo info : autoresMap.values()) {
            Autor autor = info.autor;
            List<String> librosDelAutor = info.libros;
            librosDelAutor.sort(String::compareToIgnoreCase);

            System.out.println("Autor: " + autor.getNombre());
            System.out.println("Fecha de nacimiento: " +
                    (autor.getFechaNacimiento() != null ? autor.getFechaNacimiento() : "No disponible"));
            System.out.println("Fecha de fallecimiento: " +
                    (autor.getFechaFallecimiento() != null ? autor.getFechaFallecimiento() : "No disponible"));
            System.out.println("Libros registrados: " + String.join(", ", librosDelAutor));
            System.out.println("-----------------------------");
        }
    }





    public void listarAutoresVivosEnAnio() {
        System.out.println("Ingresa el año para consultar autores vivos:");
        int anio;

        try {
            anio = Integer.parseInt(teclado.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Año inválido. Intenta con un número.");
            return;
        }

        List<Libro> libros = repositorio.findAutores();

        class AutorInfo {
            Autor autor;
            List<String> libros = new ArrayList<>();
            AutorInfo(Autor autor) { this.autor = autor; }
        }

        Map<String, AutorInfo> autoresMap = new LinkedHashMap<>();

        for (Libro libro : libros) {
            for (Autor autor : libro.getAutor()) {
                Integer nacimiento = parseFecha(autor.getFechaNacimiento());
                Integer fallecimiento = parseFecha(autor.getFechaFallecimiento());

                if (nacimiento != null && nacimiento <= anio &&
                        (fallecimiento == null || fallecimiento >= anio)) {

                    Optional<AutorInfo> existente = Optional.ofNullable(autoresMap.get(autor.getNombre()));

                    if (existente.isPresent()) {
                        existente.get().libros.add(libro.getTitulo());
                    } else {
                        AutorInfo nuevo = new AutorInfo(autor);
                        nuevo.libros.add(libro.getTitulo());
                        autoresMap.put(autor.getNombre(), nuevo);
                    }
                }
            }
        }

        if (autoresMap.isEmpty()) {
            System.out.println("No se encontraron autores vivos en el año " + anio + ".");
            return;
        }

        for (AutorInfo info : autoresMap.values()) {
            Autor autor = info.autor;
            List<String> titulos = info.libros;
            titulos.sort(String::compareToIgnoreCase);

            System.out.println("Autor: " + autor.getNombre());
            System.out.println("Fecha de nacimiento: " +
                    (autor.getFechaNacimiento() != null ? autor.getFechaNacimiento() : "No disponible"));
            System.out.println("Fecha de fallecimiento: " +
                    (autor.getFechaFallecimiento() != null ? autor.getFechaFallecimiento() : "No disponible"));
            System.out.println("Libros registrados: " + String.join(", ", titulos));
            System.out.println("-----------------------------");
        }
    }

    private Integer parseFecha(String fecha) {
        try {
            if (fecha != null && !fecha.isBlank()) {
                return Integer.parseInt(fecha.trim());
            }
        } catch (NumberFormatException e) {
            System.out.println("Formato inválido de fecha: " + fecha);
        }
        return null;
    }


    public void listarLibrosPorLenguaje() {
        Scanner scanner = new Scanner(System.in);
        String lenguaje;

        do {
            System.out.println("Selecciona un idioma para listar libros:");
            System.out.println("es - Español");
            System.out.println("en - Inglés");
            System.out.println("fr - Francés");
            System.out.println("pt - Portugués");
            System.out.print("Ingresa el código de idioma (es, en, fr, pt): ");
            lenguaje = scanner.nextLine().trim().toLowerCase();

            if (!lenguaje.equals("es") && !lenguaje.equals("en") && !lenguaje.equals("fr") && !lenguaje.equals("pt")) {
                System.out.println("Código inválido. Por favor, ingresa uno de los siguientes: es, en, fr, pt.");
            }
        } while (!lenguaje.equals("es") && !lenguaje.equals("en") && !lenguaje.equals("fr") && !lenguaje.equals("pt"));


        List<Libro> libros = repositorio.findLibrosPorIdioma(lenguaje);

        if (libros.isEmpty()) {
            System.out.println("No se encontraron libros para el idioma seleccionado: " + lenguaje);
            return;
        }

        System.out.println("----- Libros en idioma '" + lenguaje + "' -----");
        for (Libro libro : libros) {

            String autoresConcatenados = libro.getAutor().stream()
                    .map(Autor::getNombre)
                    .collect(Collectors.joining(", "));


            String idiomasConcatenados = libro.getLenguaje().stream()
                    .map(Lenguaje::getLenguaje)
                    .collect(Collectors.joining(", "));

            System.out.println("Título: " + libro.getTitulo());
            System.out.println("Autor(es): " + autoresConcatenados);
            System.out.println("Idioma(s): " + idiomasConcatenados);
            System.out.println("Número de descargas: " + libro.getDescargas());
            System.out.println("-----------------------------");
        }
    }







}
