package estructuras;

import java.util.*;

/**
 * Lista enlazada simple generica implementada manualmente con nodos.
 *
 * <p>Se usa para pre-requisitos de materias e historial academico.</p>
 */
public class ListaEnlazada<T> implements Iterable<T> {
    private Nodo<T> cabeza, cola;
    private int tamanio;

     /**
     * Agrega un dato al final de la lista.
     */
    public void agregar(T dato) {
        Nodo<T> n = new Nodo<>(dato);
        if (cabeza == null) cabeza = cola = n;
        else { cola.siguiente = n; cola = n; }
        tamanio++;
    }

    /**
     * Elimina la primera aparicion del dato indicado.
     */
    public boolean eliminar(T dato) {
        Nodo<T> actual = cabeza, anterior = null;
        while (actual != null) {
            if (Objects.equals(actual.dato, dato)) {
                if (anterior == null) cabeza = actual.siguiente;
                else anterior.siguiente = actual.siguiente;
                if (actual == cola) cola = anterior;
                tamanio--;
                return true;
            }
            anterior = actual;
            actual = actual.siguiente;
        }
        return false;
    }

    public boolean contiene(T dato) {
        for (T e : this) if (Objects.equals(e, dato)) return true;
        return false;
    }

    public int tamanio() { return tamanio; }
    public boolean estaVacia() { return tamanio == 0; }
    public void limpiar() { cabeza = cola = null; tamanio = 0; }

    /**
     * Devuelve una copia de los datos en una lista de Java.
     */
    public List<T> aLista() {
        List<T> datos = new ArrayList<>();
        for (T e : this) datos.add(e);
        return datos;
    }

    public void reemplazarCon(Collection<T> datos) {
        limpiar();
        datos.forEach(this::agregar);
    }

    public String unir(String separador) {
        StringJoiner joiner = new StringJoiner(separador);
        for (T e : this) joiner.add(String.valueOf(e));
        return joiner.toString();
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {
            private Nodo<T> actual = cabeza;
            public boolean hasNext() { return actual != null; }
            public T next() {
                if (actual == null) throw new NoSuchElementException();
                T dato = actual.dato;
                actual = actual.siguiente;
                return dato;
            }
        };
    }
}