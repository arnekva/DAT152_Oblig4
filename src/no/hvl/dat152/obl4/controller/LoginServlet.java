package no.hvl.dat152.obl4.controller;

import java.io.IOException;
import java.util.List;

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

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.getRequestDispatcher("login.jsp").forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.removeAttribute("message");

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
			}
		}

		if (successfulLogin) {
			System.out.println("Logged inn");
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
