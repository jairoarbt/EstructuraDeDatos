import java.util.HashMap;
import java.util.Map;

public class Puerto {


    private Buque[] buques;
    private Contenedor[][] patio;

    public Puerto(){
        
    this.buques = new Buque[10];
    this.patio = new Contenedor[10][10];
    }

    public boolean agregarBuque(Buque buque) {
        for (int i = 0; i < buques.length; i++) {
            if (buques[i] == null) {
                buques[i] = buque;
                System.out.println("Buque agregado correctamente: " +  buque.getNombre() +  " en en sitio " + i);
                return true;
            }
        }
        System.out.println("No se pudo agregar el buque, no hay espacio disponible.");
        return false;
    }

    public void mostrarEsquema() {
        System.out.println("Esquema del Puerto:");
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (patio[i][j] != null) {
                    System.out.print("[X] ");
                } else {
                    System.out.print(patio[i][j].toString() + "");
                }
            }
            System.out.println();
        }
        System.out.println(" 0 1 2 3 4 5 6 7 8 9 (columnas)");
        

    }

    public boolean agregarContenedor(Contenedor contenedor, int fila, int columna) {
        if (fila >= 0 && fila < patio.length && columna >= 0 && columna < patio[fila].length) {
            if (patio[fila][columna] == null) {
                patio[fila][columna] = contenedor;
                System.out.println("Contenedor agregado correctamente: " + contenedor.getId() + " en la posición (" + fila + ", " + columna + ")");
                return true;
            } else {
                System.out.println("No se pudo agregar el contenedor, la posición (" + fila + ", " + columna + ") ya está ocupada.");
                return false;
            
        } else {
            System.out.println("No se pudo agregar el contenedor, la posición (" + fila + ", " + columna + ") es inválida.");
            return false;
        }
    }
    System.out.println("Posición inválida");
}

    public double calcularPesoTotal(){
        double pesoTotal = 0;
        for (int i = 0; i < patio.length; i++) {
            for (int j = 0; j < patio[i].length; j++) {
                if (patio[i][j] != null) {
                    pesoTotal += patio[i][j].getPeso();
                }
            }
        }
        return pesoTotal;
    }

    public void listarOrigenes() {
        System.out.println("Orígenes de los contenedores en el patio:");
        HashMap <String, Integer> ConteoOrigenes = new HashMap<>();

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (patio[i][j] != null) {
                    String origen = patio[i][j].getOrigen();
                    ConteoOrigenes.put(origen, ConteoOrigenes.getOrDefault(origen, 0) + 1);
                }
            }
        }

        if (conteoOrigenes.isEm
        }
    }



    




}

