package app;

import excepciones.*;
import modelo.Estudiante;
import util.GestorAcademico;

import java.util.Scanner;

/**
 * Punto de entrada del sistema academico.
 *
 * <p>Esta clase se encarga de la interaccion por consola: muestra el menu,
 * lee datos del usuario y delega la logica de negocio a {@link GestorAcademico}.
 * Se usa un switch clasico con {@code case:} y {@code break} para mantener
 * compatibilidad con versiones antiguas de Java.</p>
 */
public class Main {
    private static final Scanner sc = new Scanner(System.in);
    private static final GestorAcademico gestor = new GestorAcademico();

     /**
     * Ejecuta el ciclo principal del programa hasta que el usuario seleccione salir.
     */
    public static void main(String[] args) {
        System.out.println("Sistema inicializado con materias, aulas, facultades y edificios de ejemplo.");
        boolean activo = true;
        while (activo) {
            menu();
            try {
                activo = opcion(leerInt("Seleccione una opcion: "));
            } catch (EstudianteNoEncontradoException | PreRequisitoNoAprobadoException
                     | HorarioConflictivoException | PilaDeshacerVaciaException
                     | ArchivoInvalidoException e) {
                System.out.println("Error: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            } catch (RuntimeException e) {
                System.out.println("Error: " + e.getMessage());
            }
            if (activo) pausa();
        }
        System.out.println("Gracias por usar el sistema. Hasta pronto.");
    }

    /**
     * Ejecuta la accion asociada a la opcion seleccionada en el menu.
     *
     * @param op numero digitado por el usuario
     * @return {@code false} cuando se selecciona salir; {@code true} en los demas casos
     */
    private static boolean opcion(int op) throws EstudianteNoEncontradoException,
            PreRequisitoNoAprobadoException, HorarioConflictivoException,
            PilaDeshacerVaciaException, ArchivoInvalidoException {
        switch (op) {
            case 1:
                registrarEstudiante();
                break;
            case 2:
                buscarEstudiante();
                break;
            case 3:
                imprimir(gestor.listarEstudiantes());
                break;
            case 4:
                imprimir(gestor.eliminarEstudiante(txt("ID: ")));
                break;
            case 5:
                crearMateria();
                break;
            case 6:
                preRequisito();
                break;
            case 7:
                imprimir(gestor.mostrarPreRequisitos(materia()));
                break;
            case 8:
                imprimir(gestor.inscribirEstudiante(txt("ID estudiante: "), materia()));
                break;
            case 9:
                imprimir(gestor.cancelarInscripcion(txt("ID estudiante: "), materia()));
                break;
            case 10:
                imprimir(gestor.mostrarColaEspera(materia()));
                break;
            case 11:
                horario(true);
                break;
            case 12:
                horario(false);
                break;
            case 13:
                imprimir(gestor.consultarDisponibilidad(aula(), leerInt("Dia (0-6): "), leerInt("Hora (0-23): ")));
                break;
            case 14:
                conexion();
                break;
            case 15:
                ruta();
                break;
            case 16:
                nota();
                break;
            case 17:
                imprimir(gestor.generarReporteAcademico(txt("ID estudiante: ")));
                break;
            case 18:
                navegadorReportes();
                break;
            case 19:
                imprimir(gestor.deshacer());
                break;
            case 20:
                imprimir(gestor.rehacer());
                break;
            case 21:
                batch();
                break;
            case 22:
                return false;
            default:
                imprimir("Opcion no valida.");
                break;
        }
        return true;
    }

     /**
     * Imprime las opciones disponibles para acceder a todas las funcionalidades.
     */
    private static void menu() {
         System.out.println();
        System.out.println("============================================================");
        System.out.println("PLANIFICACION ACADEMICA - SISTEMA UNIVERSITARIO");
        System.out.println("============================================================");
        System.out.println("=== GESTION DE ESTUDIANTES ===");
        System.out.println("1. Registrar estudiante");
        System.out.println("2. Buscar estudiante por ID");
        System.out.println("3. Listar todos los estudiantes");
        System.out.println("4. Eliminar estudiante");
        System.out.println("=== GESTION DE MATERIAS ===");
        System.out.println("5. Crear materia");
        System.out.println("6. Agregar pre-requisito");
        System.out.println("7. Mostrar pre-requisitos");
        System.out.println("8. Inscribir estudiante");
        System.out.println("9. Cancelar inscripcion");
        System.out.println("10. Mostrar cola de espera");
        System.out.println("=== GESTION DE HORARIOS ===");
        System.out.println("11. Reservar horario en aula");
        System.out.println("12. Liberar horario");
        System.out.println("13. Consultar disponibilidad");
        System.out.println("=== RUTAS ENTRE EDIFICIOS ===");
        System.out.println("14. Agregar conexion entre edificios");
        System.out.println("15. Calcular ruta mas corta");
        System.out.println("=== REPORTES ACADEMICOS ===");
        System.out.println("16. Registrar nota");
        System.out.println("17. Ver reporte academico");
        System.out.println("18. Navegador de reportes (atras/adelante)");
        System.out.println("=== SISTEMA DESHACER/REHACER ===");
        System.out.println("19. Deshacer ultima operacion");
        System.out.println("20. Rehacer ultima operacion");
        System.out.println("=== PROCESAMIENTO POR LOTES ===");
        System.out.println("21. Procesar archivo CSV");
        System.out.println("=== SALIR ===");
        System.out.println("22. Salir");

    }
    /**
     * Solicita los datos basicos y registra un estudiante en el sistema.
     */
    private static void registrarEstudiante() {
        imprimir("--- REGISTRO DE ESTUDIANTE ---");
        String id = txt("ID: "), nombre = txt("Nombre: "), email = txt("Email: ");
        gestor.registrarEstudiante(nombre, id, email, leerInt("Semestre actual (1-10): "));
        imprimir("Estudiante registrado exitosamente.");
    }

    private static void buscarEstudiante() throws EstudianteNoEncontradoException {
        Estudiante e = gestor.buscarEstudiante(txt("ID: "));
        imprimir("Resultado encontrado:\n" + e.mostrarInformacion());
    }

    private static void crearMateria() {
        imprimir("--- CREAR MATERIA ---");
        gestor.crearMateria(txt("Codigo: "), txt("Nombre: "), leerInt("Cupos maximos: "), leerInt("Creditos: "));
        imprimir("Materia creada exitosamente.");
    }

    private static void preRequisito() {
        imprimir(gestor.listarMaterias());
        gestor.agregarPreRequisito(txt("Codigo de la materia principal: "), txt("Codigo del pre-requisito: "));
        imprimir("Pre-requisito agregado correctamente.");
    }

    private static void horario(boolean reservar) throws HorarioConflictivoException {
        String aula = aula();
        int dia = leerInt("Dia (0=Domingo, ..., 6=Sabado): ");
        int hora = leerInt("Hora (0-23): ");
        int duracion = leerInt("Duracion en horas: ");
        imprimir(reservar ? gestor.reservarHorario(aula, dia, hora, duracion)
                : gestor.liberarHorario(aula, dia, hora, duracion));
    }

    private static void conexion() {
        imprimir(gestor.listarEdificios());
        gestor.agregarConexion(leerInt("Indice origen: "), leerInt("Indice destino: "), leerInt("Distancia en metros: "));
        imprimir("Conexion registrada correctamente.");
    }

    private static void ruta() {
        imprimir(gestor.listarEdificios());
        imprimir(gestor.calcularRutaMasCorta(leerInt("Indice origen: "), leerInt("Indice destino: ")));
    }

    private static void nota() throws EstudianteNoEncontradoException {
        imprimir(gestor.registrarNota(txt("ID estudiante: "), leerInt("Semestre (1-10): "),
                txt("Codigo materia: "), leerDouble("Nota (0.0-5.0): ")));
    }

    private static void navegadorReportes() {
        imprimir("1. Atras\n2. Adelante");
        int op = leerInt("Seleccione: ");
        imprimir(op == 1 ? gestor.verReporteAnterior() : op == 2 ? gestor.verReporteSiguiente() : "Opcion no valida.");
    }

    private static void batch() throws ArchivoInvalidoException {
        imprimir("--- PROCESAMIENTO MASIVO (BATCH) ---\nFormato: idEstudiante,codigoMateria");
        imprimir(gestor.procesarArchivoCsv(txt("Ruta del archivo CSV: ")));
    }

    private static String materia() {
        imprimir(gestor.listarMaterias());
        return txt("Codigo materia: ");
    }

    private static String aula() {
        imprimir(gestor.listarAulas());
        return txt("Aula: ");
    }

    /**
     * Lee texto obligatorio desde consola y evita aceptar campos vacios.
     */
    private static String txt(String msg) {
        while (true) {
            System.out.print(msg);
            String s = sc.nextLine().trim();
            if (!s.isBlank()) return s;
            imprimir("Este campo no puede estar vacio.");
        }
    }

     /**
     * Lee un numero entero y repite la pregunta si el formato no es valido.
     */
    private static int leerInt(String msg) {
        while (true) try { return Integer.parseInt(txt(msg)); }
        catch (NumberFormatException e) { imprimir("Ingrese un numero entero valido."); }
    }

      /**
     * Lee un numero decimal. Acepta coma o punto como separador decimal.
     */
    private static double leerDouble(String msg) {
        while (true) try { return Double.parseDouble(txt(msg).replace(",", ".")); }
        catch (NumberFormatException e) { imprimir("Ingrese un numero decimal valido."); }
    }

    private static void pausa() {
        System.out.print("\nPresione ENTER para continuar...");
        sc.nextLine();
    }

    private static void imprimir(String texto) {
        System.out.println(texto);
    }
}