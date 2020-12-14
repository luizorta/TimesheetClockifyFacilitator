package br.com.mjv.control;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.com.mjv.clockify.dto.Project;
import br.com.mjv.clockify.dto.User;
import br.com.mjv.clockify.restservice.ClockifyRestService;
import br.com.mjv.dto.Atividade;
import br.com.mjv.excel.ExcelFacade;
import br.com.mjv.excel.ExcelFacadeImpl;
import br.com.mjv.ifractal.IFractalFacade;
import br.com.mjv.ifractal.IFractalTextFacadeImpl;
import br.com.mjv.utils.Log;

public class TimesheetControl {

	public byte[] iniciarProcesso(Atividade atividade, String apiKey, int mes, int ano, User user, String content) {

		Log.logDebug("Executando...");
		// IFractalFacade iFractalFacade = new IFractalHTMLFacadeImpl();
		IFractalFacade iFractalFacade = new IFractalTextFacadeImpl();

		Log.logDebug("====================================  iFractal ====================================");
		Log.logDebug("Carregando atividades no iFractal...");
		List<Atividade> atividadesIfractal = iFractalFacade.loadAtividadesFromIFractal(content, ano, mes);
		Log.logDebug("Atividades carregadas com sucesso!");
		Collections.sort(atividadesIfractal);
		Log.logDebug("Total de atividades: " + atividadesIfractal.size());
		Log.logDebug("===================================================================================");

		String periodo = mes + "/" + ano;

		Log.logDebug("====================================  Clockify ====================================");
		Log.logDebug("Buscando TODAS as entradas para o período de " + periodo + "...");

		/*
		 * BUSCA TODAS ENTRADAS NO MES ATUAL
		 */
		List<Atividade> atividadesClockify = null;
		try {
			atividadesClockify = ClockifyRestService.timeEntries(ano, mes, apiKey, user, false);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.logDebug("Entradas recuperadas com sucesso!");

		Log.logDebug("Removendo as entradas EM ANDAMENTO...");

		/*
		 * APAGA ATIVIDADES EM ANDAMENTO
		 */
		removerAtividadeClockifyEmAndamento(atividadesClockify, apiKey);
		Log.logDebug("Entradas EM ANDAMENTO removidas com sucesso!");

		Log.logDebug("Buscando as entradas FECHADAS...");
		
		/*
		 * BUSCA TODAS ATIVIDADE FECHADAS
		 */
		try {
			atividadesClockify = ClockifyRestService.timeEntries(ano, mes, apiKey, user, false);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.logDebug("Entradas recuperadas com sucesso!");

		Collections.sort(atividadesClockify);
		Log.logDebug("Total de atividades no Clockify: " + atividadesClockify.size());

		/*
		 * MONTA A LISTA COM AS ATIVIDADES A SEREM INSERIDAS
		 */
		List<Atividade> atividadesParaInserir = getListaAtividadesIFractalQueNaoForamInseridasNoClockify(
				atividadesIfractal, atividadesClockify, apiKey, atividade.getProjeto(), atividade.getDescricao());

		/*
		 * INSERE ATIVIDADES CLOCKIFY
		 */
		if (atividadesParaInserir.size() == 0) {
			Log.logDebug("Não existem atividades para inserir no Clockify");
		} else {
			ClockifyRestService.inserirAtividadesClockify(atividadesParaInserir, apiKey);
			Log.logDebug(atividadesParaInserir.size() + " atividades inseridas com sucesso!");

		}
		Log.logDebug("===================================================================================");

		Log.logDebug("===============================  Excel Timesheet ==================================");
		ExcelFacade excelFacade = new ExcelFacadeImpl();

		/*
		 * BUSCA TODAS ENTRADAS NO CLOCKIFY
		 */
		Log.logDebug("Buscando as entradas com detalhes no Clockify para o período de " + periodo + "...");
		try {
			atividadesClockify = ClockifyRestService.timeEntries(ano, mes, apiKey, user, true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.logDebug("Entradas recuperadas com sucesso!");

		Log.logDebug("Preenchendo a planilha timesheet para o período de " + periodo);
		
		byte[] bytes = null;

		/*
		 * ATUALIZA PLANILHA EXCEL
		 */
		try {
			bytes = excelFacade.updatePlanilha(user.getName(), atividadesClockify, ano, mes);
		} catch(NullPointerException e) {
			System.err.println("Erro ao ler o arquivo de planilha base xls. Verifique novamente o caminho.");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.logDebug("Planilha preenchida com sucesso!");
		Log.logDebug("===================================================================================");
		
		Log.logDebug("Processo finalizado.");
		
		
		return bytes;

	}

	private static List<Atividade> getListaAtividadesIFractalQueNaoForamInseridasNoClockify(
			List<Atividade> atividadesIfractal, List<Atividade> atividadesClockify, String apiKey, Project projeto, String descricao) {

		List<Atividade> resultado = new ArrayList<Atividade>();
		boolean findIt = false;

		for (Atividade atividadeIFractal : atividadesIfractal) {

			findIt = false;

			for (Atividade atividadeClockify : atividadesClockify) {
				
				/*
				 * Pega a descrição da ultima atividade
				 */
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
				if(!descricao.isEmpty()) {
					atividadeIFractal.setDescricao(descricao);
				}
				atividadeIFractal.setProjeto(projeto);
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