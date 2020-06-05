package br.com.mjv.ifractal;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import br.com.mjv.dto.Atividade;

public class IFractalService {

	public static List<Atividade> loadAtividadesFromIFractal(int ano, int mes) {

		String strMes = StringUtils.leftPad(String.valueOf(mes), 2, '0');

		List<Atividade> atividades = new ArrayList<Atividade>();

		BufferedReader objReader = null;
		try {
			String strCurrentLine;

			objReader = new BufferedReader(new FileReader("iFractal.html"));

			while ((strCurrentLine = objReader.readLine()) != null) {

				String toCompare = strMes + "/" + ano;

				if (strCurrentLine.contains(toCompare)) {
					Atividade atividade = new Atividade();
					int beginIndex      = strCurrentLine.indexOf(toCompare) - 3;
					int endIndex        = beginIndex + 10;
					String data         = strCurrentLine.substring(beginIndex, endIndex);
					atividade.setData(data);

					// pega a próxima linha que contem horario da primeira entrada
					String delimiter = "</td>";
					
					strCurrentLine = objReader.readLine();
					beginIndex     = strCurrentLine.indexOf(delimiter) - 6;
					endIndex       = beginIndex + 5;
					
					String horario1Entrada = strCurrentLine.substring(beginIndex, endIndex);
					
					if(validarHorario(horario1Entrada))
						atividade.setHorario1Entrada(horario1Entrada);
					
					strCurrentLine = objReader.readLine();
					beginIndex     = strCurrentLine.indexOf(delimiter) - 6;
					endIndex       = beginIndex + 5;
					
					String horario1Saida   = strCurrentLine.substring(beginIndex, endIndex);
					if(validarHorario(horario1Saida))
						atividade.setHorario1Saida(horario1Saida);
					
					strCurrentLine = objReader.readLine();
					beginIndex     = strCurrentLine.indexOf(delimiter) - 6;
					endIndex       = beginIndex + 5;
					
					String horario2Entrada = strCurrentLine.substring(beginIndex, endIndex);
					if(validarHorario(horario2Entrada))
						atividade.setHorario2Entrada(horario2Entrada);
					
					strCurrentLine = objReader.readLine();
					beginIndex     = strCurrentLine.indexOf(delimiter) - 6;
					endIndex       = beginIndex + 5;
					
					String horario2Saida   = strCurrentLine.substring(beginIndex, endIndex);
					if(validarHorario(horario2Saida))
						atividade.setHorario2Saida(horario2Saida);

					//Vai recuperar o total de horas trabalhadas no dia
					strCurrentLine         = objReader.readLine();
					beginIndex = strCurrentLine.indexOf(delimiter) - 5;
					endIndex   = beginIndex + 5;
					
					String totalHoras      = strCurrentLine.substring(beginIndex, endIndex);
					
					if(validarHorario(totalHoras))
						atividade.setTotalHoras(totalHoras);
					
					if(validarDataAtividade(atividade))
						atividades.add(atividade);
				}

			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (objReader != null)
					objReader.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		return atividades;

	}
	
	/**
	 * Valida se um horario é valido
	 * @param horario
	 * @return
	 */
	private static boolean validarHorario(String horario) {
		try {
			DateUtils.parseDate(horario, new String[] {"HH:mm"});
		}catch(ParseException e) {
			return false;
		}
		
		return true;
	}
	
	
	/**
	 * Valida se existe horario cadastrado para esse dia.
	 * @param atividade
	 * @return
	 */
	private static boolean validarDataAtividade(Atividade atividade) {
		if(atividade.getHorario1Entrada() != null || 
				atividade.getHorario1Saida() != null ||
				atividade.getHorario2Entrada() != null ||
				atividade.getHorario2Saida() != null) {
			return true;
		}
		
		return false;
	}

}
