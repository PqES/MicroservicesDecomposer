package br.ufla.felipecb.processamento;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.ufla.felipecb.bean.CasoUsoBean;
import br.ufla.felipecb.bean.ClasseBean;
import br.ufla.felipecb.bean.ComunicacaoClasseBean;
import br.ufla.felipecb.bean.ComunicacaoMetodoBean;
import br.ufla.felipecb.bean.MetodoBean;
import br.ufla.felipecb.bean.MicrosservicoMetodoBean;
import br.ufla.felipecb.entidades.Classe;
import br.ufla.felipecb.entidades.Versao;
import br.ufla.felipecb.util.Utilitario;

@Component
public class ProcessamentoMicrosservicosMetodos {

	@Autowired
	ComunicacaoMetodoBean cmBean;
	
	@Autowired
	ComunicacaoClasseBean ccBean;
	
	@Autowired
	ClasseBean classeBean;
	
	@Autowired
	MetodoBean metodoBean;
	
	@Autowired
	MicrosservicoMetodoBean microsservicoMetBean;
	
	@Autowired
	CasoUsoBean casoUsoBean;
	
	@Autowired
	ProcessarLcom4 processarLcom4;
		
	public void montarGrupos(Versao versao) {
		
		Long noAtual;
		List<Long> nosPai;
		Queue<Long> aPercorrer = new LinkedList<Long>(cmBean.buscaNosFolha(null, versao.getId()));
		List<Long> nosAdicionados = new ArrayList<Long>(aPercorrer);
				
		List<List<Long>> grupos = criaGrupos(aPercorrer, null);
	    
	    //Executa enquanto existir nós a percorrer
	    while(! aPercorrer.isEmpty()) { 
	    	//No atual, pega o primeiro da fila e/ remove da fila o item percorrido
	        noAtual = aPercorrer.remove(); 
	        
	        //Busca os pais do nó atual (todos nós origem)
	        nosPai = cmBean.buscaNosPai(noAtual);
	        
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
	            aPercorrer.addAll(cmBean.buscaNosFolha(nosAdicionados, versao.getId()));
	            nosAdicionados.addAll(aPercorrer);
	            grupos = criaGrupos(aPercorrer, grupos);
	        }
	        
	        //Verifica se sobrou nós
	        if(aPercorrer.isEmpty()){ 
	            aPercorrer.addAll(cmBean.buscaNosNaoContemplados(nosAdicionados, versao.getId()));
	            nosAdicionados.addAll(aPercorrer);
	            grupos = criaGrupos(aPercorrer, grupos);
	        }
	    }
	    
	    salvarSugestaoMicrosservicos(grupos);
	    
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
			microsservicoMetBean.salvar(grupo, "MS-"+Utilitario.formatarCasas(index, grupos.size()));
			index++;
		}
	}

