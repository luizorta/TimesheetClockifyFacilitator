package br.com.mjv.ifractal;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import br.com.mjv.dto.Atividade;
import br.com.mjv.utils.DateUtils;

public class IFractalTextFacadeImpl implements IFractalFacade {

	
	/*
	 * Leitura do Ctrl + A da página
	 */
	public List<Atividade> loadAtividadesFromIFractal(int ano, int mes) {

		List<Atividade> atividades = new ArrayList<Atividade>();

		BufferedReader objReader = null;

		try {
			String strCurrentLine;

			objReader = new BufferedReader(new FileReader("iFractal.copied"));

			String strMes = StringUtils.leftPad(String.valueOf(mes), 2, '0');
			
			while ((strCurrentLine = objReader.readLine()) != null) {

				String toCompare = strMes + "/" + ano;

				if (strCurrentLine.contains(toCompare)) {

					Atividade atividade = new Atividade();

					int beginIndex = 0;
					int endIndex   = 10;
					String strDate = strCurrentLine.substring(beginIndex, endIndex);
					
					LocalDate date = null;
					
					try {
						date = LocalDate.parse(strDate, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
					}
					catch(DateTimeParseException e) {

						/*
						 * Se der problema no parser, 2 coisas podem ter acontecido:
						 * 1) pegou um registro que tem Alteração (Justificativa)
						 * 08:09 10:30 11:30 16:39	14/01/2020 Ter	08:09e	16:39e				07:30					ATESTADO MEDICO	Atestado de comparecimento em consulta médica.
						 * Solução: Tenta pegar os horarios ANTES da data 
						 * 
						 * 2) pegou uma linha que não interessa 
						 * Referente	Espelho de ponto 01/01/2020 até 30/06/2020
						 * Solução: Ignora, segue o barco... 
						 */
						String data = "";
						
						try {
							beginIndex = strCurrentLine.indexOf(toCompare) - 3;
							endIndex = beginIndex + 10;
							
							data = strCurrentLine.substring(beginIndex, endIndex).trim();
							
							strCurrentLine = strCurrentLine.substring(0, strCurrentLine.indexOf(toCompare)-3).trim();
							
							String[] horarios = StringUtils.split(strCurrentLine, " ");
							
							String horario1Entrada = horarios[0];
							if (DateUtils.validarHorario(horario1Entrada))
								atividade.setHorario1Entrada(LocalTime.parse(horario1Entrada, DateTimeFormatter.ofPattern("HH:mm")));
							
							String horario1Saida   = horarios[1];
							if (DateUtils.validarHorario(horario1Saida))
								atividade.setHorario1Saida(LocalTime.parse(horario1Saida, DateTimeFormatter.ofPattern("HH:mm")));
							
							String horario2Entrada = horarios[2];
							if (DateUtils.validarHorario(horario2Entrada))
								atividade.setHorario2Entrada(LocalTime.parse(horario2Entrada, DateTimeFormatter.ofPattern("HH:mm")));
							
							String horario2Saida   = horarios[3];
							if (DateUtils.validarHorario(horario2Saida))
								atividade.setHorario2Saida(LocalTime.parse(horario2Saida, DateTimeFormatter.ofPattern("HH:mm")));
							
							if(atividade.getHorario1Entrada() != null) {
								atividade.setData(LocalDate.parse(data, DateTimeFormatter.ofPattern("dd/MM/yyyy")));
								atividades.add(atividade);	
							}
							
							continue;
							
						}
						catch(ArrayIndexOutOfBoundsException aiobe) {
							if(atividade.getHorario1Entrada() != null) {
								atividade.setData(LocalDate.parse(data, DateTimeFormatter.ofPattern("dd/MM/yyyy")));
								atividades.add(atividade);	
							}
							continue;
						}
						catch(StringIndexOutOfBoundsException siobe) {
							continue;
						}
						
						
					}
					
					atividade.setData(date);

					
					// Elimina os caracteres antes da primeira data de entrada
					strCurrentLine = strCurrentLine.substring(15, strCurrentLine.length());
					
					/****************************************************
					 *                  1a ENTRADA
					 *                 
					 * Se nao encontrar Deve ignorar e sair
					 ***************************************************/
					beginIndex = strCurrentLine.indexOf("e") - 5;
					endIndex = beginIndex + 5;

					if (beginIndex < 0) {
						if (DateUtils.validarDataAtividade(atividade))
							atividades.add(atividade);
						continue;
					}

					String horario1Entrada = strCurrentLine.substring(beginIndex, endIndex);

					if (DateUtils.validarHorario(horario1Entrada))
						atividade.setHorario1Entrada(LocalTime.parse(horario1Entrada, DateTimeFormatter.ofPattern("HH:mm")));
					
					// Elimina a data que foi utilizada
					strCurrentLine = strCurrentLine.substring(7, strCurrentLine.length());
					
					
					/****************************************************
					 *                     1 SAÍDA
					 *                   
					 * Se nao encontrar Deve ignorar e sair
					 ***************************************************/
					beginIndex = strCurrentLine.indexOf("e") - 5;
					endIndex = beginIndex + 5;

					if (beginIndex < 0) {
						if (DateUtils.validarDataAtividade(atividade))
							atividades.add(atividade);
						continue;
					}
					
					String horario1Saida = strCurrentLine.substring(beginIndex, endIndex);
					if (DateUtils.validarHorario(horario1Saida))
						atividade.setHorario1Saida(LocalTime.parse(horario1Saida, DateTimeFormatter.ofPattern("HH:mm")));
					
					// Elimina a data que foi utilizada
					strCurrentLine = strCurrentLine.substring(7, strCurrentLine.length());

					/****************************************************
					 *                   2a ENTRADA
					 *                
					 * Se nao encontrar Deve ignorar e sair
					 ***************************************************/
					beginIndex = strCurrentLine.indexOf("e") - 5;
					endIndex = beginIndex + 5;

					if (beginIndex < 0) {
						if (DateUtils.validarDataAtividade(atividade))
							atividades.add(atividade);
						continue;
					}

					String horario2Entrada = strCurrentLine.substring(beginIndex, endIndex);
					if(DateUtils.validarHorario(horario2Entrada))
						atividade.setHorario2Entrada(LocalTime.parse(horario2Entrada, DateTimeFormatter.ofPattern("HH:mm")));
					
					
					// Elimina a data que foi utilizada
					strCurrentLine = strCurrentLine.substring(7, strCurrentLine.length());
					
					
					/****************************************************
					 *                    2a SAÍDA
					 *                
					 * Se nao encontrar Deve ignorar e sair
					 ***************************************************/
					beginIndex = strCurrentLine.indexOf("e") - 5;
					endIndex = beginIndex + 5;
					
					if (beginIndex < 0) {
						if (DateUtils.validarDataAtividade(atividade))
							atividades.add(atividade);
						continue;
					}
					

					String horario2Saida = strCurrentLine.substring(beginIndex, endIndex);
					if(DateUtils.validarHorario(horario2Saida))
						atividade.setHorario2Saida(LocalTime.parse(horario2Saida, DateTimeFormatter.ofPattern("HH:mm")));

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

		DateUtils.setTotalHorasIFractal(atividades);
		
		return atividades;

	}
	

}