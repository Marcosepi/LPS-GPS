package br.com.gestor.controller;

import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.persistence.PersistenceException;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.gestor.database.dao.sprintDao;
import br.com.gestor.database.dao.sprintJPADao;
import br.com.gestor.database.modelo.sprint;
import br.com.gestor.database.util.ReportUtil;

@ManagedBean(name = "sprintBean")
public class SprintBean {

	private sprint sprint;

	public SprintBean() {
		this.sprint = new sprint();
	}

	public sprint getsprint() {
		return sprint;
	}

	public void setsprint(sprint sprint) {
		this.sprint = sprint;
	}

	public String addsprint() {

		sprintDao dao = new sprintJPADao();
		dao.save(sprint);

		return "/gerente/sprint?faces-redirect=true";
	}

	public List<sprint> getsprints() {
		sprintDao dao = new sprintJPADao();
		return dao.getList();
	}

	public String removersprint(){

		HttpServletRequest request = (HttpServletRequest) FacesContext
				.getCurrentInstance().getExternalContext().getRequest();
		int id = Integer.parseInt(request.getParameter("idForm"));
		sprintDao dao = new sprintJPADao();
		this.sprint = dao.find(id);
		dao.removersprint(this.sprint);
		this.sprint = new sprint();
		return "?faces-redirect=true";
	}

	public String preparartarefas() {

		HttpServletRequest request = (HttpServletRequest) FacesContext
				.getCurrentInstance().getExternalContext().getRequest();
		int id = Integer.parseInt(request.getParameter("idForm"));
		sprintDao dao = new sprintJPADao();
		this.sprint = dao.findsprintData(id);

		if (sprint != null) {
			Flash flash = FacesContext.getCurrentInstance()
					.getExternalContext().getFlash();
			flash.put("form", sprint.getId());

			return "/gerente/resposta-equipe";
		} else {
			FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage("Aviso",
									"Formulário ainda está aberto, espere a data de fechamento!"));
			return "?faces-redirect=true";
		}

	}

	public String calcularPontos() {

		HttpServletRequest request = (HttpServletRequest) FacesContext
				.getCurrentInstance().getExternalContext().getRequest();
		int id = Integer.parseInt(request.getParameter("idForm"));
		sprintDao dao = new sprintJPADao();
		this.sprint = dao.find(id);
		try {
			dao.realizaCalculoPontos(this.sprint);
			//Dando commit para os dados que serão usados no formulário
			dao.commit();
			dao.begin();
		} catch (PersistenceException ex) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Cálculo de pontos não realizado","Verifique se os gabaritos foram preenchidos"));
		}
		
		FacesContext facesContext = FacesContext.getCurrentInstance();
		facesContext.responseComplete();
		ServletContext scontext = (ServletContext) facesContext
				.getExternalContext().getContext();
		String path = scontext.getRealPath("/WEB-INF/relatorios/relatorioPontuacao.jrxml");
		System.out.println(path);
		try {
			ReportUtil util = new ReportUtil();
			byte[] bytes = util.geraRelatorioPontuacao(this.sprint, path);
			if(bytes != null && bytes.length > 0){
				
				HttpServletResponse response = (HttpServletResponse) facesContext
						.getExternalContext().getResponse();

				response.setContentType("application/pdf");

				response.setHeader("Content-disposition",
						"inline; filename=\"relat.pdf\"");

				response.setContentLength(bytes.length);

				ServletOutputStream outputStream;
				outputStream = response.getOutputStream();

				System.out.println("Escrevendo bytes na saída");
				System.out.println(bytes);	
				outputStream.write(bytes);
				
				outputStream.flush();

				outputStream.close();
				
			}
		} catch (Exception e) {	
			FacesContext
			.getCurrentInstance()
			.addMessage(
					null,
					new FacesMessage("Aviso",
							"Verifique se as tarefas e fatores foram adicionados"));
			e.printStackTrace();
		}
		
		this.sprint = new sprint();
		
		return "";
	}

	public String carregasprint(){
		
		HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
		int idForm = Integer.parseInt(request.getParameter("idForm"));
		
		sprintDao dao = new sprintJPADao();
		
		this.sprint = dao.find(idForm);
		
		return "/gerente/visualiza-sprint";
	}
	
	public String atualizasprint(){
		
		sprintDao dao = new sprintJPADao();
			
		dao.update(sprint);
		
		Flash flash = FacesContext.getCurrentInstance().getExternalContext().getFlash();
		flash.setKeepMessages(true); 
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Formulário alterado com sucesso!"));
		
		return "/gerente/sprint?faces-redirect=true";
	}

	public String relatorioMelhoresEquipes(){
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		
		sprintDao fDao = new sprintJPADao();
		Integer idsprint = Integer.parseInt(request.getParameter("idForm"));
		this.sprint = fDao.find(idsprint);
		
		
		FacesContext facesContext = FacesContext.getCurrentInstance();
		facesContext.responseComplete();
		ServletContext scontext = (ServletContext) facesContext
				.getExternalContext().getContext();
		String path = scontext.getRealPath("/WEB-INF/relatorios/relatorioMelhorEquipe.jrxml");
		System.out.println(path);
		
		try {
			ReportUtil util = new ReportUtil();
			byte[] bytes = util.geraRelatorioMelhoresEquipes(this.sprint, path);
			if(bytes != null && bytes.length > 0){
				
				HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();

				response.setContentType("application/pdf");

				response.setHeader("Content-disposition",
						"inline; filename=\"relat.pdf\"");

				response.setContentLength(bytes.length);

				ServletOutputStream outputStream;
				outputStream = response.getOutputStream();

				System.out.println("Escrevendo bytes na saída");
				System.out.println(bytes);	
				outputStream.write(bytes);
				
				outputStream.flush();

				outputStream.close();
				
			}
		} catch (Exception e) {	
			FacesContext
			.getCurrentInstance()
			.addMessage(
					null,
					new FacesMessage("Aviso",
							"Verifique se as tarefas e fatores foram adicionados"));
			e.printStackTrace();
		}

		
		return "/gerente/sprint";
	}
	
	public String relatorioMelhoresApostadores(){
		
HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		
		sprintDao fDao = new sprintJPADao();
		Integer idsprint = Integer.parseInt(request.getParameter("idForm"));
		this.sprint = fDao.find(idsprint);
		
		
		FacesContext facesContext = FacesContext.getCurrentInstance();
		facesContext.responseComplete();
		ServletContext scontext = (ServletContext) facesContext
				.getExternalContext().getContext();
		String path = scontext.getRealPath("/WEB-INF/relatorios/relatorioMelhorApostadorRodada.jrxml");
		System.out.println(path);
		
		try {
			ReportUtil util = new ReportUtil();
			byte[] bytes = util.geraRelatorioMelhoresApostadores(this.sprint, path);
			if(bytes != null && bytes.length > 0){
				
				HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();

				response.setContentType("application/pdf");

				response.setHeader("Content-disposition",
						"inline; filename=\"relat.pdf\"");

				response.setContentLength(bytes.length);

				ServletOutputStream outputStream;
				outputStream = response.getOutputStream();

				System.out.println("Escrevendo bytes na saída");
				System.out.println(bytes);	
				outputStream.write(bytes);
				
				outputStream.flush();

				outputStream.close();
				
			}
		} catch (Exception e) {	
			FacesContext
			.getCurrentInstance()
			.addMessage(
					null,
					new FacesMessage("Aviso",
							"Verifique se as tarefas e fatores foram adicionados"));
			e.printStackTrace();
		}

		
		return "/gerente/sprint";
	}
	
}








