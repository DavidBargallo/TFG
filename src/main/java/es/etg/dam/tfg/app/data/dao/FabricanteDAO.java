package es.etg.dam.tfg.app.data.dao;

import es.etg.dam.tfg.app.modelo.Fabricante;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;

public class FabricanteDAO {

    private final EntityManager em;

    public FabricanteDAO(EntityManager em) {
        this.em = em;
    }

    public void insertarFabricante(Fabricante fabricante) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.persist(fabricante);
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) transaction.rollback();
            e.printStackTrace();
        }
    }

    public Fabricante obtenerFabricantePorId(int id) {
        return em.find(Fabricante.class, id);
    }

    public List<Fabricante> obtenerTodosLosFabricantes() {
        return em.createQuery("SELECT f FROM Fabricante f", Fabricante.class).getResultList();
    }

    public void actualizarFabricante(Fabricante fabricante) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.merge(fabricante);
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) transaction.rollback();
            e.printStackTrace();
        }
    }

    public void eliminarFabricante(int id) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            Fabricante fabricante = em.find(Fabricante.class, id);
            if (fabricante != null) {
                em.remove(fabricante);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) transaction.rollback();
            e.printStackTrace();
        }
    }
}

