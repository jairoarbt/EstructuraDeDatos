package util;

import estructuras.*;
import excepciones.*;
import modelo.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Clase central del sistema academico.
 *
 * <p>Coordina estudiantes, materias, aulas, rutas, reportes, procesamiento por
 * lotes y operaciones de deshacer/rehacer. En esta clase se integran las
 * estructuras exigidas por el proyecto: {@link HashMap}, {@link TreeMap},
 * arreglos nativos, matrices nativas, listas enlazadas, colas y pilas.</p>
 */
public class GestorAcademico {
    private static final int MAX_EDIFICIOS = 10, INF = 1_000_000_000;

    // HashMap: busqueda rapida por ID o codigo.
    private final HashMap<String, Estudiante> estudiantes = new HashMap<>();
    private final HashMap<String, Materia> materias = new HashMap<>();
    // TreeMap: mantiene las aulas ordenadas por nombre.
    private final TreeMap<String, Aula> aulas = new TreeMap<>();
    // Arreglo estatico obligatorio de cinco facultades.
    private final Facultad[] facultades = new Facultad[5];
    // Pilas usadas para deshacer y rehacer operaciones.
    private final Pila<OperacionDeshacer> deshacer = new Pila<>(), rehacer = new Pila<>();
    // Pilas usadas para navegar reportes hacia atras y adelante.
    private final Pila<String> reportesAtras = new Pila<>(), reportesAdelante = new Pila<>();
    // Arreglo de nombres y matriz de adyacencia para el grafo de edificios.
    private final String[] edificios = new String[MAX_EDIFICIOS];
    private final int[][] distancias = new int[MAX_EDIFICIOS][MAX_EDIFICIOS];
    private int totalEdificios;
    private String reporteActual;

     /**
     * Inicializa el gestor y carga datos base para probar el menu sin empezar vacio.
     */
    public GestorAcademico() { datosBase(); }

    /**
     * Registra un estudiante usando su ID como clave dentro del HashMap.
     */
    public void registrarEstudiante(String nombre, String id, String email, int semestre) {
        if (estudiantes.containsKey(id)) throw new IllegalArgumentException("Ya existe un estudiante con ID: " + id);
        estudiantes.put(id, new Estudiante(nombre, id, email, semestre));
    }

    /**
     * Busca un estudiante por ID.
     *
     * @throws EstudianteNoEncontradoException si el estudiante no existe
     */
    public Estudiante buscarEstudiante(String id) throws EstudianteNoEncontradoException {
        Estudiante e = estudiantes.get(id);
        if (e == null) throw new EstudianteNoEncontradoException("No existe estudiante con ID: " + id);
        return e;
    }

    /**
     * Devuelve un texto con todos los estudiantes registrados.
     */
    public String listarEstudiantes() {
        if (estudiantes.isEmpty()) return "No hay estudiantes registrados.";
        StringBuilder s = new StringBuilder("--- ESTUDIANTES REGISTRADOS ---\n");
        for (Estudiante e : estudiantes.values()) s.append(e.mostrarInformacion()).append("\n--------------------\n");
        return s.toString();
    }

    /**
     * Elimina un estudiante y guarda la operacion para poder deshacerla.
     */
    public String eliminarEstudiante(String id) throws EstudianteNoEncontradoException {
        Estudiante e = buscarEstudiante(id);
        Map<String, EstadoMateria> antes = estadosMaterias();
        estudiantes.remove(id);
        materias.values().forEach(m -> { m.cancelarInscripcion(id); m.retirarDeCola(id); });
        Map<String, EstadoMateria> despues = estadosMaterias();
        op("Eliminar estudiante " + id, () -> { estudiantes.put(id, e); restaurarMaterias(antes); },
                () -> { estudiantes.remove(id); restaurarMaterias(despues); });
        return "Estudiante eliminado. La baja definitiva se puede deshacer desde la pila.";
    }

    /**
     * Crea una materia con cupos, creditos, pre-requisitos, inscritos y cola de espera.
     */
    public void crearMateria(String codigo, String nombre, int cupos, int creditos) {
        codigo = codigo.toUpperCase();
        if (materias.containsKey(codigo)) throw new IllegalArgumentException("Ya existe una materia con codigo: " + codigo);
        materias.put(codigo, new Materia(codigo, nombre, cupos, creditos));
    }

