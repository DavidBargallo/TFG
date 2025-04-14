package es.etg.dam.tfg.app.data.dao;

import es.etg.dam.tfg.app.modelo.Compania;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;

public class CompaniaDAO {

    private final EntityManager em;

    public CompaniaDAO(EntityManager em) {
        this.em = em;
    }

    public boolean existeCompania(String nombre, String pais) {
        String jpql = "SELECT COUNT(c) FROM Compania c WHERE c.nombre = :nombre AND c.pais = :pais";
        Long count = em.createQuery(jpql, Long.class)
            .setParameter("nombre", nombre)
            .setParameter("pais", pais)
            .getSingleResult();
        return count > 0;
    }

    public void insertarCompania(Compania compania) {
        if (existeCompania(compania.getNombre(), compania.getPais())) {
            System.out.println("La compañía ya existe. No se insertó.");
            return;
        }

        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(compania);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        }
    }

    public List<Compania> obtenerTodasLasCompanias() {
        return em.createQuery("SELECT c FROM Compania c", Compania.class).getResultList();
    }
}

