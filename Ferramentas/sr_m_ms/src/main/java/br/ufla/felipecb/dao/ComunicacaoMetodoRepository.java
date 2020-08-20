package br.ufla.felipecb.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import br.ufla.felipecb.entidades.ComunicacaoMetodo;
import br.ufla.felipecb.entidades.Metodo;

@Component
public interface ComunicacaoMetodoRepository extends CrudRepository<ComunicacaoMetodo, Long> {
	
	@Query("SELECT cm FROM ComunicacaoMetodo cm WHERE cm.metodoOrigem.id = :idMetodoOrigem and cm.metodoDestino.id = :idMetodoDestino")
	ComunicacaoMetodo existeComunicacaoMetodo(@Param("idMetodoOrigem") Long idMetodoOrigem, @Param("idMetodoDestino") Long idMetodoDestino);
	
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query("UPDATE ComunicacaoMetodo cm SET cm.quantidadeChamadas = cm.quantidadeChamadas + 1 WHERE cm.id = :id")
	int atualizaQuantidadeChamadas(@Param("id") Long id);

	@Query("SELECT cm FROM ComunicacaoMetodo cm WHERE cm.metodoOrigem.classe.versao.id = :idVersao and cm.metodoDestino.classe.versao.id = :idVersao")
	List<ComunicacaoMetodo> buscarComunicacoesPorProjeto(@Param("idVersao")Long idVersao);

	@Query("SELECT distinct cm.metodoDestino.id FROM ComunicacaoMetodo cm WHERE cm.metodoOrigem.classe.versao.id = :idVersao "
			+ "	and cm.metodoDestino.classe.versao.id = :idVersao "
			//remove os ja percorridos
			+ " and cm.metodoDestino.id not in (:nos) "
			//nao pode chama nenhum outro vértice
			+ " and cm.metodoDestino.id not in ("
				+ " select distinct cmetodo.metodoOrigem.id FROM ComunicacaoMetodo cmetodo WHERE "
				//remove desconsidera as comunicacoes quando a origem e destino é o mesmo metodo
				+ " cmetodo.metodoDestino.id != cmetodo.metodoOrigem.id "
				//remove os percorridos
				+ " and cmetodo.metodoDestino.id not in (:nos) ) "
				//Não considerea atributo uma amarração
				+ " and cm.metodoOrigem.atributo is false and cm.metodoDestino.atributo is false ")
	List<Long> buscaNosFolha(@Param("nos") List<Long> nosAdicionados, @Param("idVersao") Long idVersao);

	@Query("SELECT distinct cm.metodoOrigem.id FROM ComunicacaoMetodo cm "
			+ " WHERE cm.metodoDestino.id = :noInformado "
			+ " and cm.metodoDestino.id != cm.metodoOrigem.id "
			//Não considerea atributo uma amarração
			+ " and cm.metodoOrigem.atributo is false and cm.metodoDestino.atributo is false " )
	List<Long> buscaNosPai(Long noInformado);

	@Query("SELECT distinct cm.metodoOrigem.id FROM ComunicacaoMetodo cm WHERE "
			+ " cm.metodoOrigem.classe.versao.id = :idVersao and cm.metodoDestino.classe.versao.id = :idVersao "
			//remove os ja percorridos
			+ " and cm.metodoOrigem.id not in (:nos) "
			//remove desconsidera as comunicacoes quando a origem e destino é a mesma metodo
			+ " and cm.metodoDestino.id != cm.metodoOrigem.id "
			//Não considerea atributo uma amarração
			+ " and cm.metodoOrigem.atributo is false and cm.metodoDestino.atributo is false ")
	List<Long> buscaNosNaoContemplados(@Param("nos") List<Long> nosAdicionados, @Param("idVersao") Long idVersao);

	
//	@Query("SELECT cm FROM ComunicacaoMetodo cm "
//			+ " WHERE (cm.metodoOrigem.id = :idMetodo or cm.metodoDestino.id = :idMetodo) "
//			//+ " and cm.metodoOrigem.classe.microsservico.id <> cm.metodoDestino.classe.microsservico.id "
//			
//			
//			+ " and cm.metodoOrigem.classe.id = cm.metodoDestino.classe.id ")
//	List<ComunicacaoMetodo> buscarComunicacoesMetodo(Long idMetodo);
	
	@Query("SELECT cm FROM ComunicacaoMetodo cm "
			//comunicacoes do metodo especificado e
			+ " WHERE (cm.metodoOrigem.id = :idMetodo or cm.metodoDestino.id = :idMetodo) "
			// dentro do microssrviço X
			+ " and ( cm.metodoOrigem.classe.microsservico.id = :idMicrosservicoClasse "
				//ou comunicação interna da classe
				+ " or ( (cm.metodoOrigem.classe.id = :idClasse or cm.metodoOrigem.classe.id = :idNovaClasse) "
					+ " and (cm.metodoDestino.classe.id = :idClasse or cm.metodoDestino.classe.id = :idNovaClasse) "
				+ ") )"
				)
	List<ComunicacaoMetodo> buscarComunicacoesMetodo(Long idMetodo, Long idMicrosservicoClasse, 
			Long idClasse, Long idNovaClasse);

