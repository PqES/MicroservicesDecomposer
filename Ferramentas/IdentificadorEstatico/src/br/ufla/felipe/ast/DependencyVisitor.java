package br.ufla.felipe.ast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

public class DependencyVisitor extends ASTVisitor {

	//Verificadores para entidade
	private CompilationUnit fullClass;
	private boolean atributosPrivados = true;
	private boolean soGetSetEqualsHashCode = true;
	private boolean temAoMenosUmAtributo = false;
	private boolean temAnotacaoEntity = false;

	//verificares para classe utilitaria
	private boolean classeDeclaradaFinal = false;
	private boolean construtoresPrivado = true;
	private boolean temConstrutorDeclarado = false;
	private boolean metodosAtributosPublicosEstaticos = true;
	
	private List<String> atributos = new ArrayList<String>();
	private List<IVariableBinding> listaAtributos = new ArrayList<IVariableBinding>();
	
	private List<String> metodos = new ArrayList<String>();
	//Metyodo, lista de atributos que utiliza
	private Map<String, Set<String>> metodoAtributos = new HashMap<String, Set<String>>();
	
	public DependencyVisitor(ICompilationUnit unit) throws JavaModelException {
		ASTParser parser = ASTParser.newParser(AST.JLS12);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(unit);
		parser.setCompilerOptions(JavaCore.getOptions());
		parser.setProject(unit.getJavaProject());
		parser.setResolveBindings(true);
		parser.setBindingsRecovery(true);

		this.fullClass = (CompilationUnit) parser.createAST(null);
		this.fullClass.accept(this);
		
		classeDeclaradaFinal = Modifier.isFinal(fullClass.getClass().getModifiers());
	}
	
	@Override
	public boolean visit(MarkerAnnotation node) {
		
		ITypeBinding typeBinding = node.resolveTypeBinding();
		
		if(typeBinding != null && typeBinding.getQualifiedName().equals("javax.persistence.Entity")){
			temAnotacaoEntity = true;
		}
		
		return super.visit(node);
	}
	
	//pega todos metodos
	@Override
	public boolean visit(MethodDeclaration node) {
		
		IMethodBinding binding = node.resolveBinding();
		if( ! metodos.contains(binding.getName())) {
			metodos.add(binding.getName());
			System.out.println("Metodo: "+binding.getName());
		}
		
		verificadorMetodoEntidade(node);
		
		verificadorMetodoUtilitaria(node);
			
		return super.visit(node);
	}
	
