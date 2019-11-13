package no.hvl.dat152.obl4.util;

import org.apache.commons.lang.StringEscapeUtils;

public class Validator {

	public static String validString(String parameter) {
		System.out.println(parameter);
		return parameter != null ? StringEscapeUtils.escapeSql(parameter) : "null";
	}
}
