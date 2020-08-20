package br.ufla.felipecb.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import br.ufla.felipecb.entidades.ComunicacaoClasse;

@Component
public interface ComunicacaoClasseRepository extends CrudRepository<ComunicacaoClasse, Long> {
	
	@Query("SELECT cc FROM ComunicacaoClasse cc WHERE cc.classeOrigem.id = :idClasseOrigem and cc.classeDestino.id = :idClasseDestino")
	ComunicacaoClasse existeComunicacaoClasse(@Param("idClasseOrigem") Long idClasseOrigem, @Param("idClasseDestino") Long idClasseDestino);

	@Query("SELECT cc FROM ComunicacaoClasse cc WHERE cc.classeOrigem.versao.id = :idVersao and "
			+ " cc.classeDestino.versao.id = :idVersao and  cc.classeOrigem.id != cc.classeDestino.id")
	List<ComunicacaoClasse> buscarComunicacoesPorVersao(@Param("idVersao") Long idVersao);

	@Query("SELECT cc FROM ComunicacaoClasse cc WHERE cc.classeOrigem.versao.id = :idVersao and "
			+ " cc.classeDestino.versao.id = :idVersao and cc.classeOrigem.id != cc.classeDestino.id and "
			//MS não finalizados
			+ " cc.classeOrigem.microsservico.finalizado is false and "
			+ " cc.classeDestino.microsservico.finalizado is false" )
	List<ComunicacaoClasse> buscarComunicacoesPorVersaoMicrosservicoSemFinalizado(@Param("idVersao") Long idVersao);
	
	@Query("SELECT cc FROM ComunicacaoClasse cc WHERE cc.classeOrigem.versao.id = :idVersao and "
			+ " cc.classeDestino.versao.id = :idVersao and  cc.classeOrigem.id != cc.classeDestino.id and "
				//se origem alguma das classes esriverem em um MS não finalizado
			+ " (cc.classeOrigem.microsservico.finalizado is false or "
			+ "  cc.classeDestino.microsservico.finalizado is false or "
			+ "  cc.classeOrigem.microsservico.id != cc.classeDestino.microsservico.id ) " )
	List<ComunicacaoClasse> buscarComunicacoesPorVersaoMicrosservicoFechado(@Param("idVersao") Long idVersao);
	
	@Query("SELECT distinct cc.classeDestino.id FROM ComunicacaoClasse cc WHERE cc.classeOrigem.versao.id = :idVersao and cc.classeDestino.versao.id = :idVersao "
			//remove os ja percorridos
			+ " and cc.classeDestino.id not in (:nos) "
			//nao pode chama nenhum outro vértice
			+ " and cc.classeDestino.id not in ("
				+ " select distinct cclass.classeOrigem.id FROM ComunicacaoClasse cclass WHERE "
				//remove desconsidera as comunicacoes quando a origem e destino é a mesma classe
				+ " cclass.classeDestino.id != cclass.classeOrigem.id "
				//remove os percorridos
				+ " and cclass.classeDestino.id not in (:nos) "
				//Não pode ser entidade e classe utilitaria
				+ " and cclass.classeDestino.entidade is false and cclass.classeDestino.utilitaria is false "
				//Não pode ser entidade e classe utilitaria
			+ " ) and cc.classeDestino.entidade is false and cc.classeDestino.utilitaria is false ")
	List<Long> buscaNosFolha(@Param("nos") List<Long> nos, @Param("idVersao") Long idVersao);

	
	@Query("SELECT distinct cc.classeOrigem.id FROM ComunicacaoClasse cc WHERE cc.classeDestino.id = :noInformado "
			// não pode ser separado ou é o nó mais forte (sem considerar entidade e classe utilitaria)
			+ " and ( cc.obrigatoria is true or ( "
				+ " cc.peso >= ( "
						+ " SELECT MAX(peso) FROM ComunicacaoClasse ccl WHERE ccl.classeDestino.id = :noInformado "
						+ " and ccl.classeDestino.id != ccl.classeOrigem.id"
					+ " )"
				+ " and cc.peso > 0 ) ) "
				+ " and cc.classeDestino.id != cc.classeOrigem.id "
			+ " and cc.classeDestino.entidade is false and cc.classeDestino.utilitaria is false ")
	List<Long> buscaNosPai(@Param("noInformado") Long noInformado);

