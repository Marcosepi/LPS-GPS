package br.com.gestor.database.modelo;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Tarefa{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String descricao;
	@ManyToOne()
	@JoinColumn(name = "id_formulario", nullable = false)
	private Sprint formulario;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Sprint getFormulario() {
		return formulario;
	}

	public void setFormulario(Sprint formulario) {
		this.formulario = formulario;
	}

	@Override
	public boolean equals(Object obj) {
		
		if(obj == this) return true;
		if(!(obj instanceof Tarefa)) return false;
		
		Tarefa p = (Tarefa) obj;
		
		return p.getId() == this.getId();
	}
	
}
