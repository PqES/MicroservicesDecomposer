//package br.ufla.felipecb.imagem;
//
//import java.io.File;
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import br.ufla.felipecb.bean.ClasseBean;
//import br.ufla.felipecb.bean.ComunicacaoMetodoBean;
//import br.ufla.felipecb.entidades.Classe;
//import br.ufla.felipecb.entidades.ComunicacaoMetodo;
//import br.ufla.felipecb.entidades.Metodo;
//import br.ufla.felipecb.entidades.Projeto;
//import br.ufla.felipecb.util.Constante;
//import br.ufla.felipecb.util.GraphViz;
//import br.ufla.felipecb.util.Utilitario;
//
//@Component
//public class GerarComunicacaoClassesMetodos {
//
//    @Autowired
//    ComunicacaoMetodoBean cmBean;
//    
//    @Autowired
//    ClasseBean classeBean;
//    
//    public void buscarProjeto(Projeto projeto) {
//    	
//    	List<ComunicacaoMetodo> listaCM = cmBean.buscarComunicacoesPorProjeto(projeto.getId());
//    	comunicacaoMetodosAgrupadosClasse(listaCM, projeto);
//    }
//    
//    public void comunicacaoMetodosAgrupadosClasse(List<ComunicacaoMetodo> listaCM, Projeto projeto) {
//		String valor;
//			
//		GraphViz gv = new GraphViz();
//		gv.addln(gv.start_graph());
//		 
//		gv.addln("\t ratio = fill;");
//		
//		gv.addln("\t node [style=filled shape=record fillcolor=white];");
//		
//		for(ComunicacaoMetodo cm : listaCM) {
//			
//			valor = "\t "+cm.getComunicacaoId()+" [ label = \""+cm.getQuantidadeChamadas()+"\" ] ";
//			valor += Utilitario.getCor();
//			gv.addln(valor);
//		}
//		
//		StringBuilder escrever;
//		StringBuilder valores;
//		
//		List<Classe> classes = classeBean.buscarClassesComMetodos(projeto.getId());
//		
//		for(Classe classe : classes) {
//			escrever = new StringBuilder("\t subgraph cluster_"+classe.getId()+" {\n");
//
//			escrever.append("\t\t style=filled; fillcolor=\"0.0 0.00 0.93\";\n");
//			
//			if(classe.isEntidade()) {
//				escrever.append("\t\t style=filled; fillcolor=beige;\n");
//			}
//			if(classe.isUtilitaria()) {
//				escrever.append("\t\t style=filled; fillcolor=green4; fontcolor=white;\n");
//			}
//
//			valores = new StringBuilder();
//			
//			for(Metodo metodo : classe.getMetodos()) {
//				valores.append("\t\t "+metodo.getIdClasseMetodo()+";\n");
//			}
//			
//			escrever.append(valores);
//				
//			valor = "\t\t label = "+classe.getIdNome()+";\n";
//			
//			escrever.append(valor);
//			escrever.append("\t };");
//			
//			gv.addln(escrever.toString());
//		}
//
//		String type = "png";
//		String representationType="dot";
//		File saida = new File(Utilitario.getPath(Constante.COMUNICACAO_CLASSES_METODOS_IMG, projeto.getCaminho(), projeto.getId()));
//		
//		gv.addln(gv.end_graph());
//		gv.writeGraphToFile( gv.getGraph(gv.getDotSource(), type, representationType), saida );
//			
//    }
//    
//}