    /**
     * Agrega un pre-requisito a una materia usando la lista enlazada de la materia.
     */
    public void agregarPreRequisito(String codigoMateria, String codigoPre) {
        if (!materias.containsKey(codigoPre.toUpperCase())) throw new IllegalArgumentException("El pre-requisito debe existir como materia.");
        if (codigoMateria.equalsIgnoreCase(codigoPre)) throw new IllegalArgumentException("Una materia no puede ser pre-requisito de si misma.");
        materia(codigoMateria).agregarPreRequisito(codigoPre);
    }

    public String mostrarPreRequisitos(String codigoMateria) { return materia(codigoMateria).mostrarPreRequisitos(); }

    public String listarMaterias() {
        if (materias.isEmpty()) return "No hay materias registradas.";
        StringBuilder s = new StringBuilder("--- MATERIAS DISPONIBLES ---\n");
        materias.values().forEach(m -> s.append(m.resumen()).append("\n"));
        return s.toString();
    }

    /**
     * Inscribe un estudiante si cumple los pre-requisitos; si no hay cupo,
     * lo agrega a la cola de espera de la materia.
     */
    public String inscribirEstudiante(String id, String codigo)
            throws EstudianteNoEncontradoException, PreRequisitoNoAprobadoException {
        Estudiante e = buscarEstudiante(id);
        Materia m = materia(codigo);
        if (m.estaInscrito(id)) return "El estudiante ya esta inscrito en " + m.getCodigo() + ".";
        if (m.estaEnCola(id)) return "El estudiante ya esta en la cola de espera de " + m.getCodigo() + ".";

        m.validarPreRequisitos(e);
        EstadoMateria antes = estado(m);
        String msg;
        try {
            m.inscribirDirecto(id);
            msg = "Inscripcion exitosa. Cupos restantes: " + m.getCuposDisponibles();
        } catch (CupoLlenoException ex) {
            m.agregarAColaDeEspera(id);
            msg = "Materia llena. Estudiante agregado a la cola de espera.";
        }
        EstadoMateria despues = estado(m);
        op("Inscribir estudiante " + id + " en " + m.getCodigo(),
                () -> m.restaurarEstado(antes.inscritos, antes.espera),
                () -> m.restaurarEstado(despues.inscritos, despues.espera));
        return msg;
    }

    /**
     * Cancela una inscripcion. Cuando se libera un cupo, intenta asignarlo
     * automaticamente al primer estudiante de la cola de espera.
     */
    public String cancelarInscripcion(String id, String codigo) throws EstudianteNoEncontradoException {
        buscarEstudiante(id);
        Materia m = materia(codigo);
        EstadoMateria antes = estado(m);
        boolean inscrito = m.cancelarInscripcion(id), enCola = !inscrito && m.retirarDeCola(id);
        if (!inscrito && !enCola) throw new IllegalArgumentException("El estudiante no esta inscrito ni en espera para " + m.getCodigo() + ".");

        String msg = enCola ? "El estudiante fue retirado de la cola de espera." : "Cancelacion exitosa. Cupo liberado.";
        if (inscrito) try { msg += "\nAsignando cupo al primer estudiante en cola: " + m.asignarPrimerEstudianteEnEspera(); }
        catch (ColaDeEsperaVaciaException ex) { msg += "\nNo habia estudiantes en cola de espera."; }
        catch (CupoLlenoException ex) { msg += "\nNo fue posible asignar el cupo: " + ex.getMessage(); }

        EstadoMateria despues = estado(m);
        op("Cancelar inscripcion de " + id + " en " + m.getCodigo(),
                () -> m.restaurarEstado(antes.inscritos, antes.espera),
                () -> m.restaurarEstado(despues.inscritos, despues.espera));
        return msg;
    }

    public String mostrarColaEspera(String codigoMateria) { return materia(codigoMateria).mostrarColaEspera(); }

     /**
     * Reserva un bloque horario usando la matriz boolean[7][24] del aula.
     */
    public String reservarHorario(String nombreAula, int dia, int hora, int duracion) throws HorarioConflictivoException {
        Aula a = aula(nombreAula);
        boolean[][] antes = a.copiarHorario();
        a.reservar(dia, hora, duracion);
        boolean[][] despues = a.copiarHorario();
        op("Reservar horario en aula " + a.getNombre(), () -> a.restaurarHorario(antes), () -> a.restaurarHorario(despues));
        return "Reserva exitosa en " + a.getNombre() + " para " + Aula.nombreDia(dia) + " desde las " + hora + ":00 durante " + duracion + " hora(s).";
    }

