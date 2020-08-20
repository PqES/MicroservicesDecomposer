package br.ufla.felipecb.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import br.ufla.felipecb.bean.ClasseBean;
import br.ufla.felipecb.bean.ComunicacaoClasseBean;
import br.ufla.felipecb.bean.MicrosservicoBean;
import br.ufla.felipecb.bean.VersaoBean;
import br.ufla.felipecb.entidades.Classe;
import br.ufla.felipecb.entidades.ComunicacaoClasse;
import br.ufla.felipecb.entidades.Microsservico;
import br.ufla.felipecb.entidades.Versao;
import br.ufla.felipecb.util.Constante;
import br.ufla.felipecb.util.GraphViz;
import br.ufla.felipecb.util.Utilitario;
import br.ufla.felipecb.util.UtilitarioArquivo;
import br.ufla.felipecb.vo.FiltroImagemVO;

@Controller
@RequestMapping("/resultado")
public class ResultadoController {

	@Autowired
    MicrosservicoBean microsservicoBean;
    
	@Autowired
    ComunicacaoClasseBean ccBean;
	
	@Autowired
    ClasseBean classeBean;
	
	@Autowired
    VersaoBean versaoBean;
	
	@RequestMapping("/novo/{idVersao}/{idProjeto}")
    public String carregar(Model model, @PathVariable("idVersao") Long idVersao, 
    		@PathVariable("idProjeto") Long idProjeto) {

		Versao versao = versaoBean.buscarVersao(idVersao);
		
		FiltroImagemVO filtro = new FiltroImagemVO(idVersao, versao.getCodigoCompleto(), idProjeto);
		model.addAttribute("filtro", filtro);
		
		return "gerarImagens";
	}

