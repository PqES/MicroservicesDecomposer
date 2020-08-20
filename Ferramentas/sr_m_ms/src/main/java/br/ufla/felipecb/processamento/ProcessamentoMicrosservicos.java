package br.ufla.felipecb.processamento;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.ufla.felipecb.bean.ClasseBean;
import br.ufla.felipecb.bean.ComunicacaoClasseBean;
import br.ufla.felipecb.bean.ComunicacaoMetodoBean;
import br.ufla.felipecb.bean.MetodoBean;
import br.ufla.felipecb.bean.MicrosservicoBean;
import br.ufla.felipecb.bean.MicrosservicoMetodoBean;
import br.ufla.felipecb.bean.VersaoBean;
import br.ufla.felipecb.entidades.CasoUso;
import br.ufla.felipecb.entidades.Classe;
import br.ufla.felipecb.entidades.ComunicacaoClasse;
import br.ufla.felipecb.entidades.ComunicacaoMetodo;
import br.ufla.felipecb.entidades.Metodo;
import br.ufla.felipecb.entidades.Versao;
import br.ufla.felipecb.util.Utilitario;

@Component
public class ProcessamentoMicrosservicos {

	@Autowired
	ComunicacaoClasseBean ccBean;
	
	@Autowired
	ComunicacaoMetodoBean cmBean;
	
	@Autowired
	ClasseBean classeBean;
	
	@Autowired
	MetodoBean metodoBean;
	
	@Autowired
	VersaoBean versaoBean;
	
	@Autowired
	MicrosservicoBean microsservicoBean;
	
	@Autowired
	MicrosservicoMetodoBean microsservicoMetBean;	
	
	public void montarGrupos(Versao versao, Long idVersaoAjuste) {
		
//		ccBean.limparComunicacoesReplicas(versao.getId());
		classeBean.limparMirosservicos(versao.getId());
//		microsservicoBean.limparMicrosservicos(versao.getId());
		
		ccBean.descobrirArestasMaisForte(versao.getId());
		
		Long noAtual;
		List<Long> nosPai;
		Queue<Long> aPercorrer = new LinkedList<Long>(ccBean.buscaNosFolha(null, versao.getId()));
		List<Long> nosAdicionados = new ArrayList<Long>(aPercorrer);
				
		List<List<Long>> grupos = criaGrupos(aPercorrer, null);
	    
	    //Executa enquanto existir nós a percorrer
	    while(! aPercorrer.isEmpty()) { 
	    	
	    	//No atual, pega o primeiro da fila e/ remove da fila o item percorrido
	        noAtual = aPercorrer.remove(); 
	    	
	        //Busca o pai do nó atual
	        nosPai = ccBean.buscaNosPai(noAtual);
	        
	        Integer grupoAtual = identificaGrupoPertencente(noAtual, grupos);
	        
	        for(Long noPai : nosPai) {

		        //Se o no pai já foi adicionado na fila
		        if( nosAdicionados.contains(noPai) ) { 
		            Integer grupoPai = identificaGrupoPertencente(noPai, grupos);
		            unirGrupos(grupoPai, grupoAtual, grupos); 
		            //recalcula o grupo atual
		            grupoAtual = identificaGrupoPertencente(noAtual, grupos);
		        
		        //Caso o no pai não esteja na fila, adiciona
		        } else { 
		            aPercorrer.add(noPai);
		            nosAdicionados.add(noPai);
		            //Adciona o nó pai ao grupo do no atual
		            grupos.get(grupoAtual).add(noPai);
		        }
	        }
	        
	        //Verifica se existira novos nós folhas caso a fila esvazie
	        if(aPercorrer.isEmpty()){ 
	            aPercorrer.addAll(ccBean.buscaNosFolha(nosAdicionados, versao.getId()));
	            nosAdicionados.addAll(aPercorrer);
	            grupos = criaGrupos(aPercorrer, grupos);
	        }
	        
	        //Verifica se sobrou nós
	        if(aPercorrer.isEmpty()){ 
	            aPercorrer.addAll(ccBean.buscaNosNaoContemplados(nosAdicionados, versao.getId()));
	            nosAdicionados.addAll(aPercorrer);
	            grupos = criaGrupos(aPercorrer, grupos);
	        }
	    }
	    
	    //Salva desconsiderando classes utilitarias e entidades
	    salvarSugestaoMicrosservicos(grupos);
	    
	    if(versao.isSplitClasses() && idVersaoAjuste.equals(versao.getId())) {
		    ccBean.recriarComunicacoesClasses(versao.getProjeto().getId(), versao.getId(), idVersaoAjuste);
		    tentarSepararClassesChamamMsDistinto(versao.getId());
	    }
	    
//	    microsserviceBean.adicionarClassesSobraram();
	    
	    if(versao.getLimiar() > 0) {
	    	ccBean.recriarComunicacoesClasses(versao.getProjeto().getId(), versao.getId(), idVersaoAjuste);
	    	List<Long> idsMicrosservico = microsservicoBean.verificarMicrosservicosMenores(versao.getId(), versao.getLimiar());
	    	System.out.println(idsMicrosservico.size());
	    }
	    
	    ccBean.recriarComunicacoesClasses(versao.getProjeto().getId(), versao.getId(), idVersaoAjuste);
	    microsservicoBean.adicionarClassesUtilitarias(versao.getId());
	    microsservicoBean.adicionarEntidades(versao.getId());
	    
	    ccBean.recriarComunicacoesClasses(versao.getProjeto().getId(), versao.getId(), idVersaoAjuste);
		ccBean.descobrirArestasMaisForte(versao.getId());
	    
	    //Gera as metricas para o monolitico antes de realizar alterações
	    classeBean.atualizarMetricas(versao.getId());
	}

