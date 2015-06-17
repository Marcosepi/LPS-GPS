package br.com.gestor.database.dao;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import br.com.gestor.database.modelo.Dev;
import br.com.gestor.database.modelo.Equipe;
import br.com.gestor.database.modelo.Sprint;
import br.com.gestor.database.modelo.Tarefa;
import br.com.gestor.database.modelo.PontuacaoRodada;
import br.com.gestor.database.modelo.Gerente;
import br.com.gestor.database.modelo.Resposta;
import br.com.gestor.database.modelo.Usuario;

public class FormularioJPADao extends GenericJPADao<Sprint> implements
		FormularioDao {

	public FormularioJPADao() {
		this.persistenceClass = Sprint.class;
	}

	@Override
	public List<Sprint> getList() {

		String sql = "select f from Formulario f";

		Query query = em.createQuery(sql);

		return query.getResultList();
	}

	@Override
	public Sprint formRodada() {

		String sql = "select f from Formulario f where current_date() between f.dataInicio and f.dataFim";

		Query query = em.createQuery(sql);
		return (Sprint) query.getSingleResult();

	}

	@Override
	public List<Tarefa> getPerguntasForm(Sprint form) {

		String sql = "select p from Pergunta p where p.formulario = :form order by p.id";

		Query query = em.createQuery(sql);
		query.setParameter("form", form);

		return query.getResultList();
	}

	@Override
	public List<Resposta> respostasCadastradas(Sprint form,
			Usuario usuario, Equipe equipe) {

		String sql = "select distinct r from Resposta r inner join r.pergunta p where p.formulario = :form and r.usuario = :usuario and r.equipe = :equipe";
		/**
		 * select distinct a from Aluno a inner join a.respostas as r inner join
		 * r.pergunta as p where p.formulario = :formulario
		 * 
		 * select r from Resposta r, Pergunta p where r.pergunta = p and
		 * p.formulario = :form and r.usuario = :usuario and r.equipe = :equipe
		 */

		Query query = em.createQuery(sql);
		query.setParameter("form", form);
		query.setParameter("usuario", usuario);
		query.setParameter("equipe", equipe);

		return query.getResultList();
	}

	public void removePontuacao(Sprint form) {

		String hql = "delete from PontuacaoRodada p where p.formulario = :formulario";

		Query query = this.em.createQuery(hql);
		query.setParameter("formulario", form);
		query.executeUpdate();

	}

	@Override
	public void realizaCalculoPontos(Sprint formulario) {
		// removendo pontuações anteriores
		removePontuacao(formulario);
		String consultaAluno = "select distinct a from Aluno a inner join a.respostas as r inner join a.equipe as e inner join r.pergunta as p "
				+ "where p.formulario = :formulario order by a.nome";
		Query queryAluno = em.createQuery(consultaAluno);
		queryAluno.setParameter("formulario", formulario);

		List<Dev> alunos = queryAluno.getResultList();

		// TODO: Verificar se não está duplicando
		String consultaProfessor = "select p from Professor p, Resposta r, Pergunta p2 where r.usuario = p.id and "
				+ "r.pergunta = p2 and p2.formulario = :formulario "
				+ "order by p2.id";
		Query queryProfessor = em.createQuery(consultaProfessor);
		queryProfessor.setParameter("formulario", formulario);

		Gerente professor = (Gerente) queryProfessor.getSingleResult();

		Map<Integer, Integer> pontEquipeProf = new LinkedHashMap<>();
		EquipeDao equipeDao = new EquipeJPADao();
		List<Equipe> equipes = equipeDao.getList();
		for (Equipe equipe : equipes) {
			pontEquipeProf.put(equipe.getId(), 0);
		}

		int valoresRespostas[] = new int[5];
		valoresRespostas[0] = 0;
		valoresRespostas[1] = 20;
		valoresRespostas[2] = 10;
		valoresRespostas[3] = 5;
		valoresRespostas[4] = 0;

		for (Resposta resposta : professor.getRespostas()) {
			if (resposta.getPergunta().getFormulario().equals(formulario)) {
				int idEquipe = resposta.getEquipe().getId();
				int undoPontos = pontEquipeProf.get(idEquipe);
				int pontosResposta = valoresRespostas[resposta.getResposta()];
				pontEquipeProf.put(idEquipe, undoPontos + pontosResposta);
			}
		}

		Map<Integer, Integer> pontEquipeAluno = new LinkedHashMap<>();
		for (Dev aluno : alunos) {
			for (Equipe equipe : equipes) {
				pontEquipeAluno.put(equipe.getId(), 0);
			}

			for (Resposta resposta : aluno.getRespostas()) {
				if (resposta.getPergunta().getFormulario().equals(formulario)) {
					int idEquipe = resposta.getEquipe().getId();
					int undoPontos = pontEquipeAluno.get(idEquipe);
					int pontosResposta = valoresRespostas[resposta
							.getResposta()];
					pontEquipeAluno.put(idEquipe, undoPontos + pontosResposta);
				}
			}

			int pontuacaoTotal = 0;
			for (Map.Entry<Integer, Integer> map : pontEquipeProf.entrySet()) {
				int pEquipeProf = map.getValue();
				if (pontEquipeAluno.get(map.getKey()) == null) {
					pontuacaoTotal += pEquipeProf;
				} else {
					int pEquipeAluno = pontEquipeAluno.get(map.getKey());
					pontuacaoTotal += Math.abs(pEquipeProf - pEquipeAluno);
				}
			}

			PontuacaoRodada pontuacaoRodada = new PontuacaoRodada();
			pontuacaoRodada.setFormulario(formulario);
			pontuacaoRodada.setAluno(aluno);
			pontuacaoRodada.setPontos(pontuacaoTotal);
			this.em.persist(pontuacaoRodada);

		}

		// Zera pontuação de alunos sem respostas de formulários
		consultaAluno = "select aluno from Aluno aluno where aluno not in "
				+ "(select a from  Aluno a inner join a.respostas as r inner join r.pergunta as p where p.formulario = :formulario)";
		Query query = em.createQuery(consultaAluno);
		query.setParameter("formulario", formulario);
		List<Dev> alunosSemResposta = query.getResultList();
		int pontuacaoTotalProfessor = 0;
		for (Map.Entry<Integer, Integer> map : pontEquipeProf.entrySet()) {
			pontuacaoTotalProfessor += map.getValue();
		}
		System.out.println("Pontuacao do professor : " + pontuacaoTotalProfessor);
		for (Dev aluno : alunosSemResposta) {
			PontuacaoRodada pontuacaoRodada = new PontuacaoRodada();
			pontuacaoRodada.setFormulario(formulario);
			pontuacaoRodada.setAluno(aluno);
			pontuacaoRodada.setPontos(pontuacaoTotalProfessor);
			this.em.persist(pontuacaoRodada);
		}

	}

	/*
	 * @Override public void realizaCalculoPontos(Formulario formulario) { //
	 * removendo pontuações anteriores removePontuacao(formulario);
	 * 
	 * String consultaAluno =
	 * "select distinct a from Aluno a inner join a.respostas as r inner join a.equipe as e inner join r.pergunta as p "
	 * + "where p.formulario = :formulario order by a.nome"; Query queryAluno =
	 * em.createQuery(consultaAluno); queryAluno.setParameter("formulario",
	 * formulario);
	 * 
	 * List<Aluno> alunos = queryAluno.getResultList();
	 * 
	 * // TODO: Verificar se não está duplicando String consultaProfessor =
	 * "select p from Professor p, Resposta r, Pergunta p2 where r.usuario = p.id and "
	 * + "r.pergunta = p2 and p2.formulario = :formulario " + "order by p2.id";
	 * Query queryProfessor = em.createQuery(consultaProfessor);
	 * queryProfessor.setParameter("formulario", formulario);
	 * 
	 * Professor professor = (Professor) queryProfessor.getSingleResult();
	 * EquipeDao equipeDao = new EquipeJPADao(); for (Aluno aluno : alunos) {
	 * Map<Integer, Integer> pontuacaoEquipe = new LinkedHashMap<>();
	 * List<Equipe> equipes = equipeDao.getList(); for (Equipe equipe : equipes)
	 * { pontuacaoEquipe.put(equipe.getId(), 0); } for (Resposta resposta :
	 * aluno.getRespostas()) { // TODO // verifica se a resposta é daquele
	 * formulario ou nao - // modificar if
	 * (resposta.getPergunta().getFormulario().getId() != formulario .getId())
	 * continue; int pontos = 0; Resposta respostaGabarito; if
	 * ((respostaGabarito = buscaResposta(professor.getRespostas(), resposta))
	 * != null) { if (resposta.getResposta() == 0) { pontos = 0; } else { pontos
	 * = Math.abs(resposta.getResposta() - respostaGabarito.getResposta()) * 5;
	 * if (pontos == 0) { // resposta correta pontos = 20; } else { pontos = 15
	 * - pontos; } } } // System.out.println("gabarito " + //
	 * respostaGabarito.getResposta() + "--"); // System.out.println("aluno " +
	 * resposta.getResposta() + "--"); // System.out.println(pontos);
	 * pontuacaoEquipe.put(resposta.getEquipe().getId(),
	 * pontuacaoEquipe.get(resposta.getEquipe().getId()) + pontos); }
	 * 
	 * int pontosTotalForm = 0;
	 * System.out.println("--------------------------------------");
	 * System.out.println(aluno.getNome());
	 * System.out.println("--------------------------------------"); for
	 * (Map.Entry<Integer, Integer> m : pontuacaoEquipe.entrySet()) {
	 * System.out.println("Equipe = " + m.getKey());
	 * System.out.println("Pontos = " + m.getValue()); pontosTotalForm +=
	 * m.getValue(); }
	 * 
	 * PontuacaoRodada pontuacaoRodada = new PontuacaoRodada();
	 * pontuacaoRodada.setFormulario(formulario);
	 * pontuacaoRodada.setAluno(aluno);
	 * pontuacaoRodada.setPontos(pontosTotalForm);
	 * this.em.persist(pontuacaoRodada); }
	 * 
	 * // Zera pontuação de alunos sem respostas de formulários consultaAluno =
	 * "select aluno from Aluno aluno where aluno not in " +
	 * "(select a from  Aluno a inner join a.respostas as r inner join r.pergunta as p where p.formulario = :formulario)"
	 * ; Query query = em.createQuery(consultaAluno);
	 * query.setParameter("formulario", formulario); List<Aluno>
	 * alunosSemResposta = query.getResultList(); for (Aluno aluno :
	 * alunosSemResposta) { PontuacaoRodada pontuacaoRodada = new
	 * PontuacaoRodada(); pontuacaoRodada.setFormulario(formulario);
	 * pontuacaoRodada.setAluno(aluno); pontuacaoRodada.setPontos(0);
	 * this.em.persist(pontuacaoRodada); }
	 * 
	 * }
	 */
	/*
	 * private static Resposta buscaResposta(List<Resposta> respostas, Resposta
	 * resposta) {
	 * 
	 * for (Resposta r : respostas) { if
	 * (r.getEquipe().equals(resposta.getEquipe()) &&
	 * r.getPergunta().equals(resposta.getPergunta())) { return r; } }
	 * 
	 * return null; }
	 */

	@Override
	public void removerFormulario(Sprint formulario) {

		String hqlFE = "DELETE FROM FatorEquipe ef WHERE ef.formulario = :form";
		Query deleteFE = em.createQuery(hqlFE);
		deleteFE.setParameter("form", formulario);
		deleteFE.executeUpdate();

		String hqlAposta = "DELETE FROM Aposta ap WHERE ap.formulario = :form";
		Query deleteAposta = em.createQuery(hqlAposta);
		deleteAposta.setParameter("form", formulario);
		deleteAposta.executeUpdate();

		String hqlPontuacao = "DELETE FROM PontuacaoRodada p WHERE p.formulario = :form";
		Query deletePontuacao = em.createQuery(hqlPontuacao);
		deletePontuacao.setParameter("form", formulario);
		deletePontuacao.executeUpdate();

		String hqlResposta = "DELETE FROM Resposta r WHERE r.pergunta IN ( SELECT p FROM Pergunta p where p.formulario = :formulario)";
		Query deleteRespostas = em.createQuery(hqlResposta);
		deleteRespostas.setParameter("formulario", formulario);
		deleteRespostas.executeUpdate();

		String hqlPergunta = "DELETE FROM Pergunta p where p.formulario = :formulario)";
		Query deletePergunta = em.createQuery(hqlPergunta);
		deletePergunta.setParameter("formulario", formulario);
		deletePergunta.executeUpdate();

		delete(formulario);

	}

	/**
	 * Busca formulário se a data de fim dele for menor que a data atual
	 */
	@Override
	public Sprint findFormularioData(int id) {

		String hql = "select f from Formulario f where f.id = :id and current_date() > f.dataFim";
		Query query = this.em.createQuery(hql);
		query.setParameter("id", id);
		try {
			Sprint form = (Sprint) query.getSingleResult();
			return form;
		} catch (NoResultException ex) {
			// ex.printStackTrace();
			return null;
		}
	}

}
