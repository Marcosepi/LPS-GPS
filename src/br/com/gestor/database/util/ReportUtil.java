package br.com.gestor.database.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRResultSetDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import br.com.gestor.database.dao.FormularioDao;
import br.com.gestor.database.dao.FormularioJPADao;
import br.com.gestor.database.modelo.Sprint;

public class ReportUtil {

	private static final String user = "postgres";

	 private static final String url =
	 "jdbc:postgresql://localhost:5432/game-empreendedorismo";
	 private static final String password = "1234";

	public byte[] geraRelatorioPontuacao(Sprint form, String path)
			throws SQLException, JRException {

		JasperDesign design = JRXmlLoader.load(path);

		JasperReport report = JasperCompileManager.compileReport(design);

		Connection con = DriverManager.getConnection(url, user, password);
		PreparedStatement stmt = con
				.prepareStatement(" select u.login, u.nome, f.descricao, p.pontos from PontuacaoRodada "
						+ "p inner join Usuario u on u.login = p.dev inner join dev a on u.login = a.login"
						+ " inner join Formulario f on f.id = p.formulario where formulario = ?"
						+ " order by p.pontos;");

		stmt.setInt(1, form.getId());

		ResultSet rs = stmt.executeQuery();

		JRDataSource data = new JRResultSetDataSource(rs);

		Map<String, Object> map = new HashMap<>();
		map.put("formulario", form.getId());

		JasperPrint print = JasperFillManager.fillReport(report, map, data);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		JRPdfExporter exporter = new JRPdfExporter();

		exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);

		exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, baos);

		exporter.exportReport();

		byte[] bytes = baos.toByteArray();

		con.close();
		return bytes;
	}

	public byte[] geraRelatorioMelhoresEquipes(Sprint form, String path)
			throws SQLException, JRException {

		JasperDesign design = JRXmlLoader.load(path);

		JasperReport report = JasperCompileManager.compileReport(design);

		Connection con = DriverManager.getConnection(url, user, password);
		PreparedStatement stmt = con
				.prepareStatement("select distinct(e.nome), sum(a.valor*f.fator) as soma,"
						+ " a.id_formulario, f.fator "
						+ "from aposta a inner join fatorequipe f on "
						+ "f.id_equipe = a.id_equipe inner join equipe e on "
						+ "e.id = a.id_equipe where a.id_formulario = f.id_formulario "
						+ "and a.id_formulario = ? group by e.nome, a.id_formulario, f.fator "
						+ "order by a.id_formulario, soma desc");
		stmt.setInt(1, form.getId());

		ResultSet rs = stmt.executeQuery();

		JRDataSource data = new JRResultSetDataSource(rs);

		Map<String, Object> map = new HashMap<>();
		map.put("formulario", form.getId());

		JasperPrint print = JasperFillManager.fillReport(report, map, data);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		JRPdfExporter exporter = new JRPdfExporter();

		exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);

		exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, baos);

		exporter.exportReport();

		byte[] bytes = baos.toByteArray();

		con.close();
		return bytes;
	}

	public byte[] geraRelatorioMelhoresApostadores(Sprint form, String path)
			throws SQLException, JRException {

		JasperDesign design = JRXmlLoader.load(path);

		JasperReport report = JasperCompileManager.compileReport(design);

		Connection con = DriverManager.getConnection(url, user, password);
		PreparedStatement stmt = con
				.prepareStatement("select u.nome,a.id_dev,sum(a.valor * f.fator) as Soma,"
						+ " form.descricao from aposta a inner join fatorequipe f on f.id_equipe = a.id_equipe"
						+ " left join usuario u on u.login = a.id_dev"
						+ " inner join formulario form on form.id = a.id_formulario"
						+ " where f.id_formulario = ? and a.id_formulario = ?"
						+ " group by a.id_dev,u.nome,a.id_formulario, form.descricao");

		stmt.setInt(1, form.getId());
		stmt.setInt(2, form.getId());

		ResultSet rs = stmt.executeQuery();

		JRDataSource data = new JRResultSetDataSource(rs);

		Map<String, Object> map = new HashMap<>();
		map.put("formulario", form.getId());

		JasperPrint print = JasperFillManager.fillReport(report, map, data);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		JRPdfExporter exporter = new JRPdfExporter();

		exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);

		exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, baos);

		exporter.exportReport();

		byte[] bytes = baos.toByteArray();

		con.close();
		return bytes;
	}

	public static void main(String[] args) throws SQLException, JRException,
			IOException {
		Sprint f = new Sprint();
		f.setId(1);
		FormularioDao dao = new FormularioJPADao();
		f = dao.find(1);
		dao.begin();
		dao.realizaCalculoPontos(f);
		dao.commit();
		byte[] bytes = new ReportUtil().geraRelatorioPontuacao(f,
				"WebContent/WEB-INF/relatorios/relatorioPontuacao.jrxml");
		System.out.println(bytes);
		Path path = Paths.get("/home/alex/relat.pdf");
		Files.write(path, bytes);
	}

}
