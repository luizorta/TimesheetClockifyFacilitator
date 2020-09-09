package br.com.mjv.clockify.restservice;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.mjv.clockify.dto.Entry;
import br.com.mjv.clockify.dto.Project;
import br.com.mjv.clockify.dto.ReportSummaryResponse;
import br.com.mjv.clockify.dto.User;
import br.com.mjv.dto.Atividade;
import br.com.mjv.utils.DateUtils;

public class ClockifyRestService {

	private static final String MJV_WORKSPACE = "5cd5b8daf15c98690baa2da3";

	public static List<Atividade> reportSummary(int ano, int mes, String apiKey) throws IOException {

		String url = "https://api.clockify.me/api/workspaces/" + MJV_WORKSPACE + "/reports/summary/";

		List<Atividade> atividades = new ArrayList<Atividade>();

		Client client = ClientBuilder.newClient();
		WebTarget resource = client.target(url.toString());

		Invocation.Builder request = resource.request();
		request.header("X-Api-Key", apiKey);
		request.accept(MediaType.APPLICATION_JSON_TYPE);

		String startDate = ano + "-" + StringUtils.leftPad(String.valueOf(mes), 2, '0') + "-01T00:00:00Z";

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, mes - 1);
		cal.set(Calendar.YEAR, ano);

		int lastDayOfMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		String endDate = ano + "-" + StringUtils.leftPad(String.valueOf(mes), 2, '0') + "-" + lastDayOfMonth
				+ "T23:59:59Z";

		String body = "{\n" + "\"startDate\": \"" + startDate + "\",\n" + "\"endDate\": \"" + endDate + "\",\n"
				+ "\"me\": \"true\",\n" + "\"userGroupIds\": [],\n" + "\"userIds\": [],\n" + "\"projectIds\": [],\n"
				+ "\"clientIds\": [],\n" + "\"taskIds\": [],\n" + "\"tagIds\": [],\n" + "\"billable\": \"BOTH\",\n"
				+ "\"includeTimeEntries\": \"true\",\n" + "\"zoomLevel\": \"month\",\n" + "\"description\": \"\",\n"
				+ "\"archived\": \"Active\",\n" + "\"roundingOn\": \"false\"\n" + "}";

		ReportSummaryResponse response = request.post(Entity.json(body)).readEntity(ReportSummaryResponse.class);

		for (Entry resultado : response.getTimeEntries()) {

			Atividade atividade = new Atividade();
			atividade.setDescricao(resultado.getDescription() != null ? resultado.getDescription() : "");
			atividade.setProjeto(resultado.getProject());

			LocalDate dataAtividade = resultado.getTimeInterval().getStart().toLocalDate();
			atividade.setData(dataAtividade);
			atividade.setTotalHoras(DateUtils.getTotalHorasClockify(dataAtividade, response.getTimeEntries()));

			if (atividades.size() >= 1) {
				Atividade ultimaAtividade = atividades.get(atividades.size() - 1);

				if (!ultimaAtividade.getData().isEqual(atividade.getData())) {
					atividades.add(atividade);
				}
			} else {
				atividades.add(atividade);
			}

		}

