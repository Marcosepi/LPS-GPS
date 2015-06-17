package br.com.gestor.controller;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.persistence.PersistenceException;
import javax.servlet.http.HttpSession;

import br.com.gestor.database.dao.GerenteDao;
import br.com.gestor.database.dao.GerenteJPADao;
import br.com.gestor.database.dao.UsuarioDao;
import br.com.gestor.database.dao.UsuarioJPADao;
import br.com.gestor.database.modelo.Gerente;
import br.com.gestor.exceptions.ErroLoginException;

@ManagedBean
public class GerenteBean {

	private Gerente gerente;
	private String novaSenha;

	public GerenteBean() {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
				.getExternalContext().getSession(false);
		Object usuario;

		if (session != null
				&& (usuario = session.getAttribute("usuario")) != null
				&& (usuario instanceof Gerente)) {
			gerente = (Gerente) usuario;
		} else {
			this.gerente = new Gerente();
		}

	}

	public Gerente getgerente() {
		return gerente;
	}

	public void setgerente(Gerente gerente) {
		this.gerente = gerente;
	}

	public String getNovaSenha() {
		return novaSenha;
	}

	public void setNovaSenha(String novaSenha) {
		this.novaSenha = novaSenha;
	}

	public String alterarDados() {

		GerenteDao dao = new GerenteJPADao();

		try {

			dao.update(gerente);

		} catch (PersistenceException ex) {
			ex.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_WARN,
							"Erro ao tentar editar dados, tente novamente",
							"Erro"));
		}
		return "/gerente/home?faces-redirect=true";
	}

	public String alterarSenha() {

		UsuarioDao dao = new UsuarioJPADao();

		try {

			dao.alterarSenha(gerente, novaSenha);
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Senha alterada com sucesso",
							"Informação"));
			
		} catch (PersistenceException ex) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Erro ao tentar editar senha, tente novamente!",
							"Erro"));
			ex.printStackTrace();
		} catch (ErroLoginException e) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Senha inválida, tente novamente", "Erro"));
			e.printStackTrace();
		}
		return "";
	}

}
