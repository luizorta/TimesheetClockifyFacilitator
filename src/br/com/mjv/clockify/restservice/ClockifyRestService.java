package br.com.mjv.clockify.restservice;

import java.io.IOException;
import java.util.ArrayList;
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
	
	

	

	public List<Atividade> carregarAtividadesFromClockify(int ano, int mes, String apiKey) throws IOException {

		String url = "https://api.clockify.me/api/workspaces/" + MJV_WORKSPACE + "/reports/summary/";

		List<Atividade> atividades = new ArrayList<Atividade>();

		Client client = ClientBuilder.newClient();
		WebTarget resource = client.target(url.toString());

		Invocation.Builder request = resource.request();
		request.header("X-Api-Key", apiKey);
		request.accept(MediaType.APPLICATION_JSON_TYPE);

		String startDate = ano + "-" + StringUtils.leftPad(String.valueOf(mes), 2, '0') + "-01T00:00:00Z";
		//2020-05-31T23:59:59.999Z
		String endDate   = ano + "-" + StringUtils.leftPad(String.valueOf(mes), 2, '0') + "-31T23:59:59Z";
		
		
		String body = "{\n" + "\"startDate\": \"" + startDate + "\",\n"
				+ "\"endDate\": \"" + endDate + "\",\n" + "\"me\": \"true\",\n" + "\"userGroupIds\": [],\n"
				+ "\"userIds\": [],\n" + "\"projectIds\": [],\n" + "\"clientIds\": [],\n" + "\"taskIds\": [],\n"
				+ "\"tagIds\": [],\n" + "\"billable\": \"BOTH\",\n" + "\"includeTimeEntries\": \"true\",\n"
				+ "\"zoomLevel\": \"month\",\n" + "\"description\": \"\",\n" + "\"archived\": \"Active\",\n"
				+ "\"roundingOn\": \"false\"\n" + "}";

		ClockifyResponse response = request.post(Entity.json(body)).readEntity(ClockifyResponse.class);

		for (Entry resultado : response.getTimeEntries()) {

			Atividade atividade = new Atividade();
			atividade.setDescricao(resultado.getDescription());
			atividade.setNomeProjeto(resultado.getProject().getName());

			String firstDate = resultado.getTimeInterval().getStart();
			firstDate = firstDate.substring(0, firstDate.indexOf("T"));

			atividade.setData(firstDate);

			atividade.setTotalHoras(Util.getTotalHoras(firstDate, response));

			if (atividades.size() >= 1) {
				Atividade ultimaAtividade = atividades.get(atividades.size() - 1);

				if (!ultimaAtividade.getData().equalsIgnoreCase(atividade.getData())) {
					atividades.add(atividade);
				}
			} else {
				atividades.add(atividade);
			}

		}

		return atividades;

	}

	

	

	

	
}