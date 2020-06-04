package br.com.mjv.utils;

import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import br.com.mjv.clockify.dto.ClockifyResponse;
import br.com.mjv.clockify.dto.Entry;
import br.com.mjv.dto.Atividade;

public class Util {
	
	public static String getTotalHorasMes(List<Atividade> atividades) {

		PeriodFormatter pf = new PeriodFormatterBuilder().minimumPrintedDigits(2).printZeroAlways().appendHours()
				.appendLiteral(":").appendMinutes().toFormatter();
		Period period = Period.ZERO;

		for (Atividade atividade : atividades) {

			period = period.plus(pf.parsePeriod(atividade.getTotalHoras()));
		}

		return pf.print(period.normalizedStandard(PeriodType.time()));

	}
	
	public static String getTotalHoras(String dataBase, ClockifyResponse response) {

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
	
	public static List<String> getDatasMes(int ano, int mes) {

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

}
