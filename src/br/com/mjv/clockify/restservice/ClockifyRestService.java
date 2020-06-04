package br.com.mjv.clockify.restservice;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import br.com.mjv.clockify.dto.ClockifyResponse;
import br.com.mjv.clockify.dto.Entry;
import br.com.mjv.dto.Atividade;

public class ClockifyRestService {

	private static int mes;

	private static int ano;
	
	private static String apiKey;
	
	private static final String MJV_WORKSPACE = "5cd5b8daf15c98690baa2da3";
	
	private static String NOME_COLABORADOR;

	public static void main(String[] args) throws IOException, InvalidFormatException, ParseException {

		System.out.println("Executando...");
		apiKey = "Xg5CvFFchCIa1aT8";
		//NOME_COLABORADOR = args[]
		mes = 5;
		ano = 2020;
		
		
		

		List<Atividade> atividades = carregarAtividadesFromClockify();

		updatePlanilha(atividades);

		System.out.println("Processo finalizado.");

	}

	private static List<Atividade> carregarAtividadesFromClockify() throws IOException {

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

			atividade.setTotalHoras(getTotalHoras(firstDate, response));

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

	private static void updatePlanilha(List<Atividade> atividades) throws InvalidFormatException, IOException {

		File file = new File("entrada.xlsx");
		FileInputStream fis = new FileInputStream(file);
		XSSFWorkbook workbook = new XSSFWorkbook(fis);
		XSSFSheet sheet = workbook.getSheetAt(0);

		/*
		 * Atualiza as informações do NOME DO COLABORADOR
		 */
		CellReference cellReference = new CellReference("E8");
		Row row = sheet.getRow(cellReference.getRow());
		Cell cell = row.getCell(cellReference.getCol());
		cell.setCellType(CellType.STRING);
		cell.setCellValue("LUIZ EDUARDO ARPELAU ORTA");

		/*
		 * Inicio das datas Domingos nao aparecem na planilha
		 * 
		 */
		List<String> datas = getDatasMes();
		int i = 1;
		for (String data : datas) {

			try {

				for (Atividade atividade : atividades) {

					/*
					 * Adiciona o horário total do dia
					 */
					if (atividade.getData().equalsIgnoreCase(data)) {
						cellReference = new CellReference("B" + (i + 10));
						row = sheet.getRow(cellReference.getRow());
						cell = row.getCell(cellReference.getCol());
						cell.setCellType(CellType.STRING);
						cell.setCellValue(atividade.getTotalHoras() + ":00");
						// Sets the allignment to the created cell
						CellUtil.setAlignment(cell, HorizontalAlignment.CENTER);

						/**
						 * Adiciona a descrição da atividade
						 */
						cellReference = new CellReference("D" + (i + 10));
						row = sheet.getRow(cellReference.getRow());
						cell = row.getCell(cellReference.getCol());
						cell.setCellType(CellType.STRING);
						cell.setCellValue(atividade.getDescricao());

						/**
						 * Adiciona a projeto
						 */
						cellReference = new CellReference("F" + (i + 10));
						row = sheet.getRow(cellReference.getRow());
						cell = row.getCell(cellReference.getCol());
						cell.setCellType(CellType.STRING);
						cell.setCellValue(atividade.getNomeProjeto());
						CellUtil.setAlignment(cell, HorizontalAlignment.CENTER);
					}

				}
			} catch (NullPointerException e) {
				System.out.println("ERRO: " + e);
			}

			i++;

		}

		/*
		 * Total de horas no mês
		 */
		cellReference = new CellReference("D40");
		row = sheet.getRow(cellReference.getRow());
		cell = row.getCell(cellReference.getCol());
		cell.setCellType(CellType.STRING);
		cell.setCellValue(getTotalHorasMes(atividades) + ":00");

		// Write the output to the file
		FileOutputStream fileOut = new FileOutputStream("saida.xlsx");
		workbook.write(fileOut);
		fileOut.close();

		// Closing the workbook
		workbook.close();

	}

	private static List<String> getDatasMes() {

		List<String> dates = new ArrayList<String>();

		for (int i = 1; i <= 31; i++) {

			try {

				LocalDate date = LocalDate.of(ano, mes, i);
				DateTimeFormatter outputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
				String result = date.format(outputFormat);

				DayOfWeek dayOfWeek = date.getDayOfWeek();

				if (dayOfWeek != DayOfWeek.SUNDAY) {
					dates.add(result);
				}

				date.plusDays(1);

			} catch (DateTimeException e) {
				return dates;
			}

		}

		return dates;
	}

	private static String getTotalHoras(String dataBase, ClockifyResponse response) {

		PeriodFormatter pf = new PeriodFormatterBuilder().minimumPrintedDigits(2).printZeroAlways().appendHours()
				.appendLiteral(":").appendMinutes().toFormatter();
		Period period = Period.ZERO;

		for (Entry entrada : response.getTimeEntries()) {

			String dataToCompare = entrada.getTimeInterval().getStart().substring(0,
					entrada.getTimeInterval().getStart().indexOf("T"));

			if (dataBase.equalsIgnoreCase(dataToCompare)) {

				String aux = entrada.getTimeInterval().getDuration();
				String horas = aux.substring(2, aux.indexOf("H")) + ":"
						+ aux.substring(aux.indexOf("H") + 1, aux.indexOf("M"));

				period = period.plus(pf.parsePeriod(horas));

			}

		}

		return pf.print(period.normalizedStandard());

	}

	public static String getTotalHorasMes(List<Atividade> atividades) {

		PeriodFormatter pf = new PeriodFormatterBuilder().minimumPrintedDigits(2).printZeroAlways().appendHours()
				.appendLiteral(":").appendMinutes().toFormatter();
		Period period = Period.ZERO;

		for (Atividade atividade : atividades) {

			period = period.plus(pf.parsePeriod(atividade.getTotalHoras()));
		}

		return pf.print(period.normalizedStandard(PeriodType.time()));

	}
}