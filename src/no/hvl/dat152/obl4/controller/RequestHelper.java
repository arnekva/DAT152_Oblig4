package no.hvl.dat152.obl4.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import no.hvl.dat152.obl4.database.*;

public class RequestHelper {

	public static String getCookieValue(HttpServletRequest request,
			String cookieName) {

		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie c : cookies) {
				if (c.getName().equals(cookieName)) {
					return c.getValue();
				}
			}
		}
		return null;
	}
	
	public static boolean isLoggedIn(HttpServletRequest request) {
		return request.getSession().getAttribute("user") != null;
	}
	public static String getLoggedInUsername(HttpServletRequest request) {
        return ((AppUser) request.getSession().getAttribute("user")).getUsername();
    }
}
