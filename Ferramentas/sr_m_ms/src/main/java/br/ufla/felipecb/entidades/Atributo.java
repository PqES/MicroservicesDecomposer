//package br.ufla.felipecb.entidades;
//
//import java.util.List;
//
//import javax.persistence.CascadeType;
//import javax.persistence.Column;
//import javax.persistence.Entity;
//import javax.persistence.FetchType;
//import javax.persistence.GeneratedValue;
//import javax.persistence.GenerationType;
//import javax.persistence.Id;
//import javax.persistence.JoinColumn;
//import javax.persistence.ManyToMany;
//import javax.persistence.ManyToOne;
//import javax.persistence.SequenceGenerator;
//import javax.persistence.Table;
//
//@Table(name="atributo")
//@Entity
////@Audited
//public class Atributo implements Cloneable {
//
//	@Id @Column(name="id")
//	@GeneratedValue(
//		strategy = GenerationType.SEQUENCE,
//		generator = "sequence_id_atributo"
//	)
//	@SequenceGenerator(
//		name =  "sequence_id_atributo",
//		sequenceName = "sequence_atributo"
//	)
//	private Long id;
//	
//	@Column(name="nome")
//	private String nome;
//	
//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name="id_classe")
//	private Classe classe;
//	
//	//sem getters e setter, usa apenas no remove
//	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "metodoOrigem", cascade = CascadeType.ALL)
//	private List<Metodo> comunicacoes;
//	
//	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//	@JoinColumn(name="id_microsservico")
//	private MicrosservicoMetodo microsservico;
//	
//	@Column(name="atributo")
//	private boolean atributo = false;
//	
//	//Para amarrar atraves de ligações especiais como: RETORNO, PARAMETRO
//	@Column(name="ligacao_especial")
//	private boolean ligacaoEspecial = false;
//	
//	
//	public Atributo() {}
//	
//	public Atributo(String nome, Classe classe) {
//		this.nome = nome;
//		this.classe = classe;
//	}
//	
//}