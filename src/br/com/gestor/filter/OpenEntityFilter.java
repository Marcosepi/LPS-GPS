package br.com.gestor.filter;

import java.io.IOException;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

import br.com.gestor.database.util.JPAUtil;

/**
 * Servlet Filter implementation class OpenEntityFilter
 */
@WebFilter("/*")
public class OpenEntityFilter implements Filter {

	/**
	 * Default constructor.
	 */
	public OpenEntityFilter() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		String uri = ((HttpServletRequest) request).getRequestURI();
		if (uri.contains("javax.faces.resource") || uri.matches(".+\\.(jpg|gif|png)")) {
			chain.doFilter(request, response);
		} else {
			try {
				System.err.println(((HttpServletRequest) request)
						.getRequestURI());
				EntityManager em = JPAUtil.createEntityManager();
				JPAUtil.beginTransaction();
				chain.doFilter(request, response);
				if(em.getTransaction().isActive()){
					JPAUtil.commit();					
				}
			} catch (PersistenceException e) {
				FacesContext f = FacesContext.getCurrentInstance();
				System.out.println(f);
				System.out.println(e.getMessage() + "\n" + e.getCause());
				//e.printStackTrace();
				JPAUtil.rollback();
				//throw new ServletException(e);
			} finally {
				JPAUtil.close();
			}
		}

	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}
