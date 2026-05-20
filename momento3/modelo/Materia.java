package modelo;

import estructuras.*;
import excepciones.*;
import java.util.List;

/**
 * Representa una materia academica con cupos, creditos, pre-requisitos,
 * estudiantes inscritos y cola de espera.
 */
public class Materia {
    private String codigo, nombre;
    private int cuposMaximos, creditos;
    private ListaEnlazada<String> preRequisitos = new ListaEnlazada<>();
    private ListaEnlazada<String> estudiantesInscritos = new ListaEnlazada<>();
    private Cola<String> colaEspera = new Cola<>();

    /**
     * Crea una materia y valida que tenga al menos un cupo.
     */
    public Materia(String codigo, String nombre, int cuposMaximos, int creditos) {
        if (cuposMaximos <= 0) throw new IllegalArgumentException("La materia debe tener al menos un cupo.");
        this.codigo = codigo.toUpperCase();
        this.nombre = nombre;
        this.cuposMaximos = cuposMaximos;
        this.creditos = creditos;
    }

    public String getCodigo() { return codigo; }
    public int getCuposDisponibles() { return cuposMaximos - estudiantesInscritos.tamanio(); }
    public boolean estaInscrito(String id) { return estudiantesInscritos.contiene(id); }
    public boolean estaEnCola(String id) { return colaEspera.contiene(id); }

    /**
     * Agrega un codigo de materia a la lista enlazada de pre-requisitos.
     */
    public void agregarPreRequisito(String codigoPre) {
        codigoPre = codigoPre.toUpperCase();
        if (!preRequisitos.contiene(codigoPre)) preRequisitos.agregar(codigoPre);
    }

    /**
     * Comprueba que el estudiante haya aprobado todos los pre-requisitos.
     */
    public void validarPreRequisitos(Estudiante e) throws PreRequisitoNoAprobadoException {
        for (String pre : preRequisitos)
            if (!e.aproboMateria(pre))
                throw new PreRequisitoNoAprobadoException("El estudiante " + e.getId() + " no ha aprobado " + pre + ".");
    }

    /**
     * Inscribe directamente si existe cupo disponible.
     */
    public void inscribirDirecto(String id) throws CupoLlenoException {
        if (getCuposDisponibles() <= 0) throw new CupoLlenoException("La materia " + codigo + " no tiene cupos disponibles.");
        if (!estudiantesInscritos.contiene(id)) estudiantesInscritos.agregar(id);
    }

    public boolean cancelarInscripcion(String id) { return estudiantesInscritos.eliminar(id); }
    public void agregarAColaDeEspera(String id) { if (!colaEspera.contiene(id)) colaEspera.encolar(id); }
    public boolean retirarDeCola(String id) { return colaEspera.eliminar(id); }

    /**
     * Saca de la cola al primer estudiante en espera y lo inscribe.
     */
    public String asignarPrimerEstudianteEnEspera() throws ColaDeEsperaVaciaException, CupoLlenoException {
        if (colaEspera.estaVacia()) throw new ColaDeEsperaVaciaException("La cola de espera de " + codigo + " esta vacia.");
        String id = colaEspera.desencolar();
        inscribirDirecto(id);
        return id;
    }

    public List<String> copiarInscritos() { return estudiantesInscritos.aLista(); }
    public List<String> copiarColaEspera() { return colaEspera.aLista(); }
    /**
     * Restaura inscritos y cola de espera desde copias guardadas.
     */
    public void restaurarEstado(List<String> inscritos, List<String> espera) {
        estudiantesInscritos.reemplazarCon(inscritos);
        colaEspera.reemplazarCon(espera);
    }

    public String mostrarPreRequisitos() {
        return preRequisitos.estaVacia() ? codigo + " no tiene pre-requisitos registrados."
                : codigo + " requiere: " + preRequisitos.unir(", ");
    }

    public String mostrarColaEspera() {
        StringBuilder s = new StringBuilder("--- COLA DE ESPERA ---\nMateria: " + codigo + " - " + nombre
                + "\nCupos totales: " + cuposMaximos + "\n");
        List<String> espera = colaEspera.aLista();
        if (espera.isEmpty()) return s.append("No hay estudiantes en espera.\n").toString();
        for (int i = 0; i < espera.size(); i++) s.append("Posicion ").append(i + 1).append(": ").append(espera.get(i)).append("\n");
        return s.append("Total en espera: ").append(espera.size()).append("\n").toString();
    }

    public String resumen() {
        return codigo + " - " + nombre + " | creditos: " + creditos + " | cupos: "
                + estudiantesInscritos.tamanio() + "/" + cuposMaximos + " | espera: " + colaEspera.tamanio();
    }
}
