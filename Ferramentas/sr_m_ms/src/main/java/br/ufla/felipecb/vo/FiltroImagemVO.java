package br.ufla.felipecb.vo;

public class FiltroImagemVO {
	
	public FiltroImagemVO() { }
	
	public FiltroImagemVO(Long idVersao, String codigoVersao, Long idProjeto) {
		this.idVersao = idVersao;
		this.codigoVersao = codigoVersao;
		this.idProjeto = idProjeto;
		//dafault
		this.exibeEntidadeUtil = true;
		this.exibeLigacoesFracas = true;
		this.exibeMicrosservicoFinalizado = false;
		this.exibeMicrosservicoAberto = false;
		this.agrupadoPorMicrosservicos = false;
		this.exibeClassesMicrosservicoParticionadas = false;
	}
	private Long idVersao;
	private String codigoVersao;
	private Long idProjeto;

	private boolean exibeEntidadeUtil;
	private boolean exibeLigacoesFracas;
	private boolean exibeMicrosservicoFinalizado;
	private boolean exibeClassesMicrosservicoParticionadas;
	/**
	 * true aberto - false fechado
	 */
	private boolean exibeMicrosservicoAberto;
	private boolean agrupadoPorMicrosservicos;
	
	private Long idMicrosservico;
	
	public Long getIdVersao() {
		return idVersao;
	}
	public void setIdVersao(Long idVersao) {
		this.idVersao = idVersao;
	}
	public String getCodigoVersao() {
		return codigoVersao;
	}
	public void setCodigoVersao(String codigoVersao) {
		this.codigoVersao = codigoVersao;
	}
	public Long getIdProjeto() {
		return idProjeto;
	}
	public void setIdProjeto(Long idProjeto) {
		this.idProjeto = idProjeto;
	}
	public boolean isExibeEntidadeUtil() {
		return exibeEntidadeUtil;
	}
	public void setExibeEntidadeUtil(boolean exibeEntidadeUtil) {
		this.exibeEntidadeUtil = exibeEntidadeUtil;
	}
	public boolean isExibeLigacoesFracas() {
		return exibeLigacoesFracas;
	}
	public void setExibeLigacoesFracas(boolean exibeLigacoesFracas) {
		this.exibeLigacoesFracas = exibeLigacoesFracas;
	}

	public boolean isAgrupadoPorMicrosservicos() {
		return agrupadoPorMicrosservicos;
	}
	public void setAgrupadoPorMicrosservicos(boolean agrupadoPorMicrosservicos) {
		this.agrupadoPorMicrosservicos = agrupadoPorMicrosservicos;
	}

	public boolean isExibeMicrosservicoFinalizado() {
		return exibeMicrosservicoFinalizado;
	}
	public void setExibeMicrosservicoFinalizado(boolean exibeMicrosservicoFinalizado) {
		this.exibeMicrosservicoFinalizado = exibeMicrosservicoFinalizado;
	}

	public Long getIdMicrosservico() {
		return idMicrosservico;
	}
	public void setIdMicrosservico(Long idMicrosservico) {
		this.idMicrosservico = idMicrosservico;
	}

	public boolean isExibeMicrosservicoAberto() {
		return exibeMicrosservicoAberto;
	}
	public void setExibeMicrosservicoAberto(boolean exibeMicrosservicoAbertoFechado) {
		this.exibeMicrosservicoAberto = exibeMicrosservicoAbertoFechado;
	}

	public boolean isExibeClassesMicrosservicoParticionadas() {
		return exibeClassesMicrosservicoParticionadas;
	}

	public void setExibeClassesMicrosservicoParticionadas(boolean exibeClassesMicrosservicoParticionadas) {
		this.exibeClassesMicrosservicoParticionadas = exibeClassesMicrosservicoParticionadas;
	}
}