    @RequestMapping(value = "/gerarImagem", 
    		produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public @ResponseBody HttpEntity<byte[]> gerarImagem(Model model, @ModelAttribute FiltroImagemVO filtro, HttpServletResponse response) {
    	
    	File file = comunicacaoMicrosservico(filtro);	
		
		model.addAttribute("mensagem","Gerado em: "+file.getAbsolutePath());
		
		try {
			HttpHeaders headers = new HttpHeaders();
		    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		    response.setHeader("Content-Disposition", "attachment; filename=" + file.getName());

		    return new HttpEntity<byte[]>(Files.readAllBytes(file.toPath()), headers);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
    }
    
    
    private File comunicacaoMicrosservico(FiltroImagemVO filtro) {
    	
    	
    	//se não é agrupado por microsserviços, a comunicação é somente entrer as classes originais do sistema
		List<ComunicacaoClasse> listaCC = ccBean.buscarComunicacoesPorVersao(filtro.getIdVersao(), 
				filtro.isAgrupadoPorMicrosservicos(), filtro.isExibeMicrosservicoAberto(),
				filtro.isExibeMicrosservicoFinalizado(), filtro.isExibeClassesMicrosservicoParticionadas());
		
		String valor;
		GraphViz gv = new GraphViz();
		gv.addln(gv.start_graph());
		gv.addln("\t ratio = fill;");
		gv.addln("\t node [style=filled shape=record fillcolor=\"0.0 0.00 0.93\"];");
		//diminui o espaçamento
		gv.addln("\t\t graph [pad=\"0.1\", nodesep=\"0.3\", ranksep=\"0.1\"]; \n");
		
		Set<String> entidades = new HashSet<String>();
		Set<String> utilitarias = new HashSet<String>();
		//Classes que não são util e entidade
		Set<String> classesAExibir = new HashSet<String>();
		
		Set<Long> classesMicUtil = microsservicoBean.buscarClassesMicroUtil(filtro.getIdVersao());
		
		for(ComunicacaoClasse cc : listaCC) {
			
			//Se exibe classes é entidade ou utilitária, só exibe se permitir classes especiais
			if( filtro.isExibeEntidadeUtil() && ( (cc.getClasseOrigem().isEntidade() || cc.getClasseOrigem().isUtilitaria()
					|| cc.getClasseDestino().isEntidade() || cc.getClasseDestino().isUtilitaria()) ) 
				|| ( ! cc.getClasseOrigem().isEntidade() && ! cc.getClasseOrigem().isUtilitaria() 
					&&  ! cc.getClasseDestino().isEntidade() && ! cc.getClasseDestino().isUtilitaria()) ) {
				
				if( filtro.isExibeLigacoesFracas() || (cc.isObrigatoria() || cc.isMaisForte()) ) {

					if(filtro.isAgrupadoPorMicrosservicos()) {
						
						if(filtro.isExibeMicrosservicoAberto() ||
							( ! cc.getClasseOrigem().getMicrosservico().isFinalizado() &&  
							  ! cc.getClasseDestino().getMicrosservico().isFinalizado() ) ) {
							valor = "\t "+cc.getComunicacaoId()+" [ label = \""+cc.getPeso()+"\" ] ";
						
						} else {
							
							String origem = null;
							String destino = null;
							
							if(cc.getClasseOrigem().getMicrosservico().isFinalizado()) {
								origem = "\""+cc.getClasseOrigem().getMicrosservico().getNome()+"\"";
							} else {
								origem = cc.getClasseOrigem().getIdNome();
							}
							
							if(cc.getClasseDestino().getMicrosservico().isFinalizado()) {
								destino = "\""+cc.getClasseDestino().getMicrosservico().getNome()+"\"";
							} else {
								destino = cc.getClasseDestino().getIdNome();
							}
							
							valor = "\t "+origem+" -> "+destino+" [ label = \""+cc.getPeso()+"\" ] ";
						}
						
					} else {
						valor = "\t "+cc.getComunicacaoId()+" [ label = \""+cc.getQuantidadeComunicacoes()+"\" ] ";
					}
					
					//se for agrupado por Microserviço desconsidera as ligações para classe Utilitária
					if( ! filtro.isAgrupadoPorMicrosservicos() ||
						(	!(classesMicUtil.contains(cc.getClasseOrigem().getId()) || 
							classesMicUtil.contains(cc.getClasseDestino().getId())) 
						  && filtro.isAgrupadoPorMicrosservicos() ) ) {
						
						valor += Utilitario.getCor(cc.isObrigatoria(), cc.isMaisForte());
						
						//verifica entidades
						if(cc.getClasseOrigem().isEntidade()) {
							entidades.add(cc.getClasseOrigem().getIdNome());
						}
						if(cc.getClasseDestino().isEntidade()) {
							entidades.add(cc.getClasseDestino().getIdNome());
						}
						
						//verifica classes utilitaria
						if(cc.getClasseOrigem().isUtilitaria()) {
							utilitarias.add(cc.getClasseOrigem().getIdNome());
						}
						if(cc.getClasseDestino().isUtilitaria()) {
							utilitarias.add(cc.getClasseDestino().getIdNome());
						}
						gv.addln(valor);
					
					} else {
//						if(cc.getClasseOrigem().isUtilitaria()){
//							valor = "\t "+cc.getClasseDestino().getIdNome()+" [ label = \""+cc.getClasseDestino().getId()+"."+cc.getClasseDestino().getNome()+" \n *(.util) \" ]\n ";
//						}
						//caso microsserviço esteja fecahdo, associa ao microsserviço
						if(cc.getClasseDestino().isUtilitaria()){
							if( ! filtro.isExibeMicrosservicoAberto()
									&& cc.getClasseOrigem().getMicrosservico().isFinalizado()) {
								valor = "\t \""+cc.getClasseOrigem().getMicrosservico().getNome()+"\" [ label = \""+
										cc.getClasseOrigem().getMicrosservico().getNome()+" *(.util) \" ]\n ";
								
							//caso esteja aberto associa a classe
							} else {
								valor = "\t "+cc.getClasseOrigem().getIdNome()+" [ label = \""+cc.getClasseOrigem().getId()+"."
										+cc.getClasseOrigem().getNome()+" *(.util) \" ]\n ";
							}
						}
						gv.addln(valor);
					}
				}
			}
		}
		
		//Adiciona as classes que devem ser exibidas
		classesAExibir.addAll(classeBean.buscarClassesFormatadasIdNome(filtro.getIdVersao(), filtro.isExibeEntidadeUtil()));
		
		if(filtro.isAgrupadoPorMicrosservicos()) {
			List<Microsservico> listaMicrosservicos = microsservicoBean.buscarMicrosservicos(filtro.getIdVersao());
			
			StringBuilder escrever;
			StringBuilder valores;
			for(Microsservico mic : listaMicrosservicos) {
				if((mic.isFinalizado() && filtro.isExibeMicrosservicoFinalizado() )
						|| ! mic.isFinalizado() ) {
					if( filtro.isExibeMicrosservicoAberto()  || ! mic.isFinalizado() ) {	
						
						//cria a estrutura de MS
						escrever = new StringBuilder("\t subgraph cluster_"+mic.getId()+" {\n");
						
						//pinta o quadrado do MS
						escrever.append("\t\t style=filled; fillcolor=\"0.0 0.00 0.99\"; \n");
						if( mic.isFinalizado() ) {
							//borda verde se estiver finalizado
							escrever.append("\t\t color=green3;\n");
						}
						
						escrever.append("\t\t label = \""+mic.getNome()+"\";\n");
						
						valores = new StringBuilder();
						for(Classe classe : mic.getClasses()) {
							if(classesAExibir.contains(classe.getIdNome())) {
								//colore as classes de acordo com sua especificacao
								if(classe.isEntidade() && filtro.isExibeEntidadeUtil()) {
									valores.append("\t\t "+classe.getIdNome()+" [fillcolor=beige];\n");
									
								} else if(classe.isUtilitaria() && filtro.isExibeEntidadeUtil()) {
									valores.append("\t\t "+classe.getIdNome()+" [fillcolor=darkorchid3 fontcolor=white];\n");
									
								} else if( ! classe.isEntidade() && ! classe.isUtilitaria()){
									valores.append("\t\t "+classe.getIdNome()+" [fillcolor=\"0.0 0.00 0.93\"];\n");
								}
							}
						}
						escrever.append(valores);
						escrever.append("\t };\n ");
					} else {
						//exibe os MS finalizados e fechados como circulo de borda verde
						escrever = new StringBuilder("\t\t \""+mic.getNome()+"\" [color=green3 shape=folder style=filled fillcolor=\"0.0 0.00 0.93\"] \n");
					}
					gv.addln(escrever.toString());
				}
			}
		} else {
			//adiciona
			for(String classe: classesAExibir) {
				valor = "\t "+classe+" [fillcolor=\"0.0 0.00 0.93\"];\n";
				gv.addln(valor);
			}
			
			//adiciona entidades 
			for(String entidade : entidades) {
				valor = "\t "+entidade+" [fillcolor=beige]\n";
				gv.addln(valor);
			}
			
			//adiciona classes utilitarias
			for(String util : utilitarias) {
				valor = "\t "+util+" [fillcolor=darkorchid3 fontcolor=white]\n";
				gv.addln(valor);
			}
		}
		
		String type = "png";
		String representationType="dot";
		File saida = UtilitarioArquivo.gerarArquivo(filtro.getIdProjeto(), filtro.getIdVersao(), Constante.IMAGEM_RESULTADO);
		gv.addln(gv.end_graph());
		gv.writeGraphToFile( gv.getGraph(gv.getDotSource(), type, representationType), saida );
		
		return saida;
    }
}