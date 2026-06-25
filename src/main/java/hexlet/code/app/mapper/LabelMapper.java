package hexlet.code.app.mapper;

import hexlet.code.app.dto.LabelCreateDTO;
import hexlet.code.app.dto.LabelDTO;
import hexlet.code.app.dto.LabelUpdateDTO;
import hexlet.code.app.model.Label;
import org.springframework.stereotype.Component;

@Component
public class LabelMapper {

    public LabelDTO toDTO(Label label) {
        var labelDto = new LabelDTO();
        labelDto.setId(label.getId());
        labelDto.setName(label.getName());
        labelDto.setCreatedAt(label.getCreatedAt());
        return labelDto;
    }

    public Label toEntity(LabelCreateDTO labelCreateDTO) {
        var label = new Label();
        label.setName(labelCreateDTO.getName());
        return label;
    }

    public void updateEntity(LabelUpdateDTO labelUpdateDTO, Label label) {

        if (labelUpdateDTO.getName() != null) {
            label.setName(labelUpdateDTO.getName());
        }
    }
}
