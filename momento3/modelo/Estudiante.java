package modelo;

import estructuras.ListaEnlazada;
import java.util.List;

/**
 * Representa a un estudiante de la universidad.
 *
 * <p>Hereda de {@link Persona} y agrega el semestre actual, la matriz nativa
 * {@code Double[10][20]} para notas por semestre y una lista enlazada para
 * el historial de materias cursadas.</p>
 */
public class Estudiante extends Persona {
    private static final int SEMESTRES = 10, MATERIAS = 20;
    private static final double APROBATORIA = 3.0;

    private int semestreActual;
    private Double[][] notas = new Double[SEMESTRES][MATERIAS];
    private String[][] codigosMaterias = new String[SEMESTRES][MATERIAS];
    private ListaEnlazada<String> historialMaterias = new ListaEnlazada<>();

    /**
     * Crea un estudiante validando que el semestre este dentro del rango permitido.
     */
    public Estudiante(String nombre, String id, String email, int semestreActual) {
        super(nombre, id, email);
        validarSemestre(semestreActual);
        this.semestreActual = semestreActual;
    }

    /**
     * Registra o actualiza una nota dentro de la matriz de notas.
     */
    public void registrarNota(int semestre, String codigoMateria, double nota) {
        validarSemestre(semestre); validarNota(nota);
        int fila = semestre - 1, libre = -1;
        String codigo = codigoMateria.toUpperCase();
        for (int col = 0; col < MATERIAS; col++) {
            if (codigo.equalsIgnoreCase(codigosMaterias[fila][col])) {
                notas[fila][col] = nota;
                agregarHistorial(codigo);
                return;
            }
            if (libre == -1 && notas[fila][col] == null) libre = col;
        }
        if (libre == -1) throw new IllegalStateException("El semestre " + semestre + " ya tiene 20 notas.");
        codigosMaterias[fila][libre] = codigo;
        notas[fila][libre] = nota;
        agregarHistorial(codigo);
    }

     /**
     * Indica si el estudiante aprobo una materia con nota mayor o igual a 3.0.
     */
    public boolean aproboMateria(String codigoMateria) {
        for (int s = 0; s < SEMESTRES; s++)
            for (int m = 0; m < MATERIAS; m++)
                if (codigosMaterias[s][m] != null && codigosMaterias[s][m].equalsIgnoreCase(codigoMateria)
                        && notas[s][m] != null && notas[s][m] >= APROBATORIA) return true;
        return false;
    }

    /**
     * Calcula el promedio de un semestre especifico.
     */
    public double calcularPromedioSemestre(int semestre) {
        validarSemestre(semestre);
        return promedioFila(semestre - 1);
    }

    /**
     * Calcula el promedio acumulado con todas las notas registradas.
     */
    public double calcularPromedioAcumulado() {
        double suma = 0; int cant = 0;
        for (int s = 0; s < SEMESTRES; s++)
            for (int m = 0; m < MATERIAS; m++)
                if (notas[s][m] != null) { suma += notas[s][m]; cant++; }
        return cant == 0 ? 0 : suma / cant;
    }

    public int contarAprobadas() { return contar(true); }
    public int contarReprobadas() { return contar(false); }

    /**
     * Construye un reporte academico con promedios y materias reprobadas.
     */
    public String generarReporteAcademico() {
        StringBuilder r = new StringBuilder("--- REPORTE ACADEMICO ---\n")
                .append("Estudiante: ").append(getNombre()).append(" (ID: ").append(getId()).append(")\n");

        for (int s = 0; s < SEMESTRES; s++) if (tieneNotas(s)) {
            r.append("Semestre ").append(s + 1).append(":\n");
            for (int m = 0; m < MATERIAS; m++)
                if (notas[s][m] != null) r.append(codigosMaterias[s][m]).append(": ").append(fmt(notas[s][m])).append("\n");
            r.append("Promedio: ").append(fmt(promedioFila(s))).append("\n\n");
        }

        return r.append("=== RESUMEN ===\n")
                .append("Promedio acumulado: ").append(fmt(calcularPromedioAcumulado())).append("\n")
                .append("Materias aprobadas: ").append(contarAprobadas()).append("\n")
                .append("Materias reprobadas: ").append(contarReprobadas()).append("\n")
                .append(detalleReprobadas()).toString();
    }

