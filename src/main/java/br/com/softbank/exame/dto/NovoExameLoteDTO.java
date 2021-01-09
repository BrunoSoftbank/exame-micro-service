package br.com.softbank.exame.dto;

import java.util.List;

import javax.validation.Valid;

public class NovoExameLoteDTO {

	@Valid
	private List<NovoExameDTO> exames;

	public List<NovoExameDTO> getExames() {
		return exames;
	}

	public void setExames(List<NovoExameDTO> exames) {
		this.exames = exames;
	}
}
