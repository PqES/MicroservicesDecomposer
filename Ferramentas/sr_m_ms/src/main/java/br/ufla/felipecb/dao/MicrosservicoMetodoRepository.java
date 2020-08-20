package br.ufla.felipecb.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import br.ufla.felipecb.entidades.MicrosservicoMetodo;

@Component
public interface MicrosservicoMetodoRepository extends CrudRepository<MicrosservicoMetodo, Long> {

	
	@Query("SELECT distinct metodo.microsservico FROM Metodo as metodo left join ComunicacaoMetodo cc "
			+ " on ( metodo.id = cc.metodoDestino.id or metodo.id = cc.metodoOrigem.id ) "
			+ " WHERE (cc.metodoDestino.id = :noInformado or cc.metodoOrigem.id = :noInformado) ")
	List<MicrosservicoMetodo> buscarMicrosservicosMetodoAssociado(@Param("noInformado") Long noInformado);

	@Query("SELECT distinct metodo.microsservico FROM Metodo metodo WHERE metodo.classe.versao.id = :idVersao")
	List<MicrosservicoMetodo> buscarMicrosservicos(@Param("idVersao") Long idVersao);

	@Query("SELECT distinct mm.id FROM MicrosservicoMetodo mm"
			+ " left join mm.metodos as metodo "
			+ " WHERE metodo.classe.id = :idClasse ")
//			+ " and metodo.classe.entidade is not true")
	List<Long> buscarPorClasse(Long idClasse);
	
	@Query("SELECT distinct metodo.id FROM MicrosservicoMetodo mm left join mm.metodos as metodo "
			+ "	WHERE mm.id = :id and metodo.classe.id = :idClasse ")
//			+ " and metodo.classe.entidade is not true")
	List<Long> buscarMetodos(Long id, Long idClasse);

	@Query("SELECT distinct metodo.classe.id FROM MicrosservicoMetodo mm "
			+ " left join mm.metodos as metodo WHERE mm.id = :idMic")
	List<Long> buscarClasses(Long idMic);

	
}

