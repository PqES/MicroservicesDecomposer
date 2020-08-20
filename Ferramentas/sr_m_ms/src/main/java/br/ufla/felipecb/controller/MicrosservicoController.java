package br.ufla.felipecb.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.annotation.SessionScope;

import br.ufla.felipecb.bean.MicrosservicoBean;
import br.ufla.felipecb.entidades.Microsservico;

@Controller
@SessionScope
@RequestMapping("/microsservicos")
public class MicrosservicoController {

	private Long idVersao;
	
	@Autowired
    MicrosservicoBean microsservicoBean;
    
	//Versão é necessário para geraçao da estrutura de pasta
	@RequestMapping("/listar/{idVersao}")
    public String carregarMicrosservico(Model model, @PathVariable("idVersao") Long idVersao) {

		this.idVersao = idVersao;
		
		List<Microsservico> microsservicos = microsservicoBean.buscarMicrosservicos(idVersao);
		model.addAttribute("microsservicos", microsservicos);
		
		return "microsservicos";
	}
	
	@RequestMapping("/carregar/{idMicrosservico}")
    public String carregar(Model model, @PathVariable("idMicrosservico") Long idMicrosservico) {

		Microsservico microsservico = microsservicoBean.buscarMicrosservico(idMicrosservico);
		model.addAttribute("microsservico", microsservico);
		
		return "microsservico";
	}

    @PostMapping("/editar")
    public String editar(@ModelAttribute Microsservico microsservico, Model model) {
    	
    	microsservicoBean.editar(microsservico);
    	
    	return "redirect:/microsservicos/listar/"+idVersao;
    }

//
//    @RequestMapping(value = "/microsservico/abrir/{idVersao}/{idMicrosservico}")
//    public String abrirMicrosservico(Model model, @PathVariable("idVersao") Long idVersao,
//    		@PathVariable("idMicrosservico") Long idMicrosservico) {
//    	microsservicoBean.mudarStaus(idMicrosservico, false);
//    	return "redirect:/ajusteMicrosservico/"+idVersao;
//    }
//    
//    @RequestMapping(value = "/microsservico/fechar/{idVersao}/{idMicrosservico}")
//    public String fecharMicrosservico(Model model, @PathVariable("idVersao") Long idVersao,
//    		@PathVariable("idMicrosservico") Long idMicrosservico) {
//    	microsservicoBean.mudarStaus(idMicrosservico, true);
//    	return "redirect:/ajusteMicrosservico/"+idVersao;
//    }
}