    /**
     * Copia la matriz de notas para conservar estados de deshacer/rehacer.
     */
    public Double[][] copiarNotas() { return copiar(notas); }
    public String[][] copiarCodigosMaterias() { return copiar(codigosMaterias); }
    public List<String> copiarHistorial() { return historialMaterias.aLista(); }

    /**
     * Restaura notas, codigos e historial desde una copia guardada.
     */
    public void restaurarEstadoAcademico(Double[][] nuevasNotas, String[][] nuevosCodigos, List<String> historial) {
        notas = copiar(nuevasNotas);
        codigosMaterias = copiar(nuevosCodigos);
        historialMaterias.reemplazarCon(historial);
    }

    /**
     * Implementacion polimorfica del metodo definido en Persona.
     */
    @Override
    public String mostrarInformacion() {
        return "ID: " + getId() + "\nNombre: " + getNombre() + "\nEmail: " + getEmail()
                + "\nSemestre: " + semestreActual + "\nPromedio acumulado: " + fmt(calcularPromedioAcumulado());
    }

    private int contar(boolean aprobadas) {
        int total = 0;
        for (int s = 0; s < SEMESTRES; s++)
            for (int m = 0; m < MATERIAS; m++)
                if (notas[s][m] != null && (notas[s][m] >= APROBATORIA) == aprobadas) total++;
        return total;
    }

    private double promedioFila(int fila) {
        double suma = 0; int cant = 0;
        for (int m = 0; m < MATERIAS; m++) if (notas[fila][m] != null) { suma += notas[fila][m]; cant++; }
        return cant == 0 ? 0 : suma / cant;
    }

    private boolean tieneNotas(int fila) {
        for (int m = 0; m < MATERIAS; m++) if (notas[fila][m] != null) return true;
        return false;
    }

    private String detalleReprobadas() {
        StringBuilder d = new StringBuilder();
        for (int s = 0; s < SEMESTRES; s++)
            for (int m = 0; m < MATERIAS; m++)
                if (notas[s][m] != null && notas[s][m] < APROBATORIA)
                     d.append(d.length() == 0 ? "Detalle de materias reprobadas:\n" : "")
                            .append("- Semestre ").append(s + 1).append(": ")
                            .append(codigosMaterias[s][m]).append(" con nota ").append(notas[s][m]).append("\n");
        return d.length() == 0 ? "Detalle de materias reprobadas: Ninguna\n" : d.toString();
    }

    private void agregarHistorial(String codigo) {
        if (!historialMaterias.contiene(codigo)) historialMaterias.agregar(codigo);
    }

    private void validarSemestre(int semestre) {
        if (semestre < 1 || semestre > SEMESTRES) throw new IllegalArgumentException("El semestre debe estar entre 1 y 10.");
    }

    private void validarNota(double nota) {
        if (nota < 0 || nota > 5) throw new IllegalArgumentException("La nota debe estar en escala de 0.0 a 5.0.");
    }

    private String fmt(double n) { return String.format("%.2f", n); }

    private static Double[][] copiar(Double[][] matriz) {
        Double[][] c = new Double[SEMESTRES][MATERIAS];
        for (int i = 0; i < SEMESTRES; i++) System.arraycopy(matriz[i], 0, c[i], 0, MATERIAS);
        return c;
    }

    private static String[][] copiar(String[][] matriz) {
        String[][] c = new String[SEMESTRES][MATERIAS];
        for (int i = 0; i < SEMESTRES; i++) System.arraycopy(matriz[i], 0, c[i], 0, MATERIAS);
        return c;
    }
}