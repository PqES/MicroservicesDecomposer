package br.ufla.felipecb.processamento;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.ufla.felipecb.bean.ClasseBean;
import br.ufla.felipecb.bean.ComunicacaoClasseBean;
import br.ufla.felipecb.bean.ComunicacaoMetodoBean;
import br.ufla.felipecb.bean.VersaoBean;
import br.ufla.felipecb.util.Constante;
import br.ufla.felipecb.util.UtilitarioArquivo;

@Component
public class ProcessarDadosAjustes {

	@Autowired
	ClasseBean classeBean;

	@Autowired
	ComunicacaoClasseBean comunicacaoClasseBean;
    
	@Autowired
	ComunicacaoMetodoBean comunicacaoMetodoBean;
	
    @Autowired
    VersaoBean versaoBean;
    
	public String ajustarClasses(Long idProjeto, Long idVersaoLeitura, Long idVersaoModificar) throws FileNotFoundException {
		
		StringBuilder erro = null;
		
		File arquivo = UtilitarioArquivo.gerarArquivo(idProjeto, idVersaoLeitura, Constante.ARQUIVO_AJUSTES_CLASSES);
		
		BufferedReader reader;
		try {
			if(arquivo.exists()) {
				reader = new BufferedReader(new FileReader(arquivo));
				String line;
				long linha = 0;
				
				Boolean entidade;
				Boolean utilitaria;
				Boolean definitiva;
				while((line = reader.readLine()) != null) {
					
					linha++;
					entidade = null;
					utilitaria = null;
					definitiva = null;
					
					if(line.trim().endsWith(Constante.CLASSE_ENTIDADE)) {
						line = line.trim().replace(":"+Constante.CLASSE_ENTIDADE, "");
						entidade = true;
					} else if(line.trim().endsWith(Constante.CLASSE_NAO_ENTIDADE)) {
						line = line.trim().replace(":"+Constante.CLASSE_NAO_ENTIDADE, "");
						entidade = false;
					} else if(line.trim().endsWith(Constante.CLASSE_UTILITARIA)) {
						line = line.trim().replace(":"+Constante.CLASSE_UTILITARIA, "");
						utilitaria = true;
					} else if(line.trim().endsWith(Constante.CLASSE_NAO_UTILITARIA)) {
						line = line.trim().replace(":"+Constante.CLASSE_NAO_UTILITARIA, "");
						utilitaria = false;
					} else if(line.trim().endsWith(Constante.CLASSE_DEFINITIVA)) {
						line = line.trim().replace(":"+Constante.CLASSE_DEFINITIVA, "");
						definitiva = true;
					} else if(line.trim().endsWith(Constante.CLASSE_NAO_DEFINITIVA)) {
						line = line.trim().replace(":"+Constante.CLASSE_NAO_DEFINITIVA, "");
						definitiva = false;
					}
					
					String[] str = line.trim().split("\\.");
					String classe = str[str.length-1];
					String pacote = "";
					
					if(str.length > 1) {
						//menos o tamanho da classe, menos -1 ponto "."
						pacote = line.subSequence(0, line.length()- classe.length()-1).toString();
						classeBean.atualizarClasse(pacote, classe, idVersaoModificar, entidade, utilitaria, definitiva);
					} else {
						if(erro == null) {
							erro = new StringBuilder("Erros na(s) linha(s):");
						}
						erro.append(" ").append(linha).append(",");
					}
				}
				reader.close();
				
				if(erro != null) {
					//sem a última ,
					return erro.toString().substring(0, erro.length()-2);
				}
			}
		} catch (FileNotFoundException e) {
			throw new FileNotFoundException("Erro ao recuperar o arquivo: "+arquivo.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void ajustarDados(Long idProjeto, Long idVersaoLeitura, Long idVersaoModificar) {
		try {
			
			File arquivo = UtilitarioArquivo.gerarArquivo(idProjeto, idVersaoLeitura, Constante.ARQUIVO_AJUSTES_COMUNICACOES);
			
			BufferedReader reader = new BufferedReader(new FileReader(arquivo));
			String line;
			while((line = reader.readLine()) != null) {
				if( ! line.trim().equals("")) {
					
					String primeiraParte = line.replaceAll("\"", "").split(":")[0].trim();
					String comando = line.split(":")[1].trim();
					
					String classeOrigem = null, classeDestino = null;
					String pacoteOrigem = null, pacoteDestino = null;
					
					String strings[] = primeiraParte.split("->");
					String[] str = strings[0].trim().split("\\.");
					
					//Origem
					classeOrigem = str[str.length-1].trim();
					if(str.length > 1) {
						//menos o tamanho da classe, menos -1 ponto "."
						pacoteOrigem = strings[0].trim().subSequence(0, (strings[0].trim().length()-classeOrigem.length())-1).toString();
					}
					
					str = strings[1].trim().split("\\.");
					//Destino
					classeDestino = str[str.length-1].trim();
					if(str.length > 1) {
						//menos o tamanho da classe, menos -1 ponto "."
						pacoteDestino = strings[1].trim().subSequence(0, (strings[1].trim().length()-classeDestino.length())-1).toString();
					}
					
					switch(comando) {
						case Constante.COMUNICACAO_CLASSE_OBRIGATORIA :
						case Constante.COMUNICACAO_CLASSE_NAO_OBRIGATORIA :
							comunicacaoClasseBean.atualizaComunicacao(pacoteOrigem, classeOrigem, pacoteDestino, classeDestino, ("obrigatorio".equals(comando)), null, idVersaoModificar );
								
							break;
							
						case Constante.COMUNICACAO_CLASSE_DEFINITIVA :
						case Constante.COMUNICACAO_CLASSE_NAO_DEFINITIVA :
							comunicacaoMetodoBean.atualizaComunicacaoDefinitiva(pacoteOrigem, classeOrigem, pacoteDestino, classeDestino, ("definitiva".equals(comando)), idVersaoModificar );
								
							break;
								
						default:
							try {
								Long peso = Long.parseLong(comando);
								comunicacaoClasseBean.atualizaComunicacao(pacoteOrigem, classeOrigem, pacoteDestino, classeDestino, null, peso, idVersaoModificar );
							} catch (NumberFormatException e) {
								// TODO: handle exception
							}
							
							break;
					}
					
				}
			}
			reader.close();

		} catch (IOException e) { }
	}
}