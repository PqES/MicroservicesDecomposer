package br.ufla.felipecb.entidades;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Table(name="microsservico_metodo")
@Entity
//@Audited
public class MicrosservicoMetodo {
	
	@Id @Column(name="id")
	@GeneratedValue(
		strategy = GenerationType.SEQUENCE,
		generator = "sequence_id_microsservico_metodo"
	)
	@SequenceGenerator(
		name =  "sequence_id_microsservico_metodo",
		sequenceName = "sequence_microsservico_metodo"
	)
	private Long id;
	
	@Column(name = "nome")
	private String nome;

	@Column(name = "utilitario")
	private boolean utilitario;

	@Column(name = "finalizado")
	private boolean finalizado;
	
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "microsservico", cascade = CascadeType.ALL)
	private List<Metodo> metodos;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public List<Metodo> getMetodos() {
		return metodos;
	}
	public void setMetodos(List<Metodo> metodos) {
		this.metodos = metodos;
	}
	
	public boolean isUtilitario() {
		return utilitario;
	}
	public void setUtilitario(boolean utilitario) {
		this.utilitario = utilitario;
	}
	
	public boolean isFinalizado() {
		return finalizado;
	}
	public void setFinalizado(boolean finalizado) {
		this.finalizado = finalizado;
	}
	
	public String getIdNome() {
		return  "\""+ (nome != null ? id+":"+nome : id) +"\"";
	}
}
