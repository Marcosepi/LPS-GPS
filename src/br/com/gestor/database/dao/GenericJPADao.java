package br.com.gestor.database.dao;

import javax.persistence.EntityManager;

import br.com.gestor.database.util.JPAUtil;

public class GenericJPADao<T> implements GenericDao<T> {

	protected EntityManager em;
	protected Class<T> persistenceClass;
	
	public GenericJPADao(){
		em = JPAUtil.createEntityManager();
	}
	
	@Override
	public void save(T entity) {
		em.persist(entity);
	}

	@Override
	public void update(T entity) {
		em.merge(entity);
	}

	@Override
	public T find(Object id) {
		return em.find(persistenceClass, id);
	}

	@Override
	public void delete(T entity) {
		em.refresh(entity);
		em.remove(entity);
	}

	@Override
	public void begin() {
		JPAUtil.beginTransaction();
	}

	@Override
	public void commit() {
		JPAUtil.commit();
	}

	@Override
	public void rollback() {
		JPAUtil.rollback();
	}

	@Override
	public void close() {
		JPAUtil.close();
	}
	
	@Override
	public boolean transactionActive() {
		return em.getTransaction().isActive();
	}

	
	
}
