package modelo;

/**
 * Representa una solicitud de inscripcion leida desde un archivo CSV.
 */
public class SolicitudInscripcion {
    private String idEstudiante;
    private String codigoMateria;

    public SolicitudInscripcion(String idEstudiante, String codigoMateria) {
        this.idEstudiante = idEstudiante;
        this.codigoMateria = codigoMateria.toUpperCase();
    }

    public String getIdEstudiante() {
        return idEstudiante;
    }

    public String getCodigoMateria() {
        return codigoMateria;
    }
}

