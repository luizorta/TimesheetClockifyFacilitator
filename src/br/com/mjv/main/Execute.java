package br.com.mjv.main;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import br.com.mjv.clockify.restservice.ClockifyRestService;
import br.com.mjv.dto.Atividade;
import br.com.mjv.excel.ExcelService;
import br.com.mjv.ifractal.IFractalService;

public class Execute {
	
	private static int mes;

	private static int ano;
	
	private static String nomeColaborador;
	
	private static String apiKey;

	public static void main(String[] args) throws IOException, InvalidFormatException, ParseException {

		System.out.println("Executando...");
		
		apiKey = args[0];
		nomeColaborador = args[1];
		mes = Integer.parseInt(args[2]);
		ano = Integer.parseInt(args[3]);
		
		Execute exe = new Execute();
		exe.run();

		System.out.println("Processo finalizado.");

	}

	private void run() throws IOException, InvalidFormatException {

		List<Atividade> atividadesIfractal = IFractalService.loadAtividadesFromIFractal(ano, mes);
		
		
		
		
		ClockifyRestService.inserirAtividadesClockify(atividadesIfractal, apiKey);

		//List<Atividade> atividades = clockifyRestService.carregarAtividadesFromClockify(ano, mes, apiKey);

		//excelService.updatePlanilha(nomeColaborador, atividades, ano, mes);

	}

}
