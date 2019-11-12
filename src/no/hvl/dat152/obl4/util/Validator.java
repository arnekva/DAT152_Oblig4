package no.hvl.dat152.obl4.util;

import org.apache.commons.lang.StringEscapeUtils;

public class Validator {

	public static String validString(String parameter) {
		return StringEscapeUtils.escapeHtml(parameter);
	}
}