//	/**
//	 * Gera novas classes caso seus metodos estejam em microsserviços distintos
//	 * @param versao
//	 */
//	public void gerarNovasClasses(Long idVersao) {
//		
//		List<Classe> classes = metodoBean.buscarClassesQueNecesseitamReplica(idVersao);
//		
//		for(Classe classe : classes) {
//			
//			int index = 0;
//			//id microsservico / lista idMetodos
//			Map<Long, List<Long>> map = microsservicoMetBean.buscarMicrosservicoMetodos(classe.getId());
//			
//			for(Long mic : map.keySet()) {
//				index++;
//				//Cria uma replica da classe para cada microsservico
//				Classe classeReplica = classe.retornarReplica();
//				//Salva a classe com numero na frente para diferenciar
//				classeReplica.setNome(classeReplica.getNome() + index);
//				classeReplica = classeBean.salvar(classeReplica);
//				
//				metodoBean.atualizarClassePaiMetodos(classeReplica.getId(), map.get(mic));
//				
//				for(Long idMet : map.get(mic)) {
//					
//					List<ComunicacaoMetodo> comunicacoesMetodo = cmBean.buscarComunicacoesMetodo(idMet);
//					
//					//ajuste de comunicacao das classes por metodos
//					for(ComunicacaoMetodo cm : comunicacoesMetodo) {
//						Long idClasseOrigem = cm.getMetodoOrigem().getClasse().getId();
//						Long idClasseDestino = cm.getMetodoDestino().getClasse().getId();
//						if(cm.getMetodoOrigem().getId().equals(idMet)) {
//							idClasseOrigem = classeReplica.getId();
//						}
//						if(cm.getMetodoDestino().getId().equals(idMet)) {
//							idClasseDestino = classeReplica.getId();
//						}
//						
//						List<Long> ucs = casoUsoBean.buscarListaIdCasoUso(cm.getId());
//						//uma entrada por caso de uso
//						for(Long uc : ucs) {
//							ccBean.verificaOuSalva(idClasseOrigem, idClasseDestino, uc, true);
//						}
//					}
//				}
//			}
//			//apaga as comunicacoes entre as classes originais
//			ccBean.apagarComunicacoesClasseOriginal(classe.getId());
//			
//			classeBean.apagarClasseOriginal(classe.getId());
//		}
//		
//	}

	/**
	 * Gera novas classes caso seus metodos estejam em microsserviços distintos
	 * @param versao
	 */
	public void gerarNovasClassesLogicaSimilaridade(Long idVersao) {
		
		
		final Integer LIMITANTE = 3;
		boolean teveMudanca = true;
		
		while (teveMudanca) {
			teveMudanca = false;
			List<Classe> classes = metodoBean.buscarClassesQueNecesseitamReplica(idVersao);
			
			for(Classe classe : classes) {
				//id microsservico / lista idClasses
				Map<Long, List<Long>> map = microsservicoMetBean.buscarMicrosservicoClasses(classe.getId());
	
				//Verifica os microsserviços que tem mais de 3 classes distintas e ao menos uma em comum
				
				List<Long> keyList = new ArrayList<Long>(map.keySet());
				
				Integer maiorDif = null;
				Long idCaminhoMudanca = null;
				
				for(int i = 0; i < keyList.size()-1; i++) {
					for(int j = i+1; j < keyList.size(); j++) {
						int qtdComum = 0;
						//lista de classes do caminho 2
						for(Long idClasse : map.get(keyList.get(j))) {
							//conta quantas classes o mic1 tem em comum com mic2
							if(map.get(keyList.get(i)).contains(idClasse)) {
								qtdComum += 1;
							}
						}
	
						//qtd conjunto 1 + qtd conjunto 2 - ( iguais * 2 [pq ta nos 2 lados as classes] )
						Integer diferenca = ((map.get(keyList.get(i)).size() + map.get(keyList.get(j)).size()) - (qtdComum*2));
						
						if((maiorDif == null || maiorDif < diferenca) && qtdComum > 0) {
							maiorDif = diferenca;
							//Separa o caminho menor q será o 2
							if(map.get(keyList.get(i)).size() >= map.get(keyList.get(j)).size()) {
								idCaminhoMudanca = keyList.get(j);
							} else {
								idCaminhoMudanca = keyList.get(j);
							}
						}
					}
				}
				
				if(maiorDif != null && maiorDif >= LIMITANTE) {
					
					//modifica microsservico
					//Cria uma replica da classe para cada microsservico
					Classe classeReplica = classe.retornarReplica();
					//Salva a classe com numero do caminho(microsservicoMetodo) para diferenciar
					classeReplica.setNome(classeReplica.getNome() + idCaminhoMudanca);
					classeReplica = classeBean.salvar(classeReplica);
					
					//Novo id classe para os metodos que estão no caminho com microsservico de met em idCaminho2
					metodoBean.atualizarClassePaiMetodos(classeReplica.getId(), idCaminhoMudanca, classe.getId());
					
					teveMudanca = true;
					break;
				}
			}
		}
		
	}
	
}
