package br.com.mjv.clockify.restservice;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.mjv.clockify.dto.ClockifyResponse;
import br.com.mjv.clockify.dto.Entry;
import br.com.mjv.dto.Atividade;
import br.com.mjv.utils.Util;

public class ClockifyRestService {

	private static final String MJV_WORKSPACE = "5cd5b8daf15c98690baa2da3";

	public static List<Atividade> carregarAtividadesFromClockify(int ano, int mes, String apiKey) throws IOException {

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

		ClockifyResponse response = request.post(Entity.json(body)).readEntity(ClockifyResponse.class);

		for (Entry resultado : response.getTimeEntries()) {

			Atividade atividade = new Atividade();
			atividade.setDescricao(resultado.getDescription() != null ? resultado.getDescription() : "");
			atividade.setNomeProjeto(resultado.getProject() != null ? resultado.getProject().getName() : "");

			String firstDate = resultado.getTimeInterval().getStart();
			firstDate = firstDate.substring(0, firstDate.indexOf("T"));

			LocalDate date = LocalDate.parse(firstDate);
			atividade.setData(date);

			atividade.setTotalHoras(Util.getTotalHoras(date, response));

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

		String body = "";

		//Ajuste em horas para enviar ao servidor.
		final int ajuste = 3;
		
		String seconds = ":00Z";
		
		LocalDate dataAtividade   = atividade.getData();
		LocalTime horario1Entrada = atividade.getHorario1Entrada();
		LocalTime horario1Saida   = atividade.getHorario1Saida();
		LocalTime horario2Entrada = atividade.getHorario2Entrada();
		LocalTime horario2Saida   = atividade.getHorario2Saida();
		LocalTime horarioSaidaAlmoco  = LocalTime.of(12, 0);
		LocalTime horarioVoltaAlmoco  = LocalTime.of(13, 0);
		
		horarioSaidaAlmoco = horarioSaidaAlmoco.plusHours(ajuste);
		horarioVoltaAlmoco = horarioVoltaAlmoco.plusHours(ajuste);

				
		if(horario1Entrada != null) {
			horario1Entrada = horario1Entrada.plusHours(ajuste);
		}
		if(horario1Saida != null) {
			horario1Saida = horario1Saida.plusHours(ajuste);
		}
		if(horario2Entrada != null) {
			horario2Entrada = horario2Entrada.plusHours(ajuste);
		}
		if(horario2Saida != null) {
			horario2Saida = horario2Saida.plusHours(ajuste);
		}
		
		/**
		    Apenas o primeiro horário de entrada
		    horario1Entrada:  08:00
			horario1Saida:    null;
			horario2Entrada:  null;
			horario2Saida:    null;
		 */
		if(horario1Entrada!= null && horario1Saida == null && horario2Entrada == null && horario2Saida == null) {
			startDate = atividade.getData() + "T" + horario1Entrada + seconds;
			body      = "{\n" + "  \"start\": \"" + startDate + "\"\n" + "}";
			executeCall(body, apiKey);
		}
		
		if(horario1Entrada!= null && horario1Saida != null && horario2Entrada == null && horario2Saida == null) {
			
			/**
				Horário de entrada até meio dia + entrada às 13:00 até horário de saída
			    horario1Entrada:  08:00;
				horario1Saida:    17:00;
				horario2Entrada:  null;
				horario2Saida:    null;
			 */	
			if (horario1Saida.getHour() > (12 + ajuste)) {
				
				/*
				 * Primeiro registro.
				 * Horário de entrada até 12:00 (horário de almoço)
				 */
				startDate = dataAtividade + "T" + horario1Entrada + seconds;
				endDate   = dataAtividade + "T" + horarioSaidaAlmoco + seconds;
				body      = "{\n" + "\"start\": \"" + startDate + "\",\n" + "\"end\": \"" + endDate + "\"\n" + "}";
				executeCall(body, apiKey);

				/*
				 * Segundo registro.
				 * das 13:00 (volta do almoço) até horário de saída
				 */
				startDate = dataAtividade + "T" + horarioVoltaAlmoco + seconds;
				endDate   = dataAtividade + "T" + horario1Saida + seconds;
				body      = "{\n" + "\"start\": \"" + startDate + "\",\n" + "\"end\": \"" + endDate + "\"\n" + "}";
				executeCall(body, apiKey);
				
			}
			else {
				
				/**
					Horário de entrada até horário da primeira saída (antes do almoço)
					horario1Entrada:  08:00;
					horario1Saida:    11:00;
					horario2Entrada:  null;
					horario2Saida:    null; 
				 */
				startDate = dataAtividade + "T" + horario1Entrada + seconds;
				endDate   = dataAtividade + "T" + horario1Saida + seconds;
				body      = "{\n" + "\"start\": \"" + startDate + "\",\n" + "\"end\": \"" + endDate + "\"\n" + "}";
				executeCall(body, apiKey);
				
			}
		}
		
		
		if(horario1Entrada!= null && horario1Saida != null && horario2Entrada != null) {
			
			/**
				Horário de entrada até horário primeira saída
				horario1Entrada:  08:00;
				horario1Saida:    11:00;
				horario2Entrada:  13:00;
				horario2Saida:    ?;
			 */
			startDate = dataAtividade + "T" + horario1Entrada + seconds;
			endDate   = dataAtividade + "T" + horario1Saida + seconds;
			body      = "{\n" + "\"start\": \"" + startDate + "\",\n" + "\"end\": \"" + endDate + "\"\n" + "}";
			executeCall(body, apiKey);

			/**
				Horário segunda entrada
			    horario1Entrada:  08:00;
				horario1Saida:    11:00;
				horario2Entrada:  13:22;
				horario2Saida:    null;
			 */
			if (horario2Saida == null) {
				/**
				 * + Horário segunda entrada até horário segunda saída
				 */
				startDate = dataAtividade + "T" + horario2Entrada + seconds;
				body      = "{\n" + "  \"start\": \"" + startDate + "\"\n" + "}";
				executeCall(body, apiKey);
			} else {
				/**
					Horário segunda entrada + horário da segunda saída
				    horario1Entrada:  08:00;
					horario1Saida:    11:00;
					horario2Entrada:  13:00;
					horario2Saida:    19:00;
				 */
				startDate = dataAtividade + "T" + horario2Entrada + seconds;
				endDate   = dataAtividade + "T" + horario2Saida + seconds;
				body      = "{\n" + "\"start\": \"" + startDate + "\",\n" + "\"end\": \"" + endDate + "\"\n" + "}";
				executeCall(body, apiKey);
			}
			
		}

	}
	
	private static void executeCall(String body, String apiKey) {
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
			if(node.get("message") != null) {
				String message = node.get("message").asText();
				System.err.println(message);
				System.err.println("body: " + body);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}