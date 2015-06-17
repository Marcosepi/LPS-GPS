package br.com.gestor.database.dao;

import java.util.List;

import br.com.gestor.database.modelo.Dev;
import br.com.gestor.database.modelo.Equipe;

public interface EquipeDao extends GenericDao<Equipe>{

	public List<Equipe> getList();
	public void adicionarEquipe(Equipe equipe);
	public List<Dev> getAlunosSemEquipe();
	public void desativarEquipe(Equipe equipe);
}
