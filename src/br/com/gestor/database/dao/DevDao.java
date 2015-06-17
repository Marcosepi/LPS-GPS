package br.com.gestor.database.dao;

import java.util.List;

import br.com.gestor.database.modelo.Dev;
import br.com.gestor.database.modelo.Usuario;
import br.com.gestor.exceptions.ErroLoginException;

public interface DevDao extends GenericDao<Dev>{

	public List<Dev> getAlunos();
	
/*
	public boolean validacaoLogin(Usuario aluno);
*/	
}