	@Transactional
	@Modifying(clearAutomatically = true)
	@Query("UPDATE ComunicacaoClasse cc set maisForte = true "
			+ " WHERE peso >= ( "
				+ " SELECT MAX(ccl.peso) FROM ComunicacaoClasse ccl WHERE "
				+ " ccl.classeDestino.id = cc.classeDestino.id "
				+ " AND ccl.classeDestino.id != ccl.classeOrigem.id "
				+ " AND ccl.classeDestino.versao.id = :idVersao "
			+ " ) and peso > 0 "
			+ " and cc.classeDestino.id != cc.classeOrigem.id "
			+ " and cc.id in ("
				+ "SELECT ccl.id FROM ComunicacaoClasse ccl "
					+ " WHERE cc.classeDestino.versao.id = :idVersao "
					+ " AND cc.classeDestino.id != cc.classeOrigem.id "
			+ " )")
	void atualizarNosMaisFortes(@Param("idVersao") Long idVersao);
	
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query("UPDATE ComunicacaoClasse cc set cc.maisForte = false "
			+ " WHERE cc.id in ("
				+ "SELECT ccl.id FROM ComunicacaoClasse ccl "
					+ "WHERE cc.classeDestino.versao.id = :idVersao "
			+ ")")
	void resetarNosFortes(@Param("idVersao") Long idVersao);
	
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query("UPDATE ComunicacaoClasse ccl set ccl.obrigatoria = :obrigatoria "
			+ " WHERE ccl.id in ( "
				+ " SELECT cc.id FROM ComunicacaoClasse cc WHERE "
				+ " cc.classeOrigem.nomePacote = :pacoteOrigem and cc.classeOrigem.nome = :classeOrigem and "
				+ " cc.classeDestino.nomePacote = :pacoteDestino and cc.classeDestino.nome = :classeDestino "
				+ " AND cc.classeOrigem.versao.id = :idVersao AND cc.classeDestino.versao.id = :idVersao "
				+ " AND cc.classeDestino.id != cc.classeOrigem.id "
			+ " ) ")
	void atualizarObrigatoriedade(@Param("pacoteOrigem") String pacoteOrigem, @Param("classeOrigem") String classeOrigem, 
			@Param("pacoteDestino") String pacoteDestino, @Param("classeDestino") String classeDestino, 
			@Param("obrigatoria") boolean obrigatoria, @Param("idVersao") Long idVersao);

	@Transactional
	@Modifying(clearAutomatically = true)
	@Query("UPDATE ComunicacaoClasse ccl set ccl.peso = :peso "
			+ " WHERE ccl.id in ( "
				+ " SELECT cc.id FROM ComunicacaoClasse cc WHERE "
				+ " cc.classeOrigem.nomePacote = :pacoteOrigem and cc.classeOrigem.nome = :classeOrigem and "
				+ " cc.classeDestino.nomePacote = :pacoteDestino and cc.classeDestino.nome = :classeDestino "
				+ " AND cc.classeOrigem.versao.id = :idVersao AND cc.classeDestino.versao.id = :idVersao "
			+ " ) ")
	void atualizaPeso(@Param("pacoteOrigem") String pacoteOrigem, @Param("classeOrigem") String classeOrigem, 
			@Param("pacoteDestino") String pacoteDestino, @Param("classeDestino") String classeDestino, 
			@Param("peso") Long peso, @Param("idVersao") Long idVersao);
//	

	@Query("SELECT distinct cc.classeOrigem.id FROM ComunicacaoClasse cc WHERE "
			+ " cc.classeOrigem.versao.id = :idVersao and cc.classeDestino.versao.id = :idVersao "
			//remove os ja percorridos
			+ " and cc.classeOrigem.id not in (:nos) "
			//remove desconsidera as comunicacoes quando a origem e destino é a mesma classe
			+ " and cc.classeDestino.id != cc.classeOrigem.id "
			//Não pode ser entidade e classe utilitaria
			+ " and cc.classeDestino.entidade is false and cc.classeDestino.utilitaria is false ")
	List<Long> buscaNosNaoContemplados(@Param("nos") List<Long> nosAdicionados, @Param("idVersao") Long idVersao);

	@Transactional
	@Modifying(clearAutomatically = true)
	@Query("UPDATE ComunicacaoClasse cc set cc.peso = (SELECT SUM( "
				+ " SELECT count(cm.id) FROM ComunicacaoMetodo cm left join cm.listaCasoUso as ucase WHERE "
				+ " cm.metodoOrigem.classe.id = :idClasseOrigem and cm.metodoDestino.classe.id = :idClasseDestino "
				+ "	and ucase.id = uc.id "
			+ "  ) FROM CasoUso uc ) "
		+ " WHERE cc.id = :idComunicacaoClasse")
	void atualizaPeso(@Param("idClasseOrigem") Long idClasseOrigem, 
			@Param("idClasseDestino") Long idClasseDestino, @Param("idComunicacaoClasse") Long idComunicacaoClasse);

