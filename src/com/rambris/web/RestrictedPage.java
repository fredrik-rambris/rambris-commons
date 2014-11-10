/* RestrictedPage.java (c) 2014 Fredrik Rambris. All rights reserved */
package com.rambris.web;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * @author Fredrik Rambris <fredrik@rambris.com>
 * 
 */
public abstract class RestrictedPage extends Page
{
	private final Logger log = Logger.getLogger(RestrictedPage.class);
	protected User currentUser = null;

	/**
	 * @param servlet
	 * @param request
	 * @param response
	 * @param app
	 * @throws SQLException
	 * @throws RedirectedException
	 * @throws IOException
	 */
	public RestrictedPage(WebServlet servlet, HttpServletRequest request, HttpServletResponse response, WebApp app) throws IOException,
			RedirectedException, SQLException
	{
		super(servlet, request, response, app);
		getUser();
	}

	private void getUser() throws IOException, RedirectedException, SQLException
	{
		currentUser = (User) getAttribute("currentUser");

		// When we restart the server we no longer have the user in session. This way
		// we load it up by hash and the user wont be kicked out.
		if (currentUser == null && cookies.get("auth_hash") != null)
		{
			currentUser = User.AuthenticateUser(db, cookies.get("auth_hash"));
			if (currentUser != null)
			{
				setAttribute("currentUser", currentUser, true);
				log.info("Authenticated user by hash: " + currentUser.getName());
				return;
			}
		}

		if (currentUser == null)
		{
			setAttribute("return_to", SELF, true);
			redirect(ROOT + "/login");
			throw new RedirectedException();
		}
	}

	protected String audit(String message, Object... args)
	{
		return String.format(currentUser.getName() + " " + message, args);
	}
}
