package br.ufla.felipe.entidades;

import java.util.List;

public class Projeto {

	private String nome;
	private List<Pacote> pacotes;
	
	private int pacotesErrados;

	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public List<Pacote> getPacotes() {
		return pacotes;
	}
	public void setPacotes(List<Pacote> pacotes) {
		this.pacotes = pacotes;
	}
	public int getPacotesErrados() {
		return pacotesErrados;
	}
	public void setPacotesErrados(int pacotesErrados) {
		this.pacotesErrados = pacotesErrados;
	}
	
}
