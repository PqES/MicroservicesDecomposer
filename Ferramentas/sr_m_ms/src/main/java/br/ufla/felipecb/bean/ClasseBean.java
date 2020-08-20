package br.ufla.felipecb.bean;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.ufla.felipecb.dao.ClasseRepository;
import br.ufla.felipecb.entidades.Classe;
import br.ufla.felipecb.processamento.ProcessarLcom4;

@Component
public class ClasseBean {

	@Autowired
	ClasseRepository classeDao;
	
    @Autowired
    ProcessarLcom4 processarLcom4;
    
    @Autowired
    VersaoBean versaoBean;
    
	public Classe salvar(Classe classe) {
		return classeDao.save(classe);
	}

	public List<Classe> buscarClasses(Long idVersao) {
		return classeDao.buscarClasses(idVersao);
	}
	
	public Classe buscarClasse(Long idClasse) {
		return classeDao.findById(idClasse).get();
	}
	
	public void atualizarClasse(String pacote, String nomeClasse, Long idVersao, Boolean entidade, Boolean utilitaria, Boolean definitiva) {
		if(entidade != null) {
			classeDao.atualizaEntidade(pacote, nomeClasse, idVersao, entidade);
		}
		if(utilitaria != null) {
			classeDao.atualizarClasseUtilitaria(pacote, nomeClasse, idVersao, utilitaria);
		}
		if(definitiva != null) {
			classeDao.atualizarClasseDefiitiva(pacote, nomeClasse, idVersao, definitiva);
		}
	}

	public void limparMirosservicos(Long idVersao) {
		classeDao.zerarMicrosservicos(idVersao);
	}

	
	public void atualizarMetricas(Long idVersao) {
		classeDao.atualizarCbo(idVersao);
		classeDao.atualizarRfc(idVersao);
		processarLcom4.calcularLcom4(idVersao);
		versaoBean.atualizarMetricasMediaPorClasse(idVersao);
	}
	
	public List<Long> buscarIds(Long idVersao) {
		return classeDao.buscarIds(idVersao);
	}

	public void atualizarLcom4(Long idClasse, Integer lcom4) {
		classeDao.atualizarLcom4(idClasse, lcom4);
	}

	public void apagarClasseOriginal(Long idClasse) {
		classeDao.deleteById(idClasse);
	}

	public List<String> buscarClassesFormatadasIdNome(Long idVersao, boolean exibeEntidadeUtil) {
		
		List<Classe> classes = classeDao.buscarClasses(idVersao);
		List<String> retorno = new ArrayList<String>();
		
		for(Classe classe : classes) {
			if( exibeEntidadeUtil || ! (classe.isEntidade() || classe.isUtilitaria())) {
				retorno.add(classe.getIdNome());
			}
		}
		
		return retorno;
	}
}
