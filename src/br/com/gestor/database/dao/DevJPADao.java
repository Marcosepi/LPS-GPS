package br.com.gestor.database.dao;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import br.com.gestor.database.modelo.Dev;
import br.com.gestor.database.modelo.Usuario;
import br.com.gestor.database.util.SenhaCriptografada;
import br.com.gestor.exceptions.ErroLoginException;

public class DevJPADao extends GenericJPADao<Dev> implements DevDao {

	public DevJPADao(){
		persistenceClass = Dev.class;
	}

	@Override
	public List<Dev> getAlunos() {
		String sql = "select a from Aluno a";
		
		Query query = em.createQuery(sql);
		
		return query.getResultList();
	}
	
}
