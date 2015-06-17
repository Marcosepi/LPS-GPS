package br.com.gestor.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import br.com.gestor.database.modelo.Dev;
import br.com.gestor.database.util.JPAUtil;

@FacesConverter(value="alunoConverter",forClass = Dev.class)
public class DevConverter implements Converter {

	public String getAsString(FacesContext context, UIComponent component,
			Object object) {
		//System.err.println("Utilizando Converter\n\n" + object);
		Dev aluno = (Dev) object;
		if (aluno == null || aluno.getLogin() == null)
			return null;
		return String.valueOf(aluno.getLogin());
	}

	public Object getAsObject(FacesContext context, UIComponent component,
			String string) {
		//System.err.println("Utilizando Convertern\n\n" + string);
		if (string == null || string.isEmpty())
			return null;
		Long login = Long.valueOf(string);
		Dev aluno = JPAUtil.createEntityManager().find(Dev.class, login);
		return aluno;
	}
	
}
