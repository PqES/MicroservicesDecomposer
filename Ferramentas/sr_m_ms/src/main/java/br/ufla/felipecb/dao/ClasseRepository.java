package br.ufla.felipecb.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import br.ufla.felipecb.entidades.Classe;

@Component
public interface ClasseRepository extends CrudRepository<Classe, Long> {
	
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query("UPDATE Classe c set c.entidade = :ehEntidade WHERE c.versao.id = :idVersao and c.nome = :nome and c.nomePacote = :pacote")
	int atualizaEntidade(@Param("pacote") String pacote, @Param("nome") String nome, 
			@Param("idVersao") Long idVersao, @Param("ehEntidade") boolean entidade);
	
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query("UPDATE Classe c set c.utilitaria = :ehUtilitaria WHERE c.versao.id = :idVersao and c.nome = :nome and c.nomePacote = :pacote")
	int atualizarClasseUtilitaria(@Param("pacote") String pacote, @Param("nome") String nome, 
			@Param("idVersao") Long idVersao, @Param("ehUtilitaria") boolean utilitaria);


	@Transactional
	@Modifying(clearAutomatically = true)
	@Query("UPDATE Classe c set c.definitiva = :definitiva WHERE c.versao.id = :idVersao and c.nome = :nome and c.nomePacote = :pacote")
	void atualizarClasseDefiitiva(@Param("pacote") String pacote, @Param("nome") String nome, 
			@Param("idVersao") Long idVersao, @Param("definitiva") boolean definitiva);
	
	@Query("SELECT c FROM Classe c WHERE c.versao.id = :idVersao order by c.nome")
	List<Classe> buscarClasses(@Param("idVersao") Long idVersao);

	@Query("SELECT c FROM Classe c WHERE c.versao.id = :idVersao and c.utilitaria is true")
	List<Classe> buscarClassesUtilitarias(@Param("idVersao") Long idVersao);
	
	@Query("SELECT c FROM Classe c WHERE c.versao.id = :idVersao and c.entidade is true")
	List<Classe> buscarEntidades(@Param("idVersao") Long idVersao);
	
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query("UPDATE Classe set microsservico.id = null WHERE versao.id = :idVersao")
	void zerarMicrosservicos(@Param("idVersao")Long idVersao);
	
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query("UPDATE Classe c set cbo = ("
			+ " SELECT count(distinct cc.id) FROM ComunicacaoClasse cc "
			+ " WHERE cc.classeOrigem.id = c.id "
				+ " and cc.classeOrigem.id != cc.classeDestino.id ) "
		+ " WHERE versao.id = :idVersao ")
	void atualizarCbo(@Param("idVersao")Long idVersao);
	
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query("UPDATE Classe c set rfc = ("
				+ " ( SELECT count( distinct cm.metodoDestino.id) FROM ComunicacaoMetodo cm "
					+ " WHERE cm.metodoOrigem.classe.id = c.id "
					+ " and cm.metodoOrigem.classe.id != cm.metodoDestino.classe.id "
					//RFC não conta com atributos
					+ " and cm.metodoOrigem.atributo is false "
					+ " and cm.metodoDestino.atributo is false "
					//RFC não conta esse tipo de associacao (não é chamada de método)
					//RETORNO PARAMETRO...
					+ " and cm.metodoOrigem.ligacaoEspecial is false "
					+ " and cm.metodoDestino.ligacaoEspecial is false ) + "
				+ " ( SELECT count(m.id) FROM Metodo m WHERE m.classe.id = c.id "
					+ " and m.ligacaoEspecial is false and m.atributo is false)"
			+ " ) WHERE versao.id = :idVersao  ")
	void atualizarRfc(@Param("idVersao")Long idVersao);
	
	@Query("SELECT c.id FROM Classe c WHERE c.versao.id = :idVersao")
	List<Long> buscarIds(Long idVersao);

	@Transactional
	@Modifying(clearAutomatically = true)
	@Query("UPDATE Classe set lcom4 = :lcom4 WHERE id = :idClasse  ")
	void atualizarLcom4(@Param("idClasse")Long idClasse, @Param("lcom4")Integer lcom4);

	@Transactional
	@Modifying(clearAutomatically = true)
	@Query("DELETE FROM Classe WHERE id in (:idClasses) ")
	void deleteAllById(@Param("idClasses")List<Long> idClasses);
	
}