	/**
	 * Cria um grupo para cada nó a percorrer
	 * @param aPercorrer
	 * @return
	 */
	private List<List<Long>> criaGrupos(Queue<Long> aPercorrer, List<List<Long>> grupos) {
		
		if(aPercorrer.isEmpty()) {
			return grupos;
		}
		
		if(grupos == null) {
			grupos = new ArrayList<List<Long>>();
		}
		
		for(Long item : aPercorrer) {
			List<Long> lista = new ArrayList<Long>();
			lista.add(item);
			grupos.add(lista);
		}
		return grupos;
	}
	
	/**
	 * Identifica o grupo do noAtual
	 * @param noAtual
	 * @return
	 */
	private Integer identificaGrupoPertencente(Long noAtual, List<List<Long>> grupos) {
		
		for(int i = 0; i < grupos.size(); i++) {
			if(grupos.get(i).contains(noAtual)) {
				return i;
			}
		}
		//Nunca é para passar aqui
		return 0;
	}
	
	/**
	 * Realiza a junção dos grupos que o nó pai esta e do grupo do nó atual
	 * @param grupoPai
	 * @param grupoAtual
	 */
	private void unirGrupos(Integer grupoPai, Integer grupoAtual, List<List<Long>> grupos) {
		if(grupoPai != grupoAtual) {
			//adiciona todo integrantes do grupo do pai ao grupo atual
			grupos.get(grupoAtual).addAll(grupos.get(grupoPai));
			//remove o grupo do pai
			grupos.remove(grupoPai.intValue());
		}
	}
	
	/**
	 * Salva no banco de dados as sugestões de microsserviços
	 * @param grupos
	 */
	private void salvarSugestaoMicrosservicos(List<List<Long>> grupos) {
		
		int index = 1;
		for(List<Long> grupo: grupos) {
			microsservicoBean.salvar(grupo, "MS-"+Utilitario.formatarCasas(index, grupos.size()));
			index++;
		}
	}
	
	/**
	 * Reliza a separação de uma classe q esta em uma microsserviço e chama outro.
	 * Essa separação só é realizada em casos onde não há metodos em comunm.
	 * É é possível desmembrar a classes orgiem
	 * @param id
	 */
	public void tentarSepararClassesChamamMsDistinto(Long idVersao) {

		List<ComunicacaoClasse> comunicacoesEntreMS = ccBean.buscarClassesClassesChamamMsDistinto(idVersao);
		
		for(ComunicacaoClasse cc : comunicacoesEntreMS) {
			verificaPodeSeparar(cc.getClasseOrigem().getId(), cc.getClasseDestino().getId());
		}
	}
	
