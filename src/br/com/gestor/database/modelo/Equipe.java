package br.com.gestor.database.modelo;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import br.com.gestor.database.dao.EquipeDao;
import br.com.gestor.database.dao.EquipeJPADao;

@Entity
public class Equipe implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7756819748278806310L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String nome;
	private boolean ativo;
	@OneToMany(mappedBy = "equipe", fetch = FetchType.EAGER, cascade = { CascadeType.MERGE })
	private List<Dev> alunos;

	public List<Dev> getAlunos() {
		return alunos;
	}

	public void setAlunos(List<Dev> alunos) {
		this.alunos = alunos;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	
	public String getInfoAlunos() {
		String info = "";
		if (alunos.size() > 0) {
			for (Dev aluno : alunos) {
				info += aluno.getNome() + "-" + aluno.getLogin() + " | ";
			}
			return info;
		} else {
			return "Nenhum aluno";
		}

	}

	@Override
	public boolean equals(Object obj) {

		if(obj == this) return true;
		if(!(obj instanceof Equipe)) return false;
		
		Equipe e = (Equipe) obj;
		
		return e.getId() == this.getId();
		
	}
	
}
