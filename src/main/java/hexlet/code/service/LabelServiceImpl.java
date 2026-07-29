package hexlet.code.service;

import hexlet.code.dto.LabelCreateDTO;
import hexlet.code.dto.LabelDTO;
import hexlet.code.dto.LabelUpdateDTO;
import hexlet.code.exception.ConflictException;
import hexlet.code.exception.LabelInUseException;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.repository.LabelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LabelServiceImpl implements LabelService {

    private final LabelRepository labelRepository;

    private final LabelMapper labelMapper;

    @Override
    public List<LabelDTO> getAllLabels() {
        var labels = labelRepository.findAll();
        return labels.stream()
                .map(labelMapper::toDTO)
                .toList();
    }

    @Override
    public LabelDTO createLabel(LabelCreateDTO labelCreateDTO) {
        if (labelRepository.existsByName(labelCreateDTO.getName())) {
            throw new ConflictException("Label with given name already exists!");
        }
        var label = labelMapper.toEntity(labelCreateDTO);
        var savedLabel = labelRepository.save(label);
        return labelMapper.toDTO(savedLabel);
    }

    @Override
    public LabelDTO getLabel(Long id) {
        var label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label not found!"));
        return labelMapper.toDTO(label);
    }

    @Override
    public LabelDTO updateLabel(LabelUpdateDTO labelUpdateDTO, Long id) {
        var label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label not found!"));
        labelMapper.updateEntity(labelUpdateDTO, label);
        var savedLabel = labelRepository.save(label);
        return labelMapper.toDTO(savedLabel);
    }

    @Override
    @Transactional
    public void deleteLabel(Long id) {
        var label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label not found!"));

        if (!label.getTasks().isEmpty()) {
            throw new LabelInUseException("Label is used by tasks");
        }

        labelRepository.deleteById(id);
    }
}
