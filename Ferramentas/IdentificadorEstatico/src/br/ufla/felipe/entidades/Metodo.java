package br.ufla.felipe.entidades;

import java.util.Set;

public class Metodo {

	String nome;
	Set<String> atributos;
	
	public Metodo(String nome, Set<String> atributos) {
		this.nome = nome;
		this.atributos = atributos;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public Set<String> getAtributos() {
		return atributos;
	}
	public void setAtributos(Set<String> atributos) {
		this.atributos = atributos;
	}
}
