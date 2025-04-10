package es.etg.dam.tfg.app.data.dao;

import es.etg.dam.tfg.app.modelo.Consola;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.List;

public class ConsolaDAO {

    private final EntityManager em;

    public ConsolaDAO(EntityManager em) {
        this.em = em;
    }

    public void insertarConsola(Consola consola) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.persist(consola);
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public Consola obtenerConsolaPorId(int id) {
        return em.find(Consola.class, id);
    }

    public List<Consola> obtenerTodasLasConsolas() {
        String jpql = "SELECT c FROM Consola c";
        return em.createQuery(jpql, Consola.class).getResultList();
    }

    public void actualizarConsola(Consola consola) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.merge(consola);
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public void eliminarConsola(int id) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            Consola consola = em.find(Consola.class, id);
            if (consola != null) {
                em.remove(consola);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }
}