		return atividades;

	}

	public static void inserirAtividadesClockify(List<Atividade> atividades, String apiKey) {

		for (Atividade atividade : atividades) {
			inserirAtividadeClockify(atividade, apiKey);
		}

	}

	public static void inserirAtividadeClockify(Atividade atividade, String apiKey) {

		System.out.println("Inserindo registro(s) referente ao dia: " + atividade.getData());

		String startDate;
		String endDate;

		String body = "{ \n  \"description\": \"" + atividade.getDescricao() + "\",\n  \"projectId\": \"" + atividade.getProjeto().getId() + "\",\n";
		String initBody = body;

		// Ajuste em horas para enviar ao servidor.
		final int ajuste = 3;

		String seconds = ":00Z";

		LocalDate dataInicioAtividade = atividade.getData();
		LocalDate dataFinalAtividade  = dataInicioAtividade;
		boolean virouDia = false;
		
		LocalTime horario1Entrada = atividade.getHorario1Entrada();
		LocalTime horario1Saida = atividade.getHorario1Saida();
		LocalTime horario2Entrada = atividade.getHorario2Entrada();
		LocalTime horario2Saida = atividade.getHorario2Saida();
		LocalTime horarioSaidaAlmoco = LocalTime.of(12, 0);
		LocalTime horarioVoltaAlmoco = LocalTime.of(13, 0);

		horarioSaidaAlmoco = horarioSaidaAlmoco.plusHours(ajuste);
		horarioVoltaAlmoco = horarioVoltaAlmoco.plusHours(ajuste);

		if (horario1Entrada != null) {
			horario1Entrada = horario1Entrada.plusHours(ajuste);
		}
		if (horario1Saida != null) {
			horario1Saida = horario1Saida.plusHours(ajuste);
			
			if(horario1Saida.isAfter(LocalTime.of(0, 0, 0) ) &&  horario1Saida.isBefore(LocalTime.of(10, 0))){
				virouDia = true;
			}
			
		}
		if (horario2Entrada != null) {
			horario2Entrada = horario2Entrada.plusHours(ajuste);
		}
		if (horario2Saida != null) {
			horario2Saida = horario2Saida.plusHours(ajuste);
		}

		/**
		 * Apenas o primeiro horário de entrada horario1Entrada: 08:00 horario1Saida:
		 * null; horario2Entrada: null; horario2Saida: null;
		 */
		if (horario1Entrada != null && horario1Saida == null && horario2Entrada == null && horario2Saida == null) {
			startDate = dataInicioAtividade + "T" + horario1Entrada + seconds;
			body += "  \"start\": \"" + startDate + "\"";
			body += "\n}";
			insertTimeEntry(body, apiKey);
			body = initBody;
		}

		if (horario1Entrada != null && horario1Saida != null && horario2Entrada == null && horario2Saida == null) {

			/**
			 * Horário de entrada até meio dia - Entrada às 13:00 até horário de saída
			 * horario1Entrada: 08:00; horario1Saida: 17:00; horario2Entrada: null;
			 * horario2Saida: null;
			 */
			if (horario1Saida.getHour() > (12 + ajuste) || (virouDia)) {

				/*
				 * Primeiro registro. Horário de entrada até 12:00 (horário de almoço)
				 */
				startDate = dataInicioAtividade + "T" + horario1Entrada + seconds;
				endDate = dataFinalAtividade + "T" + horarioSaidaAlmoco + seconds;
				body += "  \"start\": \"" + startDate + "\",\n" + "  \"end\": \"" + endDate + "\"";
				body += "\n}";
				insertTimeEntry(body, apiKey);
				body = initBody;

				/*
				 * Segundo registro. das 13:00 (volta do almoço) até horário de saída
				 */
				startDate = dataInicioAtividade + "T" + horarioVoltaAlmoco + seconds;
				if(virouDia) {
					dataFinalAtividade = atividade.getData().plusDays(1);
				}
				endDate = dataFinalAtividade + "T" + horario1Saida + seconds;
				body += "  \"start\": \"" + startDate + "\",\n" + "  \"end\": \"" + endDate + "\"";
				body += "\n}";
				insertTimeEntry(body, apiKey);
				body = initBody;

			} else {

				/**
				 * Horário de entrada até horário da primeira saída (antes do almoço)
				 * horario1Entrada: 08:00; horario1Saida: 11:00; horario2Entrada: null;
				 * horario2Saida: null;
				 */
				startDate = dataInicioAtividade + "T" + horario1Entrada + seconds;
				endDate = dataFinalAtividade + "T" + horario1Saida + seconds;
				body += "  \"start\": \"" + startDate + "\",\n" + "  \"end\": \"" + endDate + "\"";
				body += "\n}";
				insertTimeEntry(body, apiKey);
				body = initBody;
			}
		}

		if (horario1Entrada != null && horario1Saida != null && horario2Entrada != null) {

			/**
			 * Horário de entrada até horário primeira saída horario1Entrada: 08:00;
			 * horario1Saida: 11:00; horario2Entrada: 13:00; horario2Saida: ?;
			 */
			startDate = dataInicioAtividade + "T" + horario1Entrada + seconds;
			endDate = dataFinalAtividade + "T" + horario1Saida + seconds;
			body += "  \"start\": \"" + startDate + "\",\n" + "  \"end\": \"" + endDate + "\"";
			body += "\n}";
			insertTimeEntry(body, apiKey);
			body = initBody;

			/**
			 * Horário segunda entrada horario1Entrada: 08:00; horario1Saida: 11:00;
			 * horario2Entrada: 13:22; horario2Saida: null;
			 */
			if (horario2Saida == null) {
				/**
				 * + Horário segunda entrada até horário segunda saída
				 */
				startDate = dataInicioAtividade + "T" + horario2Entrada + seconds;
				body += "  \"start\": \"" + startDate + "\"";
				body += "\n}";
				insertTimeEntry(body, apiKey);
				body = initBody;
			} else {
				/**
				 * Horário segunda entrada + horário da segunda saída horario1Entrada: 08:00;
				 * horario1Saida: 11:00; horario2Entrada: 13:00; horario2Saida: 19:00;
				 */
				startDate = dataInicioAtividade + "T" + horario2Entrada + seconds;
				endDate = dataFinalAtividade + "T" + horario2Saida + seconds;
				body += "  \"start\": \"" + startDate + "\",\n" + "  \"end\": \"" + endDate + "\"";
				body += "\n}";
				insertTimeEntry(body, apiKey);
				body = initBody;
			}

		}

	}

	private static void insertTimeEntry(String body, String apiKey) {
		String url = "https://api.clockify.me/api/v1/workspaces/" + MJV_WORKSPACE + "/time-entries";

		Client client = ClientBuilder.newClient();
		WebTarget resource = client.target(url.toString());

		Invocation.Builder request = resource.request();
		request.header("X-Api-Key", apiKey);
		request.accept(MediaType.APPLICATION_JSON_TYPE);

		try {
			String json = request.post(Entity.json(body)).readEntity(String.class);
			ObjectMapper mapper = new ObjectMapper();
			JsonNode node = mapper.readTree(json);
			if (node.get("message") != null) {
				String message = node.get("message").asText();
				System.err.println(message);
				System.err.println("body: " + body);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void deleteTimeEntry(String id, String apiKey) {
		String url = "https://api.clockify.me/api/v1/workspaces/" + MJV_WORKSPACE + "/time-entries/" + id;

		Client client = ClientBuilder.newClient();
		WebTarget resource = client.target(url.toString());

		Invocation.Builder request = resource.request();
		request.header("X-Api-Key", apiKey);
		request.accept(MediaType.APPLICATION_JSON_TYPE);

		request.delete();

	}

	public static List<Atividade> timeEntries(int ano, int mes, String apiKey, User user, boolean buscarInfosProjeto) throws IOException {
		String url = "https://api.clockify.me/api/v1/workspaces/" + MJV_WORKSPACE + "/user/" + user.getId()
				+ "/time-entries/";

		List<Atividade> atividades = new ArrayList<Atividade>();

		String startDate = ano + "-" + StringUtils.leftPad(String.valueOf(mes), 2, '0') + "-01T00:00:00Z";
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, mes - 1);
		cal.set(Calendar.YEAR, ano);
		int lastDayOfMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		String endDate = ano + "-" + StringUtils.leftPad(String.valueOf(mes), 2, '0') + "-" + lastDayOfMonth
				+ "T23:59:59Z";

		Client client = ClientBuilder.newClient();
		WebTarget resource = client.target(url.toString());
		resource = resource.queryParam("start", startDate);
		resource = resource.queryParam("end", endDate);

		Invocation.Builder request = resource.request();
		request.header("X-Api-Key", apiKey);
		request.accept(MediaType.APPLICATION_JSON_TYPE);

		String json = request.get().readEntity(String.class);
		ObjectMapper mapper = new ObjectMapper();

		List<Entry> entries = mapper.readValue(json, new TypeReference<List<Entry>>() {});
		
		HashMap<String, Project> projectMap = new HashMap<String, Project>();
		
		if(buscarInfosProjeto) {
			/*
			 * Vai buscar os codigos dos projetos e guardar num HashMap.
			 */
			for (Entry entry : entries) {
				
				String codigo = entry.getProjectId();
				
				if(projectMap.get(codigo) == null) {
					Project projeto = getProjectByID(entry.getProjectId(), apiKey);
					projectMap.put(codigo, projeto);			
				}
				
			}
		}

		for (Entry entry : entries) {

			Atividade atividade = new Atividade();
			atividade.setDescricao(entry.getDescription() != null ? entry.getDescription() : "");
			
			Project projeto = projectMap.get(entry.getProjectId());
			atividade.setProjeto(projeto);

			LocalDate dataAtividade = entry.getTimeInterval().getStart().toLocalDate();
			atividade.setData(dataAtividade);

			atividade.setTotalHoras(DateUtils.getTotalHorasClockify(dataAtividade, entries));
			
			atividade.setTimeEntryID(entry.getId());

			if (atividades.size() >= 1) {
				Atividade ultimaAtividade = atividades.get(atividades.size() - 1);

				if (!ultimaAtividade.getData().isEqual(atividade.getData())) {
					atividades.add(atividade);
				}
			} else {
				atividades.add(atividade);
			}

		}

		return atividades;
	}
	
	
	public static Project getProjectByID(String projectID, String apiKey) throws IOException {
		String url = "https://api.clockify.me/api/v1/workspaces/" + MJV_WORKSPACE + "/projects/" + projectID;


		Client client = ClientBuilder.newClient();
		WebTarget resource = client.target(url.toString());

		Invocation.Builder request = resource.request();
		request.header("X-Api-Key", apiKey);
		request.accept(MediaType.APPLICATION_JSON_TYPE);

		Project response = request.get().readEntity(Project.class);
		
		return response;

	}
}