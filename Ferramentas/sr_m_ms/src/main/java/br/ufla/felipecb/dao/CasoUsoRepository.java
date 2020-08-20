package br.ufla.felipecb.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import br.ufla.felipecb.entidades.CasoUso;

@Component
public interface CasoUsoRepository extends CrudRepository<CasoUso, Long> {

	@Query("SELECT uc FROM CasoUso uc WHERE uc.codigo = :codigo")
	CasoUso recuperarPorCodigo(@Param(value = "codigo")String casoUso);

//	@Query("SELECT uc FROM CasoUso uc join uc.comunicacaoMetodos cm "
//			+ " WHERE cm.id = :idComunicacaoMetodo")
//	List<CasoUso> buscarPorIdComunicacaoMetodo(Long idComunicacaoMetodo);
	
	@Query("SELECT uc.id FROM CasoUso uc join uc.comunicacaoMetodos cm "
			+ " WHERE cm.id = :idComunicacaoMetodo")
	List<Long> buscarListaIdCasoUso(Long idComunicacaoMetodo);

	
}

