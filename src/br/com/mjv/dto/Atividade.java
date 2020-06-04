package br.com.mjv.dto;

public class Atividade {

	private String data;
	private String totalHoras;
	private String descricao;
	private String nomeProjeto;
	
	public Atividade() {
		
	}

	public Atividade(String data, String totalHoras, String descricao, String nomeProjeto) {
		this.data = data;
		this.totalHoras = totalHoras;
		this.descricao = descricao;
		this.nomeProjeto = nomeProjeto;
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