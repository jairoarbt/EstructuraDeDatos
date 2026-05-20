package estructuras;

import java.util.*;

/**
 * Cola generica FIFO implementada manualmente con nodos enlazados.
 *
 * <p>FIFO significa primero en entrar, primero en salir. Se usa en la cola
 * de espera de materias y en el procesamiento por lotes.</p>
 */
public class Cola<T> {
    private Nodo<T> frente, fin;
    private int tamanio;

    /**
     * Inserta un dato al final de la cola.
     */
    public void encolar(T dato) {
        Nodo<T> n = new Nodo<>(dato);
        if (frente == null) frente = fin = n;
        else { fin.siguiente = n; fin = n; }
        tamanio++;
    }

      /**
     * Retira y devuelve el dato ubicado al frente de la cola.
     */
    public T desencolar() {
        if (estaVacia()) return null;
        T dato = frente.dato;
        frente = frente.siguiente;
        if (frente == null) fin = null;
        tamanio--;
        return dato;
    }

    public boolean contiene(T dato) {
        for (Nodo<T> a = frente; a != null; a = a.siguiente)
            if (Objects.equals(a.dato, dato)) return true;
        return false;
    }

     /**
     * Elimina un dato especifico de la cola, usado para retirar estudiantes en espera.
     */
    public boolean eliminar(T dato) {
        Nodo<T> actual = frente, anterior = null;
        while (actual != null) {
            if (Objects.equals(actual.dato, dato)) {
                if (anterior == null) frente = actual.siguiente;
                else anterior.siguiente = actual.siguiente;
                if (actual == fin) fin = anterior;
                tamanio--;
                return true;
            }
            anterior = actual;
            actual = actual.siguiente;
        }
        return false;
    }

    public boolean estaVacia() { return frente == null; }
    public int tamanio() { return tamanio; }
    public void limpiar() { frente = fin = null; tamanio = 0; }

    public List<T> aLista() {
        List<T> datos = new ArrayList<>();
        for (Nodo<T> a = frente; a != null; a = a.siguiente) datos.add(a.dato);
        return datos;
    }

    public void reemplazarCon(Collection<T> datos) {
        limpiar();
        datos.forEach(this::encolar);
    }
}