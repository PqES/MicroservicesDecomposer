package br.ufla.felipe.handlers;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import br.ufla.felipe.ast.DependencyVisitor;
import br.ufla.felipe.entidades.Classe;
import br.ufla.felipe.entidades.Metodo;
import br.ufla.felipe.entidades.Pacote;
import br.ufla.felipe.entidades.Projeto;
import br.ufla.felipe.util.Utilitario;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * 
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class SampleHandler extends AbstractHandler {
	/**
	 * The constructor.
	 */
	public SampleHandler() {
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		IWorkbenchWindow window = HandlerUtil
				.getActiveWorkbenchWindowChecked(event);

		final InputDialog inputDialog = new InputDialog(
				window.getShell(),
				"Identificadoe estático",
				"Caminho onde salvará os arquivos",
				Utilitario.CAMINHO,
				null);

		inputDialog.open();

		if (inputDialog.getReturnCode() != Window.OK) {
			MessageDialog.openInformation(window.getShell(), "Fechando o sistema", "Nada foi realizado");
			return null;
		}

		// update model
		Utilitario.CAMINHO = inputDialog.getValue().trim();
		
		//
		List<Projeto> projetos = montaEstruturaObjetos();
		
		salvarArquivos(projetos);

		MessageDialog.openInformation(window.getShell(), "Informação", "Processamento concluído.\n\nArquivos salvos em "+Utilitario.getPath(""));
		return null;
	}

	/**
	 * Monta a estrutura do objeto lendo os prjetos(abertos) e conseuqnetemente seus pacote e classes que estão no eclipse.
	 */
	private List<Projeto> montaEstruturaObjetos() {
		List<Projeto> projetos = new ArrayList<Projeto>();
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject[] projects = root.getProjects(IContainer.INCLUDE_HIDDEN);
		
		//Cria os objetos temporários que serão utilizados para adicionar a lista
		Projeto projeto;
		Pacote pacote = null;
		Classe classe = null;
		
		//varre a lista de projetos encontrados no eclipse
		for(IProject proj: projects){
			if(proj.isOpen()) {
				try {
					projeto = new Projeto();
					//Nome do projeto
					projeto.setNome(proj.getDescription().getName());
					
					IJavaProject jproject = JavaCore.create(proj);
					
					IPackageFragmentRoot[] packages = jproject.getPackageFragmentRoots();
					
					projeto.setPacotes(new ArrayList<Pacote>());
					
					//varre a lista de pacotes do projeto
					for(IPackageFragmentRoot pack : packages){
						
						if(pack.getResource() != null && IPackageFragmentRoot.K_SOURCE == pack.getKind()) {
							
							//varre a lista de pacotes
							for (IJavaElement child : pack.getChildren()) {
								
								if (child.getElementType()==IJavaElement.PACKAGE_FRAGMENT) {
									
									pacote = new Pacote();
									//seta nomne do pacote
									pacote.setNome(child.getElementName());
									pacote.setClasses(new ArrayList<Classe>());
									
									
									IPackageFragment iPackageFragment = ((IPackageFragment)child);
									IJavaElement[] classes = iPackageFragment.getChildren();
									
									//varre a lista de classe do pacote
									for(IJavaElement element : classes) {
										classe = new Classe();
										classe.setMetodos(new ArrayList<Metodo>());
										
										if (element.getElementType()==IJavaElement.COMPILATION_UNIT) {
											
											ICompilationUnit comUni = ((ICompilationUnit)element);
											
											//seta o nome da classe
											classe.setNome(comUni.getElementName());
	
											if(classe.getNome().endsWith(".java")){
												System.out.println("Classe: "+ classe.getNome());
												DependencyVisitor dv = new DependencyVisitor(comUni);
												classe.setEntidade(dv.isEntidade());
												classe.setUtilitaria(dv.isUtilitaria());
												classe.setAtributos(dv.getAtributos());
												
												for(String met : dv.getMetodos()) {
													Metodo metodo = new Metodo(met, dv.getMetodoAtributos().get(met));
													classe.getMetodos().add(metodo);
												}
												System.out.println("_______________________");
											}
										}
										//adiciona a classe na listagem
										pacote.getClasses().add(classe);
									}
									//adiciona o pacote na listagem
									if ( ! pacote.getClasses().isEmpty()) {
										projeto.getPacotes().add(pacote);
									}
								}
							}
						}
					}
					projetos.add(projeto);
				} catch (CoreException e) {
					System.out.println("jproject.getPackageFragmentRoots() provavelmente causado pela pasta RemoteSystemsTempFiles ou projeto fechado");
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("Projeto não contém estrutura correta");
				}
			}
		}
		return projetos;
	}
	
	public static void salvarArquivos(List<Projeto> projetos) {

		try(FileWriter fw = new FileWriter(Utilitario.getPath("classes.txt"), true);
		    BufferedWriter bw = new BufferedWriter(fw);
		    PrintWriter out = new PrintWriter(bw)) {
			
			final String EXTENSAO_JAVA = ".java";
			
			for( Projeto projeto: projetos ) {
				for(Pacote pacote: projeto.getPacotes()) {
					for(Classe classe : pacote.getClasses()){
						
						if(classe.getNome().endsWith(EXTENSAO_JAVA)) {
							if(classe.isEntidade()){
								out.println(pacote.getNome()+"."+classe.getNome().replace(EXTENSAO_JAVA, ":entidade"));
							} else  if(classe.isUtilitaria()){
								out.println(pacote.getNome()+"."+classe.getNome().replace(EXTENSAO_JAVA, ":util"));
							} else {
								out.println(pacote.getNome()+"."+classe.getNome().replace(EXTENSAO_JAVA, ""));
							}
							
							String atributos = "atr:";
							if(null != classe.getAtributos() && ! classe.getAtributos().isEmpty()) {
								atributos += classe.getAtributos().toString().replace("[", "")
										.replace("]", "").replaceAll(",", ";").replaceAll(" ", "");
							}
							out.println(atributos);
							
							StringBuilder met = null;
							for(Metodo metodo : classe.getMetodos()) {
								if(met != null) {
									met.append(";");
								} else {
									met = new StringBuilder("met:");
								}
								met.append(metodo.getNome());
								
								if(metodo.getAtributos() != null && ! metodo.getAtributos().isEmpty()) {
									met.append(metodo.getAtributos().toString().replaceAll(" ", ""));
								}
							}
							if(met == null) {
								met = new StringBuilder("met:");
							}
							out.println(met);
						}
					}
				}
			}
		} catch (IOException e) { }
	}
	
	public static String retornarNomeSemExtensao(String nome){
		return nome.split("\\.")[0];
	}
}
