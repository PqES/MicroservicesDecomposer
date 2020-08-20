package br.ufla.felipecb.bean;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.ufla.felipecb.dao.CasoUsoRepository;
import br.ufla.felipecb.dao.ComunicacaoClasseRepository;
import br.ufla.felipecb.entidades.CasoUso;
import br.ufla.felipecb.entidades.Classe;
import br.ufla.felipecb.entidades.ComunicacaoClasse;
import br.ufla.felipecb.processamento.ProcessarDadosAjustes;

@Component
public class ComunicacaoClasseBean {

	@Autowired
	ComunicacaoClasseRepository comunicacaoClasseDao;
	
	@Autowired
	CasoUsoRepository ucDao;
	
	@Autowired
	ProcessarDadosAjustes pda;
	
	public void verificaOuSalva(Long idClasseOrigem, Long idClasseDestino, Long uc, boolean existeMetodoCadastrado) {
		verificaOuSalva(idClasseOrigem, idClasseDestino, ucDao.findById(uc).get(), existeMetodoCadastrado);
	}
	
	public void verificaOuSalva(Long idClasseOrigem, Long idClasseDestino, CasoUso uc, boolean existeMetodoCadastrado) {
		
		ComunicacaoClasse cc = comunicacaoClasseDao.existeComunicacaoClasse(idClasseOrigem, idClasseDestino);
		if(cc == null) {
			cc = new ComunicacaoClasse();
			Classe classeOrigem = new Classe();
			classeOrigem.setId(idClasseOrigem);
			cc.setClasseOrigem(classeOrigem);
			
			Classe classeDestino = new Classe();
			classeDestino.setId(idClasseDestino);
			cc.setClasseDestino(classeDestino);
			
			cc.setQuantidadeComunicacoes(1l);
			
			cc.setListaCasoUso(new ArrayList<CasoUso>());
			cc.getListaCasoUso().add(uc);
			
			cc.setPeso(cc.getQuantidadeComunicacoes());
			
			comunicacaoClasseDao.save(cc).getId();
			
		} else {
			if( ! existeMetodoCadastrado) {
				cc.setQuantidadeComunicacoes(cc.getQuantidadeComunicacoes()+1);
			}
			
			if( ! cc.getListaCasoUso().contains(uc)) {
				cc.getListaCasoUso().add(uc);
			}
			//zera peso
			cc.setPeso(0l);
			
			//atualiza
			cc = comunicacaoClasseDao.save(cc);
			
			//recalcula peso
			comunicacaoClasseDao.atualizaPeso(cc.getClasseOrigem().getId(), cc.getClasseDestino().getId(), cc.getId());
		}
	}

	public List<ComunicacaoClasse> buscarComunicacoesPorVersao(Long idVersao, boolean agrupadoPorMicrosservicos, 
			boolean exibeClassesMicrosservicosAberto, boolean exibeClassesMicrosservicosFinalizado,
			boolean soEntidadesOriginais) {
		
		if(agrupadoPorMicrosservicos) {
			//não exibe os finalizados
			if(! exibeClassesMicrosservicosFinalizado) {
				return comunicacaoClasseDao.buscarComunicacoesPorVersaoMicrosservicoSemFinalizado(idVersao);
			
			//exibe os abertos e os finalizados fechados
			} else if( ! exibeClassesMicrosservicosAberto) {
				return comunicacaoClasseDao.buscarComunicacoesPorVersaoMicrosservicoFechado(idVersao);
			
			//exibe os abertos e finalizados abertos
			}else {
				return comunicacaoClasseDao.buscarComunicacoesPorVersao(idVersao);
			}
		} else {
			return comunicacaoClasseDao.buscarComunicacoesPorVersao(idVersao);
			
		}
	}

	/**
	 * Método responsável por buscar nós folha ignorando os adicionados
	 * @param nosAdicionados
	 * @return
	 */
	public List<Long> buscaNosFolha(List<Long> nosAdicionados, Long idVersao) {
		
		if(nosAdicionados == null || nosAdicionados.isEmpty()) {
			nosAdicionados = new ArrayList<Long>();
			nosAdicionados.add(0l);
		}
		return comunicacaoClasseDao.buscaNosFolha(nosAdicionados, idVersao);
	}

	/**
	 * Busca o pai para o nó informado
	 * @param noAtual
	 * @return
	 */
	public List<Long> buscaNosPai(Long noInformado) {
		return comunicacaoClasseDao.buscaNosPai(noInformado);
	}

	/**
	 * Atualiza a comunicacao das classes sobre ser obrigatorio e sobre o peso
	 * 
	 * @param pacoteOrigem
	 * @param classeOrigem
	 * @param pacoteDestino
	 * @param classeDestino
	 * @param obrogatorio
	 * @param object
	 */
	public void atualizaComunicacao(String pacoteOrigem, String classeOrigem, String pacoteDestino, 
				String classeDestino, Boolean obrigatorio, Long peso, Long idVersao) {
		
		if(obrigatorio != null) {
			comunicacaoClasseDao.atualizarObrigatoriedade(pacoteOrigem, classeOrigem, pacoteDestino, classeDestino, obrigatorio, idVersao);
		} 

		if (peso != null){
			comunicacaoClasseDao.atualizaPeso(pacoteOrigem, classeOrigem, pacoteDestino, classeDestino, peso, idVersao);
		}
		
	}

	public List<Long> buscaNosNaoContemplados(List<Long> nosAdicionados, Long idVersao) {
		return comunicacaoClasseDao.buscaNosNaoContemplados(nosAdicionados, idVersao);
	}

	public void descobrirArestasMaisForte(Long idVersao) {
		comunicacaoClasseDao.resetarNosFortes(idVersao);
		comunicacaoClasseDao.atualizarNosMaisFortes(idVersao);
	}

	public void apagarComunicacoesClasseOriginal(Long idClasse) {
		comunicacaoClasseDao.apagarComunicacoesClasseOriginal(idClasse);
	}

	public List<ComunicacaoClasse> buscarClassesClassesChamamMsDistinto(Long idVersao) {
		return comunicacaoClasseDao.buscarClassesClassesChamamMsDistinto(idVersao);
	}

	/**
	 * apaga todas as comunic
	 * @param idVersao
	 * @param idVersaoAjuste 
	 */
	public void recriarComunicacoesClasses(Long idProjeto, Long idVersao, Long idVersaoAjuste) {
		comunicacaoClasseDao.apagarPorVersao(idVersao);
		
		List<Object[]> comunicacaoClasses = comunicacaoClasseDao.buscarLigacoesClasses(idVersao);
		
		ComunicacaoClasse comunicacaoClasse;
		for(Object[] obj : comunicacaoClasses) {
			comunicacaoClasse = new ComunicacaoClasse();
			comunicacaoClasse.setClasseOrigem((Classe) obj[0]);
			comunicacaoClasse.setClasseDestino((Classe) obj[1]);
			comunicacaoClasse.setQuantidadeComunicacoes((Long) obj[2]);
			comunicacaoClasseDao.save(comunicacaoClasse);
		}
		
		comunicacaoClasseDao.atualizaPeso(idVersao);
		
		if(idVersaoAjuste != null) {
			pda.ajustarDados(idProjeto, idVersaoAjuste, idVersao);
		}
		
	}

//	public void atualizaPeso(Long idVersao) {
//		comunicacaoClasseDao.atualizaPeso(idVersao);
//	}
}
