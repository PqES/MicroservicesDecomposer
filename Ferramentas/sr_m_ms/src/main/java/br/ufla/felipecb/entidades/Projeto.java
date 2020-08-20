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
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Table(name="projeto")
@Entity
//@Audited
public class Projeto {

	public Projeto(){}
	
	@Id @Column(name="id")
	@GeneratedValue(
		strategy = GenerationType.SEQUENCE,
		generator = "sequence_id_projeto"
	)
	@SequenceGenerator(
		name =  "sequence_id_projeto",
		sequenceName = "sequence_projeto"
	)
	private Long id;
	
	@Column(name="nome")
	private String nome;
	
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "projeto", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("codigoCompleto")
	private List<Versao> versoes;
	
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

	public List<Versao> getVersoes() {
		return versoes;
	}
	public void setVersoes(List<Versao> versoes) {
		this.versoes = versoes;
	}
	
}
