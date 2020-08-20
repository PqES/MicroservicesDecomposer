package br.ufla.felipecb.dao;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import br.ufla.felipecb.entidades.Microsservico;

@Component
public interface MicrosservicoRepository extends CrudRepository<Microsservico, Long> {

	
	@Query("SELECT distinct classe.microsservico FROM Classe as classe left join ComunicacaoClasse cc "
			+ " on ( classe.id = cc.classeDestino.id or classe.id = cc.classeOrigem.id ) "
			+ " WHERE (cc.classeDestino.id = :noInformado or cc.classeOrigem.id = :noInformado) "
			+ " and classe.microsservico.id is not null ")
	List<Microsservico> buscarMicrosservicosClasseAssociada(@Param("noInformado") Long noInformado);

	@Query("SELECT distinct classe.microsservico FROM Classe classe WHERE classe.versao.id = :idVersao order by classe.microsservico.nome")
	List<Microsservico> buscarMicrosservicos(@Param("idVersao") Long idVersao);

	@Query("SELECT distinct classe.id FROM Classe as classe "
			+ " WHERE classe.versao.id = :idVersao and classe.microsservico.utilitario is true")
	Set<Long> buscarClassesMicroUtil(Long idVersao);
	
	@Query("SELECT distinct classe.microsservico.id FROM Classe as classe left join ComunicacaoClasse cc "
			+ " on ( classe.id = cc.classeDestino.id or classe.id = cc.classeOrigem.id ) "
			+ " WHERE (cc.classeDestino.id = :noInformado or cc.classeOrigem.id = :noInformado)"
			+ " and classe.microsservico.id is not null ")
	List<Long> buscaIdsMicrosservicos(@Param("noInformado") Long noInformado);

	@Query("SELECT distinct classe.microsservico.id FROM Classe classe WHERE classe.versao.id = :idVersao "
			+ " group by classe.microsservico having count(classe.id) < :limiar ")
	List<Long> verificarMicrosservicosMenores(Long idVersao, Long limiar);
	
}

