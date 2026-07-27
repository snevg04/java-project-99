package hexlet.code;

import hexlet.code.dto.TaskStatusCreateDTO;
import hexlet.code.dto.TaskStatusUpdateDTO;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;

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

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser(username = "hexlet@example.com")
class TaskStatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Test
    public void testIndexTaskStatuses() throws Exception {

        mockMvc.perform(get("/api/task_statuses"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    var body = result.getResponse().getContentAsString();
                    assertThatJson(body).isArray();
                });
    }

    @Test
    public void testCreateTaskStatus() throws Exception {
        var payload = new TaskStatusCreateDTO();
        payload.setName("New");
        payload.setSlug("new");

        var result = mockMvc.perform(post("/api/task_statuses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(payload)))
                .andExpect(status().isCreated())
                .andReturn();

        assertThatJson(result.getResponse().getContentAsString()).and(
                json -> json.node("id").isPresent(),
                json -> json.node("name").isEqualTo("New"),
                json -> json.node("slug").isEqualTo("new"),
                json -> json.node("createdAt").isPresent()
        );
    }

    @Test
    public void testShowTaskStatus() throws Exception {

        var status = new TaskStatus();
        status.setName("Work-in-progress");
        status.setSlug("work_in_progress");

        var saved = taskStatusRepository.save(status);

        var result = mockMvc.perform(get("/api/task_statuses/" + saved.getId()))
                .andExpect(status().isOk())
                .andReturn();

        assertThatJson(result.getResponse().getContentAsString()).and(
                json -> json.node("id").isEqualTo(saved.getId()),
                json -> json.node("name").isEqualTo("Work-in-progress"),
                json -> json.node("slug").isEqualTo("work_in_progress"),
                json -> json.node("createdAt").isPresent()
        );
    }

    @Test
    public void testUpdateTaskStatus() throws Exception {

        var status = new TaskStatus();
        status.setName("Old");
        status.setSlug("old");

        var saved = taskStatusRepository.save(status);

        var payload = new TaskStatusUpdateDTO();
        payload.setName("Updated");

        var result = mockMvc.perform(put("/api/task_statuses/" + saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andReturn();

        assertThatJson(result.getResponse().getContentAsString()).and(
                json -> json.node("id").isEqualTo(saved.getId()),
                json -> json.node("name").isEqualTo("Updated"),
                json -> json.node("slug").isEqualTo("old")
        );
    }

    @Test
    public void testDeleteTaskStatus() throws Exception {

        var status = new TaskStatus();
        status.setName("Temp");
        status.setSlug("temp");

        var saved = taskStatusRepository.save(status);

        mockMvc.perform(delete("/api/task_statuses/" + saved.getId()))
                .andExpect(status().isNoContent());

        assertThat(taskStatusRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    @WithMockUser
    public void testCreateValidationNameBlank() throws Exception {

        var payload = new TaskStatusCreateDTO();
        payload.setName("");
        payload.setSlug("valid_slug");

        var result = mockMvc.perform(post("/api/task_statuses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(payload)))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertThatJson(result.getResponse().getContentAsString()).and(
                json -> json.node("message").isEqualTo("Validation error"),
                json -> json.node("errors.name").isEqualTo("must not be blank")
        );
    }

    @Test
    @WithMockUser
    public void testCreateValidationSlugBlank() throws Exception {

        var payload = new TaskStatusCreateDTO();
        payload.setName("Valid Name");
        payload.setSlug("");

        var result = mockMvc.perform(post("/api/task_statuses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(payload)))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertThatJson(result.getResponse().getContentAsString()).and(
                json -> json.node("message").isEqualTo("Validation error"),
                json -> json.node("errors.slug").isEqualTo("must not be blank")
        );
    }
}
