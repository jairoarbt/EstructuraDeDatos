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
                    System.out.print("[ ] ");
                }
            }
            System.out.println();
        }
        System.out.println(" 0 1 2 3 4 5 6 7 8 9 (columnas)");
        

    }

    public boolean agregarContenedor(Contenedor contenedor, int fila, int columna) {
        if(columna < 0 || columna >= 10){
            System.out.println(" Error columna invalida");
            return false;
        }

        for(fila = 9; fila >= 0; fila--){
            if(patio[fila][columna] == null){
                patio[fila][columna] = contenedor;
                System.out.println("Contenedor agregado correctamente en la posición (" + fila + ", " + columna + ")");
                return true;
            }
        }
            
    
    System.out.println("La columna " + columna + " está llena.");
    return false;
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

        if (ConteoOrigenes.isEmpty()){
            System.out.println("No hay contenedores en el patio.");
        }else {
            for (Map.Entry<String, Integer> entry : ConteoOrigenes.entrySet()) {
                System.out.println("Origen: " + entry.getKey() + ", Cantidad: " + entry.getValue());
            }
        }
    }



}

