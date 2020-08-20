package br.ufla.felipecb.controller;

import java.io.FileNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import br.ufla.felipecb.bean.ComunicacaoClasseBean;
import br.ufla.felipecb.bean.VersaoBean;
import br.ufla.felipecb.entidades.Versao;
import br.ufla.felipecb.processamento.ProcessamentoMicrosservicos;
import br.ufla.felipecb.processamento.ProcessamentoMicrosservicosMetodos;
import br.ufla.felipecb.processamento.ProcessarDadosAjustes;
import br.ufla.felipecb.processamento.ProcessarDadosIniciais;
import br.ufla.felipecb.util.Constante;
import br.ufla.felipecb.util.UtilitarioArquivo;
import br.ufla.felipecb.vo.FiltroAjustesVO;

@Controller
@RequestMapping("/versaoProjeto")
public class VersaoProjetoController {

    @Autowired
    VersaoBean versaoBean;

    @Autowired
    ComunicacaoClasseBean ccBean;
    
    @Autowired
    ProcessarDadosAjustes pca;

    @Autowired
    ProcessarDadosIniciais procDadosIniciais;

    @Autowired
    ProcessamentoMicrosservicos procMic;
    
    @Autowired
    ProcessamentoMicrosservicosMetodos procMicMet;

    @RequestMapping("/nova/{idVersao}")
    public ModelAndView carregar(@PathVariable("idVersao") Long idVersao) {
    	
    	Versao versao = versaoBean.buscarVersao(idVersao);
    	
    	ModelAndView mv = new ModelAndView();
    	mv.addObject("versao", versao);
    	mv.addObject("ajusteClasses", "ajusteClasses.txt");
    	mv.addObject("ajusteComunicacoes", "ajusteComunicacoes.txt");
    	
    	FiltroAjustesVO faVO = new FiltroAjustesVO();
    	faVO.setIdVersaoBase(versao.getId());
    	faVO.setLimiar(Constante.LIMIAR_DEFAULT);
    	mv.addObject("filtro", faVO);
    	
    	mv.setViewName("ajustarClasses");
    	
        return mv;
    }
    
    @PostMapping("/criar")
    public String criarVersao(@ModelAttribute FiltroAjustesVO filtro, 
    		@RequestParam("ajustesClasses") MultipartFile ajustesClasses, 
    		@RequestParam("ajustesComunicacoes") MultipartFile ajustesComunicacoes, Model model) {
    	
    	try {
    		//Cria uma nova versão 1.X... com dados 
			Versao ver = versaoBean.criarNovaVersao(filtro.getIdVersaoBase(), 
					filtro.isRealizaSplitClass(), Long.parseLong(filtro.getLimiar().toString()));
			
			//Cria arquivos de upload
			UtilitarioArquivo.salvar(ajustesClasses, ver.getProjeto().getId(), ver.getId(), Constante.ARQUIVO_AJUSTES_CLASSES, filtro.getIdVersaoBase());
			UtilitarioArquivo.salvar(ajustesComunicacoes, ver.getProjeto().getId(), ver.getId(), Constante.ARQUIVO_AJUSTES_COMUNICACOES, filtro.getIdVersaoBase());
			
			processarVersao(ver, ver.getId());
			
	    	model.addAttribute("mensagem", "Ajustes realizados");
	    	
	    } catch (FileNotFoundException e) {
	    	model.addAttribute("erro", e.getMessage());
	    	model.addAttribute("mensagem", e.getMessage());
		}
    	
        return "index";
    }
    
    /**
     * Recria as versões passa a passo
     * @param ver
     */
    public void processarVersao(Versao versao, Long idVersaoAnalisada) throws FileNotFoundException {
    	
    	//de uma vez
    	//gera a base novamente para uma nova versão
		procDadosIniciais.processarArquivos(versao);
		
		pca.ajustarClasses(versao.getProjeto().getId(), idVersaoAnalisada, versao.getId());
		
		//cria os microsservicospor métodos
		procMicMet.montarGrupos(versao);

		ccBean.recriarComunicacoesClasses(versao.getProjeto().getId(), versao.getId(), idVersaoAnalisada);
		
		//cria os microsservicos
		procMic.montarGrupos(versao, idVersaoAnalisada);

    }
    
    @RequestMapping("/excluir/{idProjeto}/{idVersao}")
    public String excluirVersao(@PathVariable("idProjeto") Long idProjeto,
    			@PathVariable("idVersao") Long idVersao, Model model) {
    	
    	if(idProjeto==null || idVersao== null) {
    		model.addAttribute("erro", true);
			model.addAttribute("mensagem", "Ocorreu erro com o identificador do projeto");
    		return "index";
    	}

    	versaoBean.excluirVersao(idVersao);
    	//apaga as referencias dos uploads da versão
    	UtilitarioArquivo.excluir(idProjeto, idVersao);
    	
    	model.addAttribute("erro", false);
		model.addAttribute("mensagem", "Versão excluída");
    	
        return "redirect:/projeto/listar";
    }

}