package br.ufla.felipecb.bean;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.ufla.felipecb.dao.CasoUsoRepository;
import br.ufla.felipecb.entidades.CasoUso;

@Component
public class CasoUsoBean {

	@Autowired
	CasoUsoRepository casoUsoDao;
	
	public CasoUso salvar(String casoUso) {
		
		CasoUso uc = casoUsoDao.recuperarPorCodigo(casoUso);
		if(uc == null) {
			uc = new CasoUso();
			uc.setCodigo(casoUso);
			
			uc = casoUsoDao.save(uc);
		}
		return uc;
	}

//	public List<CasoUso> buscarPorIdComunicacaoMetodo(Long idMetodo) {
//		return casoUsoDao.buscarPorIdComunicacaoMetodo(idMetodo);
//	}
	
	public List<Long> buscarListaIdCasoUso(Long idMetodo) {
		return casoUsoDao.buscarListaIdCasoUso(idMetodo);
	}
}
