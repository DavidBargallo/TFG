package es.etg.dam.tfg.app.data.dao;

import es.etg.dam.tfg.app.modelo.Videojuego;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.time.LocalDate;
import java.util.List;

public class VideojuegoDAO {

    private final EntityManager em;

    public VideojuegoDAO(EntityManager em) {
        this.em = em;
    }

    public boolean existeVideojuego(String nombre, LocalDate fechaLanzamiento) {
        String jpql = "SELECT COUNT(v) FROM Videojuego v WHERE v.nombre = :nombre AND v.fechaLanzamiento = :fecha";
        Long count = em.createQuery(jpql, Long.class)
                .setParameter("nombre", nombre)
                .setParameter("fecha", fechaLanzamiento)
                .getSingleResult();
        return count > 0;
    }

    public void insertarVideojuego(Videojuego videojuego) {
        if (existeVideojuego(videojuego.getNombre(), videojuego.getFechaLanzamiento())) {
            System.out.println("El videojuego ya existe. No se insertó.");
            return;
        }

        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.persist(videojuego);
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public Videojuego obtenerVideojuegoPorId(int id) {
        return em.find(Videojuego.class, id);
    }

    public List<Videojuego> obtenerTodosLosVideojuegos() {
        String jpql = "SELECT v FROM Videojuego v";
        return em.createQuery(jpql, Videojuego.class).getResultList();
    }

    public void actualizarVideojuego(Videojuego videojuego) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.merge(videojuego);
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public void eliminarVideojuego(int id) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            Videojuego videojuego = em.find(Videojuego.class, id);
            if (videojuego != null) {
                em.remove(videojuego);
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
