package estructuras;

/**
 * Nodo basico usado internamente por lista enlazada, pila y cola.
 */
class Nodo<T> {
    T dato;
    Nodo<T> siguiente;

    Nodo(T dato) {
        this.dato = dato;
    }
}