	//obj.metodo
	@Override
	public boolean visit(MethodInvocation node) {
		ASTNode relevantParent = getRelevantParent(node);

		switch (relevantParent.getNodeType()) {
			case ASTNode.METHOD_DECLARATION:
				MethodDeclaration md = (MethodDeclaration) relevantParent;
				if (node.getExpression() != null) {
					if(atributos.contains(node.getExpression().toString())) {
						System.out.println("METAtr "+md.getName()+" : "+node.getExpression());
						
		            	//adiciona na listagem do metodo
		    			Set<String> listaAtributos = metodoAtributos.get(md.getName().toString());
		    			if(listaAtributos == null) {
		    				listaAtributos = new HashSet<String>();
		    			}
		    			listaAtributos.add(node.getExpression().toString());
		    			
		    			metodoAtributos.put(md.getName().toString(), listaAtributos);
		    			
					}
				}
				break;
		}
		return true;
	}
	
//	private void findAllDeclarations(IProgressMonitor monitor, WorkingCopyOwner owner) throws CoreException {
//		List<IMethod> fDeclarations = new ArrayList<>();
//
//		class MethodRequestor extends SearchRequestor {
//			@Override
//			public void acceptSearchMatch(SearchMatch match) throws CoreException {
//				IMethod method = (IMethod) match.getElement();
//				boolean isBinary = method.isBinary();
//				if (!isBinary) {
//					fDeclarations.add(method);
//				}
//			}
//		}
//
//		int limitTo = IJavaSearchConstants.DECLARATIONS | IJavaSearchConstants.IGNORE_DECLARING_TYPE 
//				| IJavaSearchConstants.IGNORE_RETURN_TYPE;
//		int matchRule = SearchPattern.R_ERASURE_MATCH | SearchPattern.R_CASE_SENSITIVE;
//		
//		SearchPattern pattern = SearchPattern.createPattern(fMethod, limitTo, matchRule);
//		MethodRequestor requestor = new MethodRequestor();
//		SearchEngine searchEngine = owner != null ? new SearchEngine(owner) : new SearchEngine();
//
//		searchEngine.search(pattern, new SearchParticipant[] { 
//				SearchEngine.getDefaultSearchParticipant() }, createSearchScope(), requestor, monitor);
//	}
//	
//	protected SearchPattern createOccurrenceSearchPattern(IJavaElement[] elements) throws CoreException {
//		if (elements == null || elements.length == 0) {
//			return null;
//		}
//		Set<IJavaElement> set = new HashSet<>(Arrays.asList(elements));
//		Iterator<IJavaElement> iter = set.iterator();
//		IJavaElement first = iter.next();
//		SearchPattern pattern = SearchPattern.createPattern(first, IJavaSearchConstants.ALL_OCCURRENCES);
//		if (pattern == null) {
//			throw new CoreException(Status.CANCEL_STATUS);
//		}
//		while (iter.hasNext()) {
//			IJavaElement each = iter.next();
//			SearchPattern nextPattern = SearchPattern.createPattern(each, IJavaSearchConstants.ALL_OCCURRENCES);
//			if (nextPattern == null) {
//				return null;
//			}
//			pattern = SearchPattern.createOrPattern(pattern, nextPattern);
//		}
//		return pattern;
//	}
//	
//	public static MethodDeclaration convertToAstNode(final IMethod method) throws JavaModelException {
//	    final ICompilationUnit compilationUnit = method.getCompilationUnit();
//
//	    final ASTParser astParser = ASTParser.newParser( AST.JLS4 );
//	    astParser.setSource( compilationUnit );
//	    astParser.setKind( ASTParser.K_COMPILATION_UNIT );
//	    astParser.setResolveBindings( true );
//	    astParser.setBindingsRecovery( true );
//
//	    final ASTNode rootNode = astParser.createAST( null );
//
//	    final CompilationUnit compilationUnitNode = (CompilationUnit) rootNode;
//
//	    final String key = method.getKey();
//
//	    final ASTNode javaElement = compilationUnitNode.findDeclaringNode( key );
//
//	    final MethodDeclaration methodDeclarationNode = (MethodDeclaration) javaElement;
//

	//this.atributo
	@Override
    public boolean visit(FieldAccess node) {
        IVariableBinding variable = node.resolveFieldBinding();

        if (variable.isField() && atributos.contains(node.getName().getIdentifier())) {
	       
	        if(node.getParent().getParent().getParent().getParent() instanceof MethodDeclaration) {
	        	MethodDeclaration metDecl = (MethodDeclaration) node.getParent().getParent().getParent().getParent();
	             
	        	System.out.println("MetAtr2: "+metDecl.getName()+": "+node.getName().getIdentifier());
	
	        	//adiciona na listagem do metodo
				Set<String> listaAtributos = metodoAtributos.get(metDecl.getName().toString());
				if(listaAtributos == null) {
					listaAtributos = new HashSet<String>();
				}
				listaAtributos.add(node.getName().getIdentifier());
				
				metodoAtributos.put(metDecl.getName().toString(), listaAtributos);
	        }
        }
        return super.visit(node);
    }
	
	//pega todos atributos
	@Override
	public boolean visit(FieldDeclaration node) {
		
		String attributeName = "";
        Object object = node.fragments().get(0);
        if (object instanceof VariableDeclarationFragment) {
            attributeName = ((VariableDeclarationFragment) object).getName().toString();
            if( ! atributos.contains(attributeName)) {
    			atributos.add(attributeName);
    			System.out.println("Atributo: "+attributeName);
    		}
        }
		verificadorAtributoEntidade(node);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(ReturnStatement node) {
		ASTNode relevantParent = getRelevantParent(node);

		switch (relevantParent.getNodeType()) {
			case ASTNode.METHOD_DECLARATION:
				MethodDeclaration md = (MethodDeclaration) relevantParent;
				if (node.getExpression() != null) {
					if(atributos.contains(node.getExpression().toString())) {
						System.out.println("Return "+md.getName()+" : "+node.getExpression());
						
		            	//adiciona na listagem do metodo
		    			Set<String> listaAtributos = metodoAtributos.get(md.getName().toString());
		    			if(listaAtributos == null) {
		    				listaAtributos = new HashSet<String>();
		    			}
		    			listaAtributos.add(node.getExpression().toString());
		    			
		    			metodoAtributos.put(md.getName().toString(), listaAtributos);
		    			
					}
				}
				break;
		}
		return super.visit(node);
	}
	
	private void verificadorAtributoEntidade(FieldDeclaration node) {
		// tem um agtributo que não seja serialVersionUID
		
		int modifiers = node.getModifiers();
		if( ! (Modifier.isPrivate(modifiers) && Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers) )
				&& node.getType().isSimpleType() ) {
//		if(node.getModifiers() != (Modifier.PRIVATE+Modifier.STATIC+Modifier.FINAL) 
//				&& node.getType().getNodeType() == SimpleType.SIMPLE_TYPE) {
			temAoMenosUmAtributo = true;
			
			if(node.getModifiers() != Modifier.PRIVATE) {
				atributosPrivados = false;
			}
		}
		
		Object o = node.fragments().get(0);
		if(o instanceof VariableDeclarationFragment){
			String nome = ((VariableDeclarationFragment) o).getName().toString();
			if( ! atributos.contains(nome)) {
				atributos.add(nome);
			}
		}
		
	}
	
