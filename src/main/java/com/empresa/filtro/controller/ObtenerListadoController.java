package com.empresa.filtro.controller;


import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;



@RestController
@RequestMapping("/api")
public class ObtenerListadoController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ObtenerListadoController.class);
	
	// Token por defecto, obtenido para un nuevo usuario dado de alta. Como el token no expira, se puede utilizar
	private String token="teQunYUyT/T2DT6A1mL2hZcvej+t6A0sTVRFTL6+wmBZ9xjCBCeIa7vkHbisls64hiOb1fkoPvwoeGLGwEmM3A==";

	/**
	 * Recupera toda la colección de la API externa
	 * @return
	 */
	@GetMapping(value = "/listado")
	public ResponseEntity<String> getListado() {
		
		HttpResponse<String> response = null;
		try {
			var client = HttpClient.newHttpClient();
			var request = HttpRequest.newBuilder()
		            .uri(new URI("https://unsplash.com/collections"))
		            .header("Authorization", token)
		            .header("accept", "application/json")
		            .build();
		    response = client.send(request, HttpResponse.BodyHandlers.ofString());
		}
		catch (URISyntaxException | IOException | InterruptedException e) {
			e.printStackTrace();
			LOGGER.error("Error al conectar con API");
		}
		
		// Parsea el json que viene en el HTML recuperado de la llamada
		String[] json = response.body().split("window.__INITIAL_STATE__ =");
		String[] json2 = json[1].split("</script>");
		String json3 = json2[0].replace("JSON.parse(", "");
		String json4 = json3.substring(0, json3.length()-3);
		String json5 = json4.replaceAll("\\\\", "").replaceFirst("\"", "");

		StringBuffer resultado = new StringBuffer();
		try {
		     
		     ObjectMapper mapper = new ObjectMapper();
		     // Lee el árbol JSON
		     JsonNode actualObj = mapper.readTree(json5);
		     List<JsonNode> listaFiltrada = new ArrayList<JsonNode>();
		     // Obtiene el nodo entities
		     JsonNode entities = actualObj.get("entities");
		     // Obtiene el nodo collections
		     JsonNode colecciones = entities.path("collections");
		     // Recorre la lista de colecciones y lo añade a la lista final
		     colecciones.forEach(nodo -> {
    			ObjectNode parentNode = mapper.createObjectNode(); 
		    	 parentNode.set(nodo.get("id").asText(), nodo);
    			listaFiltrada.add(parentNode);
		     });
		     
		     // Va concatenando el texto de los nodos
		     listaFiltrada.stream().forEach(nodo -> {
		    	 resultado.append(nodo.toPrettyString());
		     });
		     LOGGER.info("Json creado");
		}catch (JSONException | JsonProcessingException err){
			err.printStackTrace();
			LOGGER.error("Error", err.toString());
		}
		
		return ResponseEntity.ok(resultado.toString());

	}
	
	
	
	/**
	 * Recupera la colección de la API externa y se filtren los datos obtenidos en función del parámetro filter
	 * @param filtro
	 * @return
	 */
	@GetMapping(value = "/listadoreducido/{filtro}")
	public ResponseEntity<String> getListado(@PathVariable(required = false) String filtro) {
		
		HttpResponse<String> response = null;
		try {
			var client = HttpClient.newHttpClient();
			var request = HttpRequest.newBuilder()
					.GET()
		            .header("Authorization", token)
		            .header("accept", "application/json")
		            .uri(new URI("https://unsplash.com/collections"))
		            .build();
		    response = client.send(request, HttpResponse.BodyHandlers.ofString());
		}
		catch (URISyntaxException | IOException | InterruptedException e) {
			e.printStackTrace();
			LOGGER.error("Error al conectar con API");
		}
		
		// Parsea el json que viene en el HTML recuperado de la llamada  
		String[] json = response.body().split("window.__INITIAL_STATE__ =");
		String[] json2 = json[1].split("</script>");
		String json3 = json2[0].replace("JSON.parse(", "");
		String json4 = json3.substring(0, json3.length()-3);
		String json5 = json4.replaceAll("\\\\", "").replaceFirst("\"", "");

		StringBuffer resultado = new StringBuffer();
		try {
		     
		     ObjectMapper mapper = new ObjectMapper();
		     // Lee el árbol JSON
		     JsonNode actualObj = mapper.readTree(json5);
		     List<JsonNode> listaFiltrada = new ArrayList<JsonNode>();
		     // Obtiene el nodo entities
		     JsonNode entities = actualObj.get("entities");
		     // Obtiene el nodo collections
		     JsonNode colecciones = entities.path("collections");
		     
		     // Recorre la lista de colecciones y lo añade a la lista final si coincide con el filtro introducido
		     colecciones.forEach(nodo -> {
		    	 // Sólo se aplica el filtro a los campos id, description, title y coverPhotoId, por tanto se recuperan esos
		    	 // nodos para trabajar sobre ellos
		    	 JsonNode nodoId = nodo.path("id");
		    	 JsonNode nodoDescripcion = nodo.path("description");
		    	 JsonNode nodoTitle = nodo.path("title");
		    	 JsonNode nodoCoverPhotoId = nodo.path("coverPhotoId");
		    	 
		    	if (nodoDescripcion!=null) {
		    		// Si cualquiera de los nodos: id, descripción, title o coverPhotoId contiene el texto buscado
		    		if (nodoId.asText().contains(filtro) || nodoDescripcion.asText().contains(filtro) || 
		    				nodoTitle.asText().contains(filtro) || nodoCoverPhotoId.asText().contains(filtro)) {
		    			ObjectNode parentNode = mapper.createObjectNode(); 
				    	 parentNode.set(nodo.get("id").asText(), nodo);
		    			listaFiltrada.add(parentNode);
		    		}
		    	}
		     });
		     
		     // Va concatenando el texto de los nodos
		     listaFiltrada.stream().forEach(nodo -> {
		    	 resultado.append(nodo.toPrettyString());
		     });
		     LOGGER.info("Json creado");
		}catch (JSONException | JsonProcessingException err){
			err.printStackTrace();
			LOGGER.error("Error", err.toString());
		}
		
		return ResponseEntity.ok(resultado.toString());

	}
	
	/**
	 * Prueba con página de inicio y llamada ajax a la autenticación por GET
	 * @return
	 */
	@GetMapping(value = "/inicio")
	public ModelAndView inicio() {
		ModelAndView modelAndView = new ModelAndView();
	    modelAndView.setViewName("index");
	    return modelAndView;
	}
	

	/**
	 * Realiza la autenticación y la obtención del token (pasos 1 y 3 del workflow de autorización descrito en la página)
	 * @param session
	 * @param request
	 * @param response
	 */
	@GetMapping(value = "/login")
	public void login(HttpSession session, HttpServletRequest request,  
			HttpServletResponse response) {
		token = getJWT();
	}
	
	
	
	
	/**
	 * Implementa el paso 1 del workflow de autorización
	 * @return
	 */
	@GetMapping(value = "/autorizacion")
	private HttpResponse<String> autorizacion() {
		HttpResponse<String> response = null;

		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(
				"https://unsplash.com/oauth/authorize?client_id=sXP7aVJMe2YiFd9T5zm58vRTg-UZ7NE4bmwF-jvczMY&redirect_uri=http://localhost:8001/filtrado/api/listado&response_type=code&scope=public"))
				.build();
		try {
			response = client.send(request, HttpResponse.BodyHandlers.ofString());

		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}

		return response;
	}
	
	
	/**
	 * Método que obtiene un token válido
	 * client_id y client_secret se han obtenido de un nuevo usuario dado de alta. Si probamos con el usuario/pwd proporcionado
	 * en el enunciado, no funciona
	 * @return
	 */
	private String getJWT() {
		String resultado = "";

		// Paso 1 del workflow de autorización
		autorizacion();

		try {

			var values = new HashMap<String, String>() {
				{
					put("client_id", "sXP7aVJMe2YiFd9T5zm58vRTg-UZ7NE4bmwF-jvczMY");
					put("client_secret", "QHicB4XPQCQFsyRxRPZrOgnPHXjHJ50oW0KWRuo9vJk");
					put("redirect_uri", "http://localhost:8001/filtrado/api/listado");
					put("code", "code");
					put("grant_type", "authorization_code");
				}
			};

			var objectMapper = new ObjectMapper();
			String requestBody = objectMapper.writeValueAsString(values);

			HttpClient client = HttpClient.newHttpClient();
			HttpRequest request = HttpRequest.newBuilder().header("Content-Type", "application/json")
					.uri(URI.create("https://unsplash.com/oauth/token"))
					.POST(HttpRequest.BodyPublishers.ofString(requestBody)).build();

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

			resultado = response.body();
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return resultado;
	}
	
}
