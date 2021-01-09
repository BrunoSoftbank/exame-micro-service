package br.com.softbank.exame.dto;

import java.util.List;

import javax.validation.Valid;

public class AtualizarExameLoteDTO {

	@Valid
	private List<AtualizarExameDTO> exames;

	public List<AtualizarExameDTO> getExames() {
		return exames;
	}

	public void setExames(List<AtualizarExameDTO> exames) {
		this.exames = exames;
	}
}
