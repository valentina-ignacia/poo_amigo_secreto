package cl.duoc.amigosecreto.model;

/**
 * Participante niño o niña (0 a 17 años), acompañado por un adulto responsable.
 * <p>
 * Presupuesto del regalo: base de $5.000; si tiene 12 años o más, se incrementa en un 50%.
 */
public class Nino extends Participante {

    public static final String TIPO = "Niño";
    private static final double PRESUPUESTO_BASE = 5000;
    private static final double INCREMENTO = 0.50;
    private static final int EDAD_INCREMENTO = 12;

    private String nombreTutor;

    public Nino(String nombre, int edad, String sugerenciaRegalo, String nombreTutor) {
        super(nombre, edad, sugerenciaRegalo);
        setNombreTutor(nombreTutor);
    }

    /** @return nombre del adulto responsable. */
    public String getNombreTutor() {
        return nombreTutor;
    }

    public void setNombreTutor(String nombreTutor) {
        if (nombreTutor == null || nombreTutor.isBlank()) {
            throw new IllegalArgumentException("El nombre del adulto responsable no puede estar vacío.");
        }
        this.nombreTutor = nombreTutor.trim();
    }

    @Override
    public String getTipo() {
        return TIPO;
    }

    @Override
    protected int getEdadMinima() {
        return 0;
    }

    @Override
    protected int getEdadMaxima() {
        return 17;
    }

    @Override
    public double calcularPresupuesto() {
        double presupuesto = PRESUPUESTO_BASE;
        if (getEdad() >= EDAD_INCREMENTO) {
            presupuesto += PRESUPUESTO_BASE * INCREMENTO;
        }
        return presupuesto;
    }
}
