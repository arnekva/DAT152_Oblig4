package no.hvl.dat152.obl4.util;


import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringEscapeUtils;

public class Validator {

	public static String validString(String parameter) {
		System.out.println(parameter);
		return parameter != null ? StringEscapeUtils.escapeSql(parameter) : "null";
	}
	
	public static boolean isCSRFTokenInvalid(HttpServletRequest r) {
		String x = (String) r.getSession().getAttribute("AntiCSRFToken");
		String y = r.getParameter("AntiCSRFToken");
		return !x.equals(y) || x == null;
		
	}
	public static String validHTML(String s) {
		return s != null ? StringEscapeUtils.escapeHtml(s) : "null";
	}
	
	
}