	private void verificadorMetodoEntidade(MethodDeclaration node) {

		if(!temAnotacaoEntity && !node.isConstructor()) {
			if(Modifier.isPublic(node.getModifiers())) {
				//se o nome do metodo comeca com get, verifica se a sintaxe esta correta
				if(node.getName().getFullyQualifiedName().startsWith("get")){
					if(!node.parameters().isEmpty()) {
						soGetSetEqualsHashCode = false;
					
					} else if (node.getReturnType2().isPrimitiveType()) {
				        if (((PrimitiveType)node.getReturnType2()).getPrimitiveTypeCode() == PrimitiveType.VOID) {
				        	soGetSetEqualsHashCode = false;
				        }
				    }
//					if(! node.getReturnType2().toString().equals("void")) {
//						soGetSetEqualsHashCode = false;
//					}
				//se o nome do metodo comeca com is, verifica se a sintaxe esta correta
				} else if(node.getName().getFullyQualifiedName().startsWith("is")){
					if(!node.parameters().isEmpty()) {
						soGetSetEqualsHashCode = false;
					
					} else if (node.getReturnType2().isPrimitiveType()) {
				        if (((PrimitiveType)node.getReturnType2()).getPrimitiveTypeCode() != PrimitiveType.BOOLEAN) {
				        	soGetSetEqualsHashCode = false;
				        }
				    }
//					if( !node.getReturnType2().toString().equals("boolean")) {
//						soGetSetEqualsHashCode = false;
//					}
						
				//se o nome do metodo comeca com set verifica se a sintaxe esta correta
				} else if(node.getName().getFullyQualifiedName().startsWith("set")){
					if(node.parameters() == null || node.parameters().size() != 1){
						soGetSetEqualsHashCode = false;
						
					} else if (node.getReturnType2().isPrimitiveType()) {
				        if (((PrimitiveType)node.getReturnType2()).getPrimitiveTypeCode() != PrimitiveType.VOID) {
				        	soGetSetEqualsHashCode = false;
				        }
				    }
						
				//Regras do equals
				} else if(node.getName().getFullyQualifiedName().equals("equals")) {
					if(node.parameters() == null || node.parameters().size() != 1){
						soGetSetEqualsHashCode = false;
						
					} else if (node.getReturnType2().isPrimitiveType()) {
				        if (((PrimitiveType)node.getReturnType2()).getPrimitiveTypeCode() != PrimitiveType.BOOLEAN) {
				        	soGetSetEqualsHashCode = false;
				        }
				    }
					
				//regras do hashCode
				} else if(node.getName().getFullyQualifiedName().equals("hashCode")) {
					if(node.parameters() != null || node.parameters().isEmpty()) {
						soGetSetEqualsHashCode = false;
					
					} else if (node.getReturnType2().isPrimitiveType()) {
				        if (((PrimitiveType)node.getReturnType2()).getPrimitiveTypeCode() != PrimitiveType.INT) {
				        	soGetSetEqualsHashCode = false;
				        }
				    }
					
				} else {
					soGetSetEqualsHashCode = false;
				}
				
			} else {
				soGetSetEqualsHashCode = false;
			}
		}
	}

	private void verificadorMetodoUtilitaria(MethodDeclaration node) {
		if(node.isConstructor()) {
			temConstrutorDeclarado = true;
			if(! Modifier.isPrivate(node.getModifiers())) {
				construtoresPrivado = false;
			}
		} else {
			if(Modifier.isPublic(node.getModifiers())) {
				if( ! Modifier.isStatic(node.getModifiers())) {
					metodosAtributosPublicosEstaticos = false;
				}
			}
		}
	}
	
