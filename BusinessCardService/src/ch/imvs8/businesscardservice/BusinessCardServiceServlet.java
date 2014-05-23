package ch.imvs8.businesscardservice;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

@WebServlet(name = "BusinessCardService", urlPatterns = { "/scanner" })
@MultipartConfig
public class BusinessCardServiceServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final int maxFileSize = 50 * 1024;
	private static final int maxMemSize = 4 * 1024;

	private String uploadedFolder;

	public BusinessCardServiceServlet() {

	}

	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		this.uploadedFolder = servletConfig.getInitParameter("uploadedFolder");
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.getRequestDispatcher("BusinessCardService/start.html").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd_HH:mm:ss");
		Date date = new Date(System.currentTimeMillis());

		OutputStream output = null;
		InputStream filecontent = null;

		try {
			final Part filePart = request.getPart("file");
			final String fileName = getFileName(filePart);

			//create parent folder
			File parentFolder = new File(uploadedFolder + File.separator + dateFormat.format(date));
			if (!parentFolder.exists())
				parentFolder.mkdirs();

			File image = new File(parentFolder.getAbsolutePath() + File.separator + fileName);
			//write file
			output = new FileOutputStream(image);
			filecontent = filePart.getInputStream();
			int read = 0;
			final byte[] bytes = new byte[1024];

			while ((read = filecontent.read(bytes)) != -1) {
				output.write(bytes, 0, read);
			}

			output.close();
			filecontent.close();

			out.println("<html>");
			out.println("<head>");
			out.println("<title>Business Card Service</title>");
			out.println("</head>");
			out.println("<body>");

			this.scanAndPrintResults(image, out);
		} catch (Exception e) {
			out.println("<p>Error while uploading Image.</p>");
		}

		out.println("</body>");
		out.println("</html>");
	}

	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//save user result
		request.getRequestDispatcher("BusinessCardService/thankyou.html").forward(request, response);
	}

	private String getFileName(final Part part) {
		final String partHeader = part.getHeader("content-disposition");
		for (String content : part.getHeader("content-disposition").split(";")) {
			if (content.trim().startsWith("filename")) {
				return content.substring(content.indexOf('=') + 1).trim().replace("\"", "");
			}
		}
		return null;
	}

	private void scanAndPrintResults(File image, PrintWriter out) {

		out.println("<form name=\"user-solution\" method=\"put\" action=\"/BusinessCardService/scanner\">");
		//color STYLE="color: #FFFFFF; font-family: Verdana; font-weight: bold; font-size: 12px; background-color: #72A4D2;"

		out.println("");
		out.println("<input type=\"submit\" value=\"Submit\">");
	}
}
