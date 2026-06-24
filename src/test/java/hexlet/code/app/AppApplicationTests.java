package hexlet.code.app;

import hexlet.code.app.dto.TaskCreateDTO;
import hexlet.code.app.dto.TaskStatusCreateDTO;
import hexlet.code.app.dto.TaskStatusUpdateDTO;
import hexlet.code.app.dto.UserCreateDTO;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.model.Task;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.model.User;
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

    private final Faker faker = new Faker();

    @Test
    public void testWelcome() throws Exception {
        mockMvc.perform(get("/welcome"))
                .andExpect(status().isOk());
    }

    @Test
    public void testIndex() throws Exception {
        var result = mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
    }

    @Test
    public void testCreate() throws Exception {
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
    public void testShow() throws Exception {

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
    public void testUpdate() throws Exception {

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
    public void testDelete() throws Exception {

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
    public void testTaskStatusIndex() throws Exception {

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

        var result = mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(payload)))
                .andExpect(status().isCreated())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                j -> j.node("name").isEqualTo("Test task"),
                j -> j.node("taskStatusId").isEqualTo(1)
        );
    }

    @Test
    void testShowTask() throws Exception {

        var status = taskStatusRepository.findById(1L).orElseThrow();

        var task = new Task();
        task.setName("Show task");
        task.setTaskStatus(status);

        taskRepository.save(task);

        var result = mockMvc.perform(get("/api/tasks/" + task.getId()))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                j -> j.node("id").isEqualTo(task.getId().intValue()),
                j -> j.node("name").isEqualTo("Show task")
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

        var task = new Task();
        task.setName("Old name");
        task.setTaskStatus(status);

        taskRepository.save(task);

        var payload = new HashMap<String, Object>();
        payload.put("name", "New name");

        var result = mockMvc.perform(put("/api/tasks/" + task.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                j -> j.node("id").isEqualTo(task.getId().intValue()),
                j -> j.node("name").isEqualTo("New name")
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
}
