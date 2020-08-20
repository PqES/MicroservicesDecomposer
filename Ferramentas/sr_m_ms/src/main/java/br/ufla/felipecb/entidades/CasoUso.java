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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Table(name="caso_uso")
@Entity
//@Audited
public class CasoUso {
	
	@Id @Column(name="id")
	@GeneratedValue(
		strategy = GenerationType.SEQUENCE,
		generator = "sequence_id_caso_uso"
	)
	@SequenceGenerator(
		name =  "sequence_id_caso_uso",
		sequenceName = "sequence_caso_uso"
	)
	private Long id;
	
	@Column(name="codigo")
	private String codigo;
	
	//sem getters e setter, usa apenas no remove
	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "caso_uso_comunicacao_classe",
            joinColumns = {
                    @JoinColumn(name = "id_caso_uso", referencedColumnName = "id",
                            nullable = false, updatable = false)},
            inverseJoinColumns = {
                    @JoinColumn(name = "id_comunicacao_classe", referencedColumnName = "id",
                            nullable = false, updatable = false)})
	private List<ComunicacaoClasse> comunicacaoClasses;

	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "caso_uso_comunicacao_metodo",
            joinColumns = {
                    @JoinColumn(name = "id_caso_uso", referencedColumnName = "id",
                            nullable = false, updatable = false)},
            inverseJoinColumns = {
                    @JoinColumn(name = "id_comunicacao_metodo", referencedColumnName = "id",
                            nullable = false, updatable = false)})
	private List<ComunicacaoMetodo> comunicacaoMetodos;
	
	public CasoUso(){}
	
	public CasoUso(Long id) {
		this.id = id;
	}
	
	public CasoUso(String codigo) {
		this.codigo = codigo;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public List<ComunicacaoClasse> getComunicacaoClasses() {
		return comunicacaoClasses;
	}
	public void setComunicacaoClasses(List<ComunicacaoClasse> comunicacaoClasses) {
		this.comunicacaoClasses = comunicacaoClasses;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codigo == null) ? 0 : codigo.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CasoUso other = (CasoUso) obj;
		if (codigo == null) {
			if (other.codigo != null)
				return false;
		} else if (!codigo.equals(other.codigo))
			return false;
		return true;
	}
	
	
	
}
