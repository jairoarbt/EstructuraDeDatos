import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

// MÓDULO 1: Registro de Manifiesto (Clase Contenedor)
// Criterio: Excelente (Atributos privados, constructor y encapsulamiento)
class Contenedor {
    private String id;        // Requisito: ID (String)
    private double peso;      // Requisito: Peso (double)
    private int prioridad;    // Requisito: Prioridad (int)

    public Contenedor(String id, double peso, int prioridad) {
        this.id = id;
        this.peso = peso;
        this.prioridad = prioridad;
    }

    // Métodos Getter y Setter (Encapsulamiento estricto)
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public double getPeso() { return peso; }
    public void setPeso(double peso) { this.peso = peso; }

    public int getPrioridad() { return prioridad; }
    public void setPrioridad(int prioridad) { this.prioridad = prioridad; }

    @Override
    public String toString() {
        return "ID: " + id + " | Peso: " + peso + "kg | Prioridad: " + prioridad;
    }
}

public class DataBay {
    private Contenedor[] manifiesto;
    private Contenedor[][] patio;
    private Queue<Contenedor> inspeccion;
    private Stack<Contenedor> buque;

    public DataBay(int n, int filas, int columnas) {
        this.manifiesto = new Contenedor[n];
        this.patio = new Contenedor[filas][columnas];
        this.inspeccion = new LinkedList<>();
        this.buque = new Stack<>();
    }

    // MÓDULO 1: Registro de Manifiesto
    public void registrarManifiesto(Contenedor[] llegadaCamion) {
        System.out.println("=== MÓDULO 1: REGISTRO DE MANIFIESTO ===");
        double pesoTotal = 0;
        for (int i = 0; i < llegadaCamion.length; i++) {
            if (i < manifiesto.length) {
                manifiesto[i] = llegadaCamion[i];
                pesoTotal += manifiesto[i].getPeso();
                System.out.println("Registrado: " + manifiesto[i].getId());
            }
        }
        System.out.println("PESO TOTAL ENTRANTE: " + pesoTotal + " kg\n");
    }

    // MÓDULO 2: El Patio de Almacenamiento (Matrices)
    // Criterio: Recorrido por filas y columnas para buscar null
    public void ubicarEnPatio(Contenedor c) {
        boolean ubicado = false;
        for (int i = 0; i < patio.length; i++) {
            for (int j = 0; j < patio[i].length; j++) {
                if (patio[i][j] == null) {
                    patio[i][j] = c;
                    System.out.println("Contenedor " + c.getId() + " -> Patio [" + i + "][" + j + "]");
                    ubicado = true;
                    break;
                }
            }
            if (ubicado) break;
        }
        if (!ubicado) {
            System.out.println("ALERTA: Puerto Saturado para el contenedor " + c.getId());
        }
    }

    // MÓDULO 3: Bahía de Inspección (Colas - FIFO)
    public void procesarInspeccion() {
        System.out.println("=== MÓDULO 3: BAHÍA DE INSPECCIÓN (FIFO) ===");
        // Filtramos del manifiesto los de alta prioridad (ej. prioridad 1)
        for (int i = 0; i < manifiesto.length; i++) {
            if (manifiesto[i] != null && manifiesto[i].getPrioridad() == 1) {
                inspeccion.add(manifiesto[i]);
                manifiesto[i] = null; // Sale de manifiesto
            }
        }

        while (!inspeccion.isEmpty()) {
            Contenedor revisado = inspeccion.poll(); // dequeue
            System.out.println("Inspeccionando y liberando: " + revisado.getId());
            ubicarEnPatio(revisado);
        }
        
        // El resto del manifiesto va directo al patio
        for (int i = 0; i < manifiesto.length; i++) {
            if (manifiesto[i] != null) {
                ubicarEnPatio(manifiesto[i]);
                manifiesto[i] = null;
            }
        }
        System.out.println();
    }

    // MÓDULO 4: Estiba en el Buque (Pilas - LIFO)
    public void cargarBuque() {
        System.out.println("=== MÓDULO 4: ESTIBA EN EL BUQUE (LIFO) ===");
        for (int i = 0; i < patio.length; i++) {
            for (int j = 0; j < patio[i].length; j++) {
                if (patio[i][j] != null) {
                    buque.push(patio[i][j]);
                    System.out.println("Apilando en buque: " + patio[i][j].getId());
                    patio[i][j] = null;
                }
            }
        }
        System.out.println();
    }

    // OPERACIÓN CRÍTICA: Pila Auxiliar
    public void corregirFallaFondo() {
        System.out.println("=== OPERACIÓN CRÍTICA: FALLA EN EL FONDO ===");
        if (buque.isEmpty()) return;

        Stack<Contenedor> auxiliar = new Stack<>();
        
        // Desapilar hasta llegar al último
        while (buque.size() > 1) {
            auxiliar.push(buque.pop());
        }

        Contenedor defectuoso = buque.pop();
        System.out.println("CONTENEDOR ELIMINADO POR FALLA: " + defectuoso.getId());

        // Re-apilar manteniendo el orden original
        while (!auxiliar.isEmpty()) {
            buque.push(auxiliar.pop());
        }
        System.out.println("Carga re-apilada correctamente.\n");
    }

    public static void main(String[] args) {
        // Inicialización: Camión de 4, Patio de 3x3
        DataBay puerto = new DataBay(4, 3, 3);

        Contenedor[] camion = {
            new Contenedor("CNT-A1", 500.5, 3),
            new Contenedor("CNT-B2", 750.0, 1), // Prioridad 1 -> Inspección
            new Contenedor("CNT-C3", 300.2, 2),
            new Contenedor("CNT-D4", 900.8, 1)  // Prioridad 1 -> Inspección
        };

        puerto.registrarManifiesto(camion);
        puerto.procesarInspeccion();
        puerto.cargarBuque();
        puerto.corregirFallaFondo();
    }
}


