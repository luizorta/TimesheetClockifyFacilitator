package br.com.mjv.utils;

import java.text.ParseException;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import br.com.mjv.clockify.dto.Entry;
import br.com.mjv.dto.Atividade;

public class DateUtils {

	public static Duration getTotalHorasMes(List<Atividade> atividades) {

		Duration duracaoTotal = Duration.ofHours(0);

		for (Atividade atividade : atividades) {
			duracaoTotal = duracaoTotal.plus(atividade.getTotalHoras());
		}

		return duracaoTotal;

	}

	public static void setTotalHorasIFractal(List<Atividade> atividades) {

		Duration totalHorasDiaria = Duration.ZERO;

		for (Atividade atividade : atividades) {

			LocalTime horario1Entrada = atividade.getHorario1Entrada();
			LocalTime horario1Saida = atividade.getHorario1Saida();
			Duration totalHoras1Jornada = Duration.ZERO;

			if (horario1Saida != null) {
				totalHoras1Jornada = Duration.between(horario1Entrada, horario1Saida);
			}

			LocalTime horario2Entrada = atividade.getHorario2Entrada();
			LocalTime horario2Saida = atividade.getHorario2Saida();
			Duration totalHoras2Jornada = Duration.ZERO;

			if (horario2Entrada != null && horario2Saida != null) {
				totalHoras2Jornada = Duration.between(horario2Entrada, horario2Saida);
			}

			totalHorasDiaria = totalHoras1Jornada.plus(totalHoras2Jornada);

			atividade.setTotalHoras(totalHorasDiaria);

		}

	}

	/**
	 * Recupera o total de horas trabalhadas em um dia.
	 * 
	 * @param data
	 * @param response
	 * @return
	 */
	public static Duration getTotalHorasClockify(LocalDate data, List<Entry> entries) {
		
		Duration totalHorasDiaria = Duration.ZERO;

		for (Entry entry : entries) {

			LocalDate startDate = entry.getTimeInterval().getStart().toLocalDate();
			
			if (data.isEqual(startDate)) {
				if(entry.getTimeInterval().getDuration()!=null) {
					totalHorasDiaria = totalHorasDiaria.plus(entry.getTimeInterval().getDuration());
				}
			}

		}

		return totalHorasDiaria;

	}
	

	public static List<LocalDate> getDatasMes(int ano, int mes) {

		List<LocalDate> dates = new ArrayList<LocalDate>();
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, mes - 1);
		cal.set(Calendar.YEAR, ano);
		int lastDayOfMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

		for (int i = 1; i <= lastDayOfMonth; i++) {

			LocalDate date = LocalDate.of(ano, mes, i);
			DayOfWeek dayOfWeek = date.getDayOfWeek();

			if (dayOfWeek != DayOfWeek.SUNDAY) {
				dates.add(date);
			}

			date.plusDays(1);

		}

		return dates;
	}

	/**
	 * Valida se um horario é valido
	 * 
	 * @param horario
	 * @return
	 */
	public static boolean validarHorario(String horario) {
		try {
			org.apache.commons.lang.time.DateUtils.parseDate(horario, new String[] { "HH:mm" });
		} catch (ParseException e) {
			return false;
		}

		return true;
	}

	/**
	 * Valida se existe horario cadastrado para esse dia.
	 * 
	 * @param atividade
	 * @return
	 */
	public static boolean validarDataAtividade(Atividade atividade) {
		if (atividade.getHorario1Entrada() != null || atividade.getHorario1Saida() != null
				|| atividade.getHorario2Entrada() != null || atividade.getHorario2Saida() != null) {
			return true;
		}

		return false;
	}

}
