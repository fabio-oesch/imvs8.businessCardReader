package ch.imvs8.businesscardservice;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.FileItemIterator;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;

@WebServlet("/scanner")
public class BusinessCardServiceServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final int maxFileSize = 50 * 1024;
	private static final int maxMemSize = 4 * 1024;
	private static final String saveFolder = "/";

	private final String repoFolder;
	private DiskFileItemFactory factory;

	public BusinessCardServiceServlet() {
		final String os = System.getProperty("os.name");

		if (os.contains("Windows"))
			repoFolder = "C:\\temp";
		else
			repoFolder = "/tmp";

		factory = new DiskFileItemFactory();
		factory.setSizeThreshold(maxMemSize);
		// Location to save data that is larger than maxMemSize.
		factory.setRepository(new File(this.repoFolder));
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("get");
		request.getRequestDispatcher("BusinessCardService/start.html").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("Post");
		response.setContentType("text/html");

		PrintWriter out = response.getWriter();
		out.println("<html>");
		out.println("<head>");
		out.println("<title>Business Card Service</title>");
		out.println("</head>");
		out.println("<body>");
		if (!ServletFileUpload.isMultipartContent(request)) {
			out.println("<p>Nothing uploaded</p>");
		} else {
			// Create a new file upload handler

			ServletFileUpload upload = new ServletFileUpload(factory);

			// maximum file size to be uploaded.
			upload.setSizeMax(maxFileSize);
			System.out.println("hello");
			File f = this.saveFile(upload, request);
			//businesscardreader call
			this.printUserForm(out);
		}

		out.println("</body>");
		out.println("</html>");
	}

	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//save user result
		request.getRequestDispatcher("BusinessCardService/thankyou.html").forward(request, response);
	}

	/**
	 * Saves a file by the user
	 * 
	 * @param upload
	 * @param request
	 * @return File object
	 */
	private File saveFile(ServletFileUpload upload, HttpServletRequest request) {
		File file = null;
		try {
			System.out.println("bla");
			FileItemIterator it = upload.getItemIterator(request);
			/*
			 * Map<String, List<FileItem>> stuff =
			 * upload.parseParameterMap(request);
			 * 
			 * Iterator<List<FileItem>> it = stuff.values().iterator();
			 */

			while (it.hasNext()) {
				FileItem item = (FileItem) it.next();
				if (!item.isFormField()) {
					String fileName = item.getName();
					String contentType = item.getContentType();
					boolean isInMemory = item.isInMemory();
					long sizeInBytes = item.getSize();
					file = new File(saveFolder + fileName);
					item.write(file);
					System.out.println(file);
					return file;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private void printUserForm(PrintWriter out) {
		out.println("<form name=\"user-solution\" method=\"put\" action=\"/BusinessCardService/scanner\">");
		//color STYLE="color: #FFFFFF; font-family: Verdana; font-weight: bold; font-size: 12px; background-color: #72A4D2;"

		out.println("");
		out.println("<input type=\"submit\" value=\"Submit\">");
		out.println("</form>");
	}
}
