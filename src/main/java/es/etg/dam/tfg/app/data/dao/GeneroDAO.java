package es.etg.dam.tfg.app.data.dao;

import es.etg.dam.tfg.app.modelo.Genero;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;

public class GeneroDAO {

    private final EntityManager em;

    public GeneroDAO(EntityManager em) {
        this.em = em;
    }

    public boolean existeGenero(String nombre) {
        String jpql = "SELECT COUNT(g) FROM Genero g WHERE g.nombre = :nombre";
        Long count = em.createQuery(jpql, Long.class)
                .setParameter("nombre", nombre)
                .getSingleResult();
        return count > 0;
    }

    public void insertarGenero(Genero genero) {
        if (existeGenero(genero.getNombre())) {
            System.out.println("El género ya existe. No se insertó.");
            return;
        }

        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.persist(genero);  
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) transaction.rollback();
            e.printStackTrace();
        }
    }

    public Genero obtenerGeneroPorId(int id) {
        return em.find(Genero.class, id);
    }

    public List<Genero> obtenerTodosLosGeneros() {
        String jpql = "SELECT g FROM Genero g";
        return em.createQuery(jpql, Genero.class).getResultList();
    }

    public void actualizarGenero(Genero genero) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.merge(genero); 
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) transaction.rollback();
            e.printStackTrace();
        }
    }

    public void eliminarGenero(int id) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            Genero genero = em.find(Genero.class, id);
            if (genero != null) em.remove(genero); 
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) transaction.rollback();
            e.printStackTrace();
        }
    }
}
