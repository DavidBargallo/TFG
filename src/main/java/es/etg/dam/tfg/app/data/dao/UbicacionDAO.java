package es.etg.dam.tfg.app.data.dao;

import es.etg.dam.tfg.app.modelo.Ubicacion;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;

public class UbicacionDAO {

    private final EntityManager em;

    public UbicacionDAO(EntityManager em) {
        this.em = em;
    }

    public boolean existeUbicacion(String lugar, String zona, String indicaciones) {
        String jpql = "SELECT COUNT(u) FROM Ubicacion u WHERE u.lugar = :lugar AND u.zona = :zona AND u.indicaciones = :indicaciones";
        Long count = em.createQuery(jpql, Long.class)
            .setParameter("lugar", lugar)
            .setParameter("zona", zona)
            .setParameter("indicaciones", indicaciones)
            .getSingleResult();
        return count > 0;
    }

    public void insertarUbicacion(Ubicacion ubicacion) {
        if (existeUbicacion(ubicacion.getLugar(), ubicacion.getZona(), ubicacion.getIndicaciones())) {
            System.out.println("La ubicación ya existe. No se insertó.");
            return;
        }

        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(ubicacion);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        }
    }

    public List<Ubicacion> obtenerTodasLasUbicaciones() {
        return em.createQuery("SELECT u FROM Ubicacion u", Ubicacion.class).getResultList();
    }
}

