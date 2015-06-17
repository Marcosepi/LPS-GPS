package br.com.gestor.database.dao;

import br.com.gestor.database.modelo.Usuario;
import br.com.gestor.exceptions.ErroLoginException;

public interface UsuarioDao extends GenericDao<Usuario>{
	
	public Usuario loginUsuario(Usuario usuario);
	public void alterarSenha(Usuario usuario, String senhaNova) throws ErroLoginException; 
	
}
