package br.ufla.felipecb.processamento;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.ufla.felipecb.bean.CasoUsoBean;
import br.ufla.felipecb.bean.ClasseBean;
import br.ufla.felipecb.bean.ComunicacaoClasseBean;
import br.ufla.felipecb.bean.ComunicacaoMetodoBean;
import br.ufla.felipecb.bean.MetodoBean;
import br.ufla.felipecb.entidades.CasoUso;
import br.ufla.felipecb.entidades.Classe;
import br.ufla.felipecb.entidades.Metodo;
import br.ufla.felipecb.entidades.Versao;
import br.ufla.felipecb.util.Constante;
import br.ufla.felipecb.util.UtilitarioArquivo;

@Component
public class ProcessarDadosIniciais {

	@Autowired
	ClasseBean classeBean;
	
	@Autowired
	MetodoBean metodoBean;
	
	@Autowired
	CasoUsoBean casoUsoBean;

	@Autowired
	ComunicacaoClasseBean comunicacaoClasseBean;
	
	@Autowired
	ComunicacaoMetodoBean comunicacaoMetodoBean;
	
	@Autowired
    ProcessamentoMicrosservicos procMic;
	
	List<String> atributos;
	
	//Mantem os métodos salvos nesse array pra não ter de buscar o id no banco
	Map<String, Long> metodosNomeMetodoId = new HashMap<String, Long>();
	
	public void processarArquivos(final Versao versao) throws FileNotFoundException {
		
		/*
		 * salva as classes do projeto e deixa com os ids na memória para facilitar salvar a
		 * comunicacao das classes sem verificar toda hora qual é o id das classes no banco
		*/
		Map<String, Long> classesPacoteClasseId = salvarClasses(versao);
		
		//Salva as comunicações, gerando classes e métodos
		salvarClassesMetodos(classesPacoteClasseId, versao.getProjeto().getId());
		
	}
	
	private Map<String, Long> salvarClasses(Versao versao) {

		Map<String, Long> classesPacoteClasseId = new HashMap<String, Long>();

		try {
			File arquivo = UtilitarioArquivo.gerarArquivo(versao.getProjeto().getId(), null, Constante.ARQUIVO_CLASSES);
			
			BufferedReader reader = new BufferedReader(new FileReader(arquivo));
			String line;
			Classe classe = new Classe();
			while((line = reader.readLine()) != null) {
				if( ! line.trim().equals("")) {
					
					if(line.startsWith("atr:")) {
						//se tiver atributos
						if( ! line.equals("atr:")) {
							//Cria um metodo especial(pra representar ligacao com atributo)
							for(String atr : line.replace("atr:", "").split(";")) {
								//salva pq é um atributo na realidade
								Long idMetodo = verificaSalvaMetodo(atr, classe.getId(), true, false);
								
								Metodo met = new Metodo();
								met.setId(idMetodo);
							}
						}
						
					} else if (line.startsWith("met:")) {
						//caso tenha valor
						if( ! line.equals("met:")) {
							Metodo metodo;
							for(String met : line.replace("met:", "").split(";")) {
								String str[] = met.split("\\[");
								metodo = new Metodo(str[0], classe);
								metodo = metodoBean.salvar(metodo);
								metodosNomeMetodoId.put(metodo.getClasse().getId()+"::"+metodo.getNome(), metodo.getId());
								
								if(str.length > 1) {
									for(String atributo : str[1].replace("]", "").split(",")) {
										Long idMetodoDestino = verificaSalvaMetodo(atributo, classe.getId(), false, false);
										
										CasoUso ucEspecial = casoUsoBean.salvar("#ESPECIAL#");
										
										//Verifica, salva comunicação método com atributo(que é metodo)
										comunicacaoMetodoBean.verificaOuSalva(metodo.getId(), idMetodoDestino, ucEspecial);
										
										//Salva comunicação métodos
										comunicacaoMetodoBean.verificaOuSalva(metodo.getId(), idMetodoDestino, ucEspecial);
									}
								}
							}
						}
					} else {
						classe = new Classe();
						
						String strs[] = line.split(":");
						String pacote_classe = strs[0].trim();
						
						//caso seja entidade ou classe utilitaria
						if(strs.length > 1) {
							String tipoClasse = strs[1].trim();
							switch (tipoClasse) {
								case Constante.CLASSE_ENTIDADE:
									classe.setEntidade(true);
									break;
								case Constante.CLASSE_UTILITARIA:
									classe.setUtilitaria(true);
									break;
								default:
									break;
							}
						}
						String[] str = pacote_classe.trim().split("\\.");
						
						classe.setNome(str[str.length-1]);
						
						if(str.length > 1) {
							//menos o tamanho da classe, menos -1 ponto "."
							classe.setNomePacote(pacote_classe.subSequence(0, pacote_classe.length()-classe.getNome().length()-1).toString());
						}
	
						classe.setVersao(versao);
						classe = classeBean.salvar(classe);
						
						classesPacoteClasseId.put(pacote_classe, classe.getId());
					}
				}
			}
			reader.close();

		} catch (IOException e) { }
		return classesPacoteClasseId;
	}

