package br.ufla.felipecb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import br.ufla.felipecb.bean.ClasseBean;
import br.ufla.felipecb.bean.ComunicacaoClasseBean;
import br.ufla.felipecb.bean.ProjetoBean;
import br.ufla.felipecb.bean.VersaoBean;
import br.ufla.felipecb.entidades.Projeto;
import br.ufla.felipecb.entidades.Versao;
import br.ufla.felipecb.processamento.ProcessamentoMicrosservicos;
import br.ufla.felipecb.processamento.ProcessamentoMicrosservicosMetodos;
import br.ufla.felipecb.processamento.ProcessarDadosIniciais;
import br.ufla.felipecb.util.Constante;
import br.ufla.felipecb.util.UtilitarioArquivo;

@Controller
@RequestMapping("/projeto")
public class ProjetoController {

    @Autowired
    ProjetoBean projetoBean;
    
    @Autowired
    ComunicacaoClasseBean ccBean;
    
    @Autowired
    VersaoBean versaoBean;
    
    @Autowired
    ClasseBean classeBean;
    
    @Autowired
    ProcessarDadosIniciais procDadosIniciais;
    
    @Autowired
    ProcessamentoMicrosservicos procMic;
    
    @Autowired
    ProcessamentoMicrosservicosMetodos procMicMet;
    
    @RequestMapping("/novo")
    public String index(Model model) {
    	
    	Projeto projeto = new Projeto();
    	model.addAttribute("projeto", projeto);

    	return "cadastroProjeto";
    }
    
    @PostMapping("/salvar")
    public String carregarArquivos(@ModelAttribute Projeto projeto, 
    		@RequestParam("classe") MultipartFile classe, 
    		@RequestParam("comunicacoes") MultipartFile comunicacoes, Model model) {
    	
    	try {
    		projeto = projetoBean.salvar(projeto);
    		
    		UtilitarioArquivo.salvar(classe, projeto.getId(), null, Constante.ARQUIVO_CLASSES, null);
    		UtilitarioArquivo.salvar(comunicacoes, projeto.getId(), null, Constante.ARQUIVO_COMUNICACOES, null);
    		
    		Versao versaoMonolitica = projeto.getVersoes().get(0);
    		//processa os arquivos e gera versão monolitica 1.0
			procDadosIniciais.processarArquivos(versaoMonolitica);
			
			//gera comunicação de classes
			ccBean.recriarComunicacoesClasses(versaoMonolitica.getProjeto().getId(), versaoMonolitica.getId(), null);
			ccBean.descobrirArestasMaisForte(versaoMonolitica.getId());
			
			//Gera as metricas para o monolitico
			classeBean.atualizarMetricas(versaoMonolitica.getId());
			
//			
//			//Cria uma nova versão 1.1 com dados 
//			Versao ver = versaoBean.criarNovaVersao(projeto.getVersoes().get(0).getId(), false);
//			
//			//gera a base novamente para uma nova versão
//			procDadosIniciais.processarArquivos(ver);
//			
//			//cria os microsservicos por métodos
//			procMicMet.montarGrupos(ver);
//
//			ccBean.recriarComunicacoesClasses(ver.getProjeto().getId(), ver.getId(), null);
//			
//			//cria os microsservicos
//			procMic.montarGrupos(ver, ver.getId());
			
			model.addAttribute("mensagem", "Projeto "+ projeto.getNome()+ " cadastrado");
			return "index";
			
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("erro", true);
			model.addAttribute("mensagem", e.getMessage());
			
			if(projeto.getId() != null) {
				projetoBean.excluirProjeto(projeto.getId());
			}
			
	    	return "cadastroProjeto";
		}
    }
    
    @PostMapping("/alterar")
    public String alterar(@ModelAttribute Projeto projeto, Model model) {
    	
    	projetoBean.alterar(projeto);
		model.addAttribute("mensagem", "Projeto "+ projeto.getNome()+ " atualizado");
		
		return "redirect:/projeto/listar";
    }
    
    @RequestMapping("/listar")
    public String listar(Model model) {
    	
    	if(model.getAttribute("mensagem") != null) {
    		model.addAttribute("erro", model.getAttribute("erro"));
			model.addAttribute("mensagem", model.getAttribute("mensagem"));
    	}
    	Iterable<Projeto> projetos = projetoBean.buscarProjetos();
    	
    	model.addAttribute("projetos", projetos);
    	
        return "projetos";
    }
    
    @RequestMapping("/buscar/{id}")
    public String buscarProjeto(@PathVariable("id") Long id, Model model) {
    	
    	if(id== null) {
    		model.addAttribute("erro", true);
			model.addAttribute("mensagem", "Ocorreu erro com o identificador do projeto");
    		return "index";
    	}
    	
    	Projeto projeto = projetoBean.buscarProjeto(id);
    	model.addAttribute("projeto", projeto);
    	
    	return "cadastroProjeto";
    }
    
    @RequestMapping("/excluir/{id}")
    public String excluir(@PathVariable("id") Long id, Model model) {
    	
    	if(id== null) {
    		model.addAttribute("erro", true);
			model.addAttribute("mensagem", "Ocorreu erro com o identificador do projeto");
    		return "index";
    	}
    	
    	projetoBean.excluirProjeto(id);
    	//apaga as referencias dos uploads de todos projetos
    	UtilitarioArquivo.excluir(id, null);
    	
    	model.addAttribute("erro", false);
		model.addAttribute("mensagem", "Projeto excluído");
    	
		return "index";
    }

}