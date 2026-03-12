

public class Suministro {

    private String id; 
    private int nivelEnergia;
    private String prioridad; 

      public Sumistros(String id, int nivelEnergia, String Prioridad) {
        this.id = id;
        this.nivelEnergia = nivelEnergia;
        this.Prioridad = Prioridad;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getNivelEnergia() {
        return nivelEnergia;
    }

    public void setNivelEnergia(int nivelEnergia) {
        this.nivelEnergia = nivelEnergia;
    }

    public String getPrioridad() {
        return Prioridad;
    }

    public void setPrioridad(String prioridad) {
        Prioridad = prioridad;
    }

    @Override
    public String toString() {  
        return "Sumistros{" +
                "id='" + id + '\'' +
                ", nivelEnergia=" + nivelEnergia +
                ", Prioridad='" + Prioridad + '\'' +
                '}';
    }





    
}