	private void verificaPodeSeparar(Long idClasseOrigem, Long idClasseDestino) {
		
		List<ComunicacaoMetodo> comunicacoesMetodos = cmBean.buscaMetodosOrigemChamamDestinoNaoDefinitiva(idClasseOrigem, idClasseDestino);
		
		//Empecilho por metodo da própria origem
		List<Long> idsMetodosEmpecilhos = new ArrayList<Long>();
		//Empecilho por outra classe
		List<Long> idsClassesEmpecilhos = new ArrayList<Long>();
		
		List<Long> metodos = new ArrayList<Long>();
		List<Long> ucs = new ArrayList<Long>();
		
		for(ComunicacaoMetodo cm : comunicacoesMetodos) {

			//Pega todos metodo destino que utiliza do metodo origem
			List<Metodo> metodosDestinos = cmBean.buscarMetodosPelaOrigem(cm.getMetodoOrigem().getId());
			
			for(Metodo metDest : metodosDestinos) {
				
				//Tem destino outra classe sem ser o destino esperado
				if( ! metDest.getClasse().getId().equals(idClasseDestino)) {
					//chama método dentro da mesma classe 
					//TODO (AVALIAR se é necessário migrar esse método junto)
					if( metDest.getClasse().getId().equals(idClasseOrigem)) {
						idsMetodosEmpecilhos.add(metDest.getId());
					
					//TODO mudar para verificar se o empecilho é MS distinto
					} else if(!metDest.getClasse().getId().equals(idClasseDestino)) {
						idsClassesEmpecilhos.add(metDest.getClasse().getId());
					
					} else {
						metodos.add(cm.getMetodoOrigem().getId());
						for(CasoUso uc : cm.getListaCasoUso()) {
							ucs.add(uc.getId());
						}
					}
				} else {
					metodos.add(cm.getMetodoOrigem().getId());
					for(CasoUso uc : cm.getListaCasoUso()) {
						ucs.add(uc.getId());
					}
				}
			}
		}
		
		if(idsMetodosEmpecilhos.isEmpty() && idsClassesEmpecilhos.isEmpty() && ! metodos.isEmpty()) {	
			//criar classe e mudar metodos
			
			Classe classeDividida = classeBean.buscarClasse(idClasseOrigem).retornarReplica();
			//seta o MS da classe destino
			classeDividida.setMicrosservico(classeBean.buscarClasse(idClasseDestino).getMicrosservico());
			//Salva a classe com numero do microsservico destino para diferenciar
			classeDividida.setNome(classeDividida.getNome() + classeDividida.getMicrosservico().getId());
			
			classeDividida = classeBean.salvar(classeDividida);
			
			//Novo id classe para os metodos que estão na lista
			metodoBean.atualizarClassePaiMetodos(classeDividida.getId(), metodos);
			List<Long> apagarComunicacaoMetodos = new ArrayList<Long>();
			List<Long> apagarAtributos = new ArrayList<Long>();
			
			for(Long idMet : metodos) {
				//replica os atributos do metodo
				List<Long> listaComuMetAtributo = cmBean.buscarAtributosMetodoUtiliza(idMet);
				
				Metodo atributo;
				for(Long idComMetAtr : listaComuMetAtributo) {
					
					ComunicacaoMetodo comMetAtr = cmBean.buscarPorId(idComMetAtr);
					atributo = metodoBean.verificaAtributoJaReplicado(comMetAtr.getMetodoDestino().getNome(), classeDividida.getId());
					
					if(atributo == null) {
						//cria um atributo 
						atributo = comMetAtr.getMetodoDestino().retornarReplica();
						atributo.setClasse(classeBean.buscarClasse(classeDividida.getId()));
						metodoBean.salvar(atributo);
					}
					
					//Novo id classe para os metodos que estão na lista
					for(CasoUso uc : comMetAtr.getListaCasoUso()) {
						cmBean.verificaOuSalva(comMetAtr.getMetodoOrigem().getId(), atributo.getId(), uc);
					}
					apagarComunicacaoMetodos.add(idComMetAtr);
					
					apagarAtributos.add(comMetAtr.getMetodoDestino().getId());
				}
			}
			
			//remover comunicacao classes do metodo da antiga classe para a nova
			for(Long id : apagarComunicacaoMetodos) {
				cmBean.apagarComunicacoesMetodoPorId(id);
			}
			
			//Apaga os atributos caso não sejam mais referenciados
			for(Long id : apagarAtributos) {
				metodoBean.apagarMetodoSeNaoReferenciadoNaComunicacao(id);
			}
		}
	}

}
