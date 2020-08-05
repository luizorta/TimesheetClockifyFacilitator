package br.com.mjv.ifractal;

import java.util.List;

import br.com.mjv.dto.Atividade;

public interface IFractalFacade {
	
	public List<Atividade> loadAtividadesFromIFractal(String content, int ano, int mes);

}
