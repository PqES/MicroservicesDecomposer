package br.ufla.felipe.entidades;

import java.util.List;

public class Classe {

	String nome;
	boolean entidade;
	boolean utilitaria;
	
	List<Metodo> metodos;
	List<String> atributos;
	
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public boolean isEntidade() {
		return entidade;
	}
	public void setEntidade(boolean entidade) {
		this.entidade = entidade;
	}
	public boolean isUtilitaria() {
		return utilitaria;
	}
	public void setUtilitaria(boolean utilitaria) {
		this.utilitaria = utilitaria;
	}
	public List<Metodo> getMetodos() {
		return metodos;
	}
	public void setMetodos(List<Metodo> metodos) {
		this.metodos = metodos;
	}
	public List<String> getAtributos() {
		return atributos;
	}
	public void setAtributos(List<String> atributos) {
		this.atributos = atributos;
	}
}
