package br.com.mjv.service;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.mjv.clockify.excel.ExcelService;
import br.com.mjv.clockify.restservice.ClockifyRestService;
import br.com.mjv.dto.Atividade;

public class Execute {
	
	private static int mes;

	private static int ano;
	
	private static String NOME_COLABORADOR;
	
	private static String apiKey;

	@Autowired
	ClockifyRestService clockifyRestService;
	
	@Autowired
	ExcelService excelService;

	public static void main(String[] args) throws IOException, InvalidFormatException, ParseException {

		System.out.println("Executando...");
		
		apiKey = args[0];//"Xg5CvFFchCIa1aT8";
		NOME_COLABORADOR = args[1];
		mes = Integer.parseInt(args[2]);
		ano = Integer.parseInt(args[3]);
		
		Execute exe = new Execute();
		exe.run();

		System.out.println("Processo finalizado.");

	}

	private void run() throws IOException, InvalidFormatException {

		

		List<Atividade> atividades = clockifyRestService.carregarAtividadesFromClockify(ano, mes, apiKey);

		excelService.updatePlanilha(atividades, ano, mes);

	}

}
