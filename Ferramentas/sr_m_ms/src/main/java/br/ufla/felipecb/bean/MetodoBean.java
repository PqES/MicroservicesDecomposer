package br.ufla.felipecb.bean;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.ufla.felipecb.dao.MetodoRepository;
import br.ufla.felipecb.entidades.Classe;
import br.ufla.felipecb.entidades.Metodo;

@Component
public class MetodoBean {

	@Autowired
	MetodoRepository metodoDao;
	
	public Metodo salvar(String nome, Long idClasse, boolean especial, boolean atributo) {
		Metodo metodo = new Metodo();
		metodo.setNome(nome);
		Classe classe = new Classe();
		classe.setId(idClasse);
		metodo.setClasse(classe);
		metodo.setLigacaoEspecial(especial);
		metodo.setAtributo(atributo);
		
		return metodoDao.save(metodo);
	}
	
	public Metodo salvar(Metodo metodo) {
		return metodoDao.save(metodo);
	}

	public List<Classe> buscarClassesQueNecesseitamReplica(Long idVersao) {
		return metodoDao.buscarClassesQueNecesseitamReplica(idVersao);
	}

	public void atualizarClassePaiMetodos(Long idClasse, List<Long> listaIdMetodos) {
		metodoDao.atualizarClassePaiMetodos(idClasse, listaIdMetodos);
	}
	
	public void atualizarClassePaiMetodos(Long idClasse, Long idMicMet, Long idClasseOrigem) {
		metodoDao.atualizarClassePaiMetodos(idClasse, idMicMet, idClasseOrigem);
	}

	public List<Long> buscarIds(Long idClasse) {
		return metodoDao.buscarIds(idClasse);
	}

	public List<Long> buscarIdsSemLigacoesEspeciais(Long idClasse) {
		return metodoDao.buscarIdsSemLigacoesEspeciais(idClasse);
	}

	public void apagarMetodoSeNaoReferenciadoNaComunicacao(Long id) {
		metodoDao.apagarMetodoSeNaoReferenciadoNaComunicacao(id);
	}

	public Metodo verificaAtributoJaReplicado(String nome, Long idClasse) {
		return metodoDao.verificaAtributoJaReplicado(nome, idClasse);
	}
}
