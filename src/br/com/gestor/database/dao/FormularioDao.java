package br.com.gestor.database.dao;

import java.util.List;

import br.com.gestor.database.modelo.Equipe;
import br.com.gestor.database.modelo.Formulario;
import br.com.gestor.database.modelo.Tarefa;
import br.com.gestor.database.modelo.Resposta;
import br.com.gestor.database.modelo.Usuario;

public interface FormularioDao extends GenericDao<Formulario>{

	public List<Formulario> getList();
	public Formulario formRodada();
	public List<Tarefa> getPerguntasForm(Formulario form);
	public List<Resposta> respostasCadastradas(Formulario form, Usuario usuario,Equipe equipe);
	public void realizaCalculoPontos(Formulario formulario);
	public void removerFormulario(Formulario formulario);
	public Formulario findFormularioData(int id);
	
}
