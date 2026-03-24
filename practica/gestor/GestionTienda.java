publ ic class GestionTienda {
    public static void main(String[] args) {
        // 1. Crear el arreglo de objetos
        Videojuego[] inventario = new Videojuego[3];

        // 2. Llenar el arreglo (usando el constructor)
        inventario[0] = new Videojuego("Super Mario Odyssey", 45.0, 5);
        inventario[1] = new Videojuego("The Witcher 3", 30.0, 12);
        inventario[2] = new Videojuego("Elden Ring", 60.0, 8);

        // 3. Mostrar la información (usando getters y bucle for)
        System.out.println("--- INVENTARIO DE LA TIENDA ---");
        for (int i = 0; i < inventario.length; i++) {
            System.out.println("Juego: " + inventario[i].getTitulo() + 
                               " | Precio: $" + inventario[i].getPrecio() + 
                               " | Stock: " + inventario[i].getStock());
        }
    }
}


