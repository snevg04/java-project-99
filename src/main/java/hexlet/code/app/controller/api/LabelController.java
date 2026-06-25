package hexlet.code.app.controller.api;

import hexlet.code.app.dto.LabelCreateDTO;
import hexlet.code.app.dto.LabelDTO;
import hexlet.code.app.dto.LabelUpdateDTO;
import hexlet.code.app.service.LabelService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/labels")
public class LabelController {

    @Autowired
    private LabelService labelService;

    @GetMapping("")
    public List<LabelDTO> index() {
        return labelService.getAllLabels();
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public LabelDTO createLabel(@Valid @RequestBody LabelCreateDTO labelCreateDTO) {
        return labelService.createLabel(labelCreateDTO);
    }

    @GetMapping("/{id}")
    public LabelDTO show(@PathVariable Long id) {
        return labelService.getLabel(id);
    }

    @PutMapping("/{id}")
    public LabelDTO update(@Valid @RequestBody LabelUpdateDTO labelUpdateDTO, @PathVariable Long id) {
        return labelService.updateLabel(labelUpdateDTO, id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        labelService.deleteLabel(id);
    }
}
