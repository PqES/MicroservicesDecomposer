package br.ufla.felipecb.entidades;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

@Table(name="microsservico")
@Entity
//@Audited
public class Microsservico {
	
	@Id @Column(name="id")
	@GeneratedValue(
		strategy = GenerationType.SEQUENCE,
		generator = "sequence_id_microsservico"
	)
	@SequenceGenerator(
		name =  "sequence_id_microsservico",
		sequenceName = "sequence_microsservico"
	)
	private Long id;
	
	@Column(name = "nome")
	private String nome;

	@Column(name = "utilitario")
	private boolean utilitario;

	@Column(name = "finalizado")
	private boolean finalizado;
	
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "microsservico", cascade = CascadeType.ALL)
	private List<Classe> classes;
	
	
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
	
	public List<Classe> getClasses() {
		return classes;
	}
	public void setClasses(List<Classe> classes) {
		this.classes = classes;
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
	
	public Double cbo() {
		Double qtd = new Double(0);
		for(Classe cl : classes) {
			qtd += cl.getCbo();
		}
		BigDecimal bd = BigDecimal.valueOf(qtd/classes.size());
	    bd = bd.setScale(2, RoundingMode.HALF_UP);
	    
		return bd.doubleValue();
	}
	public Double rfc() {
		Double qtd = new Double(0);
		for(Classe cl : classes) {
			qtd += cl.getRfc();
		}
		BigDecimal bd = BigDecimal.valueOf(qtd/classes.size());
	    bd = bd.setScale(2, RoundingMode.HALF_UP);
	    
		return bd.doubleValue();
	}
	
	public Double lcom4() {
		Double qtd = new Double(0);
		for(Classe cl : classes) {
			qtd += cl.getLcom4();
		}
		BigDecimal bd = BigDecimal.valueOf(qtd/classes.size());
	    bd = bd.setScale(2, RoundingMode.HALF_UP);
	    
		return bd.doubleValue();
	}
	
	public String getIdNome() {
		return  "\""+ (nome != null ? id+":"+nome : id) +"\"";
	}
}
