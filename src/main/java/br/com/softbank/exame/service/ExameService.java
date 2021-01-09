package br.com.softbank.exame.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import br.com.softbank.exame.dto.AtualizarExameLoteDTO;
import br.com.softbank.exame.dto.NovoExameLoteDTO;
import br.com.softbank.exame.enuns.ErrosDefaultEnum;
import br.com.softbank.exame.enuns.StatusEnum;
import br.com.softbank.exame.exception.ExameNotFoundException;
import br.com.softbank.exame.model.Exame;
import br.com.softbank.exame.model.Status;
import br.com.softbank.exame.model.Tipo;
import br.com.softbank.exame.repository.ExameRepository;

@Service
public class ExameService {
	
	private static final Logger LOG = LoggerFactory.getLogger(ExameService.class);

	@Autowired
	private StatusService statusService;

	@Autowired
	private TipoExameService tipoExameService;
	
	@Autowired
	private ExameRepository exameRepository;

	@Cacheable(cacheNames = "Exame", key = "#root.method.name")
	public List<Exame> findAll() {
		return exameRepository.findAll();
	}

	@Cacheable(cacheNames = "Exame", key="#id")
	public Exame findById(Long id) {
		Optional<Exame> exameOptional = exameRepository.findById(id);
		if (exameOptional.isPresent()) {
			return exameOptional.get();
		}
		throw new ExameNotFoundException(String.format(ErrosDefaultEnum.EXAME_NAO_ENCONTRADO.getDescricao(), id));
	}

	@Transactional
	@CacheEvict(cacheNames = "Exame", allEntries = true)
	public void deleteByIds(Long[] ids) {
		if (ids != null && ids.length > 0) {
			Arrays.asList(ids).stream().forEach(id -> {
				
				LOG.warn(this.getClass().getSimpleName() + ".deleteByIds(Long[] ids) " + String.valueOf(id));
				
				Exame exame = this.findById(id);
				exameRepository.delete(exame);
			});
		}
	}

	@Transactional
	@CacheEvict(cacheNames = "Exame", allEntries = true)
	public List<Exame> save(NovoExameLoteDTO novoExameLoteDTO) {
		Status status = statusService.findById(StatusEnum.ATIVO.getId());
		List<Exame> exames = new ArrayList<>();

		if(novoExameLoteDTO.getExames() != null && !novoExameLoteDTO.getExames().isEmpty()) {
			novoExameLoteDTO.getExames().stream().forEach(dto -> {
				
				LOG.warn(this.getClass().getSimpleName() + ".save(NovoExameLoteDTO novoExameLoteDTO) " + dto.convertToEntity());
				
				Exame exame = dto.convertToEntity();
				exame.setStatus(status);

				Tipo tipo = tipoExameService.findById(exame.getTipo().getId());
				exame.setTipo(tipo);
				
				exames.add(exameRepository.save(exame));
			});
		}
		return exames;
	}

	@Transactional
	@CacheEvict(cacheNames = "Exame", allEntries = true)
	public List<Exame> update(AtualizarExameLoteDTO atualizarExameLoteDTO) {
		List<Exame> exames = new ArrayList<>();
		if(atualizarExameLoteDTO.getExames() != null && !atualizarExameLoteDTO.getExames().isEmpty()) {
			atualizarExameLoteDTO.getExames().stream().forEach(dto -> {
				
				LOG.warn(this.getClass().getSimpleName() + ".(AtualizarExameLoteDTO atualizarExameLoteDTO) " + dto.convertToEntity());
				
				Exame exameDB = this.findById(dto.getId());
				exameDB.setNome(dto.getNome());

				Tipo tipo = tipoExameService.findById(dto.getTipo().getId());
				exameDB.setTipo(tipo);
				exames.add(exameRepository.save(exameDB));
			});
		}
		return exames;
	}

	@CacheEvict(cacheNames = "Exame", allEntries = true)
	public Exame patch(Long id) {
		LOG.warn(this.getClass().getSimpleName() + ".patch(Long id) " + String.valueOf(id));
		
		Exame exame = this.findById(id);
		if(exame.getStatus().getId() == StatusEnum.ATIVO.getId()) {
			exame.setStatus(statusService.findById(StatusEnum.INATIVO.getId()));
		} else {
			exame.setStatus(statusService.findById(StatusEnum.ATIVO.getId()));
		}		
		return exameRepository.save(exame);
	}
}
