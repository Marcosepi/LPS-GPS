package br.com.gestor.database.util;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JPAUtil {

	private static final EntityManagerFactory emf = Persistence
			.createEntityManagerFactory("dev");
	private static ThreadLocal<EntityManager> ems = new ThreadLocal<EntityManager>();

	public static EntityManager createEntityManager() {
		System.err.println("Criando EntityManager\n\n");
		EntityManager em = ems.get();
		if (em == null) {
			em = emf.createEntityManager();
			ems.set(em);
		}

		return em;
	}

	public static void close() {
		System.out.println("Close EntityManager");
		EntityManager em = ems.get();

		if (em != null) {

			if (em.getTransaction().isActive()) {
				em.getTransaction().commit();
			}

			em.close();
			ems.set(null);

		}
	}

	public static void beginTransaction() {
		createEntityManager().getTransaction().begin();
	}

	public static void commit() {
		EntityTransaction tx = createEntityManager().getTransaction();
		if (tx.isActive()) {
			tx.commit();
		}
	}

	public static void rollback() {
		EntityTransaction tx = createEntityManager().getTransaction();
		if (tx.isActive()) {
			tx.rollback();
		}
	}

}
