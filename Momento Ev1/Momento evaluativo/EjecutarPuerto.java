import java.util.InputMismatchException;
import java.util.Scanner;

public class EjecutarPuerto{
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Puerto puertoJH = new Puerto();
        int opcion = 0;
        do {
            System.out.println(" ---- MENU INTERACCION PUERTO JH ----");
            System.out.println("1. Agregar Buque");
            System.out.println("2. Mostrar Esquema");
            System.out.println("3. Agregar Contenedor");
            System.out.println("4. Calcular Peso Total");
            System.out.println("5. Listar Orígenes");
            System.out.println("6. Salir");
            System.out.print("Seleccione una opción: ");
            try {
                opcion = scanner.nextInt();
                switch (opcion) {
                    case 1:
                        // Lógica para agregar buque
                        break;
                    case 2:
                        puertoJH.mostrarEsquema();
                        break;
                    case 3:
                        // Lógica para agregar contenedor
                        break;
                    case 4:
                        System.out.println("Peso total del patio: " + puertoJH.calcularPesoTotal());
                        break;
                    case 5:
                        puertoJH.listarOrigenes();
                        break;
                    case 6:
                        System.out.println("Saliendo del programa.");
                        break;
                    default:
                        System.out.println("Opción no válida. Por favor, seleccione una opción válida.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Error: Por favor, ingrese un número válido.");
                scanner.nextLine(); // Limpiar el buffer de entrada
            }
        } while (opcion != 6);

        scanner.close();
    }
}
