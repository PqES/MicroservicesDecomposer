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

@Table(name="versao")
@Entity
//@Audited
public class Versao {

	public Versao(){}
	
	public Versao(Long id){
		this.id = id;
	}
	
	@Id @Column(name="id")
	@GeneratedValue(
		strategy = GenerationType.SEQUENCE,
		generator = "sequence_id_versao"
	)
	@SequenceGenerator(
		name =  "sequence_id_versao",
		sequenceName = "sequence_versao"
	)
	private Long id;
	
	@Column(name="codigo")
	private Long codigo;
	
	@Column(name="codigo_completo")
	private String codigoCompleto;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="id_projeto")
	private Projeto projeto;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="id_versao_base")
	private Versao versaoBase;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "versao", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Classe> classes;
	
	@Column(name="cbo")
	private Double cbo;
	
	@Column(name="rfc")
	private Double rfc;
	
	@Column(name="lcom4")
	private Double lcom4;

	@Column(name="lcom4SemEntidades")
	private Double lcom4SemEntidades;
	
	@Column(name="split_classes")
	private boolean splitClasses;

	@Column(name="limiar")
	private Long limiar;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public Long getCodigo() {
		return codigo;
	}
	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}
	
	public String getCodigoCompleto() {
		return codigoCompleto;
	}
	public void setCodigoCompleto(String codigoCompleto) {
		this.codigoCompleto = codigoCompleto;
	}

	public Projeto getProjeto() {
		return projeto;
	}
	public void setProjeto(Projeto projeto) {
		this.projeto = projeto;
	}
	
	public Versao getVersaoBase() {
		return versaoBase;
	}
	public void setVersaoBase(Versao versaoBase) {
		this.versaoBase = versaoBase;
	}

	public List<Classe> getClasses() {
		return classes;
	}
	public void setClasses(List<Classe> classes) {
		this.classes = classes;
	}
	
	public Double getCbo() {
		return cbo;
	}
	public void setCbo(Double cbo) {
		this.cbo = cbo;
	}

	public Double getRfc() {
		return rfc;
	}
	public void setRfc(Double rfc) {
		this.rfc = rfc;
	}

	public Double getLcom4() {
		return lcom4;
	}
	public void setLcom4(Double lcom4) {
		this.lcom4 = lcom4;
	}

	public Double getLcom4SemEntidades() {
		return lcom4SemEntidades;
	}
	public void setLcom4SemEntidades(Double lcom4SemEntidades) {
		this.lcom4SemEntidades = lcom4SemEntidades;
	}

	public boolean isSplitClasses() {
		return splitClasses;
	}
	public void setSplitClasses(boolean splitClasses) {
		this.splitClasses = splitClasses;
	}

	public Long getLimiar() {
		return limiar;
	}
	public void setLimiar(Long limiar) {
		this.limiar = limiar;
	}

	public String getCodigoVersao() {
		if(versaoBase != null) {
			return versaoBase.getCodigoVersao()+"."+codigo;
		} else {
			return codigo.toString();
		}
	}
}
