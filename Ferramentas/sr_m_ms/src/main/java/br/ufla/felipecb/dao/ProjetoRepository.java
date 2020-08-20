package br.ufla.felipecb.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import br.ufla.felipecb.entidades.Projeto;

@Component
public interface ProjetoRepository extends CrudRepository<Projeto, Long> {
	
}

