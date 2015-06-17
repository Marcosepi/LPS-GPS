package br.com.gestor.database.dao;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import br.com.gestor.database.modelo.Equipe;
import br.com.gestor.database.modelo.FatorEquipe;
import br.com.gestor.database.modelo.Sprint;

public class FatorEquipeJPADao extends GenericJPADao<FatorEquipe> implements FatorEquipeDao{

	public FatorEquipeJPADao(){
		this.persistenceClass = FatorEquipe.class;
	}
	
	@Override
	public List<FatorEquipe> getList() {
		
		Query query = this.em.createNamedQuery("FatorEquipe.list");
		
		return query.getResultList();
	}

	@Override
	public boolean salvaFatorEquipe(FatorEquipe fator) {
		
		String hql = "select f from FatorEquipe f"
				+ " where f.equipe = :equipe and f.formulario = :formulario";
		
		fator.setEquipe(this.em.find(Equipe.class, fator.getEquipe().getId()));
		fator.setFormulario(this.em.find(Sprint.class, fator.getFormulario().getId()));
		
		try{
			
		Query query = this.em.createQuery(hql);
		query.setParameter("equipe", fator.getEquipe());
		query.setParameter("formulario", fator.getFormulario());
		
			try{
				FatorEquipe fEquipe = (FatorEquipe) query.getSingleResult();
				if(fEquipe != null){
					fEquipe.setFator(fator.getFator());
					update(fEquipe);
				}else{
					throw new NoResultException();
				}
				
			}catch(NoResultException e){
				this.save(fator);
			}
			
		return true;
		}catch(PersistenceException ex){
			ex.printStackTrace();
			return false;
		}
		
	}

	@Override
	public FatorEquipe buscaFatorEquipe(FatorEquipe fEquipe) {

		String hql = "select f from FatorEquipe f"
				+ " where f.equipe = :equipe and f.formulario = :formulario";

		try{
			Query query = this.em.createQuery(hql);
			query.setParameter("equipe", fEquipe.getEquipe());
			query.setParameter("formulario", fEquipe.getFormulario());
			
			return (FatorEquipe)query.getSingleResult();
		}catch(NoResultException ex){
			
			return null;
		}
	}
	
}
