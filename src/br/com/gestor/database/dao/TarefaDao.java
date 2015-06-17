package br.com.gestor.database.dao;

import java.util.List;

import br.com.gestor.database.modelo.Sprint;
import br.com.gestor.database.modelo.Tarefa;

public interface TarefaDao extends GenericDao<Tarefa>{

	public List<Tarefa> getList(Sprint form);
	public void removerPergunta(Tarefa pergunta);
	
}
