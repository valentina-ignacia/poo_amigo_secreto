package cl.duoc.amigosecreto.dao;

import cl.duoc.amigosecreto.model.Participante;

import java.util.List;

/**
 * Operaciones de almacenamiento que utiliza la ventana.
 * <p>
 * Si una operación no puede completarse, debe lanzar PersistenciaException con un
 * mensaje que indique qué operación falló; la ventana muestra ese mensaje en el
 * área de mensajes.
 */
public interface ParticipanteDAO {

    /**
     * @return todos los participantes almacenados, cada uno como objeto de su tipo
     *         (Adulto o Nino), con todos sus datos, su identificador y el id de su
     *         amigo secreto (0 si no tiene).
     */
    List<Participante> obtenerTodos() throws PersistenciaException;

    /**
     * Guarda un participante nuevo con todos sus datos, incluidos los propios de su tipo.
     * El identificador lo genera la base de datos. Un participante nuevo no tiene
     * amigo secreto asignado.
     */
    void insertar(Participante participante) throws PersistenciaException;

    /**
     * Modifica todos los datos del participante que tiene el mismo identificador,
     * incluidos los propios de su tipo y el id de su amigo secreto
     * (0 = sin asignar). El sorteo usa esta operación para guardar las asignaciones.
     */
    void actualizar(Participante participante) throws PersistenciaException;

    /**
     * Elimina el participante con el identificador indicado.
     */
    void eliminar(int id) throws PersistenciaException;
}
