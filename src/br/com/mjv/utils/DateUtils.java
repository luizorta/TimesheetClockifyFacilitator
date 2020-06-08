package br.com.mjv.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import br.com.mjv.clockify.dto.ClockifyResponse;
import br.com.mjv.clockify.dto.Entry;
import br.com.mjv.dto.Atividade;

public class DateUtils {

	public static LocalTime getTotalHorasMes(List<Atividade> atividades) {

		LocalTime totalHorasMes = LocalTime.of(0, 0);

		for (Atividade atividade : atividades) {
			LocalTime totalHoras = atividade.getTotalHoras();
			totalHoras = totalHorasMes.plusHours(totalHoras.getHour()).plusMinutes(totalHoras.getMinute());
		}

		return totalHorasMes;

	}

	public static void setTotalHorasIFractal(List<Atividade> atividades) {

		LocalTime totalHorasDiaria = LocalTime.of(0, 0);

		for (Atividade atividade : atividades) {
			
			LocalTime horario1Entrada = atividade.getHorario1Entrada();
			LocalTime horario1Saida   = atividade.getHorario1Saida();
			LocalTime totalHoras1Jornada = LocalTime.of(0, 0);
			
			if(horario1Saida != null) {
				totalHoras1Jornada = horario1Saida.minusHours(horario1Entrada.getHour()).minusMinutes(horario1Entrada.getMinute());
			}
			
			LocalTime horario2Entrada = atividade.getHorario2Entrada();
			LocalTime horario2Saida   = atividade.getHorario2Saida();
			LocalTime totalHoras2Jornada = LocalTime.of(0, 0);
			
			if(horario2Entrada != null && horario2Saida != null) {
				totalHoras2Jornada = horario1Saida.minusHours(horario1Entrada.getHour()).minusMinutes(horario1Entrada.getMinute());	
			}

			totalHorasDiaria = totalHoras1Jornada.plusHours(totalHoras2Jornada.getHour()).plusMinutes(totalHoras2Jornada.getMinute());
			
			atividade.setTotalHoras(totalHorasDiaria);

		}

	}

	public static LocalTime getTotalHorasClockify(LocalDate data, ClockifyResponse response) {

		LocalTime totalHorasDiaria = LocalTime.of(0, 0);

		for (Entry entrada : response.getTimeEntries()) {

			String date = entrada.getTimeInterval().getStart().substring(0,
					entrada.getTimeInterval().getStart().indexOf("T"));

			LocalDate dateToCompare = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

			if (data.isEqual(dateToCompare)) {

				String strStart = entrada.getTimeInterval().getStart();
				strStart = strStart.substring(strStart.indexOf("T") + 1, strStart.indexOf("Z"));
				LocalTime startTime = LocalTime.parse(strStart, DateTimeFormatter.ofPattern("HH:mm:ss"));

				String strEnd = entrada.getTimeInterval().getEnd();
				strEnd = strEnd.substring(strEnd.indexOf("T") + 1, strEnd.indexOf("Z"));
				LocalTime endTime = LocalTime.parse(strEnd, DateTimeFormatter.ofPattern("HH:mm:ss"));

				LocalTime totalHorasAtividade = endTime.minusHours(startTime.getHour())
						.minusMinutes(startTime.getMinute());

				totalHorasDiaria = totalHorasDiaria.plusHours(totalHorasAtividade.getHour())
						.plusMinutes(totalHorasAtividade.getMinute()).plusSeconds(totalHorasAtividade.getSecond());

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

	/*
	 * Convert String of data dd/MM/yyyy to yyyy-MM-dd
	 */
	public static String formatDate(String data) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		String resultado = data;
		try {
			resultado = sdf
					.format(org.apache.commons.lang.time.DateUtils.parseDate(data, new String[] { "dd/MM/yyyy" }));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return resultado;

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
