package br.ufla.felipe.util;

import java.io.File;
import java.io.IOException;

public final class Utilitario {
	
	public static String CAMINHO = "/Users/fiocruz/Desktop/arquivosDecomposicao";
	
	public static String getPath(String arquivo) {
		File f1 = new File(CAMINHO);
		final String caminho = f1.getAbsolutePath() + "//" + arquivo;
		f1 = new File(caminho);
		if( ! f1.exists()){
			try {
				f1.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return caminho;
	}

}
