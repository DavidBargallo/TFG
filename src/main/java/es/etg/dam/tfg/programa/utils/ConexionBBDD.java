package es.etg.dam.tfg.programa.utils;

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
            System.out.println("Conexión establecida");
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
            emf.close();
        }
    }
}


