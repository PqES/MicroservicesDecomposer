package br.ufla.felipecb.vo;

public class FiltroAjustesVO {
	
	private Long idVersaoBase;
	private boolean realizaSplitClass;
	private Integer limiar;

	public Long getIdVersaoBase() {
		return idVersaoBase;
	}
	public void setIdVersaoBase(Long idVersaoBase) {
		this.idVersaoBase = idVersaoBase;
	}

	public boolean isRealizaSplitClass() {
		return realizaSplitClass;
	}
	public void setRealizaSplitClass(boolean realizaSplitClass) {
		this.realizaSplitClass = realizaSplitClass;
	}

	public Integer getLimiar() {
		return limiar;
	}
	public void setLimiar(Integer limiar) {
		this.limiar = limiar;
	}
}
