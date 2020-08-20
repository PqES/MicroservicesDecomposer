package br.ufla.felipecb.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import br.ufla.felipecb.bean.ClasseBean;
import br.ufla.felipecb.bean.MicrosservicoBean;
import br.ufla.felipecb.bean.VersaoBean;
import br.ufla.felipecb.entidades.Classe;
import br.ufla.felipecb.entidades.Microsservico;
import br.ufla.felipecb.entidades.Versao;
import br.ufla.felipecb.util.Utilitario;
import br.ufla.felipecb.vo.MetricasVO;

@Controller
@RequestMapping("/metricas")
public class MetricasController {

	@Autowired
    ClasseBean classeBean;
	
	@Autowired
    VersaoBean versaoBean;
	
	@Autowired
    MicrosservicoBean microsservicoBean;
	
	@RequestMapping("/classe/{idVersao}")
    public String carregarPorClasse(Model model, @PathVariable("idVersao") Long idVersao) {

		Versao versao = versaoBean.buscarVersao(idVersao);
		model.addAttribute("versao", versao);
		
		Versao versaoBase = versao;
		List<Versao> versoes = new ArrayList<Versao>();
		versoes.add(versaoBase);
	
		while(versaoBase.getVersaoBase() != null) {
			versaoBase = versaoBean.buscarVersao(versaoBase.getVersaoBase().getId());
			versoes.add(versaoBase);
		}
		model.addAttribute("versoes", versoes);
		
		List<Classe> classes = classeBean.buscarClasses(idVersao);
		
		Integer cbo = 0;
		Integer rfc = 0;
		Integer lcom4 = 0;
		Double lcom4SemEntidade = 0d;
		Integer classesSemEntidade = 0;
		
		for(Classe classe : classes) {
			cbo += classe.getCbo();
			rfc += classe.getRfc();
			lcom4 += classe.getLcom4();
			if( ! classe.isEntidade() && ! classe.isUtilitaria()) {
				lcom4SemEntidade += classe.getLcom4();
				classesSemEntidade += 1;
			}
		}
		try {
			model.addAttribute("classes", classes);
			
			model.addAttribute("cbo", cbo);
			model.addAttribute("rfc", rfc);
			model.addAttribute("lcom4", lcom4);
			model.addAttribute("numeroClasses", classes.size());
			
			model.addAttribute("mediaCbo", Utilitario.arredondar(versao.getCbo()));
			model.addAttribute("mediaRfc", Utilitario.arredondar(versao.getRfc()));
			model.addAttribute("mediaLcom4", Utilitario.arredondar(versao.getLcom4()));

			model.addAttribute("lcom4SemEntidade", lcom4SemEntidade);
			model.addAttribute("numeroClassesSemEntidade", classesSemEntidade);
			model.addAttribute("mediaLcom4SemEntidade", Utilitario.arredondar(lcom4SemEntidade/classesSemEntidade));
			
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
		
		return "metrica";
	}
	
//	@RequestMapping("/microsservico/{idVersao}")
//    public String carregarPorMicrossservico(Model model, @PathVariable("idVersao") Long idVersao) {
//
//		List<Microsservico> microsservicos = microsservicoBean.buscarMicrosservicos(idVersao);
//		model.addAttribute("microsservicos", microsservicos);
//		
//		List<Classe> classes = classeBean.buscarClassesComMicrosserrvico(idVersao);
//		
//		Integer cboMonolitico = 0;
//		Integer rfcMonolitico = 0;
//		Set<Long> idsInclusos = new HashSet<Long>();
//		
//		for(Classe classe : classes){
//			//cbo e rfc
//			if(classe.getReplica() != null && ! idsInclusos.contains(classe.getReplica().getId())) {
//				cboMonolitico += classe.getReplica().getCboMonolitico();
//				rfcMonolitico += classe.getReplica().getRfcMonolitico();
//				idsInclusos.add(classe.getReplica().getId());
//				
//			} else if(classe.getReplica() == null) {
//				cboMonolitico += classe.getCboMonolitico();
//				rfcMonolitico += classe.getRfcMonolitico();
//				idsInclusos.add(classe.getId());
//			}
//		}
//		
//		//Geral 
//		model.addAttribute("cboMonoliticoGeral", 
//			Utilitario.arredondar(Double.parseDouble(cboMonolitico.toString())/idsInclusos.size()));
//	    model.addAttribute("rfcMonoliticoGeral",
//    		Utilitario.arredondar(Double.parseDouble(rfcMonolitico.toString())/idsInclusos.size()));
//	    model.addAttribute("lcomMonoliticoGeral", 
//    		Utilitario.arredondar(Double.parseDouble(rfcMonolitico.toString())/idsInclusos.size()));
//	    
//	    
//		Versao versao = versaoBean.buscarVersao(idVersao);
//		model.addAttribute("codigo", versao.getCodigoCompleto());
//		
//		MetricasVO metricaVersao = preencherMetrica(idVersao);
//		model.addAttribute("cboMicrosservicoGeral", metricaVersao.getCbo());
//		model.addAttribute("rfcMicrosservicoGeral", metricaVersao.getRfc());
//		model.addAttribute("lcom4MicrosservicoGeral", metricaVersao.getLcom4());
//		
//		if(versao.getVersaoBase() != null) {
//			MetricasVO metricaVersaoBase = preencherMetrica(versao.getVersaoBase().getId());
//			model.addAttribute("codigoAnterior", versao.getVersaoBase().getCodigoCompleto());
//			model.addAttribute("cboMicrosservicoAnterior", metricaVersaoBase.getCbo());
//			model.addAttribute("rfcMicrosservicoAnterior", metricaVersaoBase.getRfc());
//			model.addAttribute("lcom4MicrosservicoAnterior", metricaVersaoBase.getLcom4());
//		}
//		
//		
//	    
////	    Double cboMicrosservico = 0d;
////	    Double rfcMicrosservico = 0d;
//////	    Double cboMonoliticoEquiv = 0d;
//////	    Double rfcMonoliticoEquiv = 0d;
////	    
////	    for(Microsservico mic : microsservicos) {
////	    	cboMicrosservico += mic.cbo();
////	    	rfcMicrosservico += mic.rfc();
//////
//////	    	//equivalente
//////	    	cboMonoliticoEquiv += mic.cboMonolitico();
//////	    	rfcMonoliticoEquiv += mic.rfcMonolitico();
////	    }
////	    //Geral 
////	    BigDecimal bd = BigDecimal.valueOf(cboMicrosservico/microsservicos.size());
////  	    bd = bd.setScale(2, RoundingMode.HALF_UP);
////  		model.addAttribute("cboMicrosservicoGeral", bd.doubleValue());
////  		
////  		bd = BigDecimal.valueOf(rfcMicrosservico/microsservicos.size());
////  	    bd = bd.setScale(2, RoundingMode.HALF_UP);
////  	    model.addAttribute("rfcMicrosservicoGeral", bd.doubleValue() );
//////  	    
//////  	    //Equivalente
//////  	    bd = BigDecimal.valueOf(cboMonoliticoEquiv/microsservicos.size());
//////	    bd = bd.setScale(2, RoundingMode.HALF_UP);
//////		model.addAttribute("cboMonoliticoEquiv", bd.doubleValue());
//////		
//////		bd = BigDecimal.valueOf(rfcMonoliticoEquiv/microsservicos.size());
//////	    bd = bd.setScale(2, RoundingMode.HALF_UP);
//////	    model.addAttribute("rfcMonoliticoEquiv", bd.doubleValue() );
//		
//		return "metricaMS";
//	}
//	
	
	public MetricasVO preencherMetrica(Long idVersao) {
		
		List<Microsservico> microsservicos = microsservicoBean.buscarMicrosservicos(idVersao);
		
		Double cbo = 0d;
	    Double rfc = 0d;
		for(Microsservico mic : microsservicos) {
	    	cbo += mic.cbo();
	    	rfc += mic.rfc();
	    }
		
		MetricasVO metrica = new MetricasVO();
		metrica.setCbo(Utilitario.arredondar(cbo/microsservicos.size()));
		metrica.setRfc(Utilitario.arredondar(rfc/microsservicos.size()));
		
		return metrica;
	}
}