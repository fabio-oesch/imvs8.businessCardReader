package ch.imvs8.businesscardservice;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import ch.fhnw.imvs8.businesscardreader.BusinessCardReader;
import ch.fhnw.imvs8.businesscardreader.ner.NamedEntity;

@WebServlet(name = "BusinessCardService", urlPatterns = { "/scanner" })
@MultipartConfig
public class BusinessCardServiceServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String scanResultFile = "scanresults.txt";
	private static final String actualResultFile = "actualresults.txt";
	private static final String[] labels = { "FN", "LN", "TIT", "ST", "PLZ", "ORT", "I-MN", "I-TN", "I-FN", "EMA", "WEB", "ORG" };
	private static final String[] labelNames = { "First Name", "Last Name", "Title", "Street", "Zip Code", "City", "Mobile Number:", "Fixnet Number", "Fax Number", "Email",
			"Organisation" };

	private BusinessCardReader reader;
	private String uploadedFolder;

	public BusinessCardServiceServlet() {

	}

	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		this.uploadedFolder = servletConfig.getInitParameter("uploadedFolder");
		try {
			this.reader = new BusinessCardReader(servletConfig.getInitParameter("businessCardDataFolder"));
		} catch (Exception e) {
			throw new ServletException("unable to create Servlet", e);
		}
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

			this.scanAndPrintResults(parentFolder, image, out);
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

	private void scanAndPrintResults(File folder, File image, PrintWriter out) throws IOException {
		try {
			Map<String, NamedEntity> result = reader.readImage(image.getAbsolutePath());
			FileWriter scanOutput = new FileWriter(folder.getAbsoluteFile() + File.separator + scanResultFile);
			out.println("<form name=\"user-solution\" method=\"post\" action=\"/BusinessCardService/scanner\">");

			for (int i = 0; i < labels.length; i++) {
				scanOutput.write(labels[i] + ": " + result.get(labels[i]) + "\n");

				StringBuilder outputString = new StringBuilder();
				outputString.append(labelNames[i]);
				outputString.append(": <input type=\"text\" name=\"");
				outputString.append(labels[i]);
				outputString.append("\" id=\"");
				outputString.append(labels[i]);
				outputString.append("\" text=\"");
				outputString.append(result.get(labels[i]));
				outputString.append("\"><br>");

				out.println(outputString.toString());
			}
			//color STYLE="color: #FFFFFF; font-family: Verdana; font-weight: bold; font-size: 12px; background-color: #72A4D2;"

			out.println("<input type=\"submit\" value=\"Submit\">");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			out.println("Error while reading file: " + e.getMessage());
		}
	}
}
