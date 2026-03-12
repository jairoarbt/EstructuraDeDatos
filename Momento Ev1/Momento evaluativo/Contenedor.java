

public class Contenedor {

    private String id;
    private double peso;
    private String origen;
    
    public Contenedor(String id, double peso, String origen) {
        this.id = id;
        this.peso = peso;
        this.origen = origen;
    }
    public String getId() {
        return id;
    }
    public double getPeso() {
        return peso;
    }
    public String getOrigen() {
        return origen;
    }       


@Override
    public String toString() {
        return "Contenedor{" +
                "id='" + id + '\'' +
                ", peso=" + peso +
                ", origen='" + origen + '\'' +
                '}';
    }   
}