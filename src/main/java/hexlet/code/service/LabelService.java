package hexlet.code.service;

import hexlet.code.dto.LabelCreateDTO;
import hexlet.code.dto.LabelDTO;
import hexlet.code.dto.LabelUpdateDTO;

import java.util.List;

public interface LabelService {
    List<LabelDTO> getAllLabels();
    LabelDTO createLabel(LabelCreateDTO labelCreateDTO);
    LabelDTO getLabel(Long id);
    LabelDTO updateLabel(LabelUpdateDTO labelUpdateDTO, Long id);
    LabelDTO deleteLabel(Long id);
}
