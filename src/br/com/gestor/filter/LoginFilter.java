package br.com.gestor.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import br.com.gestor.controller.LoginBean;

@WebFilter("/*")
public class LoginFilter implements Filter {

	public LoginFilter() {
		// TODO Auto-generated constructor stub
	}

	public void destroy() {
		// TODO Auto-generated method stub
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		req.setCharacterEncoding("UTF-8");
		HttpServletResponse resp = (HttpServletResponse) response;
		String uri = req.getRequestURI();
		HttpSession session = req.getSession(false);
		String contextPath = req.getContextPath();
		

		if (uri.contains("/dev/")) {
			if (session != null) {
				String tipo = (String) session.getAttribute("tipo");
				if (tipo == null || (!tipo.equals(LoginBean.TIPO_dev))) {
					resp.sendRedirect(contextPath + "/login.xhtml");
				} else {
					chain.doFilter(request, response);
				}
			} else {
				resp.sendRedirect(contextPath + "/login.xhtml");
			}
		} else if (uri.contains("/gerente/")) {
			if (session != null) {
				String tipo = (String) session.getAttribute("tipo");
				if (tipo == null || (!tipo.equals(LoginBean.TIPO_gerente))) {
					resp.sendRedirect(contextPath + "/login.xhtml");
				} else {
					chain.doFilter(request, response);
				}
			} else {
				resp.sendRedirect(contextPath + "/login.xhtml");
			}
		} else {
			chain.doFilter(request, response);
		}

	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}
