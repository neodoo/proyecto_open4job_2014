package es.opensigad.model.dao;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.JMSDestinationDefinition;
import javax.jms.JMSDestinationDefinitions;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import es.opensigad.model.vo.AlumnoSeguimiento;
import es.opensigad.model.vo.AlumnoSeguimientoDatosCorreo;



/**
 * Definition of the two JMS destinations used by the quickstart
 * (one queue and one topic).
 */
@JMSDestinationDefinitions(
        value =  {
                @JMSDestinationDefinition(
                        name = "java:/queue/NOTIFICACIONESCORREOMDBQueue",
                        interfaceName = "javax.jms.Queue"
                        //destinationName = "SeguimientoMDBQueue"
                )
        }
)


@Stateless
public class AlumnoSeguimientoDAO implements AlumnoSeguimientoDAOInterfaz {
	
	public static final Logger logger = Logger.getLogger(AlumnoSeguimientoDAO.class.getName());

	@PersistenceContext(unitName = "opensigadUnit")
	private EntityManager em;

	public EntityManager getEm() {
		return em;
	}

	public void setEm(EntityManager em) {
		this.em = em;
	}

	public AlumnoSeguimientoDAO() {
		
	}

	@Inject
	private JMSContext context;

	@Resource(lookup = "java:/queue/NOTIFICACIONESCORREOMDBQueue")
	private Queue queue;
	
	
	// Listar seguimientos de un alumno
	public List<AlumnoSeguimiento> getListaAlumnoSeguimiento(int idMatricula) {

		List<AlumnoSeguimiento> seguimientos = null;

		try {

			String query = "SELECT alumnoSeguimiento "
					+ " FROM AlumnoSeguimiento alumnoSeguimiento "
					+ " WHERE alumnoSeguimiento.alumnoMatricula.id = :pidMatricula";


			seguimientos = em.createQuery(query).setParameter("pidMatricula", idMatricula).getResultList();
			
			
			logger.log(Level.INFO,"AlumnoSeguimientoDAO.getListaAlumnoSeguimiento: OK.");

		} catch (Exception e) {

			logger.log(Level.SEVERE,"AlumnoSeguimientoDAO.getListaAlumnoSeguimiento: ERROR. " + e.getMessage());

		}
		   System.out.println("2. Sent ObjectMessage to the Queue");

		return seguimientos;

	}

	// Datos de un seguimiento
	public AlumnoSeguimiento getDetalleAlumnoSeguimiento(int idSeguimiento) {

		AlumnoSeguimiento seguimiento = null;

		try {
			
			String query = "from AlumnoSeguimiento aseg where aseg.id =" + idSeguimiento;
			
			seguimiento = (AlumnoSeguimiento) em.createQuery(query).getSingleResult();

			logger.log(Level.INFO, "AlumnoSeguimientoDAO.getDetalleAlumnoSeguimiento: OK.");

		} catch (Exception e) {
			
			logger.log(Level.SEVERE, "AlumnoSeguimientoDAO.getDetalleAlumnoSeguimiento: ERROR. " + e.getMessage());

		}

		return seguimiento;

	}

	// Insertar alumno-seguimiento
	public int insertarAlumnoSeguimiento(AlumnoSeguimiento alumnoSeguimiento) {

		int id = 0;

		try {

			//em.persist(alumnoSeguimiento);

			id = alumnoSeguimiento.getId();
			
			int idMatricula = 1;
			AlumnoSeguimientoDatosCorreo asdc = this.obtenerDatosCorreo(idMatricula);
			
			ObjectMessage objMsg = context.createObjectMessage(asdc);
			context.createProducer().send(queue, objMsg);
		
			logger.log(Level.INFO, "AlumnoSeguimientoDAO.insertarAlumnoSeguimiento: OK.");

		} catch (Exception e) {

			logger.log(Level.SEVERE, "AlumnoSeguimientoDAO.insertarAlumnoSeguimiento: ERROR. " + e.getMessage());
		}

		return id;

	}

	// Actualizar alumnos
	public boolean actualizarAlumnoSeguimiento(AlumnoSeguimiento alumnoSeguimiento) {

		boolean estado = false;

		try {

			em.merge(alumnoSeguimiento);

			estado = true;

			logger.log(Level.INFO, "AlumnoSeguimientoDAO.actualizarAlumnoSeguimiento: OK.");

		} catch (Exception e) {

			logger.log(Level.SEVERE, "AlumnoSeguimientoDAO.actualizarAlumnoSeguimiento: ERROR. " + e.getMessage());
		}

		return estado;

	}

	// Eliminar alumno-seguimiento
	public boolean eliminarAlumnoSeguimiento(AlumnoSeguimiento alumnoSeguimiento) {

		boolean estado = false;

		try {

			em.remove(em.merge(alumnoSeguimiento));

			estado = true;

			logger.log(Level.INFO, "AlumnoSeguimientoDAO.eliminarAlumnoSeguimiento: OK.");

		} catch (Exception e) {

			logger.log(Level.SEVERE, "AlumnoSeguimientoDAO.eliminarAlumnoSeguimiento: ERROR. " + e.getMessage());
		}

		return estado;

	}
	
	//@EJB
	//private AlumnoDAO alumnoDAO;
	
	//@EJB
	//private AlumnoMatriculaDAO alumnoMatriculaDAO;
	
	
	public AlumnoSeguimientoDatosCorreo obtenerDatosCorreo (int idMatricula){

		AlumnoSeguimientoDatosCorreo alumnoSeguimientoDatosCorreo = new AlumnoSeguimientoDatosCorreo();
		
		alumnoSeguimientoDatosCorreo.setAsunto("Asunto Correo");
		alumnoSeguimientoDatosCorreo.setMensaje("Mensaje Correo");
		alumnoSeguimientoDatosCorreo.setEmail("alg.pruebas@gmail.com");
		
		return alumnoSeguimientoDatosCorreo;
	}

}