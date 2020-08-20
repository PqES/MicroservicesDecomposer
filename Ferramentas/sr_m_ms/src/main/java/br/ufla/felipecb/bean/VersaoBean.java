package br.ufla.felipecb.bean;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.ufla.felipecb.dao.VersaoRepository;
import br.ufla.felipecb.entidades.Versao;

@Component
public class VersaoBean {

	@Autowired
	VersaoRepository versaoDao;
	
	public void excluirVersao(Long id) {
		versaoDao.delete(versaoDao.findById(id).get());
	}

	public Versao criarNovaVersao(Long idVersaoBase, boolean splitClasses, Long limiar) {
		
		Versao versaoBase = versaoDao.findById(idVersaoBase).get();
		Versao versao = new Versao();
		versao.setProjeto(versaoBase.getProjeto());
		versao.setVersaoBase(versaoBase);
		versao.setSplitClasses(splitClasses);
		versao.setLimiar(limiar);
		
		versao = versaoDao.save(versao);
		versaoDao.atualizarCodigo(versao.getId(), versaoBase.getId());
		
		versao = versaoDao.findById(versao.getId()).get();
		
		versaoDao.atualizarCodigoVersao(versao.getId(), versao.getCodigoVersao());
		
		return versao;
	}

	public Versao buscarVersao(Long idVersao) {
		Optional<Versao> versao = versaoDao.findById(idVersao);
		if(versao.isPresent()) {
			if(versao.get().getVersaoBase() != null &&
					versao.get().getVersaoBase().getId() != null) {
				System.out.println(versao.get().getVersaoBase().getCodigoCompleto());
			}
			return versao.get();
		}
		return null;
	}

	public void atualizarMetricasMediaPorClasse(Long idVersao) {
		versaoDao.atualizarMetricasMediaPorClasse(idVersao);
	}
}
