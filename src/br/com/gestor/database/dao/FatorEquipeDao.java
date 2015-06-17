package br.com.gestor.database.dao;

import java.util.List;

import br.com.gestor.database.modelo.FatorEquipe;

public interface FatorEquipeDao extends GenericDao<FatorEquipe>{

	public List<FatorEquipe> getList();
	public boolean salvaFatorEquipe(FatorEquipe fator); 
	public FatorEquipe buscaFatorEquipe(FatorEquipe fEquipe);
	
}
