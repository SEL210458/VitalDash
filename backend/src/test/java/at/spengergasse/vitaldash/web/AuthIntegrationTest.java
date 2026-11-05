package at.spengergasse.vitaldash.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import at.spengergasse.vitaldash.domain.AppUser;
import at.spengergasse.vitaldash.domain.Role;
import at.spengergasse.vitaldash.repository.AppUserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integrationstest gegen die lokale PostgreSQL-Datenbank (siehe README).
 * Jeder Test läuft in einer Transaktion, die am Ende zurückgerollt wird.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(properties = "vitaldash.seed-demo-users=false")
class AuthIntegrationTest {

    private static final String PASSWORD = "geheim1234";

    @Autowired MockMvc mvc;
    @Autowired AppUserRepository users;
    @Autowired PasswordEncoder passwordEncoder;
    @Autowired ObjectMapper json;

    @BeforeEach
    void benutzerAnlegen() {
        users.save(new AppUser("t_arzt", passwordEncoder.encode(PASSWORD), "Test Arzt", Role.ARZT));
        users.save(new AppUser("t_pflege", passwordEncoder.encode(PASSWORD), "Test Pflege", Role.PFLEGEKRAFT));
    }

    private String login(String username, String password) throws Exception {
        String body = json.writeValueAsString(java.util.Map.of("username", username, "password", password));
        return mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(body))
                .andReturn().getResponse().getContentAsString();
    }

    private String token(String username) throws Exception {
        JsonNode node = json.readTree(login(username, PASSWORD));
        return node.get("token").asText();
    }

    @Test
    void loginMitRichtigenDatenLiefertToken() throws Exception {
        String body = json.writeValueAsString(java.util.Map.of("username", "t_arzt", "password", PASSWORD));
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.user.role").value("ARZT"));
    }

    @Test
    void loginMitFalschemPasswortIst401() throws Exception {
        String body = json.writeValueAsString(java.util.Map.of("username", "t_arzt", "password", "falsch"));
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginOhnePflichtfelderIst400() throws Exception {
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void geschuetzterEndpunktOhneTokenIst401() throws Exception {
        mvc.perform(get("/api/users")).andExpect(status().isUnauthorized());
    }

    @Test
    void meLiefertAngemeldetenBenutzer() throws Exception {
        mvc.perform(get("/api/auth/me").header("Authorization", "Bearer " + token("t_pflege")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("t_pflege"))
                .andExpect(jsonPath("$.role").value("PFLEGEKRAFT"));
    }

    @Test
    void pflegekraftDarfBenutzerNichtVerwalten() throws Exception {
        mvc.perform(get("/api/users").header("Authorization", "Bearer " + token("t_pflege")))
                .andExpect(status().isForbidden());
    }

    @Test
    void arztDarfBenutzerAuflistenUndAnlegen() throws Exception {
        String bearer = "Bearer " + token("t_arzt");
        mvc.perform(get("/api/users").header("Authorization", bearer)).andExpect(status().isOk());

        String neu = json.writeValueAsString(java.util.Map.of(
                "username", "t_neu", "password", "passwort123", "displayName", "Neu", "role", "PFLEGEKRAFT"));
        mvc.perform(post("/api/users").header("Authorization", bearer)
                        .contentType(MediaType.APPLICATION_JSON).content(neu))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("t_neu"));
        mvc.perform(post("/api/users").header("Authorization", bearer)
                        .contentType(MediaType.APPLICATION_JSON).content(neu))
                .andExpect(status().isConflict());
    }
}
