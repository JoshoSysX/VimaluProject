package com.web.proyect.hacienda_vimalu;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.jdbc.core.JdbcTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.*;
import java.io.ByteArrayOutputStream;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest(properties={"spring.datasource.url=jdbc:h2:mem:images;MODE=Oracle;DB_CLOSE_DELAY=-1", "spring.datasource.driver-class-name=org.h2.Driver", "spring.datasource.username=sa", "spring.datasource.password=", "spring.jpa.hibernate.ddl-auto=create-drop", "spring.jpa.open-in-view=false", "logging.level.root=ERROR", "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect", "spring.jpa.properties.hibernate.default_schema=PUBLIC", "spring.jpa.show-sql=false", "server.servlet.context-path=/hacienda_vimalu"})
@AutoConfigureMockMvc
@org.springframework.security.test.context.support.WithMockUser(authorities="ADMIN")
class ImagenesTest {
 @TempDir static Path directory;
 @DynamicPropertySource static void properties(DynamicPropertyRegistry registry){registry.add("storage.location",directory::toString);}
 @Autowired MockMvc mvc; @Autowired ObjectMapper json; @Autowired JdbcTemplate db;
 byte[] png()throws Exception {var out=new ByteArrayOutputStream();ImageIO.write(new BufferedImage(2,2,BufferedImage.TYPE_INT_RGB),"png",out);return out.toByteArray();}
 org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder form(String route){var request=multipart(route);request.param("nombre","Plato").param("descripcion","Comida").param("categoria","Menu").param("precio","20.00").param("stock","10");return request;}
 String upload()throws Exception {return mvc.perform(form("/api/productos").file(new MockMultipartFile("file","../../foto.png","image/png",png())))
     .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();}
 String filename(String url){return url.substring(url.lastIndexOf('/')+1);}
 @BeforeEach void limpiar(){db.update("delete from detalle_reserva");db.update("delete from reserva");db.update("delete from producto");}
 @Test void subirLeerReemplazarYEliminar()throws Exception{
  var result=json.readTree(upload());long id=result.get("idProducto").asLong();String url=result.get("imagen").asText();
  assertThat(url).startsWith("/hacienda_vimalu/images/");Path old=directory.resolve(filename(url));assertThat(old).exists();
  String stored=db.queryForObject("select imagen from producto where id_producto=?",String.class,id);assertThat(stored).isEqualTo(filename(url));
  mvc.perform(get(url).contextPath("/hacienda_vimalu")).andExpect(status().isOk()).andExpect(content().bytes(png()));
  var edit=form("/api/productos/"+id);edit.with(request->{request.setMethod("PUT");return request;});
  mvc.perform(edit).andExpect(status().isOk()).andExpect(jsonPath("imagen").value(url));assertThat(old).exists();
  var replace=form("/api/productos/"+id).file(new MockMultipartFile("file","otra.png","image/png",png()));replace.with(request->{request.setMethod("PUT");return request;});
  String next=json.readTree(mvc.perform(replace).andExpect(status().isOk()).andReturn().getResponse().getContentAsString()).get("imagen").asText();
  assertThat(old).doesNotExist();Path current=directory.resolve(filename(next));assertThat(current).exists();
  mvc.perform(delete("/api/productos/"+id)).andExpect(status().isNoContent());assertThat(current).doesNotExist();
 }
 @Test void sinImagenYJsonSiguenFuncionando()throws Exception{
  mvc.perform(form("/api/productos")).andExpect(status().isCreated()).andExpect(jsonPath("imagen").doesNotExist());
  var r=json.readTree(upload());long id=r.get("idProducto").asLong();String url=r.get("imagen").asText();
  mvc.perform(put("/api/productos/"+id).contentType("application/json").content("{\"nombre\":\"Editado\",\"precio\":30,\"stock\":9,\"imagen\":\"../../otro.png\"}"))
      .andExpect(status().isOk()).andExpect(jsonPath("imagen").value(url));
  assertThat(directory.resolve(filename(url))).exists();
 }
 @Test void rechazaArchivoFalsoYDemasiadoGrande()throws Exception{
  mvc.perform(form("/api/productos").file(new MockMultipartFile("file","fake.png","image/png","no es imagen".getBytes())))
      .andExpect(status().isConflict());
  mvc.perform(form("/api/productos").file(new MockMultipartFile("file","big.png","image/png",new byte[1024*1024+1])))
      .andExpect(status().isConflict());
  assertThat(db.queryForObject("select count(*) from producto",Integer.class)).isZero();
 }
 @Test void productoInexistenteNoGuardaArchivo()throws Exception{
  long before;try(var files=Files.list(directory)){before=files.count();}
  var req=form("/api/productos/999999").file(new MockMultipartFile("file","x.png","image/png",png()));req.with(r->{r.setMethod("PUT");return r;});
  mvc.perform(req).andExpect(status().isNotFound());
  try(var files=Files.list(directory)){assertThat(files.count()).isEqualTo(before);}
 }
 @Test void corsAngular()throws Exception{
  mvc.perform(options("/api/productos").header("Origin","http://localhost:4200")
      .header("Access-Control-Request-Method","POST").header("Access-Control-Request-Headers","content-type"))
      .andExpect(status().isOk()).andExpect(header().string("Access-Control-Allow-Origin","http://localhost:4200"));
  mvc.perform(options("/api/productos").header("Origin","https://otro.example").header("Access-Control-Request-Method","POST"))
      .andExpect(status().isForbidden());
 }
}