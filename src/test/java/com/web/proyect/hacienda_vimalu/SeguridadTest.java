package com.web.proyect.hacienda_vimalu;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import java.nio.file.Path;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties={"spring.datasource.url=jdbc:h2:mem:security;MODE=Oracle;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver", "spring.datasource.username=sa", "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop", "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect", "spring.jpa.properties.hibernate.default_schema=PUBLIC",
        "spring.jpa.open-in-view=false", "spring.jpa.show-sql=false", "logging.level.root=ERROR",
        "security.jwt.secret-key=MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY="})
@AutoConfigureMockMvc
class SeguridadTest {
    @TempDir static Path images;
    @DynamicPropertySource static void storage(DynamicPropertyRegistry registry) { registry.add("storage.location", images::toString); }
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @Autowired JdbcTemplate db;
    @Autowired PasswordEncoder encoder;
    long persona, otraPersona, salon, mesa, otraMesa;
    String cliente, admin;

    JsonNode postJson(String url, ObjectNode body, String token, int status) throws Exception {
        var request = post(url).contentType("application/json").content(body.toString());
        if (token != null) request.header("Authorization", "Bearer " + token);
        String response = mvc.perform(request).andExpect(status().is(status)).andReturn().getResponse().getContentAsString();
        return response.isEmpty() ? json.nullNode() : json.readTree(response);
    }
    ObjectNode registro(String usuario, String dni) {
        return json.createObjectNode().put("nombre", usuario).put("dni", dni).put("email", usuario + "@test.com")
                .put("telefono", "999999999").put("usuario", usuario).put("password", "clave123");
    }
    String login(String usuario) throws Exception {
        return postJson("/api/v1/auth/login", json.createObjectNode().put("usuario", usuario).put("password", "clave123"), null, 200).get("token").asText();
    }
    @BeforeEach void setup() throws Exception {
        for (String table : new String[]{"pago", "detalle_reserva", "reserva", "mesa", "salon", "usuario", "persona", "producto"}) db.update("delete from " + table);
        persona = postJson("/api/v1/auth/registro", registro("cliente", "11111111"), null, 201).get("idPersona").asLong();
        otraPersona = postJson("/api/v1/auth/registro", registro("admin", "22222222"), null, 201).get("idPersona").asLong();
        db.update("update usuario set rol='ADMIN' where usuario='admin'");
        cliente = login("cliente"); admin = login("admin");
        salon = postJson("/api/salones", json.createObjectNode().put("nombre", "Principal")
                .put("estado", "DISPONIBLE"), admin, 200).get("idSalon").asLong();
        for(int i=0;i<4;i++){
            long id=postJson("/api/mesas",json.createObjectNode().put("idSalon",salon).put("nombre","Mesa "+i).put("capacidad",8),admin,200).get("idMesa").asLong();
            if(i==0) mesa=id;
            if(i==1) otraMesa=id;
        }
    }
    ObjectNode reserva(long idPersona) {
        return json.createObjectNode().put("idPersona", idPersona).put("idMesa", mesa)
                .put("fechaReserva", "2027-12-20").put("horaReserva", "13:30").put("cantidadPersonas", 5);
    }
    @Test void registroGuardaBCryptYLoginValidaClave() throws Exception {
        String hash = db.queryForObject("select clave from usuario where usuario='cliente'", String.class);
        assertThat(hash).isNotEqualTo("clave123"); assertThat(encoder.matches("clave123", hash)).isTrue();
        postJson("/api/v1/auth/login", json.createObjectNode().put("usuario", "cliente").put("password", "incorrecta"), null, 401);
        postJson("/api/v1/auth/registro", registro("cliente", "11111111"), null, 409);
    }
    @Test void catalogoSwaggerYAutorizacion() throws Exception {
        mvc.perform(get("/api/productos")).andExpect(status().isOk());
        mvc.perform(get("/api/salones")).andExpect(status().isOk());
        mvc.perform(get("/v3/api-docs")).andExpect(status().isOk()).andExpect(jsonPath("components.securitySchemes.BearerAuth.scheme").value("bearer"));
        mvc.perform(get("/swagger-ui/index.html")).andExpect(status().isOk());
        var producto = json.createObjectNode().put("nombre", "Plato").put("precio", 20).put("stock", 5);
        postJson("/api/productos", producto, null, 401);
        postJson("/api/productos", producto, cliente, 403);
        postJson("/api/productos", producto, admin, 201);
        mvc.perform(get("/api/pagos").header("Authorization", "Bearer " + cliente)).andExpect(status().isForbidden());
    }
    @Test void clienteSoloUsaSusReservasYAdminFinaliza() throws Exception {
        long id = postJson("/api/reservas", reserva(persona), cliente, 201).get("reserva").get("idReserva").asLong();
        postJson("/api/reservas", reserva(otraPersona), cliente, 403);
        long ajena = postJson("/api/reservas", reserva(otraPersona).put("idMesa",otraMesa), admin, 201).get("reserva").get("idReserva").asLong();
        mvc.perform(get("/api/reservas").header("Authorization", "Bearer " + cliente)).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
        mvc.perform(get("/api/reservas/" + ajena).header("Authorization", "Bearer " + cliente)).andExpect(status().isForbidden());
        ObjectNode cambio = reserva(persona).put("estadoReserva", "TERMINADO");
        mvc.perform(put("/api/reservas/" + id).header("Authorization", "Bearer " + cliente).contentType("application/json").content(cambio.toString())).andExpect(status().isForbidden());
        mvc.perform(put("/api/reservas/" + id).header("Authorization", "Bearer " + admin).contentType("application/json").content(cambio.toString())).andExpect(status().isOk());
        mvc.perform(get("/api/salones/" + salon).param("fecha", "2027-12-20")).andExpect(jsonPath("mdisponible").value(3));
    }
    @Test void tokenInvalidoYExpiradoDevuelven401() throws Exception {
        mvc.perform(get("/api/reservas")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/reservas").header("Authorization", "Bearer incorrecto")).andExpect(status().isUnauthorized());
        String expired = io.jsonwebtoken.Jwts.builder().setSubject("cliente").setExpiration(new java.util.Date(0))
                .signWith(io.jsonwebtoken.security.Keys.hmacShaKeyFor(io.jsonwebtoken.io.Decoders.BASE64.decode("MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY="))).compact();
        mvc.perform(get("/api/reservas").header("Authorization", "Bearer " + expired)).andExpect(status().isUnauthorized());
    }
}
