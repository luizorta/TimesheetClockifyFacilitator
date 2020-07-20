package br.com.mjv.dto;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

import br.com.mjv.clockify.dto.Project;

public class Atividade implements Comparable<Atividade> {
	

	private String timeEntryID;
	private LocalDate data;
	private Duration totalHoras;
	private String descricao;
	private Project projeto;
	
	private LocalTime horario1Entrada;
	private LocalTime horario1Saida;
	
	private LocalTime horario2Entrada;
	private LocalTime horario2Saida;
	
	public Atividade() {
		
	}

//	public Atividade(LocalDate data, Duration totalHoras, String descricao, String nomeProjeto) {
//		this.data = data;
//		this.totalHoras = totalHoras;
//		this.descricao = descricao;
//		this.nomeProjeto = nomeProjeto;
//	}

	public String getTimeEntryID() {
		return timeEntryID;
	}

	public void setTimeEntryID(String timeEntryID) {
		this.timeEntryID = timeEntryID;
	}
	
	public LocalDate getData() {
		return data;
	}

	public Duration getTotalHoras() {
		return totalHoras;
	}

	public String getDescricao() {
		return descricao;
	}

	public Project getProjeto() {
		return projeto;
	}

	public LocalTime getHorario1Entrada() {
		return horario1Entrada;
	}

	public LocalTime getHorario1Saida() {
		return horario1Saida;
	}

	public LocalTime getHorario2Entrada() {
		return horario2Entrada;
	}

	public LocalTime getHorario2Saida() {
		return horario2Saida;
	}

	public void setData(LocalDate data) {
		this.data = data;
	}

	public void setTotalHoras(Duration totalHoras) {
		this.totalHoras = totalHoras;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public void setProjeto(Project projeto) {
		this.projeto = projeto;
	}

	public void setHorario1Entrada(LocalTime horario1Entrada) {
		this.horario1Entrada = horario1Entrada;
	}

	public void setHorario1Saida(LocalTime horario1Saida) {
		this.horario1Saida = horario1Saida;
	}

	public void setHorario2Entrada(LocalTime horario2Entrada) {
		this.horario2Entrada = horario2Entrada;
	}

	public void setHorario2Saida(LocalTime horario2Saida) {
		this.horario2Saida = horario2Saida;
	}

	@Override
	public int compareTo(Atividade o) {
		return getData().compareTo(o.getData());
	}

}