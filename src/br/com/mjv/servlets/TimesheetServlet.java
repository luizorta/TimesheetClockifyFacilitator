package br.com.mjv.servlets;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import br.com.mjv.clockify.dto.Project;
import br.com.mjv.clockify.dto.User;
import br.com.mjv.control.TimesheetControl;
import br.com.mjv.dto.Atividade;

public class TimesheetServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		if(request.getParameter("action") == null || request.getParameter("action").equalsIgnoreCase("inicio")) {
			RequestDispatcher rd = request.getRequestDispatcher("index.jsp");  
			rd.forward(request, response);  
		} else if(request.getParameter("action").equalsIgnoreCase("inserir")) {
			
			boolean isExport = request.getParameter("isExport") == null ? false : true;
						
			String data = request.getParameter("mesAno");
			String nomeColaborador = request.getParameter("userName");
			
			int mes = Integer.parseInt(data.substring(0, 2));
			int ano = Integer.parseInt(data.substring(3, 7));
			
			byte[] bytes = iniciarProcesso(
						
					request.getParameter("description"),	
					request.getParameter("projectId"),
					request.getParameter("userName"),
					request.getParameter("userId"),
					request.getParameter("apiKey"),
					mes,
					ano,
					request.getParameter("content"),
					isExport
					
					);
			
			
			if(isExport) {
				String strMes = StringUtils.leftPad(String.valueOf(mes), 2, '0');
				
				LocalDate dt = new LocalDate(ano, mes, 1);
				DateTimeFormatter fmt = DateTimeFormat.forPattern("MMMM").withLocale(new Locale("pt", "BR"));
				//DateTimeFormatter fmt = DateTimeFormat.forPattern("MMMM").withLocale(Locale.forLanguageTag("en-US"));
				String strMesExtenso = fmt.print(dt);
				
				String filename = strMes + ". Planilha de atividades - " + strMesExtenso + "." + (ano+"").substring(2,4) + " - "+  WordUtils.capitalize(nomeColaborador.toLowerCase());
				
				
				response.reset();
				response.setContentType("application/vnd.ms-excel");
				response.setHeader("Content-Transfer-Encoding", "binary");
				response.setHeader("Content-Disposition","attachment; filename=\""+filename+".xlsx\""); 
				response.setContentLength(bytes.length);
				
				
				OutputStream out = response.getOutputStream(); 
				out.write(bytes);
				
				out.flush();
			} else {
			
				RequestDispatcher rd = request.getRequestDispatcher("index.jsp?mensagemAlerta=Atualiza��o executada com sucesso!");
				response.setHeader("Content-Type", "text/xml; charset=ISO-8859-1");
				rd.forward(request, response);

			}
			  
		}
		
		
	}

	private byte[] iniciarProcesso(String description, String projectId, String userName, String userId, String apiKey,
			int mes, int ano, String content, boolean isExport) {
		Atividade atividade = new Atividade();
		atividade.setDescricao(description);

		Project projeto = new Project();
		projeto.setId(projectId);
		atividade.setProjeto(projeto);

		User user = new User();
		user.setName(userName);
		user.setId(userId);

		TimesheetControl tsE = new TimesheetControl();
		return tsE.iniciarProcesso(atividade, apiKey, mes, ano, user, content, isExport);

	}

}
