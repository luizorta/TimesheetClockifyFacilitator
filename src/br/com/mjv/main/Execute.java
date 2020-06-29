package br.com.mjv.main;

import java.io.IOException;
import java.text.ParseException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import br.com.mjv.clockify.dto.User;
import br.com.mjv.clockify.restservice.ClockifyRestService;
import br.com.mjv.dto.Atividade;
import br.com.mjv.excel.ExcelFacade;
import br.com.mjv.excel.ExcelFacadeImpl;
import br.com.mjv.ifractal.IFractalFacade;
import br.com.mjv.ifractal.IFractalTextFacadeImpl;

public class Execute {

	private static int mes;

	private static int ano;

	private static String nomeColaborador;

	private static String apiKey;

	public static void main(String[] args) throws IOException, InvalidFormatException, ParseException {
		

		Duration.parse("PT80H30M");
		
		System.out.println("Executando...");

		/*
		 * Loading parameters...
		 */
		apiKey = args[0];
		nomeColaborador = args[1];
		mes = Integer.parseInt(args[2]);
		ano = Integer.parseInt(args[3]);

		run();

		System.out.println("Processo finalizado.");

	}

	private static void run() throws IOException, InvalidFormatException {

		//IFractalFacade iFractalFacade = new IFractalHTMLFacadeImpl();
		IFractalFacade iFractalFacade = new IFractalTextFacadeImpl();

		List<Atividade> atividadesIfractal = iFractalFacade.loadAtividadesFromIFractal(ano, mes);
		Collections.sort(atividadesIfractal);
		System.out.println("Total de atividades no iFractal: " + atividadesIfractal.size());

		User user = new User();
		user.setId("5dc07145b36ea8270fcf00c7");
		
		
		/*
		 * Busca todas as entradas para o mes atual
		 */
		List<Atividade> atividadesClockify = ClockifyRestService.timeEntries(ano, mes, apiKey, user);
		
		/*
		 * Apaga atividade que estiver aberta
		 */
		removerAtividadeClockifyEmAndamento(atividadesClockify);
		
		
		/*
		 * Busca novamente as atividades que estarão fechadas agora
		 */
		atividadesClockify = ClockifyRestService.timeEntries(ano, mes, apiKey, user);
		Collections.sort(atividadesClockify);
		System.out.println("Total de atividades no Clockify: " + atividadesClockify.size());

		List<Atividade> atividadesParaInserir = getListaAtividadesIFractalQueNaoForamInseridasNoClockify(
				atividadesIfractal, atividadesClockify);
		
		System.out.println("====================================  Clockify ====================================");
		if(atividadesParaInserir.size() == 0) {
			System.out.println("Não existem atividades para inserir no Clockify");
		} else {
			ClockifyRestService.inserirAtividadesClockify(atividadesParaInserir, apiKey);
			System.out.println(atividadesParaInserir.size() + " atividades inseridas com sucesso!");
			
		}
		System.out.println("===================================================================================");
		
		System.out.println("====================================  Excel Timesheet ====================================");
		ExcelFacade excelFacade = new ExcelFacadeImpl();
		atividadesClockify = ClockifyRestService.timeEntries(ano, mes, apiKey, user);
		excelFacade.updatePlanilha(nomeColaborador, atividadesClockify, ano, mes);
		System.out.println("==========================================================================================");
		

	}

	private static List<Atividade> getListaAtividadesIFractalQueNaoForamInseridasNoClockify(List<Atividade> atividadesIfractal,
			List<Atividade> atividadesClockify) {

		List<Atividade> resultado = new ArrayList<Atividade>();
		boolean findIt = false;

		for (Atividade atividadeIFractal : atividadesIfractal) {
			
			findIt = false;

			for (Atividade atividadeClockify : atividadesClockify) {

				/*
				 * Testa se existe uma data igual cadastrada no iFractal e no Clockify
				 * (portanto não deve inserir no clockify)
				 */
				if (atividadeIFractal.getData().isEqual(atividadeClockify.getData())) {
					findIt = true;
					break;
				}

			}
			
			if(!findIt) {
				resultado.add(atividadeIFractal);
			}

		}

		return resultado;

	}
	
	private static void removerAtividadeClockifyEmAndamento(List<Atividade> atividades) {
		
		for(Atividade atividade: atividades) {
			if(atividade.getTotalHoras().isZero()) {
				ClockifyRestService.deleteTimeEntry(atividade.getTimeEntryID(), apiKey);
			}
		}
		
	}

}
