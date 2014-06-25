package ch.imvs8.businesscardservice;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import ch.fhnw.imvs8.businesscardreader.BusinessCardReader;
import ch.fhnw.imvs8.businesscardreader.Word;


@WebServlet(name = "BusinessCardReader", urlPatterns = { "/reader" })
@MultipartConfig
public class BusinessCardServiceServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String scanResultFile = "scanresults.txt";
	private static final String actualResultFile = "actualresults.txt";
	private static final String[] labels = {"TIT", "FN", "LN", "EMA" ,"ORG","I-MN", "I-TN", "I-FN","ST", "PLZ",
			"ORT", "WEB", "IDK" };
	private static final String[] labelNames = { "Title", "First Name", "Last Name","Organisation", "Mobile Number",
		"Fixnet Number", "Fax Number","Street", "Zip Code", "City","Web","Unknown"};
	
	private BusinessCardReader reader;
	private String uploadedFolder;

	public BusinessCardServiceServlet() {

	}

	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		this.uploadedFolder = servletConfig.getInitParameter("uploadedFolder");
		try {
			this.reader = new BusinessCardReader(
					servletConfig.getInitParameter("businessCardDataFolder"));
		} catch (Exception e) {
			throw new ServletException("unable to create Servlet: "
					+ e.getMessage(), e);
		}
		
		super.init(servletConfig);
	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		request.getRequestDispatcher("BusinessCardService/start.html").forward(
				request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		if (request.getParameter("step").equals("1")) {
			PrintWriter out = response.getWriter();
			DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd_HH:mm:ss");
			Date date = new Date(System.currentTimeMillis());

			OutputStream output = null;
			InputStream filecontent = null;

			try {
				final Part filePart = request.getPart("file");
				final String fileName = getFileName(filePart);

				// create parent folder
				File parentFolder = new File(uploadedFolder + File.separator
						+ dateFormat.format(date));
				if (!parentFolder.exists())
					parentFolder.mkdirs();

				File image = new File(parentFolder.getAbsolutePath()
						+ File.separator + fileName);

				// write file
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
				out.println("<p>Error while uploading Image: " + e.getMessage()
						+ "</p>");
				out.println(e.getStackTrace());
			}

			out.println("</body>");
			out.println("</html>");
			
		} else if (request.getParameter("step").equals("2")) {
			// save user result
			response.setContentType("application/octet-stream");
			Map<String,String> corrected = new HashMap<String,String>();
			try {
				String path = request.getParameter("folder");
				FileWriter actualResultsFile = new FileWriter(path + File.separator
						+ actualResultFile);
				
				for (int i = 0; i < labels.length; i++) {
					String text = request.getParameter(labels[i]);
					if (text != null) {
						corrected.put(labels[i],text);
						actualResultsFile.write(labels[i] + ": " + text + "\n");
					}
				}
				actualResultsFile.close();
				
				
				//return vCard
				if(corrected.size() > 0) {
					String vCard =  reader.getVCardString(corrected);
					 
					 //write to local file system
					String vCardFileName = "vCard";
					FileWriter vCardFile = new FileWriter(path + File.separator+vCardFileName);
					vCardFile.write(vCard);
					vCardFile.close();
					
					HttpSession session = request.getSession();
					session.setAttribute("vcard", vCard);

					response.sendRedirect("download");	
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	private String getFileName(final Part part) {
		final String partHeader = part.getHeader("content-disposition");
		for (String content : part.getHeader("content-disposition").split(";")) {
			if (content.trim().startsWith("filename")) {
				return content.substring(content.indexOf('=') + 1).trim()
						.replace("\"", "");
			}
		}
		return null;
	}

	private void scanAndPrintResults(File folder, File image, PrintWriter out)
			throws Exception {
		Map<String, Word> result = reader.readImage(image
				.getAbsolutePath());

		FileWriter scanOutput = new FileWriter(folder.getAbsoluteFile()
				+ File.separator + scanResultFile);

		out.println("<form enctype=\"multipart/form-data\" name=\"user-solution\" method=\"post\" action=\"reader\">");
		out.println("<input type=\"hidden\" name=\"step\" value=\"2\" />");
		out.println("<input type=\"hidden\" name=\"folder\" value=\""
				+ folder.getAbsolutePath() + "\">");
		if (result != null) {
			for (int i = 0; i < labels.length; i++) {

				StringBuilder outputString = new StringBuilder();
					//write debug output
					Word word= result.get(labels[i]);
					for(int j = 0; j < word.getSubwordSize();j++) {
						scanOutput.write(labels[i] + "; "+ word.getSubwordAndPosition(j) + "\n");
					}
					
					outputString.append(labelNames[i]);
					outputString.append(": <input type=\"text\" name=\"");
					outputString.append(labels[i]);
					outputString.append("\" id=\"");
					outputString.append(labels[i]);
					outputString.append("\" value=\"");
					outputString.append(word.getWordAsString());
					outputString.append("\"");
					if(word.isUnsure())
						outputString.append(" STYLE=\"background-color: #FF9933;\"");
					if(word.isWrong())
						outputString.append(" STYLE=\"color: #FFFFFF; background-color: #800000;\"");
					outputString.append("/><br>");

					out.println(outputString.toString());
			}
			scanOutput.close();
		}
		// color
		// class="textbox"
		// STYLE="color: #FFFFFF; font-family: Verdana; font-weight: bold; font-size: 12px; background-color: #72A4D2;"

		out.println("<input type=\"submit\" value=\"Submit\">");
		out.println("</form>");
	}
}
