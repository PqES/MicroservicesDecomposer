package br.ufla.felipecb.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.ufla.felipecb.dao.ClasseRepository;
import br.ufla.felipecb.dao.ComunicacaoClasseRepository;
import br.ufla.felipecb.dao.MetodoRepository;
import br.ufla.felipecb.dao.MicrosservicoRepository;
import br.ufla.felipecb.entidades.Classe;
import br.ufla.felipecb.entidades.ComunicacaoMetodo;
import br.ufla.felipecb.entidades.Metodo;
import br.ufla.felipecb.entidades.Microsservico;

@Component
public class MicrosservicoBean {

	@Autowired
	MicrosservicoRepository microsservicoDao;
	
	@Autowired
	ClasseRepository classeDao;
	
	@Autowired
	MetodoRepository metodoDao;
	
	@Autowired
	ComunicacaoClasseRepository comunicacaoClasseRepository;
	
	@Autowired
	ComunicacaoMetodoBean cmBean;
	
	@Autowired
	CasoUsoBean ucBean;
	
	public void salvar(List<Long> idClasses, String nome) {
		Microsservico micro = new Microsservico();
		micro.setNome(nome);
		micro.setUtilitario(false);
		List<Classe> classes = (List<Classe>) classeDao.findAllById(idClasses);
		if(classes != null) {
			micro.setClasses(new ArrayList<Classe>());
			for(Classe classe : classes) {
				classe.setMicrosservico(micro);
			}
		}
		microsservicoDao.save(micro);
	}
	
	public Microsservico buscarMicrosservico(Long idMicrosservice){
		Optional<Microsservico> microsservico = microsservicoDao.findById(idMicrosservice);
		if(microsservico.isPresent()) {
			return microsservico.get();
		}
		return null;
	}
	
	public List<Microsservico> buscarMicrosservicos(Long idVersao){
		return microsservicoDao.buscarMicrosservicos(idVersao);
	}

	
	public void adicionarClassesUtilitarias(Long idVersao) {
		
		List<Classe> utilitarias = classeDao.buscarClassesUtilitarias(idVersao);
		
		Microsservico microsservico = null;
		if( null == utilitarias || utilitarias.isEmpty()) {
			return;
		}
		
		//Salva o microsserviço
		microsservico = new Microsservico();
		microsservico.setNome("Utilitário");
		microsservico.setUtilitario(true);
		
		microsservico.setClasses(new ArrayList<Classe>());
		
		for(Classe classe : utilitarias) {
			List<Microsservico> micros = microsservicoDao.buscarMicrosservicosClasseAssociada(classe.getId()); 
			
			if( ! micros.isEmpty() && micros.size() > 1) {
				classe.setMicrosservico(microsservico);
				microsservico.getClasses().add(classe);
			} else if( ! micros.isEmpty()) {
				classe.setMicrosservico(micros.get(0));
				micros.get(0).getClasses().add(classe);
				microsservicoDao.save(micros.get(0));
			}
		}
		
		if( ! microsservico.getClasses().isEmpty()) {
			//Salva o microsserviço
			microsservicoDao.save(microsservico);
		}
		
	}
	
	public void adicionarEntidades(Long idVersao) {
		
		List<Classe> entidades = classeDao.buscarEntidades(idVersao);
		
		List<Long> idClasses = new ArrayList<Long>();
		
		for(Classe classe : entidades) {
			List<Long> micros = microsservicoDao.buscaIdsMicrosservicos(classe.getId()); 
			
			if( ! micros.isEmpty()) {
				//se so pertencer a um microsserviço, usa no mesmo
				if(micros.size() == 1) {
					Microsservico ms = microsservicoDao.findById(micros.get(0)).get();
					classe.setMicrosservico(ms);
					classeDao.save(classe);
				} else {
					Classe entidadeReplica;
					//Uma entidade para cada microsserviço
					int index = 0;
					for(Long mic : micros) {
						index++;
						entidadeReplica = classeDao.findById(classe.getId()).get().retornarReplica();
						
						Microsservico ms = microsservicoDao.findById(mic).get();
						entidadeReplica.setMicrosservico(ms);
						entidadeReplica.setNome(entidadeReplica.getNome()+"*"+index);
						
						entidadeReplica = classeDao.save(entidadeReplica);
						
						//ajustar metodos da classe base
						atualizarMetodos(entidadeReplica, classe.getId(), index, mic);
					}
					
					//apaga as comunicacoes de metodos entre as classes originais
					cmBean.apagarComunicacoesMetodoOriginal(classe.getId());

					//apaga os metodos entre ds classes originais
					metodoDao.apagarMetodosOriginais(classe.getId());
					
					//apaga as comunicacoes entre a classe original
					comunicacaoClasseRepository.apagarComunicacoesClasseOriginal(classe.getId());
					
					idClasses.add(classe.getId());
				}
				
				//apaga as classes originais
				classeDao.deleteAllById(idClasses);
			}
		}
	}

	private void atualizarMetodos(Classe entidadeReplica, Long idClasseOriginal, int index, Long idMicrosservicoClasse) {
			
		//Duplicar metodos
		List<Long> metodos = metodoDao.buscarIdsPorClasse(idClasseOriginal);
		//Duplicar Com met
		List<ComunicacaoMetodo> comunicacoesMetodo;
		Metodo metodo;
		for(Long idMet : metodos) {
			
			//replica metodo com base no anterior
			metodo = metodoDao.findById(idMet).get().retornarReplica();
			metodo.setClasse(classeDao.findById(entidadeReplica.getId()).get());
			metodo.setNome(metodo.getNome()+"*"+index);
			metodo = metodoDao.save(metodo);
			
			//replica as comunicacacoes de dentro do MS informado
			comunicacoesMetodo = cmBean.buscarComunicacoesMetodo(idMet, idMicrosservicoClasse, 
					idClasseOriginal, entidadeReplica.getId());
			
			//ajuste de comunicacao das classes por metodos
			for(ComunicacaoMetodo cm : comunicacoesMetodo) {
				Long idOrigem = cm.getMetodoOrigem().getId();
				Long idDestino = cm.getMetodoDestino().getId();
				if(cm.getMetodoOrigem().getId().equals(idMet)) {
					idOrigem = metodo.getId();	
				}
				if(cm.getMetodoDestino().getId().equals(idMet)) {
					idDestino = metodo.getId();
				}
				
				List<Long> ucs = ucBean.buscarListaIdCasoUso(cm.getId());
				//uma entrada por caso de uso
				for(Long idUc : ucs) {
					cmBean.verificaOuSalva(idOrigem, idDestino, idUc);
				}
			}
		}
	}

	public void limparMicrosservicos(Long idVersao) {
		microsservicoDao.deleteAll(microsservicoDao.buscarMicrosservicos(idVersao));
	}

	public Set<Long> buscarClassesMicroUtil(Long idVersao) {
		return microsservicoDao.buscarClassesMicroUtil(idVersao);
	}

	public void editar(Microsservico microsservico) {
		microsservicoDao.save(microsservico);
	}

	public List<Long> verificarMicrosservicosMenores(Long idVersao, Long limiar) {
		return microsservicoDao.verificarMicrosservicosMenores(idVersao, limiar);
	}
}