    /**
     * Libera un bloque horario previamente ocupado en la matriz del aula.
     */
    public String liberarHorario(String nombreAula, int dia, int hora, int duracion) {
        Aula a = aula(nombreAula);
        boolean[][] antes = a.copiarHorario();
        a.liberar(dia, hora, duracion);
        boolean[][] despues = a.copiarHorario();
        op("Liberar horario en aula " + a.getNombre(), () -> a.restaurarHorario(antes), () -> a.restaurarHorario(despues));
        return "Horario liberado en " + a.getNombre() + ".";
    }

    public String consultarDisponibilidad(String nombreAula, int dia, int hora) {
        Aula a = aula(nombreAula);
        return a.getNombre() + " - " + Aula.nombreDia(dia) + " " + hora + ":00 -> "
                + (a.consultarDisponibilidad(dia, hora) ? "LIBRE" : "RESERVADO");
    }

    public String listarAulas() {
        StringBuilder s = new StringBuilder("--- AULAS ORDENADAS POR NOMBRE ---\n");
        aulas.values().forEach(a -> s.append(a).append("\n"));
        return s.toString();
    }

    /**
     * Agrega una conexion no dirigida entre dos edificios en la matriz de adyacencia.
     */
    public void agregarConexion(int origen, int destino, int metros) {
        validarEdificio(origen); validarEdificio(destino);
        if (metros <= 0) throw new IllegalArgumentException("La distancia debe ser mayor que cero.");
        distancias[origen][destino] = distancias[destino][origen] = metros;
    }

    public String listarEdificios() {
        StringBuilder s = new StringBuilder("--- EDIFICIOS REGISTRADOS ---\n");
        for (int i = 0; i < totalEdificios; i++) s.append(i).append(": ").append(edificios[i]).append("\n");
        return s.toString();
    }

    /**
     * Calcula la ruta mas corta entre dos edificios mediante Dijkstra.
     */
    public String calcularRutaMasCorta(int origen, int destino) {
        validarEdificio(origen); validarEdificio(destino);
        int[] d = new int[totalEdificios], previo = new int[totalEdificios];
        boolean[] visto = new boolean[totalEdificios];
        Arrays.fill(d, INF); Arrays.fill(previo, -1); d[origen] = 0;

        for (int i = 0; i < totalEdificios; i++) {
            int u = masCercano(d, visto);
            if (u == -1) break;
            visto[u] = true;
            for (int v = 0; v < totalEdificios; v++)
                if (distancias[u][v] > 0 && !visto[v] && d[u] + distancias[u][v] < d[v]) {
                    d[v] = d[u] + distancias[u][v];
                    previo[v] = u;
                }
        }
        if (d[destino] == INF) return "No existe ruta entre " + edificios[origen] + " y " + edificios[destino] + ".";
        return rutaTexto(camino(destino, previo), d[destino]);
    }

    /**
     * Registra una nota y guarda copias del estado academico para deshacer/rehacer.
     */
    public String registrarNota(String id, int semestre, String codigo, double nota) throws EstudianteNoEncontradoException {
        Estudiante e = buscarEstudiante(id);
        Double[][] n1 = e.copiarNotas(); String[][] c1 = e.copiarCodigosMaterias(); List<String> h1 = e.copiarHistorial();
        e.registrarNota(semestre, codigo, nota);
        Double[][] n2 = e.copiarNotas(); String[][] c2 = e.copiarCodigosMaterias(); List<String> h2 = e.copiarHistorial();
        op("Registrar nota de " + id + " en " + codigo.toUpperCase(),
                () -> e.restaurarEstadoAcademico(n1, c1, h1),
                () -> e.restaurarEstadoAcademico(n2, c2, h2));
        return "Nota registrada correctamente.";
    }

    /**
     * Genera un reporte academico y lo guarda en la pila de navegacion de reportes.
     */
    public String generarReporteAcademico(String id) throws EstudianteNoEncontradoException {
        if (reporteActual != null) reportesAtras.apilar(reporteActual);
        reporteActual = buscarEstudiante(id).generarReporteAcademico();
        reportesAdelante.limpiar();
        return reporteActual;
    }

    public String verReporteAnterior() {
        if (reportesAtras.estaVacia()) return "No hay reportes anteriores.";
        if (reporteActual != null) reportesAdelante.apilar(reporteActual);
        return reporteActual = reportesAtras.desapilar();
    }

