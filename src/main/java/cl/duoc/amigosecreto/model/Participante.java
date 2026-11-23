package cl.duoc.amigosecreto.model;

/**
 * Datos comunes de todos los participantes del amigo secreto.
 * <p>
 * El identificador (id) vale 0 mientras el participante no ha sido almacenado.
 * idAmigoSecreto guarda el id del participante al que le toca regalar
 * (0 mientras no se ha realizado el sorteo).
 * Los métodos set rechazan los datos inválidos con una IllegalArgumentException
 * cuyo mensaje explica el motivo.
 */
public abstract class Participante {

    private int id;
    private String nombre;
    private int edad;
    private String sugerenciaRegalo;
    private int idAmigoSecreto;

    protected Participante(String nombre, int edad, String sugerenciaRegalo) {
        setNombre(nombre);
        setEdad(edad);
        setSugerenciaRegalo(sugerenciaRegalo);
    }

    /** @return identificador (0 si aún no ha sido almacenado). */
    public int getId() {
        return id;
    }

    /** @param id identificador asignado al almacenar el participante (no negativo). */
    public void setId(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("El identificador no puede ser negativo.");
        }
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío.");
        }
        this.nombre = nombre.trim();
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        if (edad < getEdadMinima() || edad > getEdadMaxima()) {
            throw new IllegalArgumentException("La edad de un participante de tipo " + getTipo()
                    + " debe estar entre " + getEdadMinima() + " y " + getEdadMaxima() + " años.");
        }
        this.edad = edad;
    }

    /** @return sugerencia de regalo (texto vacío si no se indicó). */
    public String getSugerenciaRegalo() {
        return sugerenciaRegalo;
    }

    /** @param sugerenciaRegalo idea de regalo; es opcional (null se guarda como texto vacío). */
    public void setSugerenciaRegalo(String sugerenciaRegalo) {
        this.sugerenciaRegalo = sugerenciaRegalo == null ? "" : sugerenciaRegalo.trim();
    }

    /** @return id del participante al que le toca regalar (0 si no hay sorteo). */
    public int getIdAmigoSecreto() {
        return idAmigoSecreto;
    }

    /** @param idAmigoSecreto id del participante al que le toca regalar (0 = sin asignar). */
    public void setIdAmigoSecreto(int idAmigoSecreto) {
        if (idAmigoSecreto < 0) {
            throw new IllegalArgumentException("El amigo secreto no puede tener un identificador negativo.");
        }
        this.idAmigoSecreto = idAmigoSecreto;
    }

    /** @return true si el participante ya tiene amigo secreto asignado. */
    public boolean tieneAmigoSecreto() {
        return idAmigoSecreto > 0;
    }

    /** @return tipo del participante: "Adulto" o "Niño". */
    public abstract String getTipo();

    /** @return edad mínima permitida para el tipo. */
    protected abstract int getEdadMinima();

    /** @return edad máxima permitida para el tipo. */
    protected abstract int getEdadMaxima();

    /** @return presupuesto del regalo que este participante debe hacer, en pesos. */
    public abstract double calcularPresupuesto();
}
