package no.hvl.dat152.obl4.util;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringEscapeUtils;

public class Validator {

	public static String validString(String parameter) {

		// Changing from escapeSql til escapeHtml here to avoid losing the apostrophes
		// in names.
		return parameter != null ? StringEscapeUtils.escapeHtml(parameter) : "null";
	}

	public static boolean isCSRFTokenInvalid(HttpServletRequest r) {
		String x = "";
		try {
			x = (String) r.getSession().getAttribute("AntiCSRFToken");
		} catch (NullPointerException e) {
			return true;
		}
		String y = r.getParameter("AntiCSRFToken");
		return !x.equals(y);
	}

	public static String validHTML(String s) {
		//Not removing this duplicate because it's referenced a lot of places, and we have little time to finish the assignment. 
		return s != null ? StringEscapeUtils.escapeHtml(s) : "null";
	}

}
