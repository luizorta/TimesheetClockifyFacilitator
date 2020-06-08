package br.com.mjv.excel;

import java.io.IOException;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import br.com.mjv.dto.Atividade;

public interface ExcelFacade {
	
	public void updatePlanilha(String nomeColaborador, List<Atividade> atividades, int ano, int mes) throws InvalidFormatException, IOException;


}
