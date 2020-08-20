package br.ufla.felipecb.entidades;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Table(name="comunicacao_classe")
@Entity
//@Audited
public class ComunicacaoClasse {

	@Id @Column(name="id")
	@GeneratedValue(
		strategy = GenerationType.SEQUENCE,
		generator = "sequence_id_comunicacao_classe"
	)
	@SequenceGenerator(
		name =  "sequence_id_comunicacao_classe",
		sequenceName = "sequence_comunicacao_classe"
	)
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="id_classe_origem")
	private Classe classeOrigem;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="id_classe_destino")
	private Classe classeDestino;
	
	@Column(name="quantidade_comunicacoes")
	private Long quantidadeComunicacoes;
	
	@Column(name="obrigatoria")
	private boolean obrigatoria;
	
	//Calculado para saber se o nó é considerado a associação do mais forte de um vértice
	@Column(name="mais_forte")
	private boolean maisForte;
	
	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "caso_uso_comunicacao_classe",
            joinColumns = {
                    @JoinColumn(name = "id_comunicacao_classe", referencedColumnName = "id",
                            nullable = false, updatable = false)},
            inverseJoinColumns = {
                    @JoinColumn(name = "id_caso_uso", referencedColumnName = "id",
                            nullable = false, updatable = false)})
	private List<CasoUso> listaCasoUso;

	@Column(name="peso")
	private Long peso;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public Classe getClasseOrigem() {
		return classeOrigem;
	}
	public void setClasseOrigem(Classe classeOrigem) {
		this.classeOrigem = classeOrigem;
	}

	public Classe getClasseDestino() {
		return classeDestino;
	}
	public void setClasseDestino(Classe classeDestino) {
		this.classeDestino = classeDestino;
	}

	public Long getQuantidadeComunicacoes() {
		return quantidadeComunicacoes;
	}
	public void setQuantidadeComunicacoes(Long quantidadeComunicacoes) {
		this.quantidadeComunicacoes = quantidadeComunicacoes;
	}

	public boolean isObrigatoria() {
		return obrigatoria;
	}
	public void setObrigatoria(boolean obrigatoria) {
		this.obrigatoria = obrigatoria;
	}
	
	public List<CasoUso> getListaCasoUso() {
		return listaCasoUso;
	}
	public void setListaCasoUso(List<CasoUso> listCasoUso) {
		this.listaCasoUso = listCasoUso;
	}
	
	public Long getPeso() {
		return peso;
	}
	public void setPeso(Long peso) {
		this.peso = peso;
	}
	
	public boolean isMaisForte() {
		return maisForte;
	}
	public void setMaisForte(boolean maisForte) {
		this.maisForte = maisForte;
	}
	
	//Metodos facilitadores
	public String getComunicacao() {
		return classeOrigem.getPacoteNome()+" -> "+classeDestino.getPacoteNome(); 
	}
	public String getComunicacaoId() {
		return classeOrigem.getIdNome()+" -> "+classeDestino.getIdNome(); 
	}
	
}