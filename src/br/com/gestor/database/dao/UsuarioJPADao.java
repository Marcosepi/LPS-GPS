package br.com.gestor.database.dao;

import javax.persistence.PersistenceException;
import javax.persistence.Query;

import br.com.gestor.database.modelo.Usuario;
import br.com.gestor.exceptions.ErroLoginException;

public class UsuarioJPADao extends GenericJPADao<Usuario> implements UsuarioDao {

	public UsuarioJPADao() {
		persistenceClass = Usuario.class;
	}

	@Override
	public Usuario loginUsuario(Usuario usuario) {

		String sql = "select u from Usuario u where (u.login = :login or u.email = :email) and u.senha = :senha";

		Query query = em.createQuery(sql);
		query.setParameter("login", usuario.getLogin());
		query.setParameter("email", usuario.getEmail());
		query.setParameter("senha", usuario.getSenha());

		try {
			
			Usuario userConsulta = (Usuario) query.getSingleResult();
			if(userConsulta == null){
				return null;
			}else{
				return userConsulta;
			}
			
		} catch (PersistenceException ex) {
			ex.printStackTrace();
			throw new ErroLoginException("Usuário ou senha inválidos.");
		}

	}

	@Override
	public void alterarSenha(Usuario usuario, String senhaNova)
			throws ErroLoginException {
		
		Usuario user = this.loginUsuario(usuario);
		user.setSenha(senhaNova);
		this.update(user);
		
	}
	
}
