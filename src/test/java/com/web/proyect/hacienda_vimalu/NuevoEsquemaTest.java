package com.web.proyect.hacienda_vimalu;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.*;
@SpringBootTest(properties={"spring.datasource.url=jdbc:h2:mem:nuevo;MODE=Oracle;DB_CLOSE_DELAY=-1", "spring.datasource.driver-class-name=org.h2.Driver", "spring.datasource.username=sa", "spring.datasource.password=", "spring.jpa.hibernate.ddl-auto=create-drop", "spring.jpa.open-in-view=false", "logging.level.root=ERROR", "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect", "spring.jpa.properties.hibernate.default_schema=PUBLIC", "spring.jpa.show-sql=false"})
@AutoConfigureMockMvc
@org.springframework.security.test.context.support.WithMockUser(authorities="ADMIN")
class NuevoEsquemaTest {
 @org.junit.jupiter.api.io.TempDir static java.nio.file.Path images;
 @org.springframework.test.context.DynamicPropertySource static void storage(org.springframework.test.context.DynamicPropertyRegistry registry){ registry.add("storage.location", images::toString); }
 @Autowired MockMvc mvc; @Autowired ObjectMapper json; @Autowired JdbcTemplate db;
 long persona,mesa,otraMesa,salon,producto;
 ObjectNode obj(String s)throws Exception{return (ObjectNode)json.readTree(s);}
 JsonNode postBody(String path,ObjectNode body,int status)throws Exception {
  String result=mvc.perform(post(path).contentType("application/json").content(body.toString())).andExpect(status().is(status)).andReturn().getResponse().getContentAsString();
  return result.isEmpty()?json.nullNode():json.readTree(result);
 }
 @BeforeEach void setup()throws Exception{
  for(String table:new String[]{"pago","detalle_reserva","reserva","mesa","salon","usuario","persona","producto"})db.update("delete from "+table);
  persona=postBody("/api/v1/auth/registro",obj("{\"nombre\":\"Ana\",\"dni\":\"12345678\",\"email\":\"ana@test.com\",\"telefono\":\"999999999\",\"usuario\":\"ana123\",\"password\":\"clave123\"}"),201).get("idPersona").asLong();
  salon=postBody("/api/salones",salon(),200).get("idSalon").asLong();
  for(int i=0;i<4;i++){
   long id=postBody("/api/mesas",json.createObjectNode().put("idSalon",salon).put("nombre","Mesa "+i).put("capacidad",i==0?8:4),200).get("idMesa").asLong();
   if(i==0) mesa=id;
   if(i==1) otraMesa=id;
  }
  producto=postBody("/api/productos",obj("{\"nombre\":\"Plato\",\"precio\":20,\"stock\":30}"),201).get("idProducto").asLong();
 }
 ObjectNode salon(){return json.createObjectNode().put("nombre","Principal").put("estado","DISPONIBLE");}
 ObjectNode reserva()throws Exception{return obj("{\"fechaReserva\":\"2026-12-20\",\"horaReserva\":\"13:30\",\"cantidadPersonas\":6}").put("idPersona",persona).put("idMesa",mesa);}
 JsonNode crear()throws Exception{return postBody("/api/reservas",reserva(),201).get("reserva");}
 @Test void comidaStockYPagoSeConservan()throws Exception{
  ObjectNode body=reserva();body.putArray("items").addObject().put("idProducto",producto).put("cantidad",4);
  JsonNode r=postBody("/api/reservas",body,201).get("reserva");long id=r.get("idReserva").asLong();
  assertThat(r.get("total").decimalValue()).isEqualByComparingTo("80");
  assertThat(db.queryForObject("select stock from producto where id_producto=?",Integer.class,producto)).isEqualTo(26);
  long pago=db.queryForObject("select id_pago from pago where id_reserva=?",Long.class,id);
  ObjectNode payment=obj("{\"monto\":25,\"estado\":\"PAGADO\",\"tipoPago\":\"EFECTIVO\"}").put("idReserva",id);
  mvc.perform(put("/api/pagos/"+pago).contentType("application/json").content(payment.toString())).andExpect(status().isOk());
  mvc.perform(get("/api/reservas/"+id)).andExpect(jsonPath("saldoPendiente").value(55));
 }
 @Test void errorStockNoGuardaReservaNiPago()throws Exception{
  ObjectNode body=reserva();body.putArray("items").addObject().put("idProducto",producto).put("cantidad",31);
  postBody("/api/reservas",body,409);assertThat(db.queryForObject("select count(*) from reserva",Integer.class)).isZero();assertThat(db.queryForObject("select count(*) from pago",Integer.class)).isZero();
 }
 @Test void validaHoraSinGuardarDatos()throws Exception{
  for(String hora:new String[]{"24:00","12:60","9:00","13:30:00","2026-12-20"})postBody("/api/reservas",reserva().put("horaReserva",hora),400);
  assertThat(db.queryForObject("select count(*) from reserva",Integer.class)).isZero();
 }
 @Test void pagoInicialNoSeDevuelveNiPermiteSobrecobro()throws Exception{
  long id=crear().get("idReserva").asLong();
  long pago=db.queryForObject("select id_pago from pago where id_reserva=?",Long.class,id);
  ObjectNode body=json.createObjectNode().put("monto",25).put("estado","PAGADO").put("tipoPago","EFECTIVO").put("idReserva",id);
  mvc.perform(put("/api/pagos/"+pago).contentType("application/json").content(body.toString())).andExpect(status().isOk());
  mvc.perform(get("/api/reservas/"+id)).andExpect(jsonPath("saldoPendiente").value(0)).andExpect(jsonPath("totalPagado").value(25));
  postBody("/api/pagos",body,409);
  mvc.perform(delete("/api/pagos/"+pago)).andExpect(status().isConflict());
  mvc.perform(delete("/api/reservas/"+id)).andExpect(status().isConflict());
 }
 @Test void reservasSimultaneasNoVendenMasStock()throws Exception{
  long otro=postBody("/api/salones",salon(),200).get("idSalon").asLong();
  db.update("update producto set stock=1 where id_producto=?",producto);
  long otra=postBody("/api/mesas",json.createObjectNode().put("idSalon",otro).put("nombre","Otra").put("capacidad",8),200).get("idMesa").asLong();
  ObjectNode a=reserva(),b=reserva().put("idMesa",otra);
  a.putArray("items").addObject().put("idProducto",producto).put("cantidad",1);
  b.putArray("items").addObject().put("idProducto",producto).put("cantidad",1);
  var inicio=new java.util.concurrent.CountDownLatch(1);
  try(var pool=java.util.concurrent.Executors.newFixedThreadPool(2)){
   java.util.List<java.util.concurrent.Future<Integer>> results=new java.util.ArrayList<>();
   for(ObjectNode body:new ObjectNode[]{a,b})results.add(pool.submit(()->{
    inicio.await();
    return mvc.perform(post("/api/reservas").with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("admin").authorities(new org.springframework.security.core.authority.SimpleGrantedAuthority("ADMIN"))).contentType("application/json").content(body.toString())).andReturn().getResponse().getStatus();
   }));
   inicio.countDown();
   assertThat(java.util.List.of(results.get(0).get(15,java.util.concurrent.TimeUnit.SECONDS),results.get(1).get(15,java.util.concurrent.TimeUnit.SECONDS))).containsExactlyInAnyOrder(201,409);
  }
  assertThat(db.queryForObject("select stock from producto where id_producto=?",Integer.class,producto)).isZero();
  assertThat(db.queryForObject("select count(*) from reserva",Integer.class)).isEqualTo(1);
 }
 @Test void esquemaSinTipoMesaNiReservaPorSalon()throws Exception{
  mvc.perform(get("/api/salones/"+salon+"?fecha=2026-12-20")).andExpect(status().isOk())
   .andExpect(jsonPath("cantidadMesas").value(4)).andExpect(jsonPath("capacidad").value(20)).andExpect(jsonPath("mdisponible").value(4));
  for(String[] field:new String[][]{{"SALON","ID_TIPO_MESA"},{"RESERVA","ID_SALON"},{"RESERVA","CANTIDAD_MESAS_ASIG"}})
   assertThat(db.queryForObject("select count(*) from information_schema.columns where table_name=? and column_name=?",Integer.class,field[0],field[1])).isZero();
 }
 @Test void reservaOcupaUnaMesaNoTodoElSalon()throws Exception{
  JsonNode r=crear(); long id=r.get("idReserva").asLong();
  assertThat(r.get("idMesa").asLong()).isEqualTo(mesa);
  assertThat(r.has("cantidadMesasAsig")).isFalse();
  assertThat(db.queryForObject("select hora_reserva from reserva where id_reserva=?",String.class,id)).isEqualTo("13:30");
  assertThat(r.get("total").decimalValue()).isEqualByComparingTo("25");
  mvc.perform(get("/api/salones/"+salon+"?fecha=2026-12-20")).andExpect(jsonPath("mdisponible").value(3)).andExpect(jsonPath("capacidadDisponible").value(12));
  mvc.perform(get("/api/mesas/"+mesa+"?fecha=2026-12-20")).andExpect(jsonPath("disponible").value(false));
  postBody("/api/reservas",reserva().put("horaReserva","20:00"),409);
  postBody("/api/reservas",reserva().put("idMesa",otraMesa).put("cantidadPersonas",4),201);
  mvc.perform(get("/api/salones/"+salon+"?fecha=2026-12-21")).andExpect(jsonPath("mdisponible").value(4));
  postBody("/api/reservas",reserva().put("fechaReserva","2026-12-21"),201);
 }
 @Test void capacidadIndividualYSalonCerrado()throws Exception{
  postBody("/api/reservas",reserva().put("cantidadPersonas",9),409);
  postBody("/api/reservas",reserva().put("idMesa",otraMesa).put("cantidadPersonas",5),409);
  mvc.perform(put("/api/salones/"+salon).contentType("application/json").content(salon().put("estado","NO_DISPONIBLE").toString())).andExpect(status().isOk());
  postBody("/api/reservas",reserva(),409);
  mvc.perform(get("/api/mesas/"+mesa)).andExpect(jsonPath("disponible").value(false));
 }
 @Test void finalizarYRechazarLiberanYNoSeReactivaSiEstaOcupada()throws Exception{
  ObjectNode r=(ObjectNode)crear(); long id=r.get("idReserva").asLong();
  r.put("estadoReserva","TERMINADO");
  mvc.perform(put("/api/reservas/"+id).contentType("application/json").content(r.toString())).andExpect(status().isOk());
  ObjectNode siguiente=(ObjectNode)crear();
  r.put("estadoReserva","CONFIRMADO");
  mvc.perform(put("/api/reservas/"+id).contentType("application/json").content(r.toString())).andExpect(status().isConflict());
  siguiente.put("estadoReserva","RECHAZADO");
  mvc.perform(put("/api/reservas/"+siguiente.get("idReserva").asLong()).contentType("application/json").content(siguiente.toString())).andExpect(status().isOk());
  mvc.perform(get("/api/salones/"+salon+"?fecha=2026-12-20")).andExpect(jsonPath("mdisponible").value(4));
 }
 @Test void cantidadMesasSeActualizaConElCrud()throws Exception{
  mvc.perform(delete("/api/mesas/"+otraMesa)).andExpect(status().isNoContent());
  assertThat(db.queryForObject("select cantidad_mesas from salon where id_salon=?",Integer.class,salon)).isEqualTo(3);
  long destino=postBody("/api/salones",salon(),200).get("idSalon").asLong();
  ObjectNode edit=json.createObjectNode().put("idSalon",destino).put("nombre","Trasladada").put("capacidad",8);
  mvc.perform(put("/api/mesas/"+mesa).contentType("application/json").content(edit.toString())).andExpect(status().isOk());
  assertThat(db.queryForObject("select cantidad_mesas from salon where id_salon=?",Integer.class,salon)).isEqualTo(2);
  assertThat(db.queryForObject("select cantidad_mesas from salon where id_salon=?",Integer.class,destino)).isEqualTo(1);
 }
 @Test void mesaReservadaNoSeBorraNiSeReduceCapacidad()throws Exception{
  crear();
  mvc.perform(delete("/api/mesas/"+mesa)).andExpect(status().isConflict());
  mvc.perform(delete("/api/salones/"+salon)).andExpect(status().isConflict());
  ObjectNode edit=json.createObjectNode().put("idSalon",salon).put("nombre","Mesa editada").put("capacidad",4);
  mvc.perform(put("/api/mesas/"+mesa).contentType("application/json").content(edit.toString())).andExpect(status().isConflict());
 }
 @Test void dosClientesNoReservanLaMismaMesaSimultaneamente()throws Exception{
  var inicio=new java.util.concurrent.CountDownLatch(1);
  String body=reserva().toString();
  try(var pool=java.util.concurrent.Executors.newFixedThreadPool(2)){
   java.util.List<java.util.concurrent.Future<Integer>> results=new java.util.ArrayList<>();
   for(int i=0;i<2;i++)results.add(pool.submit(()->{
    inicio.await();
    return mvc.perform(post("/api/reservas").with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("admin").authorities(new org.springframework.security.core.authority.SimpleGrantedAuthority("ADMIN"))).contentType("application/json").content(body)).andReturn().getResponse().getStatus();
   }));
   inicio.countDown();
   assertThat(java.util.List.of(results.get(0).get(15,java.util.concurrent.TimeUnit.SECONDS),results.get(1).get(15,java.util.concurrent.TimeUnit.SECONDS))).containsExactlyInAnyOrder(201,409);
  }
  assertThat(db.queryForObject("select count(*) from reserva",Integer.class)).isEqualTo(1);
 }
}