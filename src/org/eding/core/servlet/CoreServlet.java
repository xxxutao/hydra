package org.eding.core.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.eding.core.CoreCipher;
import org.eding.core.common.SysConfig;
import org.eding.pro.beans.BaseBean;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * @author eding
 * 核心处理用的servlet
 *
 */
public class CoreServlet extends HttpServlet {
	@Override
	public void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		//项目所有内容使用utf8传输
		
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();
		//通过uri获取url
		String uri=request.getRequestURI();
		String url=request.getRequestURL().toString();
		uri=uri.substring(uri.lastIndexOf("/"));
		uri=uri.substring(0,uri.lastIndexOf(".c")).replaceAll("/", "");
		JSONObject jsonIn=JSONObject.fromObject(request.getParameter("params"));
		Map inData=jsonIn;
		ApplicationContext context=WebApplicationContextUtils.getWebApplicationContext(request.getSession().getServletContext());
		String[] pathInfo=uri.split("\\.");
		String beanName;
		String functionName;
		if(pathInfo.length<2){
			out.println("请勿尝试非法使用本系统");
			out.flush();
			return ;
		}
		JSONObject json=null;
		Map<Object,Object> retMap=null;
		try{
			beanName=pathInfo[0];
		 	functionName=pathInfo[1];
		 	BaseBean baseBean=(BaseBean) context.getBean(beanName);
		 	
		 	if(request.getCookies()!=null){
		 		for(Cookie cookie:request.getCookies()){
		 			if(cookie.getName().equals("userInfo")){
		 				inData.put("userInfo", CoreCipher.decryptBasedDes(cookie.getValue()));
		 			}
				}
		 	}
			
		 	baseBean.inData=inData;
		 	baseBean.request=request;
		 	baseBean.response=response;
			Class c1=baseBean.getClass();
			retMap=(Map)c1.getMethod(functionName).invoke(baseBean);
		}catch (Exception e) {
			e.printStackTrace();
			retMap=new HashMap();
			retMap.put("retCode", "500");
			retMap.put("retMsg", e.getMessage());
			json=JSONObject.fromObject(retMap);
		}finally{
			 json=JSONObject.fromObject(retMap); 
		}	
		out.println(json);
		out.flush();
		out.close();
	}

	/**
	 * Constructor of the object.
	 */
	public CoreServlet() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
	}

	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occurs
	 */
	public void init() throws ServletException {
		// Put your code here
	}

}
