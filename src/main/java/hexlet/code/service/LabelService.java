package hexlet.code.service;

import hexlet.code.dto.LabelCreateDTO;
import hexlet.code.dto.LabelDTO;
import hexlet.code.dto.LabelUpdateDTO;
import hexlet.code.exception.LabelInUseException;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.repository.LabelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LabelService {

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private LabelMapper labelMapper;

    public List<LabelDTO> getAllLabels() {
        var labels = labelRepository.findAll();
        return labels.stream()
                .map(labelMapper::toDTO)
                .toList();
    }

    public LabelDTO createLabel(LabelCreateDTO labelCreateDTO) {
        var label = labelMapper.toEntity(labelCreateDTO);
        var savedLabel = labelRepository.save(label);
        return labelMapper.toDTO(savedLabel);
    }

    public LabelDTO getLabel(Long id) {
        var label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label not found!"));
        return labelMapper.toDTO(label);
    }

    public LabelDTO updateLabel(LabelUpdateDTO labelUpdateDTO, Long id) {
        var label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label not found!"));
        labelMapper.updateEntity(labelUpdateDTO, label);
        var savedLabel = labelRepository.save(label);
        return labelMapper.toDTO(savedLabel);
    }

    public void deleteLabel(Long id) {
        var label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label not found!"));

        if (!label.getTasks().isEmpty()) {
            throw new LabelInUseException("Label is used by tasks");
        }

        labelRepository.deleteById(id);
    }
}
