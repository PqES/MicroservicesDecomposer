package br.ufla.felipe.entidades;

import java.util.List;

public class Pacote {

	String nome;
	List<Classe> classes;
	
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public List<Classe> getClasses() {
		return classes;
	}
	public void setClasses(List<Classe> classes) {
		this.classes = classes;
	}
}
