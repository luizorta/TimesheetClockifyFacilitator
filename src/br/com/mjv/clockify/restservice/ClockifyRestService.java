package br.com.mjv.clockify.restservice;

import java.io.IOException;
import java.time.LocalDate;
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
		// 2020-05-31T23:59:59.999Z
		
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, mes-1);
		cal.set(Calendar.YEAR, ano);
		
		int lastDayOfMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		String endDate = ano + "-" + StringUtils.leftPad(String.valueOf(mes), 2, '0') + "-" + lastDayOfMonth + "T23:59:59Z";

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

//		String url = "https://api.clockify.me/api/v1/workspaces/" + MJV_WORKSPACE + "/time-entries";
//
//		Client client = ClientBuilder.newClient();
//		WebTarget resource = client.target(url.toString());
//
//		Invocation.Builder request = resource.request();
//		request.header("X-Api-Key", apiKey);
//		request.accept(MediaType.APPLICATION_JSON_TYPE);
//
//		String data = Util.formatDate(atividade.getData());
//		int horaEntrada = Integer.parseInt(atividade.getHorario1Entrada().substring(0, 2));
//		int minutoEntrada = Integer.parseInt(atividade.getHorario1Entrada().substring(3, 5));
//		
//		String startDate = data + "T" + StringUtils.leftPad(String.valueOf((horaEntrada+3)), 2, '0') + 
//				":" +
//				StringUtils.leftPad(String.valueOf(minutoEntrada), 2, '0') + ":00Z";
//		
//		String body = "";
//
//		/*
//		 * Se ja tiver batido o ponto, pega o horario da saida
//		 */
//		if (atividade.getHorario1Saida() != null) {
//			
//			int horas1Saida = Integer.parseInt(atividade.getHorario1Saida().substring(0, 2));
//			
//			String endDate;
//			
//			if(horas1Saida > 12) {
//				endDate = data + "T15:00:00Z";
//				body = "{\n" + "\"start\": \"" + startDate + "\",\n" + "\"end\": \"" + endDate + "\"\n" + "}";
//				request.post(Entity.json(body)).readEntity(String.class);
//				
//				startDate = data + "T16:00:00Z";
//				
//				int horaSaida = Integer.parseInt(atividade.getHorario1Saida().substring(0, 2));
//				int minutoSaida = Integer.parseInt(atividade.getHorario1Saida().substring(3, 5));
//				endDate   = data + "T" + StringUtils.leftPad(String.valueOf((horaSaida+3)), 2, '0') + 
//						":" +
//						StringUtils.leftPad(String.valueOf(minutoSaida), 2, '0') + ":00Z";
//				body = "{\n" + "\"start\": \"" + startDate + "\",\n" + "\"end\": \"" + endDate + "\"\n" + "}";
//				
//				request.post(Entity.json(body)).readEntity(String.class);
//				
//			}else {
//				int horaSaida = Integer.parseInt(atividade.getHorario1Saida().substring(0, 2));
//				endDate = data + "T" + StringUtils.leftPad(String.valueOf((horaSaida+3)), 2, '0') + ":00Z";
//				body = "{\n" + "\"start\": \"" + startDate + "\",\n" + "\"end\": \"" + endDate + "\"\n" + "}";
//				
//				request.post(Entity.json(body)).readEntity(String.class);
//			}
//			
//		} else {
//			body = "{\n" + "  \"start\": \"" + startDate + "\"\n" + "}";
//			request.post(Entity.json(body)).readEntity(String.class);
//		}

	}

}