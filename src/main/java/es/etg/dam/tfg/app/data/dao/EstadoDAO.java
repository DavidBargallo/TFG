package es.etg.dam.tfg.app.data.dao;

import es.etg.dam.tfg.app.modelo.Estado;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;

public class EstadoDAO {

    private final EntityManager em;

    public EstadoDAO(EntityManager em) {
        this.em = em;
    }

    public boolean existeEstado(String nombreEstado) {
        String jpql = "SELECT COUNT(e) FROM Estado e WHERE e.nombreEstado = :nombre";
        Long count = em.createQuery(jpql, Long.class)
            .setParameter("nombre", nombreEstado)
            .getSingleResult();
        return count > 0;
    }

    public void insertarEstado(Estado estado) {
        if (existeEstado(estado.getNombreEstado())) {
            System.out.println("El estado ya existe. No se insertó.");
            return;
        }

        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(estado);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        }
    }

    public List<Estado> obtenerTodosLosEstados() {
        return em.createQuery("SELECT e FROM Estado e", Estado.class).getResultList();
    }
}