    public String verReporteSiguiente() {
        if (reportesAdelante.estaVacia()) return "No hay reportes siguientes.";
        if (reporteActual != null) reportesAtras.apilar(reporteActual);
        return reporteActual = reportesAdelante.desapilar();
    }

    /**
     * Deshace la ultima operacion registrada en la pila de deshacer.
     */
    public String deshacer() throws PilaDeshacerVaciaException { return mover(deshacer, rehacer, true); }
    /**
     * Rehace la ultima operacion deshecha.
     */
    public String rehacer() throws PilaDeshacerVaciaException { return mover(rehacer, deshacer, false); }
    
    public String procesarArchivoCsv(String rutaArchivo) throws ArchivoInvalidoException {
        Path ruta = Path.of(rutaArchivo);
        if (!Files.isRegularFile(ruta)) throw new ArchivoInvalidoException("No existe el archivo: " + rutaArchivo);
        if (!rutaArchivo.toLowerCase().endsWith(".csv")) throw new ArchivoInvalidoException("El archivo debe tener extension .csv");

        Cola<SolicitudInscripcion> cola = cargarCsv(ruta);
        int total = cola.tamanio(), ok = 0, espera = 0, fail = 0, i = 0;
        StringBuilder s = new StringBuilder("--- PROCESAMIENTO MASIVO (BATCH) ---\nArchivo: " + rutaArchivo
                + "\nSe encolaron " + total + " solicitudes.\nProcesando cola...\n");
        while (!cola.estaVacia()) {
            SolicitudInscripcion sol = cola.desencolar();
            try {
                String msg = inscribirEstudiante(sol.getIdEstudiante(), sol.getCodigoMateria());
                boolean enEspera = msg.toLowerCase().contains("cola de espera");
                if (enEspera) espera++; else ok++;
                s.append(lineaBatch(++i, total, sol, enEspera ? "En cola" : "Exitosa"));
            } catch (Exception e) {
                fail++;
                s.append(lineaBatch(++i, total, sol, "Fallida: " + e.getMessage()));
            }
        }
        return s.append("=== RESUMEN ===\nExitosas: ").append(ok)
                .append("\nEn cola de espera: ").append(espera).append("\nFallidas: ").append(fail).append("\n").toString();
    }

    /**
     * Crea una operacion reversible y la guarda en la pila de deshacer.
     */
    private void op(String desc, Runnable undo, Runnable redo) {
        deshacer.apilar(new OperacionSimple(desc, undo, redo));
        rehacer.limpiar();
    }

    
    /**
     * Mueve una operacion entre pilas y ejecuta su accion de deshacer o rehacer.
     */
    private String mover(Pila<OperacionDeshacer> origen, Pila<OperacionDeshacer> destino, boolean esDeshacer)
            throws PilaDeshacerVaciaException {
        if (origen.estaVacia()) throw new PilaDeshacerVaciaException("No hay operaciones para " + (esDeshacer ? "deshacer." : "rehacer."));
        OperacionDeshacer op = origen.desapilar();
        if (esDeshacer) op.deshacer(); else op.rehacer();
        destino.apilar(op);
        return "Operacion " + (esDeshacer ? "deshecha: " : "rehecha: ") + op.descripcion();
    }

    private Materia materia(String codigo) {
        Materia m = materias.get(codigo.toUpperCase());
        if (m == null) throw new IllegalArgumentException("No existe la materia con codigo: " + codigo);
        return m;
    }

    private Aula aula(String nombre) {
        Aula a = aulas.get(nombre);
        if (a == null) throw new IllegalArgumentException("No existe el aula: " + nombre);
        return a;
    }

    private EstadoMateria estado(Materia m) { return new EstadoMateria(m.copiarInscritos(), m.copiarColaEspera()); }

    private Map<String, EstadoMateria> estadosMaterias() {
        Map<String, EstadoMateria> copia = new HashMap<>();
        materias.forEach((codigo, materia) -> copia.put(codigo, estado(materia)));
        return copia;
    }

    private void restaurarMaterias(Map<String, EstadoMateria> estados) {
        estados.forEach((codigo, estado) -> {
            Materia m = materias.get(codigo);
            if (m != null) m.restaurarEstado(estado.inscritos, estado.espera);
        });
    }

