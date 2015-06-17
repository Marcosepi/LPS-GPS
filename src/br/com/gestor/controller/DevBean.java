package br.com.gestor.controller;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.persistence.PersistenceException;
import javax.servlet.http.HttpSession;

import br.com.gestor.database.dao.DevDao;
import br.com.gestor.database.dao.DevJPADao;
import br.com.gestor.database.dao.UsuarioDao;
import br.com.gestor.database.dao.UsuarioJPADao;
import br.com.gestor.database.modelo.Dev;
import br.com.gestor.database.util.SenhaCriptografada;
import br.com.gestor.exceptions.ErroLoginException;

@RequestScoped
@ManagedBean(name = "devBean")
public class DevBean {

	private Dev dev;
	private String novaSenha;

	public DevBean() {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
				.getExternalContext().getSession(false);
		Object usuario;
		if (session != null &&  (usuario = session.getAttribute("usuario")) != null && (usuario instanceof Dev)) {
			this.dev = (Dev) usuario;
		} else {
			this.dev = new Dev();
		}
	}

	public Dev getdev() {
		return dev;
	}

	public void setdev(Dev dev) {
		this.dev = dev;
	}

	public String getNovaSenha() {
		return novaSenha;
	}

	public void setNovaSenha(String novaSenha) {
		this.novaSenha = novaSenha;
	}

	public String cadastrardev() {

		DevDao devDao = new DevJPADao();
		try {
			devDao.save(this.dev);
			devDao.commit();
			System.err.println("Cadastrando dev");
			FacesContext.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_INFO,
									"dev Cadastrado com Sucesso",
									"dev Cadastrado"));
		} catch (PersistenceException ex) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_WARN,
							"Matrí­cula ou e-mail já cadastrados no sistema", "Aviso"));
			ex.printStackTrace();
		}
		return "";
	}

	public String alterarDados() {
		DevDao dao = new DevJPADao();

		try {

			dao.update(dev);

		} catch (PersistenceException ex) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_WARN,
							"Erro ao tentar editar dados, tente novamente",
							"Erro"));
			ex.printStackTrace();
		}


		return "/dev/home?faces-redirect=true";
	}

	public String alterarSenha() {

		UsuarioDao dao = new UsuarioJPADao();
		try {
			dao.alterarSenha(dev, novaSenha);
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Senha alterada com sucesso!",
							"Sucesso"));
			
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
