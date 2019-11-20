package no.hvl.dat152.obl4.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import no.hvl.dat152.obl4.database.AppUser;
import no.hvl.dat152.obl4.database.AppUserDAO;
import no.hvl.dat152.obl4.util.Role;
import no.hvl.dat152.obl4.util.Validator;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Map<String, Integer> failedLoginAttempts = new HashMap<>();
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String ip = request.getRemoteAddr();
		if (failedLoginAttempts.containsKey(ip)) {
			int forekomst = failedLoginAttempts.get(ip);
			if (forekomst >= 3) {
				request.setAttribute("message", "You have too many failed login attempts. Please contact the server administrator.");
			}
		}
		request.getRequestDispatcher("login.jsp").forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.removeAttribute("message");
		String ip = request.getRemoteAddr();
		if (failedLoginAttempts.containsKey(ip)) {
			int forekomst = failedLoginAttempts.get(ip);
			if (forekomst >= 3) {
				response.sendRedirect("login");
				return;
			}
		}

		boolean successfulLogin = false;

		String username = Validator.validString(request.getParameter("username"));
		String password = Validator.validString(request.getParameter("password"));

		if (username != null && password != null) {

			AppUserDAO userDAO = new AppUserDAO();
			AppUser authUser = userDAO.getAuthenticatedUser(username, password);

			if (authUser != null) {
				successfulLogin = true;
				request.getSession().setAttribute("user", authUser);
				request.getSession().setAttribute("updaterole", "");

				// admin issues
				if (authUser.getRole().equals(Role.ADMIN.toString())) {
					List<String> usernames = userDAO.getUsernames();
					request.getSession().setAttribute("usernames", usernames);
					request.getSession().setAttribute("AntiCSRFToken", userDAO.generateAntiCSRFToken());
					request.getSession().setAttribute("updaterole", "<a href=\"updaterole.jsp\">Update Role</a>");
				}
			} else {
				if (failedLoginAttempts.containsKey(ip)) {
					int forekomst = failedLoginAttempts.get(ip);
					forekomst++;
					failedLoginAttempts.put(ip, forekomst);
				} else {
					failedLoginAttempts.put(ip, 1);
				}
				response.sendRedirect("login");
				return;
			}
		}

		if (successfulLogin) {
			response.sendRedirect("searchpage");

		} else {
			request.setAttribute("message", "Username " + username + ": Login failed!");
			request.getRequestDispatcher("login.jsp").forward(request, response);
		}
	}
}

//XSS - <script>alert("test")</script> in search box
//SQL Injection: set username to:  1' or 1=1 -- -     and you will be logged in as admin
//
//wrap onerror i en function?? prøv
