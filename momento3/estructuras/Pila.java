package estructuras;

/**
 * Pila generica implementada con nodos enlazados.
 */
public class Pila<T> {
    private Nodo<T> cima;
    private int tamanio;

    public void apilar(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        nuevo.siguiente = cima;
        cima = nuevo;
        tamanio++;
    }

    public T desapilar() {
        if (estaVacia()) {
            return null;
        }
        T dato = cima.dato;
        cima = cima.siguiente;
        tamanio--;
        return dato;
    }

    public boolean estaVacia() {
        return cima == null;
    }

    public void limpiar() {
        cima = null;
        tamanio = 0;
    }
}