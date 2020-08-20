//package br.ufla.felipecb.imagem;
//
//import java.io.File;
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import br.ufla.felipecb.bean.ComunicacaoMetodoBean;
//import br.ufla.felipecb.entidades.ComunicacaoMetodo;
//import br.ufla.felipecb.entidades.Projeto;
//import br.ufla.felipecb.util.Constante;
//import br.ufla.felipecb.util.GraphViz;
//import br.ufla.felipecb.util.Utilitario;
//
//@Component
//public class GerarComunicacaoMetodos {
//
//    @Autowired
//    ComunicacaoMetodoBean cmBean;
//
//    public void buscarProjeto(Projeto projeto) {
//    	
//    	List<ComunicacaoMetodo> listaCM = cmBean.buscarComunicacoesPorProjeto(projeto.getId());
//    	comunicacaoMetodo(listaCM, projeto);
//    }
//    
//    public void comunicacaoMetodo(List<ComunicacaoMetodo> listaCM, Projeto projeto) {
//		String valor;
//		
//		GraphViz gv = new GraphViz();
//		gv.addln(gv.start_graph());
//		 
//		gv.addln("\t ratio = fill;");
//		
//		gv.addln("\t node [style=filled shape=record fillcolor=\"0.0 0.00 0.93\"];");
//		
//		for(ComunicacaoMetodo cm : listaCM) {
//			
//			valor = "\t "+cm.getComunicacaoId()+" [ label = \""+cm.getQuantidadeChamadas()+"\" ] ";
//			valor += Utilitario.getCor();
//			gv.addln(valor);
//		}
//
//		String type = "png";
//		String representationType="dot";
//		File saida = new File(Utilitario.getPath(Constante.COMUNICACAO_METODOS_IMG, projeto.getCaminho(), projeto.getId()));
//		
//		gv.addln(gv.end_graph());
//		gv.writeGraphToFile( gv.getGraph(gv.getDotSource(), type, representationType), saida );
//			
//    }
//    
//}