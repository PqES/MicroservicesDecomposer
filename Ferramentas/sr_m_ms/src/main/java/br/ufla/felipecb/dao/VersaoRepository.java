package br.ufla.felipecb.dao;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import br.ufla.felipecb.entidades.Versao;

@Component
public interface VersaoRepository extends CrudRepository<Versao, Long> {

	@Transactional
	@Modifying(clearAutomatically = true)
	@Query("UPDATE Versao v set v.codigo = (SELECT count(ver.id) FROM Versao ver "
				+ " WHERE ver.versaoBase.id = :idVersaoBase ) "
			+ " WHERE v.id = :idVersao")
	void atualizarCodigo(Long idVersao, Long idVersaoBase);
	
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query("UPDATE Versao v set v.codigoCompleto = :codigoCompleto WHERE v.id = :idVersao")
	void atualizarCodigoVersao(Long idVersao, String codigoCompleto);

	@Transactional
	@Modifying(clearAutomatically = true)
	@Query("UPDATE Versao v set "
//		//cbo 
		+ " cbo = (SELECT CAST(CAST(sum(c.cbo) as double)/count(c.cbo) as double) "
			+ " from Classe c WHERE c.versao.id = v.id), "
		//rfc 
		+ " rfc = (SELECT CAST(CAST(sum(c.rfc) as double)/count(c.rfc) as double) "
			+ " from Classe c WHERE c.versao.id = v.id), "
		//lcom4 
		+ " lcom4 = (SELECT CAST(CAST(sum(c.lcom4) as double) /count(c.lcom4) as double) "
			+ " from Classe c WHERE c.versao.id = v.id), "
		//lcom4 sem entidades
		+ " lcom4SemEntidades = (SELECT CAST(CAST(sum(c.lcom4) as double) /count(c.lcom4) as double) "
			+ " from Classe c WHERE c.versao.id = v.id and c.entidade is false and c.utilitaria is false) "
		
		+ " WHERE v.id = :idVersao ")
	void atualizarMetricasMediaPorClasse(Long idVersao);
	
}

