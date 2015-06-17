package br.com.gestor.database.dao;

import br.com.gestor.database.modelo.Gerente;

public class GerenteJPADao extends GenericJPADao<Gerente> implements
		GerenteDao {

	public GerenteJPADao() {
		persistenceClass = Gerente.class;
	}

}
