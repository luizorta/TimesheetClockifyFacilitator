package br.com.mjv.dto;

import java.util.ArrayList;
import java.util.List;

public class Atividades {
	
	public Atividades() {
		atividades = new ArrayList<Atividade>();
	}
	
	List<Atividade> atividades;
	
	String totalHorasMes;

	public List<Atividade> getAtividades() {
		return atividades;
	}

	public String getTotalHorasMes() {
		return totalHorasMes;
	}

	public void setAtividades(List<Atividade> atividades) {
		this.atividades = atividades;
	}

	public void setTotalHorasMes(String totalHorasMes) {
		this.totalHorasMes = totalHorasMes;
	}

}
