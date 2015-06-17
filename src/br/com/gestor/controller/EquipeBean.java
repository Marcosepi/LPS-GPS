package br.com.gestor.controller;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import br.com.gestor.database.dao.EquipeDao;
import br.com.gestor.database.dao.EquipeJPADao;
import br.com.gestor.database.modelo.Dev;
import br.com.gestor.database.modelo.Equipe;

@ManagedBean(name = "equipeBean")
public class EquipeBean {

	private Equipe equipe;
	private List<Equipe> equipes;
	private List<Dev> devsSemEquipe;
	
	public EquipeBean(){
		this.equipe = new Equipe();
		EquipeDao dao = new EquipeJPADao();
		equipes = dao.getList();
		devsSemEquipe = dao.getdevsSemEquipe();
		
	}
	
	public Equipe getEquipe() {
		return equipe;
	}

	public void setEquipe(Equipe equipe) {
		this.equipe = equipe;
	}

	public List<Equipe> getEquipes() {
		return equipes;
	}

	public void setEquipes(List<Equipe> equipes) {
		this.equipes = equipes;
	}

	public List<Dev> getdevsSemEquipe() {
		return devsSemEquipe;
	}

	public void setdevsSemEquipe(List<Dev> devsSemEquipe) {
		this.devsSemEquipe = devsSemEquipe;
	}
	
	public String adicionaEquipe(){
		
		EquipeDao dao = new EquipeJPADao();
		this.equipe.setAtivo(true);
		dao.save(this.equipe);
		
		return "/gerente/equipe?faces-redirect=true";
	}

	public String prepararParticipantes(){
		
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		int id = Integer.parseInt(request.getParameter("idEquipe"));
		this.equipe.setId(id);
		this.equipe.setNome(request.getParameter("nomeEquipe"));
		this.equipe.setAtivo(true);
		return "/gerente/adicionar-participantes";
		
	}
	
	public String adicionaParticipante(){
		
		for (Dev dev : equipe.getdevs()) {
			dev.setEquipe(this.equipe);
		}
			
		EquipeDao dao = new EquipeJPADao();
		this.equipe.setAtivo(true);
		dao.update(this.equipe);

		return "/gerente/equipe?faces-redirect=true";
	}
	
	public String removerEquipe(){
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		int id = Integer.parseInt(request.getParameter("idEquipe"));
		
		Equipe equipe = new Equipe();
		equipe.setId(id);
		
		EquipeDao dao = new EquipeJPADao();
		dao.desativarEquipe(equipe);
		
		return "/gerente/equipe?faces-redirect=true";
	}
	
}