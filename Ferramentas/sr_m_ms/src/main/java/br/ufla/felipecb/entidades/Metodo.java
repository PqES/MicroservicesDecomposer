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

@Table(name="metodo")
@Entity
//@Audited
public class Metodo implements Cloneable {

	@Id @Column(name="id")
	@GeneratedValue(
		strategy = GenerationType.SEQUENCE,
		generator = "sequence_id_metodo"
	)
	@SequenceGenerator(
		name =  "sequence_id_metodo",
		sequenceName = "sequence_metodo"
	)
	private Long id;
	
	@Column(name="nome")
	private String nome;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="id_classe")
	private Classe classe;
	
	//sem getters e setter, usa apenas no remove
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "metodoOrigem", cascade = CascadeType.ALL)
	private List<ComunicacaoMetodo> comunicacoesMetodosOrigem;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "metodoDestino", cascade = CascadeType.ALL)
	private List<ComunicacaoMetodo> comunicacoesMetodosDestino;
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name="id_microsservico")
	private MicrosservicoMetodo microsservico;
	
	@Column(name="atributo")
	private boolean atributo = false;
	
	//Para amarrar atraves de ligações especiais como: RETORNO, PARAMETRO
	@Column(name="ligacao_especial")
	private boolean ligacaoEspecial = false;
	
	
	public Metodo() {}
	
	public Metodo(String nome, Classe classe) {
		this.nome = nome;
		this.classe = classe;
	}
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
	
	public Classe getClasse() {
		return classe;
	}
	public void setClasse(Classe classe) {
		this.classe = classe;
	}
	
	public MicrosservicoMetodo getMicrosservico() {
		return microsservico;
	}
	public void setMicrosservico(MicrosservicoMetodo microsservico) {
		this.microsservico = microsservico;
	}
	
	public boolean isAtributo() {
		return atributo;
	}
	public void setAtributo(boolean atributo) {
		this.atributo = atributo;
	}

	public boolean getLigacaoEspecial() {
		return ligacaoEspecial;
	}
	public void setLigacaoEspecial(boolean ligacaoEspecial) {
		this.ligacaoEspecial = ligacaoEspecial;
	}

	//Metodos facilitadores
	public String getPacoteClasseMetodo() {
		return "\""+classe.getNomePacote()+"."+classe.getNome()+"::"+nome+"\"";
	}
	public String getIdClasseMetodo() {
		return "\""+classe.getId()+"::"+nome+"\"";
	}
	
	public Metodo retornarReplica () {
		try {
			Metodo metodo = (Metodo)this.clone();
			return metodo.limparReplica();
		}
		catch (CloneNotSupportedException ex) {
			ex.printStackTrace();
			return null;
		}
    }
	
	public Metodo limparReplica() {
		id  = null;
//		microsservico = null;
		comunicacoesMetodosDestino = null;
		comunicacoesMetodosOrigem = null;
		
		return this;
	}
	
}