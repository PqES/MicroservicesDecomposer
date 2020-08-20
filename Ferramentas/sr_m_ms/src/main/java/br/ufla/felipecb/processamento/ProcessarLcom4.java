package br.ufla.felipecb.processamento;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.ufla.felipecb.bean.ClasseBean;
import br.ufla.felipecb.bean.ComunicacaoMetodoBean;
import br.ufla.felipecb.bean.MetodoBean;
import br.ufla.felipecb.entidades.ComunicacaoMetodo;

@Component
public class ProcessarLcom4 {

	@Autowired
	ClasseBean classeBean;
	
	@Autowired
	MetodoBean metodoBean;

	@Autowired
	ComunicacaoMetodoBean comunicacaoMetodoBean;
	
	public void calcularLcom4(final Long idVersao) {

		Map<Integer, Set<Long>> grupos;
		
		List<Long> idClasses = classeBean.buscarIds(idVersao);
		
		for(Long idClasse : idClasses) {
			
			grupos = new HashMap<Integer, Set<Long>>();
			
			//Adiciona cada metodo e atributo em um grupo e vai agrupando
			List<Long> idsMetodos = metodoBean.buscarIdsSemLigacoesEspeciais(idClasse);
			int identificador = 0;
			for(Long idMetodo : idsMetodos) {
				identificador++;
				Set<Long> idMet = new HashSet<Long>();
				idMet.add(idMetodo);
				grupos.put(identificador, idMet);
			}
			
			List<ComunicacaoMetodo> comunicacaoMetodos = comunicacaoMetodoBean.buscarAssociacaoClasseSemEspeciais(idClasse);
			
			Set<Long> ids;
			List<Integer> apagar;
			
			for(ComunicacaoMetodo cm : comunicacaoMetodos) {
				apagar = new ArrayList<Integer>();
				ids = new HashSet<Long>();
				
				for(Integer key: grupos.keySet()) {
					if(grupos.get(key).contains(cm.getMetodoOrigem().getId()) || 
							grupos.get(key).contains(cm.getMetodoDestino().getId())) {
						ids.add(cm.getMetodoOrigem().getId());
						ids.add(cm.getMetodoDestino().getId());
						ids.addAll(grupos.get(key));
						apagar.add(key);
					}
				}

				identificador++;
				for(Integer key : apagar) {
					grupos.remove(key);
				}
				if(! ids.isEmpty()) {
					grupos.put(identificador, ids);
				}
			}	
			classeBean.atualizarLcom4(idClasse, grupos.size());
		}
	}
}