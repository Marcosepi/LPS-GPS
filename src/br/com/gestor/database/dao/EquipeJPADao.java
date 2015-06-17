package br.com.gestor.database.dao;

import java.util.List;

import javax.persistence.PersistenceException;
import javax.persistence.Query;

import br.com.gestor.database.modelo.Dev;
import br.com.gestor.database.modelo.Equipe;

public class EquipeJPADao extends GenericJPADao<Equipe> implements EquipeDao {

	public EquipeJPADao() {
		persistenceClass = Equipe.class;
	}

	@Override
	public List<Equipe> getList() {
		String sql = "select e from Equipe e where e.ativo = true order by e.nome";

		Query query = em.createQuery(sql);

		return query.getResultList();
	}

	
	@Override
	public void adicionarEquipe(Equipe equipe) {
		try {
			this.save(equipe);
		} catch (PersistenceException ex) {
			throw new RuntimeException();	
		}
	}

	@Override
	public List<Dev> getAlunosSemEquipe() {
		String sql = "select a from Aluno a where a.equipe not in "
				+ "(select e.id from Equipe e where e.ativo = true) or a.equipe = null";
		
		Query query = em.createQuery(sql);
		
		return query.getResultList();
	}
	
	@Override
	public void desativarEquipe(Equipe equipe) {
		equipe = find(equipe.getId());
		equipe.setAtivo(false);
		update(equipe);
	}
	
}
