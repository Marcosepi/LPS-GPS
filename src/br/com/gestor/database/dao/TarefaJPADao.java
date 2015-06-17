package br.com.gestor.database.dao;

import java.util.List;

import javax.persistence.Query;

import br.com.gestor.database.modelo.Formulario;
import br.com.gestor.database.modelo.Tarefa;

public class TarefaJPADao extends GenericJPADao<Tarefa> implements TarefaDao{
	
	public TarefaJPADao(){
		this.persistenceClass = Tarefa.class;
	}

	@Override
	public List<Tarefa> getList(Formulario form) {
		String sql = "select p from Pergunta p where p.formulario = :form";
		Query query = em.createQuery(sql);
		query.setParameter("form", form);
		
		return query.getResultList();
	}

	@Override
	public void removerPergunta(Tarefa pergunta) {
		String hql = "delete from Resposta r where r.pergunta = :pergunta";
		Query query = this.em.createQuery(hql);
		query.setParameter("pergunta", pergunta);
		query.executeUpdate();
		
		delete(pergunta);
	}
	
	
	
}
