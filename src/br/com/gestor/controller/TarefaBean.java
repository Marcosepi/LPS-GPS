package br.com.gestor.controller;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.servlet.http.HttpServletRequest;

import br.com.gestor.database.dao.FormularioDao;
import br.com.gestor.database.dao.TarefaDao;
import br.com.gestor.database.dao.TarefaJPADao;
import br.com.gestor.database.modelo.Formulario;
import br.com.gestor.database.modelo.Tarefa;

@ManagedBean
public class TarefaBean {

	private Formulario formulario;
	private Tarefa pergunta;
	
	public TarefaBean(){
		this.formulario = new Formulario();
		this.pergunta = new Tarefa();
	}

	public Formulario getFormulario() {
		return formulario;
	}

	public void setFormulario(Formulario formulario) {
		this.formulario = formulario;
	}

	public Tarefa getPergunta() {
		return pergunta;
	}

	public void setPergunta(Tarefa pergunta) {
		this.pergunta = pergunta;
	}
	
	public List<Tarefa> getPerguntas(){
		//TODO
		Flash flash = FacesContext.getCurrentInstance().getExternalContext().getFlash();
		Integer idForm = (Integer)flash.get("formulario");
		if(idForm != null){
			TarefaDao pDao = new TarefaJPADao();
			this.formulario = new Formulario();
			this.formulario.setId(idForm);
			
			List<Tarefa> perguntasDao = pDao.getList(this.formulario);
			List<Tarefa> perguntas = new ArrayList<>();
			for (Tarefa pergunta : perguntasDao) {
				Tarefa p = new Tarefa();
				p.setDescricao(pergunta.getDescricao());
				p.setId(pergunta.getId());
				perguntas.add(p);
			}
			return perguntas;
		}else{
			return new ArrayList<Tarefa>();
		}
		
	}

	public String prepararPerguntas(){
		
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		Flash flash = FacesContext.getCurrentInstance().getExternalContext().getFlash();
		
		flash.put("formulario", Integer.parseInt(request.getParameter("idForm")));
		
		return "/professor/perguntas";
	}
	
	public String addPergunta(){
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		Flash flash = FacesContext.getCurrentInstance().getExternalContext().getFlash();
		flash.put("formulario", Integer.parseInt(request.getParameter("idForm")));
		this.formulario = new Formulario();
		this.formulario.setId(Integer.parseInt(request.getParameter("idForm")));
		this.pergunta.setFormulario(formulario);
		
		TarefaDao dao = new TarefaJPADao();
		dao.save(this.pergunta);
		
		return "";
	}
	
	public String removerPergunta(){
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		Flash flash = FacesContext.getCurrentInstance().getExternalContext().getFlash();
		flash.put("formulario", Integer.parseInt(request.getParameter("idForm")));
		
		TarefaDao dao = new TarefaJPADao();
		this.pergunta = dao.find(this.pergunta.getId());
		dao.removerPergunta(this.pergunta);
		
		return "";
	}
}
