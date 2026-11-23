package cl.duoc.amigosecreto.dao;

import cl.duoc.amigosecreto.conexion.ConexionBD;
import cl.duoc.amigosecreto.model.Adulto;
import cl.duoc.amigosecreto.model.Nino;
import cl.duoc.amigosecreto.model.Participante;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MySQLParticipanteDAO implements ParticipanteDAO {

    private static final String SELECT_ALL = "SELECT * FROM participante";
    private static final String INSERT_ADULTO = "INSERT into participante (id_tipo,nombre,edad,sugerencia_regalo,trabajador_activo) VALUES (1,?,?,?,?)";
    private static final String INSERT_NINO = "INSERT into participante (id_tipo,nombre,edad,sugerencia_regalo,nombre_tutor) VALUE (2,?,?,?,?)";

    @Override
    public List<Participante> obtenerTodos() throws PersistenciaException {
        ArrayList<Participante> participantes = new ArrayList<>();
        try(
                Connection con = ConexionBD.getConnection();
                PreparedStatement ps = con.prepareStatement(SELECT_ALL);
        ){
                ResultSet rs = ps.executeQuery();
                while (rs.next()){
                    participantes.add();
                }

        }catch(SQLException e) {
            throw new PersistenciaException(e.getMessage());
        }
        return participantes;
    }

    @Override
    public void insertar(Participante participante) throws PersistenciaException {
        String sql = (participante instanceof Adulto) ? INSERT_ADULTO : INSERT_NINO;

        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)
        ){
            ps.setString(1,participante.getNombre());
            ps.setInt(2,participante.getEdad());
            ps.setString(3,participante.getSugerenciaRegalo());
            if(participante instanceof Adulto){
                Adulto adulto = new (Adulto) participante;
                ps.setBoolean(4,adulto.isTrabajadorActivo());
            }else{
                Nino nino = (Nino) participante;
                ps.setString(4,nino.getNombreTutor());
            }
            ps.executeQuery();
        } catch (SQLException e) {
            throw new PersistenciaException(e.getMessage());
        }
    }

    @Override
    public void actualizar(Participante participante) throws PersistenciaException {

    }

    @Override
    public void eliminar(int id) throws PersistenciaException {

    }
}
