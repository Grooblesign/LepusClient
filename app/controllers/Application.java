package controllers;

import play.data.Form;
import play.data.DynamicForm;
import play.libs.F.Function;
import play.libs.F.Promise;
import play.mvc.Controller;
import play.mvc.Result;
import play.*;
import play.mvc.*;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;
import views.html.*;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;
import org.apache.commons.httpclient.params.HttpMethodParams;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Application extends Controller {

    public static Result index() {
        return ok(index.render("Project Lepus"));
    }

	public static Result testUserAuthentication() {
		
		String serviceUrl = "http://localhost:8080/lepusservices/rest/authentication/user";
		// String serviceUrl = "http://10.22.99.182/lepusservices/rest/authentication/user";

		DynamicForm requestData = Form.form().bindFromRequest();

		HttpClient client = new HttpClient();
		PostMethod method = new PostMethod(serviceUrl);
		
		// As form parameters
		// method.addParameter("username", requestData.get("username"));
		// method.addParameter("password", requestData.get("password"));
		
		// As json
		String json = String.format("{\"username\": \"%s\", \"password\": \"%s\"}", requestData.get("username"), requestData.get("password"));
		try {
			StringRequestEntity requestEntity = new StringRequestEntity(
				json,
				"application/json",
				"UTF-8");
			method.setRequestEntity(requestEntity);			
		} catch (Exception e) {
			System.err.println(e);
		}	
		
		method.addRequestHeader("Accept", "application/json");
	
		int statusCode = 0;
		String body = "";
		
		try {
			statusCode = client.executeMethod(method);
			body = new String(method.getResponseBody());
		} catch (Exception e) {
			System.err.println(e);
		} finally {
			method.releaseConnection();	
		}			

		Result result = null;
		
		if (statusCode == 200) {
			result = ok(authenticated.render(body));
		} else {
			result = ok(notAuthenticated.render(String.format("%s", method.getStatusLine())));
		}
		
		return result;
		
		/*
		System.setProperty("http.proxyHost", "proxy-apac.my-it-solutions.net");
		System.setProperty("http.proxyPort", "84");

		
		String postContent = String.format("username=%s&password=%s", 
				requestData.get("username"), 
				requestData.get("password"));
		
		final Promise<Result> resultPromise = WS.url(serviceUrl)
				.setContentType("application/x-www-form-urlencoded; charset=utf-8") 
				.post(postContent)
				.map(new Function<WSResponse, Result>() {
					public Result apply(WSResponse response) {
						if (response.getStatus() == 200) {
							return ok(authenticated.render(response.getBody()));
						} else {
							return ok(notAuthenticated.render(String.format("%s - %s", response.getStatus(), response.getStatusText())));
						}
					}
				});
				
		return resultPromise;
		*/
	}
}
