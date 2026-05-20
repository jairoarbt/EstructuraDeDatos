package modelo;

import excepciones.HorarioConflictivoException;

/**
 * Representa un aula con una matriz de disponibilidad horaria.
 *
 * <p>La matriz {@code boolean[7][24]} almacena dias y horas. Un valor
 * {@code true} indica que la hora esta reservada.</p>
 */
public class Aula {
    private static final int DIAS = 7, HORAS = 24;
    private String nombre;
    private int capacidad;
    private boolean[][] horarioReservado = new boolean[DIAS][HORAS];

    public Aula(String nombre, int capacidad) {
        this.nombre = nombre;
        this.capacidad = capacidad;
    }

    public String getNombre() { return nombre; }

     /**
     * Reserva un bloque de horas si todas estan disponibles.
     */
    public void reservar(int dia, int hora, int duracion) throws HorarioConflictivoException {
        validar(dia, hora, duracion);
        for (int h = hora; h < hora + duracion; h++)
            if (horarioReservado[dia][h]) throw new HorarioConflictivoException(nombreDia(dia) + " " + h + ":00 ya esta reservado.");
        for (int h = hora; h < hora + duracion; h++) horarioReservado[dia][h] = true;
    }

    /**
     * Libera un bloque de horas de la matriz.
     */
    public void liberar(int dia, int hora, int duracion) {
        validar(dia, hora, duracion);
        for (int h = hora; h < hora + duracion; h++) horarioReservado[dia][h] = false;
    }

    /**
     * Consulta si una hora especifica esta libre.
     */
    public boolean consultarDisponibilidad(int dia, int hora) {
        validar(dia, hora, 1);
        return !horarioReservado[dia][hora];
    }

     /**
     * Genera una copia de la matriz para operaciones deshacer/rehacer.
     */
    public boolean[][] copiarHorario() {
        boolean[][] copia = new boolean[DIAS][HORAS];
        for (int d = 0; d < DIAS; d++) System.arraycopy(horarioReservado[d], 0, copia[d], 0, HORAS);
        return copia;
    }

    public void restaurarHorario(boolean[][] estado) {
        horarioReservado = new boolean[DIAS][HORAS];
        for (int d = 0; d < DIAS; d++) System.arraycopy(estado[d], 0, horarioReservado[d], 0, HORAS);
    }

    public static String nombreDia(int dia) {
        String[] n = {"Domingo", "Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sabado"};
        return dia < 0 || dia >= n.length ? "Dia invalido" : n[dia];
    }

    @Override public String toString() { return nombre + " (capacidad " + capacidad + ")"; }

    private void validar(int dia, int hora, int duracion) {
        if (dia < 0 || dia >= DIAS) throw new IllegalArgumentException("El dia debe estar entre 0 y 6.");
        if (hora < 0 || hora >= HORAS) throw new IllegalArgumentException("La hora debe estar entre 0 y 23.");
        if (duracion <= 0 || hora + duracion > HORAS) throw new IllegalArgumentException("La duracion no puede salir del rango horario del dia.");
    }
}