	public boolean isEntidade() {
		return atributosPrivados && soGetSetEqualsHashCode && temAoMenosUmAtributo;
	}
	
	public boolean isUtilitaria() {
		//TODO AJUSTAR CLASSE COMO FINAL
		return /* classeDeclaradaFinal &&*/ temConstrutorDeclarado && 
				construtoresPrivado && metodosAtributosPublicosEstaticos;
	}

	public List<String> getAtributos() {
		return atributos;
	}
	public void setAtributos(List<String> atributos) {
		this.atributos = atributos;
	}

	public List<String> getMetodos() {
		return metodos;
	}
	public void setMetodos(List<String> metodos) {
		this.metodos = metodos;
	}

	public Map<String, Set<String>> getMetodoAtributos() {
		return metodoAtributos;
	}
	public void setMetodoAtributos(Map<String, Set<String>> metodoAtributos) {
		this.metodoAtributos = metodoAtributos;
	}
	
	private ASTNode getRelevantParent(final ASTNode node) {
		for (ASTNode aux = node; aux != null; aux = aux.getParent()) {
			switch (aux.getNodeType()) {
			case ASTNode.FIELD_DECLARATION:
			case ASTNode.METHOD_DECLARATION:
			case ASTNode.INITIALIZER:
				return aux;
			}
		}
		return node;
	}

	//TODO MethodInvocation já faz
	//pega obj.metodo()
//	public boolean visit(SimpleName node) {
//	    IBinding binding = node.resolveBinding();
//
//	    if (binding instanceof IVariableBinding) {
//	        IVariableBinding variable = (IVariableBinding) binding;
//
//	        if (variable.isField() && atributos.contains(node.getIdentifier())) {
//	        	if(node.getParent().getParent().getParent().getParent() instanceof MethodDeclaration) {
//	            	MethodDeclaration metDecl = (MethodDeclaration) node.getParent().getParent().getParent().getParent();
//	                 
//	            	System.out.println("MetAtr1: "+metDecl.getName()+": "+node.getIdentifier());
//	            	//adiciona na listagem do metodo
//	    			Set<String> listaAtributos = metodoAtributos.get(metDecl.getName().toString());
//	    			if(listaAtributos == null) {
//	    				listaAtributos = new HashSet<String>();
//	    			}
//	    			listaAtributos.add(node.getIdentifier());
//	    			
//	    			metodoAtributos.put(metDecl.getName().toString(), listaAtributos);
//	            }
//	        }
//	    }
//
//	    return super.visit(node);
//	}
//	@Override
//	public boolean visit(ClassInstanceCreation node) {
//		ASTNode relevantParent = getRelevantParent(node);
//
//		switch (relevantParent.getNodeType()) {
//		case ASTNode.FIELD_DECLARATION:
//			FieldDeclaration fd = (FieldDeclaration) relevantParent;
//			System.out.println("FIELD:"+ node);
//			break;
//		case ASTNode.METHOD_DECLARATION:
//			MethodDeclaration md = (MethodDeclaration) relevantParent;
//			System.out.println("METHOD:"+ node);
//			break;
//		case ASTNode.INITIALIZER:
//			System.out.println("INI:"+ node);
//			break;
//		}
//
//		return true;
//	}
//
//	@Override
//	public boolean visit(Assignment node) {
//		// TODO Auto-generated method stub
//		System.out.println("ASSIg: "+node);
//		return super.visit(node);
//	}
//	@Override
//	public boolean visit(VariableDeclarationStatement node) {
//		ASTNode relevantParent = getRelevantParent(node);
//
//		switch (relevantParent.getNodeType()) {
//		case ASTNode.METHOD_DECLARATION:
//			MethodDeclaration md = (MethodDeclaration) relevantParent;
//			System.out.println("VariableDeclarationStatementMET:"+ node);
//			break;
//		case ASTNode.INITIALIZER:
//			System.out.println("VariableDeclarationStatementINI:"+ node);
//			break;
//		}
//
//		return true;
//	}
//	
//	@Override
//	public boolean visit(VariableDeclarationExpression node) {
//		ASTNode relevantParent = getRelevantParent(node);
//
//		switch (relevantParent.getNodeType()) {
//		case ASTNode.METHOD_DECLARATION:
//			MethodDeclaration md = (MethodDeclaration) relevantParent;
//			System.out.println("VariableDeclarationExpressionMET:"+ node);
//			break;
//		case ASTNode.INITIALIZER:
//
//			System.out.println("VariableDeclarationExpressionINI:"+ node);
//			break;
//		}
//		return true;
//	}
	
	
}
