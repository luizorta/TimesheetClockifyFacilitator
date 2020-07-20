package br.com.mjv.main;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import br.com.mjv.clockify.dto.Project;
import br.com.mjv.clockify.dto.User;
import br.com.mjv.clockify.restservice.ClockifyRestService;
import br.com.mjv.dto.Atividade;
import br.com.mjv.excel.ExcelFacade;
import br.com.mjv.excel.ExcelFacadeImpl;
import br.com.mjv.ifractal.IFractalFacade;
import br.com.mjv.ifractal.IFractalTextFacadeImpl;
import br.com.mjv.utils.Log;

public class TimesheetExecute {

	public static void main(String[] args) throws IOException, InvalidFormatException, ParseException {

		Log.logDebug("Executando...");
		
		/*
		 * Loading parameters...
		 */
		String apiKey = args[0];
		String nomeColaborador = args[1];
		int mes = Integer.parseInt(args[2]);
		int ano = Integer.parseInt(args[3]);
		String description = args[4];
		String projectId = args[5];
		
		
		Atividade atividade = new Atividade();
		atividade.setDescricao(description);
		
		Project projeto = new Project();
		projeto.setId(projectId);
		atividade.setProjeto(projeto);
		
		
		TimesheetExecute tsE = new TimesheetExecute();
		tsE.iniciarProcesso(atividade, apiKey, mes, ano, nomeColaborador);

		Log.logDebug("Processo finalizado.");

	}

	private void iniciarProcesso(Atividade atividade, String apiKey, int mes, int ano, String nomeColaborador) {

		// IFractalFacade iFractalFacade = new IFractalHTMLFacadeImpl();
		IFractalFacade iFractalFacade = new IFractalTextFacadeImpl();

		Log.logDebug("====================================  iFractal ====================================");
		Log.logDebug("Carregando atividades no iFractal...");
		List<Atividade> atividadesIfractal = iFractalFacade.loadAtividadesFromIFractal(ano, mes);
		Log.logDebug("Atividades carregadas com sucesso!");
		Collections.sort(atividadesIfractal);
		Log.logDebug("Total de atividades: " + atividadesIfractal.size());
		Log.logDebug("===================================================================================");

		String periodo = mes + "/" + ano;

		User user = new User();
		user.setId("5dc07145b36ea8270fcf00c7");

		Log.logDebug("====================================  Clockify ====================================");
		Log.logDebug("Buscando TODAS as entradas para o período de " + periodo + "...");
		/*
		 * Busca todas as entradas para o mes atual
		 */
		List<Atividade> atividadesClockify = null;
		try {
			atividadesClockify = ClockifyRestService.timeEntries(ano, mes, apiKey, user);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.logDebug("Entradas recuperadas com sucesso!");

		Log.logDebug("Removendo as entradas EM ANDAMENTO...");
		/*
		 * Apaga atividade que estiver aberta
		 */
		removerAtividadeClockifyEmAndamento(atividadesClockify, apiKey);
		Log.logDebug("Entradas EM ANDAMENTO removidas com sucesso!");

		Log.logDebug("Buscando as entradas FECHADAS...");
		/*
		 * Busca novamente as atividades que estarão fechadas agora
		 */
		try {
			atividadesClockify = ClockifyRestService.timeEntries(ano, mes, apiKey, user);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.logDebug("Entradas recuperadas com sucesso!");

		Collections.sort(atividadesClockify);
		Log.logDebug("Total de atividades no Clockify: " + atividadesClockify.size());

		List<Atividade> atividadesParaInserir = getListaAtividadesIFractalQueNaoForamInseridasNoClockify(
				atividadesIfractal, atividadesClockify);

		if (atividadesParaInserir.size() == 0) {
			Log.logDebug("Não existem atividades para inserir no Clockify");
		} else {
			ClockifyRestService.inserirAtividadesClockify(atividadesParaInserir, apiKey);
			Log.logDebug(atividadesParaInserir.size() + " atividades inseridas com sucesso!");

		}
		Log.logDebug("===================================================================================");

		Log.logDebug("===============================  Excel Timesheet ==================================");
		ExcelFacade excelFacade = new ExcelFacadeImpl();

		Log.logDebug("Buscando as entradas no Clockify para o período de " + periodo + "...");
		try {
			atividadesClockify = ClockifyRestService.timeEntries(ano, mes, apiKey, user);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.logDebug("Entradas recuperadas com sucesso!");

		Log.logDebug("Preenchendo a planilha timesheet para o período de " + periodo);
		try {
			excelFacade.updatePlanilha(nomeColaborador, atividadesClockify, ano, mes);
		} catch (InvalidFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.logDebug("Planilha preenchida com sucesso!");
		Log.logDebug("===================================================================================");

	}

	private static List<Atividade> getListaAtividadesIFractalQueNaoForamInseridasNoClockify(
			List<Atividade> atividadesIfractal, List<Atividade> atividadesClockify) {

		List<Atividade> resultado = new ArrayList<Atividade>();
		boolean findIt = false;

		for (Atividade atividadeIFractal : atividadesIfractal) {

			findIt = false;

			for (Atividade atividadeClockify : atividadesClockify) {
				
				atividadeIFractal.setProjeto(atividadeClockify.getProjeto());
				atividadeIFractal.setDescricao(atividadeClockify.getDescricao());

				/*
				 * Testa se existe uma data igual cadastrada no iFractal e no Clockify (portanto
				 * não deve inserir no clockify)
				 */
				if (atividadeIFractal.getData().isEqual(atividadeClockify.getData())) {
					findIt = true;
					break;
				}

			}

			if (!findIt) {
				resultado.add(atividadeIFractal);
			}

		}

		return resultado;

	}

	private static void removerAtividadeClockifyEmAndamento(List<Atividade> atividades, String apiKey) {

		for (Atividade atividade : atividades) {
			if (atividade.getTotalHoras().isZero()) {
				ClockifyRestService.deleteTimeEntry(atividade.getTimeEntryID(), apiKey);
			}
		}

	}

}
