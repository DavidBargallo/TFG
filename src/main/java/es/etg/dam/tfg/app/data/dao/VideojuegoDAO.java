package es.etg.dam.tfg.app.data.dao;

import es.etg.dam.tfg.app.modelo.Videojuego;
import es.etg.dam.tfg.app.modelo.Genero;
import es.etg.dam.tfg.app.modelo.Consola;
import es.etg.dam.tfg.app.modelo.Estado;
import es.etg.dam.tfg.app.modelo.Ubicacion;
import es.etg.dam.tfg.app.modelo.Compania;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.List;

public class VideojuegoDAO {

    private final EntityManager em;

    public VideojuegoDAO(EntityManager em) {
        this.em = em;
    }

    public void insertarVideojuego(Videojuego videojuego) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();

            // Persistir el Videojuego y sus relaciones muchos a muchos se manejan automáticamente
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

            // Usamos merge para actualizar
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
