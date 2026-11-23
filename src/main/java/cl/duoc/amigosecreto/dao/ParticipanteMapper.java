package cl.duoc.amigosecreto.dao;

import cl.duoc.amigosecreto.model.Adulto;
import cl.duoc.amigosecreto.model.Nino;
import cl.duoc.amigosecreto.model.Participante;

import java.sql.ResultSet;

public class ParticipanteMapper {
    public static Participante mappear(ResultSet ts) {
        Participante participante;
        int id = rs.getInt("id");
        String nombre = rs.getString("nombre");
        int edad = rs.getInt("edad");
        String sugerenciaRegalo = rs.getString("sugerencia_regalo");
        int idAmigoSecreto = rs.getInt("id_amigo_secreto");


        if(rs.getInt("id_tipo") == 1 ) {
            boolean trabajadorActivo = rs.getBoolean("trabajador_activo");
            participante = new Adulto(nombre,edad,sugerenciaRegalo,trabajadorActivo);

        }else{
            String nombreTutor = rs.getString("nombre_tutor");
            participante = new Nino(nombre,edad,sugerenciaRegalo,nombreTutor);
        }

        participante.setId(id);

        return participante;
    }
}
