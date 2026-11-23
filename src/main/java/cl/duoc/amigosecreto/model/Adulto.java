package cl.duoc.amigosecreto.model;

/**
 * Participante adulto (18 a 120 años).
 * <p>
 * Presupuesto del regalo: base de $10.000; si es trabajador activo, se incrementa en un 100%.
 */
public class Adulto extends Participante implements TrabajadorActivo {

    public static final String TIPO = "Adulto";
    private static final double PRESUPUESTO_BASE = 10000;
    private static final double INCREMENTO = 1.00;

    private boolean trabajadorActivo;

    public Adulto(String nombre, int edad, String sugerenciaRegalo, boolean trabajadorActivo) {
        super(nombre, edad, sugerenciaRegalo);
        this.trabajadorActivo = trabajadorActivo;
    }

    @Override
    public boolean isTrabajadorActivo() {
        return trabajadorActivo;
    }

    @Override
    public void setTrabajadorActivo(boolean trabajadorActivo) {
        this.trabajadorActivo = trabajadorActivo;
    }

    @Override
    public String getTipo() {
        return TIPO;
    }

    @Override
    protected int getEdadMinima() {
        return 18;
    }

    @Override
    protected int getEdadMaxima() {
        return 120;
    }

    @Override
    public double calcularPresupuesto() {
        double presupuesto = PRESUPUESTO_BASE;
        if (trabajadorActivo) {
            presupuesto += PRESUPUESTO_BASE * INCREMENTO;
        }
        return presupuesto;
    }
}