	@Query("SELECT cm FROM ComunicacaoMetodo cm "
			+ " WHERE cm.metodoOrigem.classe.id = :idClasse and cm.metodoDestino.classe.id = :idClasse "
			+ " and cm.metodoOrigem.ligacaoEspecial is false and cm.metodoDestino.ligacaoEspecial is false ")
	List<ComunicacaoMetodo> buscarAssociacaoClasseSemEspeciais(Long idClasse);

	@Transactional
	@Modifying(clearAutomatically = true)
	@Query("DELETE FROM ComunicacaoMetodo cm WHERE "
			+ " cm.metodoOrigem.id in (SELECT m.id FROM Metodo m WHERE m.classe.id = :idClasse)"
			+ " or cm.metodoDestino.id in (SELECT m.id FROM Metodo m WHERE m.classe.id = :idClasse) ")
	void apagarComunicacoesMetodoOriginal(Long idClasse);

	@Query("SELECT cm FROM ComunicacaoMetodo cm WHERE "
			+ " cm.metodoOrigem.classe.id = :idClasseOrigem and "
			+ " cm.metodoDestino.classe.id = :idClasseDestino "
			//origem não é metodo atributo, nem classe entidade ou utilitaria 
			+ " and cm.metodoOrigem.atributo is false "
			+ " and cm.metodoOrigem.classe.entidade is false "
			+ " and cm.metodoOrigem.classe.utilitaria is false "
			//A classe origem não pode ser definitiva (ou seja permite split class)
			// e a associação entre classes (ajustada nos metodos) também não é definitiva
			+ " and ( cm.metodoOrigem.classe.definitiva is false AND cm.definitiva is false )" 
			//destino não é metodo atributo, nem classe entidade ou utilitaria 
			+ " and cm.metodoDestino.atributo is false "
			+ " and cm.metodoDestino.classe.entidade is false "
			+ " and cm.metodoDestino.classe.utilitaria is false ")
	List<ComunicacaoMetodo> buscaMetodosOrigemChamamDestinoNaoDefinitiva(Long idClasseOrigem, Long idClasseDestino);

	@Query("SELECT cm.metodoDestino FROM ComunicacaoMetodo cm WHERE cm.metodoOrigem.id = :idMetodoOrigem "
			+ " and cm.metodoDestino.atributo is false and "
			+ " cm.metodoDestino.classe.entidade is false and cm.metodoDestino.classe.utilitaria is false")
	List<Metodo> buscarMetodosPelaOrigem(Long idMetodoOrigem);
	
	@Query("SELECT cm.metodoDestino FROM ComunicacaoMetodo cm WHERE "
			+ " cm.metodoOrigem.classe.id = :idClasseOrigem and cm.metodoDestino.classe.id != :idClasseDestino"
			+ " and cm.metodoDestino.atributo is false and "
			+ " cm.metodoDestino.classe.entidade is false and cm.metodoDestino.classe.utilitaria is false")
	List<Metodo> buscarMetodosOrigemOutroDestino(Long idClasseOrigem, Long idClasseDestino);

	@Query("SELECT cm.id FROM ComunicacaoMetodo cm WHERE cm.metodoOrigem.id = :idMet "
			+ " and cm.metodoDestino.atributo is true ")
	List<Long> buscarAtributosMetodoUtiliza(Long idMet);

	@Transactional
	@Modifying(clearAutomatically = true)
	@Query("DELETE FROM ComunicacaoMetodo cm WHERE cm.id = :id")
	void apagarComunicacoesMetodoPorId(Long id);
	
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query("DELETE FROM ComunicacaoMetodo cm WHERE cm.metodoOrigem.id = :idMet")
	void apagarComunicacoesPorMetodo(Long idMet);

	@Transactional
	@Modifying(clearAutomatically = true)
	@Query("UPDATE ComunicacaoMetodo set definitiva = :definitiva "
			+ " WHERE id in "
				+ " (SELECT cm.id FROM ComunicacaoMetodo cm WHERE cm.metodoOrigem.id in "
					+ "( SELECT me.id FROM Metodo me WHERE me.classe.nomePacote = :pacoteOrigem "
						+ " AND me.classe.nome = :classeOrigem AND me.classe.versao.id = :idVersao) "
					+ " AND cm.metodoDestino.id in "
						+ " ( SELECT me.id FROM Metodo me WHERE me.classe.nomePacote = :pacoteDestino "
						+ " AND me.classe.nome = :classeDestino AND me.classe.versao.id = :idVersao) "
				+ " )" )
	void atualizaComunicacaoDefinitiva(String pacoteOrigem, String classeOrigem, 
			String pacoteDestino, String classeDestino, boolean definitiva, Long idVersao);
	
}

