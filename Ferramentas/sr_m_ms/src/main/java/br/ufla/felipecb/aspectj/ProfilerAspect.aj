//package br.ufla.felipecb.aspectj;
//
//import java.io.BufferedReader;
//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileReader;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.lang.reflect.Method;
//import java.lang.reflect.Parameter;
//import java.util.ArrayList;
//import java.util.List;
//
//import org.aspectj.lang.reflect.MethodSignature;
//
//public aspect ProfilerAspect {
//
//	private static final String CAMINHO_ARQUIVOS = "/Users/fiocruz/Desktop/arquivosDecomposicao";
//
//	final String caminho;
//
//	private List<String> classes;
//	
//	public ProfilerAspect() {
//		caminho = getPath("execucaoDinamica.txt");
//		getClasses();
//	}
//
//	private void getClasses() {
//	
//		BufferedReader br = null;
//		if(classes == null) {
//			try {
//				classes = new ArrayList<String>();
//				try {
//					File file = new File(getPath("classes.txt")); 
//					br = new BufferedReader(new FileReader(file)); 
//					String st;
//				
//					while ((st = br.readLine()) != null) {
//						//remove se é entidade ou utilitaria
//						classes.add(st.split(":")[0]); 
//					}
//				} finally {
//					if(br != null) {
//						br.close();
//					}
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//	}
//	
//	private String getCasoDeUsoCorrente() {
//	
//		BufferedReader br = null;
//		try {
//			try {
//				File file = new File(getPath("casoDeUso.txt")); 
//				br = new BufferedReader(new FileReader(file)); 
//				String st;
//			
//				while ((st = br.readLine()) != null) {
//					return "#UC-"+st; 
//				}
//			} finally {
//				if(br != null) {
//					br.close();
//				}
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return "#UC-0";
//	}
//	
//	pointcut allMethods() :call(* *(..)) &&
//	(
//	    !within(br.ufla.felipecb.aspectj..*) 
//	);
//	
//	before() : allMethods() {
//		
//		String novaEntrada;
//		
//		if( ! (thisEnclosingJoinPointStaticPart.getSignature() instanceof MethodSignature)) {
//			return;
//		}
//		
//		MethodSignature methodSignature = (MethodSignature) thisEnclosingJoinPointStaticPart.getSignature();
//		MethodSignature methodSignature2 = (MethodSignature) thisJoinPoint.getSignature();
//
//		/* 
//		 * Representa o parametro recebido de uma classe, pois nao eh associada a um metodo
//		 * classe::metodo() -> classe::PARAMETRO
//		 */
//		Method met = methodSignature2.getMethod();
//		for(Parameter meto : met.getParameters()){
//			String nome = meto.getParameterizedType().getTypeName();
//			String[] lst = nome.split("\\.");
//
//			if(lst.length > 1 && classes.contains(nome.substring(0, nome.length() - (lst[lst.length-1].length() + 1) )) && 
//					classes.contains(methodSignature.getDeclaringType().getName())) {
//				novaEntrada = "\"" +
//						methodSignature.getDeclaringType().getName() + "::" +
//						methodSignature.getName() 
//						+ "\"" + " -> \""+nome+"::PARAMETRO\"";
//				
//				try(FileWriter fw = new FileWriter(caminho, true);
//				    BufferedWriter bw = new BufferedWriter(fw);
//				    PrintWriter out = new PrintWriter(bw))
//				{
//					
//					novaEntrada += getCasoDeUsoCorrente();
//					
//					out.println(novaEntrada);
//				} catch (IOException e) { }
//			}
//		}
//		
//		//Verifica se classes são válidas
////		if( !classes.contains(methodSignature.getDeclaringType().getName()) 
////				&& classes.contains(methodSignature2.getDeclaringType().getName())){
////			
////			novaEntrada = "\"INICIO::INICIO\"" + " -> " + "\"" +
////					methodSignature2.getDeclaringType().getName() + "::" +
////					methodSignature2.getName() + "\"";
////		
////		} else 
//		if(!classes.contains(methodSignature.getDeclaringType().getName()) 
//				|| !classes.contains(methodSignature2.getDeclaringType().getName())){
//			return;
//			
//		}  else {
//			novaEntrada =  "\"" +
//					methodSignature.getDeclaringType().getName() + "::" +
//					methodSignature.getName() 
//					+ "\"" + " -> " + "\"" +
//					methodSignature2.getDeclaringType().getName() + "::" +
//					methodSignature2.getName() + "\"";
//		}
//		
//		try(FileWriter fw = new FileWriter(caminho, true);
//		    BufferedWriter bw = new BufferedWriter(fw);
//		    PrintWriter out = new PrintWriter(bw))
//		{
//			novaEntrada += getCasoDeUsoCorrente();
//			out.println(novaEntrada);
//		} catch (IOException e) { }
//	}
//	
//	//Trabalhando com os dados de retorno
//    after() returning (Object o): allMethods() {
//    	String novaEntrada;
//    	
//		/* 
//		 * Representa o retorno de uma classe, pois nao eh associada a um metodo
//		 * classe::metodo() -> classe::RETORNO
//		 */
//    	if(! (thisEnclosingJoinPointStaticPart.getSignature() instanceof MethodSignature)) {
//			return;
//		}
//		MethodSignature methodSignature = (MethodSignature) thisEnclosingJoinPointStaticPart.getSignature();
//		String nome = methodSignature.getReturnType().getTypeName();
//		//trabalhando com List
//		if(o != null){
//			if(o instanceof List) {
//				List list = (List) o;
//    	    	if (list != null && ! list.isEmpty()) {
//    	    		  nome = list.get(0).getClass().getName();
//    	    	}
//			}
//		}
//		if(nome.contains("[]")){
//			nome = nome.replace("[]", "");
//		}
//        
//		String[] lst = nome.split("\\.");
//
//		if(lst.length > 1 && classes.contains(nome) && 
//				classes.contains(methodSignature.getDeclaringType().getName())) {
//			novaEntrada = "\"" +
//					methodSignature.getDeclaringType().getName() + "::" +
//					methodSignature.getName() 
//					+ "\"" + " -> \""+nome+"::RETORNO\"";
//			
//			try(FileWriter fw = new FileWriter(caminho, true);
//			    BufferedWriter bw = new BufferedWriter(fw);
//			    PrintWriter out = new PrintWriter(bw))
//			{
//				novaEntrada += getCasoDeUsoCorrente();
//				out.println(novaEntrada);
//			} catch (IOException e) { }
//		}
//    }
//    
//    public static String getPath(String arquivo){
//		File f1 = new File(CAMINHO_ARQUIVOS);
//		final String caminho = f1.getAbsolutePath() + "//" + arquivo;
//		return caminho;
//	}
//}