	private void salvarClassesMetodos(Map<String, Long> classesPacoteClasseId, Long idProjeto) {
		try {
			
			final Integer ORGIEM = 0;
			final Integer DESTINO = 1;
			final String SEPARADOR_ORIGEM_DESTINO = "->";
			final Integer CLASSE = 0;
			final Integer METODO = 1;
			final String SEPARADOR_CLASSE_METODO = "::";
			final String SEPARADOR_CASO_USO = "#";
			
			File arquivo = UtilitarioArquivo.gerarArquivo(idProjeto, null, Constante.ARQUIVO_COMUNICACOES);
			
			BufferedReader reader = new BufferedReader(new FileReader(arquivo));
			String line;
			
			while((line = reader.readLine()) != null) {
				if( ! line.trim().equals("")) {
					
					String comunicacao = line.replaceAll("\"", "").split(SEPARADOR_CASO_USO)[0].trim();
					String casoUso = line.split(SEPARADOR_CASO_USO)[1].trim();
					
					//Verifica salva origem
					String origemDestino[] = comunicacao.split(SEPARADOR_ORIGEM_DESTINO);
					String pacote_classe = origemDestino[ORGIEM].split(SEPARADOR_CLASSE_METODO)[CLASSE].trim();
					String metodo = origemDestino[ORGIEM].split(SEPARADOR_CLASSE_METODO)[METODO].trim();
					
					Long idClasseOrigem = classesPacoteClasseId.get(pacote_classe);
					Long idMetodoOrigem = verificaSalvaMetodo(metodo, idClasseOrigem, false, true);
					
					//Verifica salva destino
					origemDestino = comunicacao.split(SEPARADOR_ORIGEM_DESTINO);
					pacote_classe = origemDestino[DESTINO].split(SEPARADOR_CLASSE_METODO)[CLASSE].trim();
					metodo = origemDestino[DESTINO].split(SEPARADOR_CLASSE_METODO)[METODO].trim();
					
					Long idClasseDestino = classesPacoteClasseId.get(pacote_classe);
					Long idMetodoDestino = verificaSalvaMetodo(metodo, idClasseDestino, false, true);
					
					//Verifica, salva caso de uso e associacao classe por caso de uso
					CasoUso uc = casoUsoBean.salvar(casoUso);
					
//					CasoUso uc = casoUsoCodigoUC.get(casoUso);
//					//verifica se caso de uso já foi salvo
//					if(uc == null) {
//						uc = casoUsoBean.salvar(casoUso);
//						casoUsoCodigoUC.put(casoUso, uc);
//					}
					
					//salva comunicação método
					comunicacaoMetodoBean.verificaOuSalva(idMetodoOrigem, idMetodoDestino, uc);
//					
//					//Verifica, salva comunicação classe
//					comunicacaoClasseBean.verificaOuSalva(idClasseOrigem, idClasseDestino, uc, existe);
				}
			}
			reader.close();

		} catch (IOException e) { }
	}
	
	private Long verificaSalvaMetodo(String metodo, Long idClasse, boolean atributo, boolean ligacaoEspecial) {
		
		Long idMetodoOrigem = metodosNomeMetodoId.get(idClasse+"::"+metodo);
		//verifica se método já foi salvo
		if(idMetodoOrigem == null) {
			idMetodoOrigem = metodoBean.salvar(metodo, idClasse, ligacaoEspecial, atributo).getId();
			metodosNomeMetodoId.put(idClasse+"::"+metodo, idMetodoOrigem);
		}
		return idMetodoOrigem;
	}

}