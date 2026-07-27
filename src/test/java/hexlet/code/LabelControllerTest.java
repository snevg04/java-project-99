package hexlet.code;

import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Set;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser(username = "hexlet@example.com")
class LabelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Test
    void testIndexLabels() throws Exception {

        mockMvc.perform(get("/api/labels"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    var body = result.getResponse().getContentAsString();
                    assertThatJson(body).isArray();
                });
    }

    @Test
    void testCreateLabel() throws Exception {

        var payload = new HashMap<String, Object>();
        payload.put("name", "new label");

        var result = mockMvc.perform(post("/api/labels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(payload)))
                .andExpect(status().isCreated())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                j -> j.node("name").isEqualTo("new label"),
                j -> j.node("createdAt").isString()
        );
    }

    @Test
    void testShowLabel() throws Exception {

        var label = new Label();
        label.setName("test-label");
        labelRepository.save(label);

        var result = mockMvc.perform(get("/api/labels/" + label.getId()))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                j -> j.node("id").isEqualTo(label.getId().intValue()),
                j -> j.node("name").isEqualTo("test-label"),
                j -> j.node("createdAt").isString()
        );
    }

    @Test
    void testUpdateLabel() throws Exception {

        var label = new Label();
        label.setName("old name");
        labelRepository.save(label);

        var payload = new HashMap<String, Object>();
        payload.put("name", "updated name");

        var result = mockMvc.perform(put("/api/labels/" + label.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                j -> j.node("id").isEqualTo(label.getId().intValue()),
                j -> j.node("name").isEqualTo("updated name")
        );
    }

    @Test
    void testDeleteLabel() throws Exception {

        var label = new Label();
        label.setName("to delete");
        labelRepository.save(label);

        mockMvc.perform(delete("/api/labels/" + label.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/labels/" + label.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteLabelLinkedToTask() throws Exception {

        var status = taskStatusRepository.findById(1L).orElseThrow();

        var label = new Label();
        label.setName("label-delete-test");
        label = labelRepository.save(label);

        var task = new Task();
        task.setTitle("task");
        task.setTaskStatus(status);
        task.setLabels(Set.of(label));
        taskRepository.save(task);

        mockMvc.perform(delete("/api/labels/" + label.getId()))
                .andExpect(status().isConflict());
    }

    @Test
    void testCreateLabelValidationBlankName() throws Exception {

        var payload = new HashMap<String, Object>();
        payload.put("name", "");

        mockMvc.perform(post("/api/labels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(payload)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateLabelValidationTooShort() throws Exception {

        var payload = new HashMap<String, Object>();
        payload.put("name", "ab");

        mockMvc.perform(post("/api/labels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(payload)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateLabelValidationMissingName() throws Exception {

        var payload = new HashMap<String, Object>();

        mockMvc.perform(post("/api/labels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(payload)))
                .andExpect(status().isBadRequest());
    }
}
