package br.com.mjv.ifractal;

import java.util.List;

import br.com.mjv.dto.Atividade;

public interface IFractalFacade {
	
	public List<Atividade> loadAtividadesFromIFractal(int ano, int mes);

}
