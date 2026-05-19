package util;

public class OperacionSimple implements OperacionDeshacer {
    private String descripcion;
    private Runnable accionDeshacer;
    private Runnable accionRehacer;

    public OperacionSimple(String descripcion, Runnable accionDeshacer, Runnable accionRehacer) {
        this.descripcion = descripcion;
        this.accionDeshacer = accionDeshacer;
        this.accionRehacer = accionRehacer;
    }

    @Override
    public String descripcion() {
        return descripcion;
    }

    @Override
    public void deshacer() {
        accionDeshacer.run();
    }

    @Override
    public void rehacer() {
        accionRehacer.run();
    }
}