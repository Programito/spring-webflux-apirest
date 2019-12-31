package com.progra.springboot.webflux.app;

//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//import org.springframework.http.MediaType;

import org.springframework.web.reactive.function.server.RouterFunction;

import org.springframework.web.reactive.function.server.ServerResponse;

import com.progra.springboot.webflux.app.handler.ProductoHandler;
//import com.progra.springboot.webflux.app.models.documents.Producto;
//import com.progra.springboot.webflux.app.models.services.ProductoService;


import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class RouterFunctionConfig {

//	@Autowired
//	private ProductoService service;
	
//	@Bean
//	public RouterFunction<ServerResponse> routes(){
//		// return RouterFunctions.route(RequestPredicates.GET("/api/v2/productos"),request->{
//		return route(GET("/api/v2/productos").or(GET("api/v3/productos")),request->{
//			return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
//					.body(service.findAll(), Producto.class);
//		});
//	}
	
	// desaclopar el handler en otra funcion
	@Bean
	public RouterFunction<ServerResponse> routes(ProductoHandler handler){
		return route(GET("/api/v2/productos").or(GET("api/v3/productos")),handler::listar)
				.andRoute(GET("/api/v2/productos/{id}"), handler::ver)
				.andRoute(POST("/api/v2/productos"),handler::crear)
				.andRoute(PUT("/api/v2/productos/{id}"), handler::editar)
				.andRoute(DELETE("/api/v2/productos/{id}"), handler::eliminar)
				.andRoute(POST("/api/v2/productos/upload/{id}"),handler::upload)
				.andRoute(POST("/api/v2/productos/crear"),handler::crearConFoto);
	}
	
	
	
}
