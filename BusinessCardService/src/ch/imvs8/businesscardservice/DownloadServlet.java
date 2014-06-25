package ch.imvs8.businesscardservice;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import ch.fhnw.imvs8.businesscardreader.BusinessCardReader;

@WebServlet(name = "VCardDownloadServlet", urlPatterns = { "/download" })
public class DownloadServlet extends HttpServlet {
	
	
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		  	//write to client
			HttpSession session = request.getSession(false);
			
			String vcardString = (String)session.getAttribute("vcard");
			
			String vCardFileName = "vCard.vcard";
			OutputStream clientStream = response.getOutputStream();
			
			Enumeration<String> en = session.getAttributeNames();
			InputStream vCardIn = new ByteArrayInputStream(vcardString.getBytes());
			
			String headerKey = "Content-Disposition";
		    String headerValue = String.format("attachment; filename=\"%s\"", vCardFileName);
		    response.setHeader(headerKey, headerValue);
			 
			byte[] buffer = new byte[4096];
			int length;
			while ((length = vCardIn.read(buffer)) > 0){
			     clientStream.write(buffer, 0, length);
			}
			vCardIn.close();
			clientStream.flush();
	}

}
