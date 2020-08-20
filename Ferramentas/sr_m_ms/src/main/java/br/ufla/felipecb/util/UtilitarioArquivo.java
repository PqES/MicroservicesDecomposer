package br.ufla.felipecb.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.web.multipart.MultipartFile;

public final class UtilitarioArquivo {
	
	private UtilitarioArquivo() { }
	
	/**
	 * caso idVersao seja null, usa raiz do projeto
	 * @param idProjeto
	 * @param idVersao
	 * @return
	 */
	private static String getCaminhoBase(Long idProjeto, Long idVersao){
		String caminhoBase = Constante.CAMINHO_BASE + idProjeto + "//";
		
		if(idVersao != null) {
			caminhoBase += idVersao + "//";
		}
		
		File pasta = new File(caminhoBase);
		if( !pasta.exists()) {
			pasta.mkdirs();
		}
		return caminhoBase;
	}
	
	public static File gerarArquivo(Long idProjeto, Long idVersao, String arquivo){
		return new File(getCaminhoBase(idProjeto, idVersao)+arquivo);
	}
	
	public static void salvar(MultipartFile file, Long idProjeto, Long idVersao, String nome, Long idVersaoAnteior) {

		try {
			Path arquivoAtual = Paths.get(getCaminhoBase(idProjeto, idVersao), nome);
//			try {
//				if(file != null) {
//					Files.write(arquivoAtual, file.getBytes());
//				} else {
//					Files.write(arquivoAtual, "".getBytes());
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
			if(idVersaoAnteior != null) {
				Path arquivoVersaoAnterior = Paths.get(getCaminhoBase(idProjeto, idVersaoAnteior), nome);
			
				// Caso a versao anterior tenha algum conteúdo, deverá ser adicionado e,
				// após concluído com as modificaçõres do arquivo adicionado
				if(arquivoVersaoAnterior.toFile() != null && arquivoVersaoAnterior.toFile().exists()) {
					Files.copy(arquivoVersaoAnterior, arquivoAtual);
				}
			}
			
			//Escreve no caminho especificado o arquivo enviado
			try (OutputStream os = new FileOutputStream(arquivoAtual.toFile(), true)) {
		        os.write(file.getBytes());
		        os.write("\n".getBytes());
		    } catch (IOException e) {
				e.printStackTrace();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void excluir(Long idProjeto, Long idVersao) {
		String pasta = getCaminhoBase(idProjeto, idVersao);
		File file = new File(pasta);
		if (file.exists()) {
			if (file.isDirectory()) { 
				for (File child : file.listFiles()) {
					child.delete();
				}
			}
			file.delete();
		}
	}
//	
//	public static byte[] recuperarArquivo(Long idProjeto, Long idVersao, String arquivo){
//		File file = new File(getCaminhoBase(idProjeto, idVersao)+arquivo);
//		try {
//			return Files.readAllBytes(file.toPath());
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}

}
