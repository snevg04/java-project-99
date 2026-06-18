package hexlet.code.app;

import hexlet.code.app.model.User;
import hexlet.code.app.repository.UserRepository;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class AppApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserRepository userRepository;

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
        var payload = Instancio.of(User.class)
                .ignore(Select.field(User::getId))
                .supply(Select.field(User::getFirstName), () -> faker.name().firstName())
                .supply(Select.field(User::getLastName), () -> faker.name().lastName())
                .supply(Select.field(User::getEmail), () -> faker.internet().emailAddress())
                .supply(Select.field(User::getPassword), () -> faker.internet().password())
                .create();

        var result = mockMvc.perform(post("/api/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsString(payload)))
                .andExpect(status().isCreated())
                .andReturn();

        assertThatJson(result.getResponse().getContentAsString()).and(
                json -> json.node("id").isPresent(),
                json -> json.node("firstName").isEqualTo(payload.getFirstName()),
                json -> json.node("lastName").isEqualTo(payload.getLastName()),
                json -> json.node("email").isEqualTo(payload.getEmail()),
                json -> json.node("password").isAbsent()
        );

        var user = userRepository.findByEmail(payload.getEmail()).orElseThrow();
        assertThat(user.getFirstName()).isEqualTo(payload.getFirstName());
        assertThat(user.getLastName()).isEqualTo(payload.getLastName());
        assertThat(user.getEmail()).isEqualTo(payload.getEmail());
    }

    @Test
    public void testShow() throws Exception {
        mockMvc.perform(get("/api/users/{id}"))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdate() throws Exception {
        mockMvc.perform(get("/api/users/{id}"))
                .andExpect(status().isOk());
    }

    @Test
    public void testDelete() throws Exception {
        mockMvc.perform(get("/api/users/{id}"))
                .andExpect(status().isNoContent());
    }

}
