package br.ufla.felipecb.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.ufla.felipecb.dao.ComunicacaoClasseRepository;
import br.ufla.felipecb.dao.MetodoRepository;
import br.ufla.felipecb.dao.MicrosservicoMetodoRepository;
import br.ufla.felipecb.entidades.Metodo;
import br.ufla.felipecb.entidades.MicrosservicoMetodo;

@Component
public class MicrosservicoMetodoBean {

	@Autowired
	MicrosservicoMetodoRepository microsservicoMetDao;
	
	@Autowired
	MetodoRepository metodoDao;
	
	@Autowired
	ComunicacaoClasseRepository comunicacaoClasseRepository;
	
	public void salvar(List<Long> idMetodos, String nome) {
		MicrosservicoMetodo micro = new MicrosservicoMetodo();
		micro.setNome(nome);
		micro.setUtilitario(false);
		List<Metodo> metodos = (List<Metodo>) metodoDao.findAllById(idMetodos);
		if(metodos != null) {
			microsservicoMetDao.save(micro);
			micro.setMetodos(new ArrayList<Metodo>());
			for(Metodo metodo : metodos) {
				metodo.setMicrosservico(micro);
			}
		}
		microsservicoMetDao.save(micro);
	}
	
	public MicrosservicoMetodo buscarMicrosservico(Long idMicrosservico){
		Optional<MicrosservicoMetodo> microsservico = microsservicoMetDao.findById(idMicrosservico);
		if(microsservico.isPresent()) {
			return microsservico.get();
		}
		return null;
	}
	
	public List<MicrosservicoMetodo> buscarMicrosservicos(Long idVersao){
		return microsservicoMetDao.buscarMicrosservicos(idVersao);
	}

	public void editar(MicrosservicoMetodo microsservico) {
		microsservicoMetDao.save(microsservico);
	}
	
	public Map<Long, List<Long>> buscarMicrosservicoMetodos(Long idClasse) {
		
		Map<Long, List<Long>> map = new HashMap<Long, List<Long>>();
		
		List<Long> idsMic =  microsservicoMetDao.buscarPorClasse(idClasse);
		List<Long> idMetodos = null;
		
		for(Long idMic : idsMic) {
			idMetodos = microsservicoMetDao.buscarMetodos(idMic, idClasse);
			map.put(idMic, idMetodos);
		}
		
		return map;
	}
	
	public List<Long> buscarIdsMicrosservicosPorClasse(Long idClasse) {
		return  microsservicoMetDao.buscarPorClasse(idClasse);
	}
	
	public List<Long> buscarIdsClassessPorMicrosservico(Long idMicrosservico) {
		return  microsservicoMetDao.buscarClasses(idMicrosservico);
	}

	public Map<Long, List<Long>> buscarMicrosservicoClasses(Long idClasse) {
		
		Map<Long, List<Long>> map = new HashMap<Long, List<Long>>();
		
		List<Long> idsMic =  microsservicoMetDao.buscarPorClasse(idClasse);
		List<Long> idClasses = null;
		
		for(Long idMic : idsMic) {
			idClasses = microsservicoMetDao.buscarClasses(idMic);
			map.put(idMic, idClasses);
		}
		
		return map;
	}
	
}
