/* SuperAdminPage.java (c) 2014 Fredrik Rambris. All rights reserved */
package web;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @author Fredrik Rambris <fredrik@rambris.com>
 * 
 */
public abstract class SuperAdminPage extends RestrictedPage
{

	/**
	 * @param servlet
	 * @param request
	 * @param response
	 * @param app
	 * @throws IOException
	 * @throws RedirectedException
	 * @throws SQLException
	 * @throws PermissionDeniedException
	 */
	public SuperAdminPage(WebServlet servlet, HttpServletRequest request, HttpServletResponse response, WebApp app) throws IOException,
			RedirectedException, SQLException, PermissionDeniedException
	{
		super(servlet, request, response, app);
		if (!currentUser.isSuperAdmin()) throw new PermissionDeniedException("You do not have permission to access this page");
	}
}
