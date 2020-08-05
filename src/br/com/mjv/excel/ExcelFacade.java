package br.com.mjv.excel;

import java.io.IOException;
import java.util.List;

import br.com.mjv.dto.Atividade;

public interface ExcelFacade {
	
	public byte[] updatePlanilha(String nomeColaborador, List<Atividade> atividades, int ano, int mes) throws IOException;


}
