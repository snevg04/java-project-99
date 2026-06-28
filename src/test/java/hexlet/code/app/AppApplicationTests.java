package hexlet.code.app;

import hexlet.code.app.dto.TaskStatusCreateDTO;
import hexlet.code.app.dto.TaskStatusUpdateDTO;
import hexlet.code.app.dto.UserCreateDTO;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.model.Label;
import hexlet.code.app.model.Task;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.LabelRepository;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.repository.UserRepository;
import net.datafaker.Faker;
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

import java.util.HashMap;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser(username = "hexlet@example.com")
class AppApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private LabelRepository labelRepository;

    private final Faker faker = new Faker();

    private Task createTask(String name, User user, TaskStatus status, Label label) {
        Task task = new Task();
        task.setName(name);
        task.setDescription("Description");
        task.setIndex(1);
        task.setAssignee(user);
        task.setTaskStatus(status);
        task.setLabels(List.of(label));
        return taskRepository.save(task);
    }

    @Test
    public void testWelcome() throws Exception {
        mockMvc.perform(get("/welcome"))
                .andExpect(status().isOk());
    }

    @Test
    public void testIndexUsers() throws Exception {
        var result = mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
    }

    @Test
    public void testCreateUser() throws Exception {
        var payload = new UserCreateDTO();
        payload.setFirstName(faker.name().firstName());
        payload.setLastName(faker.name().lastName());
        payload.setEmail(faker.internet().emailAddress());
        payload.setPassword(faker.internet().password(6, 12));

        var result = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(payload)))
                .andExpect(status().isCreated())
                .andReturn();

        var id = om.readTree(result.getResponse().getContentAsString()).get("id").asLong();

        assertThatJson(result.getResponse().getContentAsString()).and(
                json -> json.node("id").isPresent(),
                json -> json.node("firstName").isEqualTo(payload.getFirstName()),
                json -> json.node("lastName").isEqualTo(payload.getLastName()),
                json -> json.node("email").isEqualTo(payload.getEmail()),
                json -> json.node("password").isAbsent()
        );

        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found!"));
        assertThat(user.getFirstName()).isEqualTo(payload.getFirstName());
        assertThat(user.getLastName()).isEqualTo(payload.getLastName());
        assertThat(user.getEmail()).isEqualTo(payload.getEmail());
    }

    @Test
    public void testShowUser() throws Exception {

        var user = new User();
        user.setFirstName(faker.name().firstName());
        user.setLastName(faker.name().lastName());
        user.setEmail(faker.internet().emailAddress());
        user.setPasswordDigest("test-password");

        var savedUser = userRepository.save(user);
        var userId = savedUser.getId();

        var result = mockMvc.perform(get("/api/users/" + userId))
                .andExpect(status().isOk())
                .andReturn();

        assertThatJson(result.getResponse().getContentAsString()).and(
                json -> json.node("id").isEqualTo(userId),
                json -> json.node("firstName").isEqualTo(savedUser.getFirstName()),
                json -> json.node("lastName").isEqualTo(savedUser.getLastName()),
                json -> json.node("email").isEqualTo(savedUser.getEmail()),
                json -> json.node("password").isAbsent(),
                json -> json.node("createdAt").isPresent()
        );
    }

    @Test
    public void testUpdateUser() throws Exception {

        var user = new User();
        user.setFirstName(faker.name().firstName());
        user.setLastName(faker.name().lastName());
        user.setEmail(faker.internet().emailAddress());
        user.setPasswordDigest("test-password");

        var savedUser = userRepository.save(user);
        var userId = savedUser.getId();

        var payload = new HashMap<String, Object>();
        payload.put("email", faker.internet().emailAddress());

        var result = mockMvc.perform(put("/api/users/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andReturn();

        assertThatJson(result.getResponse().getContentAsString()).and(
                json -> json.node("id").isEqualTo(savedUser.getId()),
                json -> json.node("email").isEqualTo(payload.get("email")),
                json -> json.node("password").isAbsent()
        );

        var updatedUser = userRepository.findById(savedUser.getId()).orElseThrow();

        assertThat(updatedUser.getEmail()).isEqualTo(payload.get("email"));
        assertThat(updatedUser.getFirstName()).isEqualTo(savedUser.getFirstName());
        assertThat(updatedUser.getLastName()).isEqualTo(savedUser.getLastName());
    }

    @Test
    public void testDeleteUser() throws Exception {

        var user = new User();
        user.setFirstName(faker.name().firstName());
        user.setLastName(faker.name().lastName());
        user.setEmail(faker.internet().emailAddress());
        user.setPasswordDigest("test-password");

        var savedUser = userRepository.save(user);
        var id = savedUser.getId();

        mockMvc.perform(delete("/api/users/" + id))
                .andExpect(status().isNoContent());

        assertThat(userRepository.findById(id)).isEmpty();
    }

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

        assertThatJson(result.getResponse().getContentAsString())
                .node("name").isEqualTo("must not be blank");
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

        assertThatJson(result.getResponse().getContentAsString())
                .node("slug").isEqualTo("must not be blank");
    }

    @Test
    @WithMockUser(username = "test")
    void testUpdateValidationBlank() throws Exception {

        var user = new User();
        user.setFirstName(faker.name().firstName());
        user.setLastName(faker.name().lastName());
        user.setEmail(faker.internet().emailAddress());
        user.setPasswordDigest(faker.internet().password(8, 12));

        var saved = userRepository.save(user);

        var payload = new HashMap<String, Object>();
        payload.put("email", "");

        mockMvc.perform(put("/api/users/" + saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(payload)))
                .andExpect(status().isBadRequest());
        var reloaded = userRepository.findById(saved.getId()).orElseThrow();

        assertThat(reloaded.getEmail()).isEqualTo(user.getEmail());
        assertThat(reloaded.getFirstName()).isEqualTo(user.getFirstName());
        assertThat(reloaded.getLastName()).isEqualTo(user.getLastName());
    }

    @Test
    void testCreateTask() throws Exception {

        Long draftId = 1L;

        var payload = new HashMap<String, Object>();
        payload.put("name", "Test task");
        payload.put("index", 10);
        payload.put("description", "desc");
        payload.put("taskStatusId", draftId.longValue());
        payload.put("labelIds", List.of(1L));

        var result = mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(payload)))
                .andExpect(status().isCreated())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                j -> j.node("name").isEqualTo("Test task"),
                j -> j.node("taskStatusId").isEqualTo(1),
                j -> j.node("labelIds").isArray()
        );
    }

    @Test
    void testShowTask() throws Exception {

        var status = taskStatusRepository.findById(1L).orElseThrow();
        var label = labelRepository.findByName("feature").orElseThrow();

        var task = new Task();
        task.setName("Show task");
        task.setTaskStatus(status);
        task.setLabels(List.of(label));

        taskRepository.save(task);

        var result = mockMvc.perform(get("/api/tasks/" + task.getId()))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                j -> j.node("id").isEqualTo(task.getId().intValue()),
                j -> j.node("name").isEqualTo("Show task"),
                j -> j.node("labelIds").isArray()
        );
    }

    @Test
    void testIndexTasks() throws Exception {

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    var body = result.getResponse().getContentAsString();
                    assertThatJson(body).isObject();
                    assertThatJson(body)
                            .node("content")
                            .isArray();
                });
    }

    @Test
    void testUpdateTask() throws Exception {

        var status = taskStatusRepository.findById(1L).orElseThrow();
        var label = labelRepository.findByName("feature").orElseThrow();

        var task = new Task();
        task.setName("Old name");
        task.setTaskStatus(status);
        task.setLabels(List.of(label));

        taskRepository.save(task);

        var payload = new HashMap<String, Object>();
        payload.put("name", "New name");
        payload.put("labelIds", List.of(label.getId()));

        var result = mockMvc.perform(put("/api/tasks/" + task.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                j -> j.node("id").isEqualTo(task.getId().intValue()),
                j -> j.node("name").isEqualTo("New name"),
                j -> j.node("labelIds").isArray()
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
    public void testCreateTaskValidationBlankName() throws Exception {

        var payload = new HashMap<String, Object>();
        payload.put("name", "");
        payload.put("taskStatusId", 1);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(payload)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateTaskValidationNoStatus() throws Exception {

        var payload = new HashMap<String, Object>();
        payload.put("name", "Valid name");

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(payload)))
                .andExpect(status().isBadRequest());
    }

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
        task.setName("task");
        task.setTaskStatus(status);
        task.setLabels(List.of(label));
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
                .node("content")
                .isArray()
                .satisfies(content -> assertThatJson(content)
                        .node("[0].name")
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

        System.out.println(body);

        assertThatJson(body)
                .node("content")
                .isArray()
                .satisfies(content -> {

                    // content — это JSON array
                    for (Object item : content) {

                        assertThatJson(item)
                                .node("assigneeId")
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
                            .node("content")
                            .isArray()
                            .satisfies(content -> assertThatJson(content)
                                    .node("[*].labelIds")
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
                            .node("content")
                            .isArray()
                            .satisfies(content -> assertThatJson(content)
                                    .node("[0].name")
                                    .isPresent());
                });
    }
}
