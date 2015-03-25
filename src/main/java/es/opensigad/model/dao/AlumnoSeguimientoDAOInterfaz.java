package es.opensigad.model.dao;

import java.util.List;

import javax.ejb.Local;

import es.opensigad.model.vo.AlumnoSeguimiento;
import es.opensigad.model.vo.AlumnoSeguimientoDatosCorreo;

@Local
public interface AlumnoSeguimientoDAOInterfaz {

	public List<AlumnoSeguimiento> getListaAlumnoSeguimiento(int idMatricula);

	public int insertarAlumnoSeguimiento(AlumnoSeguimiento alumnoSeguimiento);

	public boolean actualizarAlumnoSeguimiento(AlumnoSeguimiento alumnoSeguimiento);

	public boolean eliminarAlumnoSeguimiento(AlumnoSeguimiento alumnoSeguimiento);

	public AlumnoSeguimiento getDetalleAlumnoSeguimiento(int idSeguimiento);
	
	public AlumnoSeguimientoDatosCorreo obtenerDatosCorreo(int idMatricula);

}
