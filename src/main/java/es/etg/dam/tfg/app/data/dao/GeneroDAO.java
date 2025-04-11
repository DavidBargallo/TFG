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

    /**
     * Inserta un nuevo género en la base de datos.
     * @param genero El género a insertar.
     */
    public void insertarGenero(Genero genero) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.persist(genero);  
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    /**
     * Obtiene un género por su ID.
     * @param id El ID del género.
     * @return El género encontrado, o null si no existe.
     */
    public Genero obtenerGeneroPorId(int id) {
        return em.find(Genero.class, id);
    }

    /**
     * Obtiene todos los géneros de la base de datos.
     * @return Una lista con todos los géneros.
     */
    public List<Genero> obtenerTodosLosGeneros() {
        String jpql = "SELECT g FROM Genero g";
        return em.createQuery(jpql, Genero.class).getResultList();
    }

    /**
     * Actualiza un género en la base de datos.
     * @param genero El género a actualizar.
     */
    public void actualizarGenero(Genero genero) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.merge(genero); 
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    /**
     * Elimina un género de la base de datos.
     * @param id El ID del género a eliminar.
     */
    public void eliminarGenero(int id) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            Genero genero = em.find(Genero.class, id);
            if (genero != null) {
                em.remove(genero); 
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