	@Query("SELECT distinct cc FROM ComunicacaoClasse cc "
			+ " WHERE (cc.classeOrigem.id = :idClasse and cc.classeDestino.microsservico.id = :idMicrosservico ) "
			+ " or (cc.classeDestino.id = :idClasse and cc.classeOrigem.microsservico.id = :idMicrosservico ) ")
	List<ComunicacaoClasse> buscarComunicacaoClasse(@Param("idClasse") Long idClasse, @Param("idMicrosservico") Long idMicrosservico);

	@Transactional
	@Modifying(clearAutomatically = true)
	@Query("DELETE FROM ComunicacaoClasse cc WHERE "
			+ " cc.classeOrigem.id = :idClasse or cc.classeDestino.id = :idClasse")
	void apagarComunicacoesClasseOriginal(Long idClasse);

	@Query("SELECT distinct cc FROM ComunicacaoClasse cc "
			+ " WHERE cc.classeOrigem.versao.id = :idVersao and cc.classeDestino.versao.id = :idVersao "
			+ " and cc.classeOrigem.microsservico.id != cc.classeDestino.microsservico.id "
			+ " and cc.classeOrigem.entidade is false and  cc.classeOrigem.utilitaria is false"
			+ " and cc.classeDestino.entidade is false and  cc.classeDestino.utilitaria is false")
	List<ComunicacaoClasse> buscarClassesClassesChamamMsDistinto(Long idVersao);

	@Transactional
	@Modifying(clearAutomatically = true)
	@Query("DELETE FROM ComunicacaoClasse cc WHERE "
			+ " cc.classeOrigem.id in (select c.id from Classe c where c.versao.id = :idVersao) or "
			+ " cc.classeDestino.id in (select c.id from Classe c where c.versao.id = :idVersao) ")
	void apagarPorVersao(Long idVersao);

	@Query(" SELECT clOrgiem, clDestino, count(cm.metodoOrigem.classe) "
				+ "	FROM ComunicacaoMetodo cm left join cm.metodoOrigem.classe as clOrgiem "
				+ " left join cm.metodoDestino.classe as clDestino "
				+ " WHERE cm.metodoOrigem.classe.versao.id = :idVersao OR "
					+ " cm.metodoDestino.classe.versao.id = :idVersao "
				+ " GROUP BY clOrgiem.id, clDestino.id ")
	List<Object[]> buscarLigacoesClasses(Long idVersao);
	
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query("UPDATE ComunicacaoClasse cc set cc.peso = (SELECT SUM( "
				+ " SELECT count(cm.id) FROM ComunicacaoMetodo cm left join cm.listaCasoUso as ucase"
				+ "	left join cm.metodoOrigem as metOrigem left join cm.metodoDestino as metDestino WHERE "
				+ " metOrigem.classe.id = cc.classeOrigem.id and metDestino.classe.id = cc.classeDestino.id "
				+ "	and ucase.id = uc.id "
			+ "  ) FROM CasoUso uc ) "
		+ " WHERE cc.id in ( SELECT ccl.id FROM ComunicacaoClasse ccl WHERE "
		+ "	ccl.classeOrigem.versao.id = :idVersao or ccl.classeDestino.versao.id = :idVersao) ")
	void atualizaPeso(@Param("idVersao") Long idVersao);

	
//	@Transactional
//	@Modifying(clearAutomatically = true)
//	@Query("INSERT INTO ComunicacaoClasse (id, classeOrigem, classeDestino, quantidadeComunicacoes)  "
//			+ " SELECT nextval('sequence_comunicacao_classe'), cm.metodoOrigem.classe, cm.metodoDestino.classe, count(cm.metodoOrigem.classe) "
//				+ "	FROM ComunicacaoMetodo cm WHERE cm.metodoOrigem.classe.versao.id = :idVersao OR "
//					+ " cm.metodoDestino.classe.versao.id = :idVersao "
//				+ " GROUP BY cm.metodoOrigem.classe.id, cm.metodoDestino.classe.id "
//					)
//	void criarComunicacoesClasses(Long idVersao);
	
}

