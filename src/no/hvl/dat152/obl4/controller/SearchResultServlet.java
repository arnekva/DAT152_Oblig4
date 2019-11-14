package no.hvl.dat152.obl4.controller;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import no.hvl.dat152.obl4.database.SearchItem;
import no.hvl.dat152.obl4.database.SearchItemDAO;
import no.hvl.dat152.obl4.dictionary.DictionaryDAO;
import no.hvl.dat152.obl4.util.Validator;

@WebServlet("/dosearch")
public class SearchResultServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final String DEFAULT_DICT_URL = DictionaryDAO.DEFAULT_DICT_URL;

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		if (RequestHelper.isLoggedIn(request)) {

			String dicturl = RequestHelper.getCookieValue(request, "dicturl");
			if (dicturl == null) {
				dicturl = DEFAULT_DICT_URL;
			}

			String user = Validator.validString(request.getParameter("user"));
			//TODO: User needs to be validated
			
			
			String searchkey = Validator.validHTML(request
					.getParameter("searchkey"));
			
			if (!RequestHelper.getLoggedInUsername(request).equals(user)) {
				System.out.println("names are not equal");
                response.sendRedirect("searchpage");
                return;
            }else {
            	System.out.println("They are the same");
            }
			Timestamp datetime = new Timestamp(new Date().getTime());
			SearchItem search = new SearchItem(datetime, user, searchkey);
			
			SearchItemDAO searchItemDAO = new SearchItemDAO();
			searchItemDAO.saveSearch(search);
			DictionaryDAO dict = new DictionaryDAO(dicturl);

			List<String> foundEntries = new ArrayList<String>();
			try {
				foundEntries = dict.findEntries(searchkey);
			} catch (Exception e) {
				
				System.out.println("Dictionary did not find entries. This may be because the dictonary on localhost is configured to the wrong port.");
			}

			request.setAttribute("searchkey", searchkey);
			request.setAttribute("result", foundEntries);
			request.getRequestDispatcher("searchresult.jsp").forward(request,
					response);
		} else {
			request.getSession().invalidate();
			request.getRequestDispatcher("index.html").forward(request,
					response);
		}
	}

}
