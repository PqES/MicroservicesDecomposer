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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Table(name="classe")
@Entity
//@Audited
public class Classe implements Cloneable{

	@Id @Column(name="id")
	@GeneratedValue(
		strategy = GenerationType.SEQUENCE,
		generator = "sequence_id_classe"
	)
	@SequenceGenerator(
		name =  "sequence_id_classe",
		sequenceName = "sequence_classe"
	)
	private Long id;
	
	@Column(name="nome")
	private String nome;
	
	@Column(name="nome_pacote")
	private String nomePacote;

	@Column(name="entidade")
	private boolean entidade;
	
	@Column(name="utilitaria")
	private boolean utilitaria;
	
	//Indica se podera separar os metodos das classes (split class)
	@Column(name="definitiva")
	private boolean definitiva;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="id_versao")
	private Versao versao;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "classe", cascade = CascadeType.ALL)
	private List<Metodo> metodos;
	
	//sem getters e setter, usa apenas no remove
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "classeOrigem", cascade = CascadeType.ALL)
	private List<ComunicacaoClasse> comunicacoesClassesOrigem;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "classeDestino", cascade = CascadeType.ALL)
	private List<ComunicacaoClasse> comunicacoesClassesDestino;
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "id_microsservico")
	private Microsservico microsservico;
	
	@Column(name="cbo")
	private Integer cbo;
	
	@Column(name="rfc")
	private Integer rfc;

	@Column(name="lcom4")
	private Integer lcom4;
	
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
	
	public String getNomePacote() {
		return nomePacote;
	}
	public void setNomePacote(String nomePacote) {
		this.nomePacote = nomePacote;
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
	
	public boolean isDefinitiva() {
		return definitiva;
	}
	public void setDefinitiva(boolean definitiva) {
		this.definitiva = definitiva;
	}
	
	public Versao getVersao() {
		return versao;
	}
	public void setVersao(Versao versao) {
		this.versao = versao;
	}
	
	public List<Metodo> getMetodos() {
		return metodos;
	}
	public void setMetodos(List<Metodo> metodos) {
		this.metodos = metodos;
	}
	
	public Microsservico getMicrosservico() {
		return microsservico;
	}
	public void setMicrosservico(Microsservico microsservico) {
		this.microsservico = microsservico;
	}
	
	public Integer getCbo() {
		return cbo;
	}
	public void setCbo(Integer cbo) {
		this.cbo = cbo;
	}
	
	public Integer getRfc() {
		return rfc;
	}
	public void setRfc(Integer rfc) {
		this.rfc = rfc;
	}
	
	public Integer getLcom4() {
		return lcom4;
	}
	public void setLcom4(Integer lcom4) {
		this.lcom4 = lcom4;
	}
	
	//Metodos facilitadores
	public String getPacoteNome() {
		return "\""+nomePacote+"."+nome+ "\"";
	}
	
	public String getIdNome() {
		return "\""+id+"."+nome+ "\"";
	}
	
	public Classe retornarReplica () {
		try {
			Classe classe = (Classe)this.clone();
			return classe.limparReplica();
		}
		catch (CloneNotSupportedException ex) {
			ex.printStackTrace();
			return null;
		}
    }
	
	public Classe limparReplica() {
		id  = null;
//		microsservico = null;
		comunicacoesClassesDestino = null;
		comunicacoesClassesOrigem = null;
		metodos = null;
		
		return this;
	}
}