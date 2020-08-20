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

@Table(name="comunicacao_metodo")
@Entity
//@Audited
public class ComunicacaoMetodo {

	@Id @Column(name="id")
	@GeneratedValue(
		strategy = GenerationType.SEQUENCE,
		generator = "sequence_id_comunicacao_metodo"
	)
	@SequenceGenerator(
		name =  "sequence_id_comunicacao_metodo",
		sequenceName = "sequence_comunicacao_metodo"
	)
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="id_metodo_origem")
	private Metodo metodoOrigem;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="id_metodo_destino")
	private Metodo metodoDestino;
	
	@Column(name="quantidade_chamadas")
	private Long quantidadeChamadas;
	
	//Indica se podera separar os metodos das classes (split class)
	@Column(name="definitiva")
	private boolean definitiva;

	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "caso_uso_comunicacao_metodo",
            joinColumns = {
                    @JoinColumn(name = "id_comunicacao_metodo", referencedColumnName = "id",
                            nullable = false, updatable = false)},
            inverseJoinColumns = {
                    @JoinColumn(name = "id_caso_uso", referencedColumnName = "id",
                            nullable = false, updatable = false)})
	private List<CasoUso> listaCasoUso;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public Metodo getMetodoOrigem() {
		return metodoOrigem;
	}
	public void setMetodoOrigem(Metodo metodoOrigem) {
		this.metodoOrigem = metodoOrigem;
	}

	public Metodo getMetodoDestino() {
		return metodoDestino;
	}
	public void setMetodoDestino(Metodo metodoDestino) {
		this.metodoDestino = metodoDestino;
	}

	public Long getQuantidadeChamadas() {
		return quantidadeChamadas;
	}
	public void setQuantidadeChamadas(Long quantidadeChamadas) {
		this.quantidadeChamadas = quantidadeChamadas;
	}
	
	public List<CasoUso> getListaCasoUso() {
		return listaCasoUso;
	}
	public void setListaCasoUso(List<CasoUso> listaCasoUso) {
		this.listaCasoUso = listaCasoUso;
	}
	
	public boolean isDefinitiva() {
		return definitiva;
	}
	public void setDefinitiva(boolean definitiva) {
		this.definitiva = definitiva;
	}
	
	//Metodos facilitadores
	public String getComunicacao() {
		return metodoOrigem.getPacoteClasseMetodo()+" -> "+metodoDestino.getPacoteClasseMetodo(); 
	}
	public String getComunicacaoId() {
		return metodoOrigem.getIdClasseMetodo()+" -> "+metodoDestino.getIdClasseMetodo(); 
	}
}