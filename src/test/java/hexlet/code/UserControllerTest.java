package hexlet.code;

import hexlet.code.dto.UserCreateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
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
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
@WithMockUser(username = "hexlet@example.com")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserRepository userRepository;

    private final Faker faker = new Faker();

    @Test
    void testIndexUsers() throws Exception {
        var result = mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
    }

    @Test
    void testCreateUser() throws Exception {
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
    void testShowUser() throws Exception {

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
    void testUpdateUser() throws Exception {

        var savedUser = userRepository.findByEmail("hexlet@example.com").orElseThrow();
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
    void testDeleteUser() throws Exception {

        var savedUser = userRepository.findByEmail("hexlet@example.com").orElseThrow();
        var id = savedUser.getId();

        mockMvc.perform(delete("/api/users/" + id))
                .andExpect(status().isNoContent());

        assertThat(userRepository.findById(id)).isEmpty();
    }

    @Test
    @WithMockUser(username = "test")
    void testUpdateValidationBlank() throws Exception {

        var user = new User();
        user.setFirstName(faker.name().firstName());
        user.setLastName(faker.name().lastName());
        user.setEmail("test");
        user.setPasswordDigest(faker.internet().password(8, 12));

        var savedUser = userRepository.save(user);

        var payload = new HashMap<String, Object>();
        payload.put("firstName", "Updated Name");

        mockMvc.perform(put("/api/users/" + savedUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(payload)))
                .andExpect(status().isOk());
        var reloaded = userRepository.findById(savedUser.getId()).orElseThrow();

        assertThat(reloaded.getEmail()).isEqualTo(user.getEmail());
        assertThat(reloaded.getFirstName()).isEqualTo("Updated Name");
        assertThat(reloaded.getLastName()).isEqualTo(user.getLastName());
    }
}
