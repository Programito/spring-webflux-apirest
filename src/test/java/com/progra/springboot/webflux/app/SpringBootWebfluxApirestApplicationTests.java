package com.progra.springboot.webflux.app;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.progra.springboot.webflux.app.models.documents.Categoria;
import com.progra.springboot.webflux.app.models.documents.Producto;
import com.progra.springboot.webflux.app.models.services.ProductoService;

import reactor.core.publisher.Mono;


// crea un servidor
// @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

// simula el servidor
@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class SpringBootWebfluxApirestApplicationTests {

	@Autowired
	private WebTestClient client;
	
	@Autowired
	private ProductoService service;
	
	@Value("${config.base.endpoint}")
	private String url;
	
	@Test
	public void listarTest() {
		
		client.get()
			.uri(url)
			.accept(MediaType.APPLICATION_JSON)
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(MediaType.APPLICATION_JSON)
			.expectBodyList(Producto.class)
			.consumeWith(response -> {
				List<Producto> productos = response.getResponseBody();
				productos.forEach(p->{
					System.out.println(p.getNombre());
				});
				
				Assertions.assertThat(productos.size()>0).isTrue();
			});
			//.hasSize(11);
	}
	
	@Test
	public void verTest() {
		
		// el block arriba o dentro del get 
		// porque por pruebas unitarias tiene que ser sincrono
		// Mono<Producto> producto= service.findByNombre("Tempest MS100 Paladin Ratón Gaming 1600DPI");
		// .uri("/api/v2/productos/{id}", Collections.singletonMap("id", producto.block().getId()))
		Producto producto = service.findByNombre("Tempest MS100 Paladin Ratón Gaming 1600DPI").block();
		
	
		client.get()
			.uri(url + "/{id}", Collections.singletonMap("id", producto.getId()))
			.accept(MediaType.APPLICATION_JSON)
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(MediaType.APPLICATION_JSON)
			.expectBody(Producto.class)
			.consumeWith(response -> {
				Producto p = response.getResponseBody();
			
				Assertions.assertThat(p.getId()).isNotEmpty();
				Assertions.assertThat(p.getId().length()>0).isTrue();
				Assertions.assertThat(p.getNombre()).isEqualTo("Tempest MS100 Paladin Ratón Gaming 1600DPI");
				
			});
//			.expectBody()
//			.jsonPath("$.id").isNotEmpty()
//			.jsonPath("$.nombre").isEqualTo("Tempest MS100 Paladin Ratón Gaming 1600DPI");
	}
	
	
	// usando la forma json
	// HANDLER
	@Test
	public void crearTest() {
		
		Categoria categoria = service.findCategoriaByNombre("Mouse").block();
		
		Producto producto = new Producto("raton test", 100.00, categoria);
		// contentType request accept response
		client.post().uri(url)
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
			.body(Mono.just(producto), Producto.class)
			.exchange()
			.expectStatus().isCreated()
			.expectHeader().contentType(MediaType.APPLICATION_JSON)
			.expectBody()
			.jsonPath("$.id").isNotEmpty()
			.jsonPath("$.nombre").isEqualTo("raton test")
			.jsonPath("$.categoria.nombre").isEqualTo("Mouse");
	}
	
	// API REST
	@Test
	public void crearTest3() {
		
		Categoria categoria = service.findCategoriaByNombre("Mouse").block();
		
		Producto producto = new Producto("raton test", 100.00, categoria);
		// contentType request accept response
		client.post().uri("/api/productos")
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
			.body(Mono.just(producto), Producto.class)
			.exchange()
			.expectStatus().isCreated()
			//.expectHeader().contentType(MediaType.APPLICATION_JSON)
			.expectBody()
			.jsonPath("$.producto.id").isNotEmpty()
			.jsonPath("$.producto.nombre").isEqualTo("raton test")
			.jsonPath("$.producto.categoria.nombre").isEqualTo("Mouse");
	}
	
	
	// forma del objeto producto
	// Handler
	@Test
	public void crear2Test() {
		
		Categoria categoria = service.findCategoriaByNombre("Mouse").block();
		
		Producto producto = new Producto("raton test", 100.00, categoria);
		// contentType request accept response
		client.post().uri(url)
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
			.body(Mono.just(producto), Producto.class)
			.exchange()
			.expectStatus().isCreated()
			.expectHeader().contentType(MediaType.APPLICATION_JSON)
			.expectBody(Producto.class)
			.consumeWith(response->{
				Producto p = response.getResponseBody();
				Assertions.assertThat(p.getId()).isNotEmpty();
				Assertions.assertThat(p.getNombre()).isEqualTo("raton test");
				Assertions.assertThat(p.getCategoria().getNombre()).isEqualTo("Mouse");
			});
	}
	
	// API REST
	@Test
	public void crear4Test() {
		
		Categoria categoria = service.findCategoriaByNombre("Mouse").block();
		
		Producto producto = new Producto("raton test", 100.00, categoria);
		// contentType request accept response
		client.post().uri("/api/productos")
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
			.body(Mono.just(producto), Producto.class)
			.exchange()
			.expectStatus().isCreated()
			//.expectHeader().contentType(MediaType.APPLICATION_JSON)
			.expectBody(new ParameterizedTypeReference<LinkedHashMap<String, Object>>() {
			})
			.consumeWith(response->{
				Object o = response.getResponseBody().get("producto");
				Producto p = new ObjectMapper().convertValue(o, Producto.class);
				Assertions.assertThat(p.getId()).isNotEmpty();
				Assertions.assertThat(p.getNombre()).isEqualTo("raton test");
				Assertions.assertThat(p.getCategoria().getNombre()).isEqualTo("Mouse");
			});
	}
	
	@Test
	public void editarTest() {
		Producto producto = service.findByNombre("Logitech MX Master 2S Ratón Inalámbrico Negro 1000DPI").block();
		
		Categoria categoria = service.findCategoriaByNombre("Impresora").block();
		
		Producto productoEditado = new Producto("Impresora test", 50.00, categoria);
		
		client.put().uri(url + "/{id}", Collections.singletonMap("id", producto.getId()))
		.contentType(MediaType.APPLICATION_JSON)
		.accept(MediaType.APPLICATION_JSON)
		.body(Mono.just(productoEditado), Producto.class)
		.exchange()
		.expectStatus().isCreated()
		.expectHeader().contentType(MediaType.APPLICATION_JSON)
		.expectBody()
		.jsonPath("$.id").isNotEmpty()
		.jsonPath("$.nombre").isEqualTo("Impresora test")
		.jsonPath("$.categoria.nombre").isEqualTo("Impresora");
	}
	
	@Test
	public void eliminarTest() {
		Producto producto = service.findByNombre("Xiaomi Redmi Note 8T 4/64GB Blanco Lunar Libre").block();
		
		client.delete()
			.uri(url + "/{id}", Collections.singletonMap("id", producto.getId()))
			.exchange()
			.expectStatus().isNoContent()
			.expectBody()
			.isEmpty();
	
		client.get()
			.uri(url + "/{id}", Collections.singletonMap("id", producto.getId()))
			.exchange()
			.expectStatus().isNotFound()
			.expectBody()
			.isEmpty();
		
	}

}
