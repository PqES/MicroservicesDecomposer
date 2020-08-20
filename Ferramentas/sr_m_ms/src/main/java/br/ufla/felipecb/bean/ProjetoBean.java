package br.ufla.felipecb.bean;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.ufla.felipecb.dao.ProjetoRepository;
import br.ufla.felipecb.entidades.Projeto;
import br.ufla.felipecb.entidades.Versao;

@Component
public class ProjetoBean {

	@Autowired
	ProjetoRepository projetoDao;
	
	public Projeto salvar(Projeto projeto) {
		if(projeto.getId() == null) {
			Versao versao = new Versao();
			versao.setCodigo(1l);
			versao.setCodigoCompleto("1.0");
			versao.setProjeto(projeto);
    		projeto.setVersoes(new ArrayList<Versao>());
    		projeto.getVersoes().add(versao);
		}
		return projetoDao.save(projeto);
	}
	
	public void alterar(Projeto projeto) {
		Projeto proj = projetoDao.findById(projeto.getId()).get();
		proj.setNome(projeto.getNome());
		projetoDao.save(proj);
	}
	
	public Iterable<Projeto> buscarProjetos() {
		return projetoDao.findAll();
	}

	public Projeto buscarProjeto(Long id) {
		Optional<Projeto> projeto = projetoDao.findById(id);
		if(projeto.isPresent()) {
			return projeto.get();
		}
		return null;
	}

	public void excluirProjeto(Long id) {
		projetoDao.deleteById(id);
	}

}
