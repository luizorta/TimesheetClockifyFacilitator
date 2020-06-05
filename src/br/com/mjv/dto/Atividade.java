package br.com.mjv.dto;

public class Atividade {

	private String data;
	private String totalHoras;
	private String descricao;
	private String nomeProjeto;
	
	private String horario1Entrada;
	private String horario1Saida;
	
	private String horario2Entrada;
	private String horario2Saida;
	
	public Atividade() {
		
	}

	public Atividade(String data, String totalHoras, String descricao, String nomeProjeto) {
		this.data = data;
		this.totalHoras = totalHoras;
		this.descricao = descricao;
		this.nomeProjeto = nomeProjeto;
	}
	
	

	public String getHorario1Entrada() {
		return horario1Entrada;
	}

	public String getHorario1Saida() {
		return horario1Saida;
	}

	public String getHorario2Entrada() {
		return horario2Entrada;
	}

	public String getHorario2Saida() {
		return horario2Saida;
	}

	public void setHorario1Entrada(String horario1Entrada) {
		this.horario1Entrada = horario1Entrada;
	}

	public void setHorario1Saida(String horario1Saida) {
		this.horario1Saida = horario1Saida;
	}

	public void setHorario2Entrada(String horario2Entrada) {
		this.horario2Entrada = horario2Entrada;
	}

	public void setHorario2Saida(String horario2Saida) {
		this.horario2Saida = horario2Saida;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getNomeProjeto() {
		return nomeProjeto;
	}

	public void setNomeProjeto(String nomeProjeto) {
		this.nomeProjeto = nomeProjeto;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getTotalHoras() {
		return totalHoras;
	}

	public void setTotalHoras(String totalHoras) {
		this.totalHoras = totalHoras;
	}

}