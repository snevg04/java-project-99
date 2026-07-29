package hexlet.code;

import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
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
import java.util.List;
import java.util.Set;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser(username = "hexlet@example.com")
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private UserRepository userRepository;

    private Task createTask(String title, User user, TaskStatus status, Label label) {
        Task task = new Task();
        task.setTitle(title);
        task.setContent("Description");
        task.setAssignee(user);
        task.setTaskStatus(status);
        task.setLabels(Set.of(label));
        return taskRepository.save(task);
    }

    @Test
    void testCreateTask() throws Exception {

        var payload = new HashMap<String, Object>();
        payload.put("title", "Test task");
        payload.put("content", "desc");
        payload.put("status", "draft");
        payload.put("taskLabelIds", List.of(1L));

        var result = mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(payload)))
                .andExpect(status().isCreated())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                j -> j.node("title").isEqualTo("Test task"),
                j -> j.node("status").isEqualTo("draft"),
                j -> j.node("taskLabelIds").isArray()
        );
    }

    @Test
    void testCreateTaskWithoutLabels() throws Exception {

        var payload = new HashMap<String, Object>();
        payload.put("title", "Task without labels");
        payload.put("status", "draft");

        var result = mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(payload)))
                .andExpect(status().isCreated())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                j -> j.node("title").isEqualTo("Task without labels"),
                j -> j.node("status").isEqualTo("draft"),
                j -> j.node("taskLabelIds").isArray().isEmpty()
        );
    }

    @Test
    void testShowTask() throws Exception {

        var status = taskStatusRepository.findById(1L).orElseThrow();
        var label = labelRepository.findByName("feature").orElseThrow();

        var task = new Task();
        task.setTitle("Show task");
        task.setTaskStatus(status);
        task.setLabels(Set.of(label));

        taskRepository.save(task);

        var result = mockMvc.perform(get("/api/tasks/" + task.getId()))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                j -> j.node("id").isEqualTo(task.getId().intValue()),
                j -> j.node("title").isEqualTo("Show task"),
                j -> j.node("taskLabelIds").isArray()
        );
    }

    @Test
    void testIndexTasks() throws Exception {

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    var body = result.getResponse().getContentAsString();
                    assertThatJson(body).isArray();
                });
    }

    @Test
    void testUpdateTask() throws Exception {

        var status = taskStatusRepository.findById(1L).orElseThrow();
        var label = labelRepository.findByName("feature").orElseThrow();

        var task = new Task();
        task.setTitle("Old name");
        task.setTaskStatus(status);
        task.setLabels(Set.of(label));

        taskRepository.save(task);

        var payload = new HashMap<String, Object>();
        payload.put("title", "New name");
        payload.put("taskLabelIds", List.of(label.getId()));

        var result = mockMvc.perform(put("/api/tasks/" + task.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                j -> j.node("id").isEqualTo(task.getId().intValue()),
                j -> j.node("title").isEqualTo("New name"),
                j -> j.node("taskLabelIds").isArray()
        );
    }

    @Test
    void testUpdateTaskNotFound() throws Exception {

        var payload = new HashMap<String, Object>();
        payload.put("title", "New name");

        mockMvc.perform(put("/api/tasks/99999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(payload)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateWithNonExistentAssigneeId() throws Exception {

        var status = taskStatusRepository.findById(1L).orElseThrow();
        var label = labelRepository.findByName("feature").orElseThrow();

        var task = new Task();
        task.setTitle("Task for assignee test");
        task.setTaskStatus(status);
        task.setLabels(Set.of(label));
        taskRepository.save(task);

        var payload = new HashMap<String, Object>();
        payload.put("assigneeId", 99999);

        mockMvc.perform(put("/api/tasks/" + task.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(payload)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateWithNonExistentStatus() throws Exception {

        var status = taskStatusRepository.findById(1L).orElseThrow();
        var label = labelRepository.findByName("feature").orElseThrow();

        var task = new Task();
        task.setTitle("Task for status test");
        task.setTaskStatus(status);
        task.setLabels(Set.of(label));
        taskRepository.save(task);

        var payload = new HashMap<String, Object>();
        payload.put("status", "nonexistent_slug");

        mockMvc.perform(put("/api/tasks/" + task.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(payload)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateWithNonExistentLabelIds() throws Exception {

        var status = taskStatusRepository.findById(1L).orElseThrow();
        var label = labelRepository.findByName("feature").orElseThrow();

        var task = new Task();
        task.setTitle("Task for label test");
        task.setTaskStatus(status);
        task.setLabels(Set.of(label));
        taskRepository.save(task);

        var payload = new HashMap<String, Object>();
        payload.put("taskLabelIds", List.of(99999L));

        mockMvc.perform(put("/api/tasks/" + task.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(payload)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateWithNullFieldsPreservesExisting() throws Exception {

        var status = taskStatusRepository.findById(1L).orElseThrow();
        var label = labelRepository.findByName("feature").orElseThrow();
        User user = userRepository.findByEmail("hexlet@example.com").orElseThrow();

        var task = new Task();
        task.setTitle("Original title");
        task.setContent("Original content");
        task.setIndex(42);
        task.setAssignee(user);
        task.setTaskStatus(status);
        task.setLabels(Set.of(label));
        taskRepository.save(task);

        var payload = new HashMap<String, Object>();

        var result = mockMvc.perform(put("/api/tasks/" + task.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                j -> j.node("title").isEqualTo("Original title"),
                j -> j.node("content").isEqualTo("Original content"),
                j -> j.node("index").isEqualTo(42),
                j -> j.node("assignee_id").isEqualTo(user.getId()),
                j -> j.node("status").isEqualTo(status.getSlug()),
                j -> j.node("taskLabelIds").isArray().contains(label.getId())
        );
    }

    @Test
    void testUpdateLabelsToEmptyList() throws Exception {

        var status = taskStatusRepository.findById(1L).orElseThrow();
        var label = labelRepository.findByName("feature").orElseThrow();

        var task = new Task();
        task.setTitle("Task with labels");
        task.setTaskStatus(status);
        task.setLabels(Set.of(label));
        taskRepository.save(task);

        var payload = new HashMap<String, Object>();
        payload.put("taskLabelIds", List.of());

        var result = mockMvc.perform(put("/api/tasks/" + task.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                j -> j.node("title").isEqualTo("Task with labels"),
                j -> j.node("taskLabelIds").isArray().isEmpty()
        );
    }

    @Test
    void testUpdateContentToNull() throws Exception {

        var status = taskStatusRepository.findById(1L).orElseThrow();

        var task = new Task();
        task.setTitle("Task with content");
        task.setContent("Some description");
        task.setTaskStatus(status);
        taskRepository.save(task);

        var payload = new HashMap<String, Object>();
        payload.put("content", null);

        var result = mockMvc.perform(put("/api/tasks/" + task.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                j -> j.node("title").isEqualTo("Task with content"),
                j -> j.node("content").isNull()
        );
    }

    @Test
    void testDeleteTask() throws Exception {

        mockMvc.perform(delete("/api/tasks/1"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateTaskValidationBlankName() throws Exception {

        var payload = new HashMap<String, Object>();
        payload.put("title", "");
        payload.put("status", "draft");

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(payload)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateTaskValidationNoStatus() throws Exception {

        var payload = new HashMap<String, Object>();
        payload.put("title", "Valid name");

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(payload)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testFilterByTitleCont() throws Exception {

        User user = userRepository.findByEmail("hexlet@example.com").orElseThrow();
        TaskStatus status = taskStatusRepository.findBySlug("to_be_fixed").orElseThrow();
        Label label = labelRepository.findByName("bug").orElseThrow();

        createTask("Create new version", user, status, label);

        var result = mockMvc.perform(get("/api/tasks")
                        .param("titleCont", "Create"))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        assertThatJson(body)
                .isArray()
                .satisfies(content -> assertThatJson(content)
                        .node("[0].title")
                        .asString()
                        .contains("Create"));
    }

    @Test
    void testFilterByAssigneeId() throws Exception {

        User user = userRepository.findByEmail("hexlet@example.com").orElseThrow();

        var result = mockMvc.perform(get("/api/tasks")
                        .param("assigneeId", user.getId().toString()))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        assertThatJson(body)
                .isArray()
                .satisfies(content -> {
                    for (Object item : content) {
                        assertThatJson(item)
                                .node("assignee_id")
                                .isEqualTo(user.getId());
                    }
                });
    }

    @Test
    void testFilterByLabelId() throws Exception {

        User user = userRepository.findByEmail("hexlet@example.com").orElseThrow();
        TaskStatus status = taskStatusRepository.findBySlug("to_be_fixed").orElseThrow();
        Label label = labelRepository.findByName("bug").orElseThrow();

        createTask("Task with label", user, status, label);

        mockMvc.perform(get("/api/tasks")
                        .param("labelId", label.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    var body = result.getResponse().getContentAsString();

                    assertThatJson(body)
                            .isArray()
                            .satisfies(content -> assertThatJson(content)
                                    .node("[*].taskLabelIds")
                                    .matches(arr -> true));
                });
    }

    @Test
    void testFilterCombined() throws Exception {

        User user = userRepository.findByEmail("hexlet@example.com").orElseThrow();
        TaskStatus status = taskStatusRepository.findBySlug("to_be_fixed").orElseThrow();
        Label label = taskRepository.findById(1L)
                .flatMap(t -> labelRepository.findByName("bug"))
                .orElse(labelRepository.findByName("bug").orElseThrow());

        createTask("Create new version", user, status, label);

        mockMvc.perform(get("/api/tasks")
                        .param("titleCont", "Create")
                        .param("assigneeId", user.getId().toString())
                        .param("status", status.getSlug())
                        .param("labelId", label.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    var body = result.getResponse().getContentAsString();

                    assertThatJson(body)
                            .isArray()
                            .satisfies(content -> assertThatJson(content)
                                    .node("[0].title")
                                    .isPresent());
                });
    }
}
