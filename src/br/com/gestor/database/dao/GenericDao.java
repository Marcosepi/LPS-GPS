package br.com.gestor.database.dao;

public interface GenericDao<T> {

	public void save(T entity);
	public void update(T entity);
	public T find(Object id);
	public void delete(T entity);
	public void begin();
	public void commit();
	public void rollback();
	public void close();
	public boolean transactionActive();
	
}
