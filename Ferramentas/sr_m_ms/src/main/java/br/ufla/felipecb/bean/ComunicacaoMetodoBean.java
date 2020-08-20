package br.ufla.felipecb.bean;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.ufla.felipecb.dao.CasoUsoRepository;
import br.ufla.felipecb.dao.ComunicacaoMetodoRepository;
import br.ufla.felipecb.entidades.CasoUso;
import br.ufla.felipecb.entidades.ComunicacaoMetodo;
import br.ufla.felipecb.entidades.Metodo;

@Component
public class ComunicacaoMetodoBean {

	@Autowired
	ComunicacaoMetodoRepository comunicacaoMetodoDao;

	@Autowired
	ComunicacaoClasseBean ccBean;
	
	@Autowired
	CasoUsoRepository ucDao;
	
	public void verificaOuSalva(Long idMetodoOrigem, Long idMetodoDestino, Long uc) {
		verificaOuSalva(idMetodoOrigem, idMetodoDestino, ucDao.findById(uc).get());
	}

	public boolean verificaOuSalva(Long idMetodoOrigem, Long idMetodoDestino, CasoUso uc) {
		
		boolean existe = false;
		ComunicacaoMetodo cm = comunicacaoMetodoDao.existeComunicacaoMetodo(idMetodoOrigem, idMetodoDestino);
		
		if(cm == null) {
			cm = new ComunicacaoMetodo();
			Metodo metodoOrigem = new Metodo();
			metodoOrigem.setId(idMetodoOrigem);
			cm.setMetodoOrigem(metodoOrigem);
			
			Metodo metodoDestino = new Metodo();
			metodoDestino.setId(idMetodoDestino);
			cm.setMetodoDestino(metodoDestino);
			
			cm.setQuantidadeChamadas(1l);
			
			cm.setListaCasoUso(new ArrayList<CasoUso>());
			cm.getListaCasoUso().add(uc);
			
			cm = comunicacaoMetodoDao.save(cm);
		} else {
			existe = true;
			
			if( ! cm.getListaCasoUso().contains(uc)) {
				cm.getListaCasoUso().add(uc);
				cm = comunicacaoMetodoDao.save(cm);
			}
			
			comunicacaoMetodoDao.atualizaQuantidadeChamadas(cm.getId());
			
		}
		return existe;
	}

	public List<ComunicacaoMetodo> buscarComunicacoesPorProjeto(Long idProjeto) {
		return comunicacaoMetodoDao.buscarComunicacoesPorProjeto(idProjeto);
	}

	public List<Long> buscaNosFolha(List<Long> nosAdicionados, Long idVersao) {
		if(nosAdicionados == null || nosAdicionados.isEmpty()) {
			nosAdicionados = new ArrayList<Long>();
			nosAdicionados.add(0l);
		}
		return comunicacaoMetodoDao.buscaNosFolha(nosAdicionados, idVersao);
	}

	public List<Long> buscaNosPai(Long noInformado) {
		return comunicacaoMetodoDao.buscaNosPai(noInformado);
	}

	public List<Long> buscaNosNaoContemplados(List<Long> nosAdicionados, Long idVersao) {
		return comunicacaoMetodoDao.buscaNosNaoContemplados(nosAdicionados, idVersao);
	}

	public List<ComunicacaoMetodo> buscarComunicacoesMetodo(Long idMet, Long idMicrosservicoClasse, Long idClasse, Long idNovaClasse) {
		return comunicacaoMetodoDao.buscarComunicacoesMetodo(idMet, idMicrosservicoClasse, idClasse, idNovaClasse);
	}

	public List<ComunicacaoMetodo> buscarAssociacaoClasseSemEspeciais(Long idClasse) {
		return comunicacaoMetodoDao.buscarAssociacaoClasseSemEspeciais(idClasse);
	}

	public void apagarComunicacoesMetodoOriginal(Long idClasse) {
		comunicacaoMetodoDao.apagarComunicacoesMetodoOriginal(idClasse);
	}

	public List<ComunicacaoMetodo> buscaMetodosOrigemChamamDestinoNaoDefinitiva(Long idClasseOrigem, Long idClasseDestino) {
		return comunicacaoMetodoDao.buscaMetodosOrigemChamamDestinoNaoDefinitiva(idClasseOrigem, idClasseDestino);
	}

	public List<Metodo> buscarMetodosPelaOrigem(Long idClasseOrigem) {
		return comunicacaoMetodoDao.buscarMetodosPelaOrigem(idClasseOrigem);
	}

	public List<Metodo> buscarMetodosOrigemOutroDestino(Long idClasseOrigem, Long idClasseDestino) {
		return comunicacaoMetodoDao.buscarMetodosOrigemOutroDestino(idClasseOrigem, idClasseDestino);
	}

	public void apagarComunicacoesMetodoPorId(Long id) {
		comunicacaoMetodoDao.apagarComunicacoesMetodoPorId(id);
	}

	public List<Long> buscarAtributosMetodoUtiliza(Long idMet) {
		return comunicacaoMetodoDao.buscarAtributosMetodoUtiliza(idMet);
	}

	public void apagarComunicacoesPorMetodo(Long idMet) {
		comunicacaoMetodoDao.apagarComunicacoesPorMetodo(idMet);
	}

	public ComunicacaoMetodo buscarPorId(Long idComunicacao) {
		return comunicacaoMetodoDao.findById(idComunicacao).get();
	}

	public void atualizaComunicacaoDefinitiva(String pacoteOrigem, String classeOrigem, String pacoteDestino,
			String classeDestino, boolean definitiva, Long idVersao) {
		comunicacaoMetodoDao.atualizaComunicacaoDefinitiva(pacoteOrigem, classeOrigem, pacoteDestino,
				classeDestino, definitiva, idVersao);
	}

}


