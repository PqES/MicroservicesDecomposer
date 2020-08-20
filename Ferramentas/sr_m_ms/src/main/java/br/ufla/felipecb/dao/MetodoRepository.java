package br.ufla.felipecb.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import br.ufla.felipecb.entidades.Classe;
import br.ufla.felipecb.entidades.Metodo;

@Component
public interface MetodoRepository extends CrudRepository<Metodo, Long> {

	
//	@Query("SELECT distinct m.classe FROM MicrosservicoMetodo mm left join mm.metodos m"
//			+ " WHERE m.classe.versao.id = :idVersao "
//			+ " having count(distinct m.microsservico.id) > 1")
	
	@Query("SELECT distinct classe FROM Metodo m inner join m.classe as classe "
			+ " WHERE m.classe.versao.id = :idVersao "
			+ " group by classe "
			+ " having count(distinct m.microsservico.id) > 1 "
			+ " and m.classe.entidade is false ")
	List<Classe> buscarClassesQueNecesseitamReplica(Long idVersao);

	@Transactional
	@Modifying(clearAutomatically = true)
	@Query("UPDATE Metodo set classe.id = :idClasse WHERE id in (:idsMetodos) ")
	int atualizarClassePaiMetodos(Long idClasse, @Param("idsMetodos") List<Long> idsMetodos);

	@Query("SELECT id FROM Metodo m WHERE classe.id = :idClasse ")
	List<Long> buscarIds(Long idClasse);
	
	@Query("SELECT id FROM Metodo m WHERE classe.id = :idClasse and ligacaoEspecial is false ")
	List<Long> buscarIdsSemLigacoesEspeciais(Long idClasse);

	@Query("SELECT m FROM Metodo m WHERE m.classe.id = :idClasse ")
	List<Metodo> buscarPorClasse(Long idClasse);
	
	@Query("SELECT m.id FROM Metodo m WHERE m.classe.id = :idClasse ")
	List<Long> buscarIdsPorClasse(Long idClasse);

	@Transactional
	@Modifying(clearAutomatically = true)
	@Query("DELETE FROM Metodo m WHERE m.classe.id = :idClasse ")
	void apagarMetodosOriginais(Long idClasse);

	@Transactional
	@Modifying(clearAutomatically = true)
	@Query("UPDATE Metodo set classe.id = :idClasse WHERE "
			+ " microsservico.id = :idMicMet and classe.id = :idClasseOrigem "
			+ " and atributo is false ")
	void atualizarClassePaiMetodos(Long idClasse, Long idMicMet, Long idClasseOrigem);

	
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query("DELETE FROM Metodo WHERE id = :idMetodo and "
			+ " 0 = (SELECT count(cm.id) FROM ComunicacaoMetodo cm WHERE "
				+ " cm.metodoOrigem.id = :idMetodo or cm.metodoDestino.id = :idMetodo) ")
	void apagarMetodoSeNaoReferenciadoNaComunicacao(Long idMetodo);

	@Query("SELECT m FROM Metodo m WHERE m.nome = :nome and m.classe.id = :idClasse ")
	Metodo verificaAtributoJaReplicado(String nome, Long idClasse);

}

