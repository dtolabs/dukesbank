package com.jboss.ebank.web;

import javax.servlet.*;
import javax.servlet.http.*;
import com.sun.ebank.util.Debug;
import com.sun.ebank.web.*;
import java.io.IOException;


/**
 * this is a dumb hack.  update 4 seems to broken unless a
 * CustomerBean is placed in the request linked to the BeanManager. 
 * Naturally, we need to add a BeanManager to the session here,
 * doing some of the work the dispatcher should have done.
 */
public class CustomerHackFilter
    implements Filter
{
    private FilterConfig filterConfig = null;
    
    public void init(FilterConfig filterConfig) 
        throws ServletException 
    {
        this.filterConfig = filterConfig;
    }

    public void destroy() {
        this.filterConfig = null;
    }


    public void doFilter(ServletRequest req, ServletResponse response,
                         FilterChain chain) 
        throws IOException, 
               ServletException 
    {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpSession        session = request.getSession();

        BeanManager beanManager =
            (BeanManager) session.getAttribute("beanManager");

        if (beanManager == null) {
            Debug.print("hack - Creating bean manager.");
            beanManager = new BeanManager();
            session.setAttribute("beanManager", beanManager);
        }

        CustomerBean customerBean = new CustomerBean();
        customerBean.setBeanManager(beanManager);
        request.setAttribute("customerBean", customerBean); 
        Debug.print("hack - added customerBean to request");

        chain.doFilter(request, response);
    }
}
