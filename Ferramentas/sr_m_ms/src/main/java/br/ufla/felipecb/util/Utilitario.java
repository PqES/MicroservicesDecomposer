package br.ufla.felipecb.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class Utilitario {
	
	private Utilitario() { }
	
	public static String getCor() {
		return  " [color=black penwidth=1];";
	}
	
	public static String getCor(boolean obrigatorio, boolean maisForte) {
		
		String color = null;
		if (obrigatorio) {
			color = " [color=black penwidth=2];";
			
		} else if(maisForte) {  
			color = " [color=black penwidth=1];";
		
		} else {
			color = " [style=\"filled,dashed\", color=black penwidth=1];";
		}
		
		return color;
	}

	/**
	 * 
	 * @param index
	 * @param valorMaximo tamanho da lista
	 * @return
	 */
	public static String formatarCasas(int index, int valorMaximo) {
		
		int qtd = 1;
		while(valorMaximo >= 10) {
			valorMaximo /= 10;
			qtd += 1;
		}
		
		return String.format("%0"+qtd+"d", index);
	}
	
	public static double arredondar(Double valor) {
		BigDecimal bd = BigDecimal.valueOf(valor);
	    bd = bd.setScale(2, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}
}
