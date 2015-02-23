package es.opensigad.model.dao;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import es.opensigad.model.vo.AlumnoSeguimiento;

public class AlumnoSeguimientoDAO implements AlumnoSeguimientoDAOInterfaz,
		Serializable {

	private static final long serialVersionUID = 1L;

	public static final Logger logger = Logger
			.getLogger(AlumnoSeguimiento.class.getName());

	public EntityManagerFactory emf = null;
	public EntityManager em = null;

	public AlumnoSeguimientoDAO() {
		emf = Persistence.createEntityManagerFactory("persistenceUnit");
		em = emf.createEntityManager();
	}

	// Listar seguimientos de un alumno
	public List<AlumnoSeguimiento> getListaAlumnoSeguimiento(int pidMatricula) {

		List<AlumnoSeguimiento> seguimientos = null;

		try {

			String query = "SELECT alumnoSeguimiento FROM AlumnoSeguimiento alumnoSeguimiento "
					+ " WHERE alumnoSeguimiento.alumnoMatricula.id = :pidMatricula";

			em.getTransaction().begin();

			System.out
					.println("<h3>Listado de seguimientos (SELECT)</h3><br />");

			seguimientos = em.createQuery(query)
					.setParameter("pidMatricula", pidMatricula).getResultList();
			for (AlumnoSeguimiento seguimientoFila : seguimientos) {
				System.out.println("<h3>AlumnoSeguimiento (id = "
						+ seguimientoFila.getId() + ", idMatricula = "
						+ seguimientoFila.getAlumnoMatricula().getId()
						+ ", fecha = " + seguimientoFila.getFecha()
						+ ", sesion = " + seguimientoFila.getSesion()
						+ ", idMateria = "
						+ seguimientoFila.getEnsenanzaMateria().getMateria()
						+ ", tipo = " + seguimientoFila.getTipo()
						+ ", justificante = "
						+ seguimientoFila.getJustificante() + ")</h3><br />");
			}

			em.getTransaction().commit();

		} catch (Exception e) {

		}
		em.close();
		return seguimientos;

	}

	// Datos de una falta
	public AlumnoSeguimiento getDetalleFalta(long pnumId) {

		em.getTransaction().begin();

		System.out.println("Detalles falta: \n");

		String query = "from AlumnoSeguimiento aseg where aseg.id =" + pnumId;
		List<AlumnoSeguimiento> faltas = em.createQuery(query).getResultList();

		for (AlumnoSeguimiento faltaFila : faltas) {
			System.out.println("id falta: " + faltaFila.getId());
			System.out.println("id matricula alumno: "
					+ faltaFila.getAlumnoMatricula().getId());
			System.out.println("justificante: " + faltaFila.getJustificante());
			System.out.println("sesion: " + faltaFila.getSesion());
			System.out
					.println("observaciones: " + faltaFila.getObservaciones());
		}

		em.getTransaction().commit();
		em.close();
		return (AlumnoSeguimiento) faltas;
	}

	// Insertar/Actualizar alumno-seguimiento
	public int actualizarAlumnoSeguimiento(AlumnoSeguimiento alumnoSeguimiento) {
		int id = 0;

		em.getTransaction().begin();
		em.merge(alumnoSeguimiento);

		try {
			em.getTransaction().commit();
			id = alumnoSeguimiento.getId();
			logger.log(Level.SEVERE,
					"AlumnoSeguimientoDAO.actualizarAlumnoSeguimiento: OK.");
		} catch (Exception e) {
			em.getTransaction().rollback();
			logger.log(Level.SEVERE,
					"AlumnoSeguimientoDAO.actualizarAlumnoSeguimiento: ERROR. "
							+ e.getMessage());
		}

		em.close();
		return id;
	}

	// Eliminar alumno-seguimiento
	public int eliminarAlumnoSeguimiento(AlumnoSeguimiento alumnoSeguimiento) {
		int ok = 0;

		em.getTransaction().begin();
		em.remove(alumnoSeguimiento);

		try {
			em.getTransaction().commit();
			ok = 1;
			logger.log(Level.SEVERE,
					"AlumnoSeguimientoDAO.eliminarAlumnoSeguimiento: OK.");
		} catch (Exception e) {
			em.getTransaction().rollback();
			logger.log(Level.SEVERE,
					"AlumnoSeguimientoDAO.eliminarAlumnoSeguimiento: ERROR. "
							+ e.getMessage());
		}

		em.close();
		return ok;
	}
}