    private Cola<SolicitudInscripcion> cargarCsv(Path ruta) throws ArchivoInvalidoException {
        Cola<SolicitudInscripcion> cola = new Cola<>();
        try (BufferedReader br = Files.newBufferedReader(ruta)) {
            String linea; int n = 0;
            while ((linea = br.readLine()) != null) {
                n++;
                if (linea.isBlank()) continue;
                String[] p = linea.split(",");
                if (p.length != 2) throw new ArchivoInvalidoException("Linea " + n + " invalida. Formato: idEstudiante,codigoMateria");
                cola.encolar(new SolicitudInscripcion(p[0].trim(), p[1].trim()));
            }
        } catch (IOException e) {
            throw new ArchivoInvalidoException("No fue posible leer el archivo: " + e.getMessage());
        }
        return cola;
    }

    private String lineaBatch(int i, int total, SolicitudInscripcion s, String estado) {
        return "[" + i + "/" + total + "] " + s.getIdEstudiante() + " -> " + s.getCodigoMateria() + " -> " + estado + "\n";
    }

    private int edificio(String nombre) {
        if (totalEdificios == MAX_EDIFICIOS) throw new IllegalStateException("No se pueden registrar mas edificios.");
        edificios[totalEdificios] = nombre;
        return totalEdificios++;
    }

    private void validarEdificio(int i) {
        if (i < 0 || i >= totalEdificios) throw new IllegalArgumentException("Indice de edificio invalido: " + i);
    }

    /**
     * Selecciona el edificio no visitado con menor distancia temporal.
     */
    private int masCercano(int[] d, boolean[] visto) {
        int pos = -1, menor = INF;
        for (int i = 0; i < totalEdificios; i++) if (!visto[i] && d[i] < menor) { menor = d[i]; pos = i; }
        return pos;
    }

    private List<Integer> camino(int destino, int[] previo) {
        List<Integer> c = new ArrayList<>();
        for (int actual = destino; actual != -1; actual = previo[actual]) c.add(0, actual);
        return c;
    }

    private String rutaTexto(List<Integer> camino, int total) {
        StringBuilder s = new StringBuilder("--- RESULTADO ---\nRuta mas corta:\n").append(edificios[camino.get(0)]);
        for (int i = 1; i < camino.size(); i++) {
            int a = camino.get(i - 1), b = camino.get(i);
            s.append(" -> ").append(edificios[b]).append(" (").append(distancias[a][b]).append("m)");
        }
        return s.append("\nDistancia TOTAL: ").append(total).append(" metros").toString();
    }

    
    
    /**
     * Carga datos iniciales: facultades, aulas, materias, pre-requisitos y edificios.
     */
    private void datosBase() {
        facultades[0] = new Facultad("ING", "Ingenieria");
        facultades[1] = new Facultad("SAL", "Ciencias de la Salud");
        facultades[2] = new Facultad("ECO", "Ciencias Economicas");
        facultades[3] = new Facultad("HUM", "Humanidades");
        facultades[4] = new Facultad("ART", "Artes");

        aulas.put("101", new Aula("101", 35));
        aulas.put("201", new Aula("201", 45));
        aulas.put("LAB-A", new Aula("LAB-A", 25));

        crearMateria("CALC101", "Calculo I", 3, 4);
        crearMateria("CALC102", "Calculo II", 3, 4);
        crearMateria("FIS101", "Fisica I", 3, 4);
        crearMateria("PROG101", "Programacion I", 4, 3);
        crearMateria("MATDIS", "Matematicas Discretas", 3, 3);
        agregarPreRequisito("CALC102", "CALC101");

        int ing = edificio("Ingenieria"), bib = edificio("Biblioteca"), caf = edificio("Cafeteria");
        int rec = edificio("Rectoria"), lab = edificio("Laboratorios");
        agregarConexion(ing, bib, 120); agregarConexion(ing, caf, 150); agregarConexion(caf, rec, 180);
        agregarConexion(bib, rec, 260); agregarConexion(ing, lab, 90); agregarConexion(lab, caf, 110);
    }

    /**
     * Copia del estado de una materia usada por deshacer/rehacer.
     */
    private static class EstadoMateria {
        private List<String> inscritos;
        private List<String> espera;

        private EstadoMateria(List<String> inscritos, List<String> espera) {
            this.inscritos = new ArrayList<>(inscritos);
            this.espera = new ArrayList<>(espera);
        }
    }
}