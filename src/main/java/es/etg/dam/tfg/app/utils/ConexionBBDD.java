package es.etg.dam.tfg.app.utils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class ConexionBBDD {
    
    private EntityManagerFactory emf;
    private EntityManager em;

    public ConexionBBDD() {
        emf = Persistence.createEntityManagerFactory("tfgPU");  
    }

    public void conectar() {
        try {
            em.getTransaction().begin();
            System.out.println("Conexión a Neon establecida con éxito");
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
            emf.close();
        }
    }
}


