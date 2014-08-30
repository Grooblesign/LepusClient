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

public class Application extends Controller {

    public static Result index() {
        return ok(index.render("Project Lepus"));
    }

	public static Promise<Result> testUserAuthentication() {
		
		String serviceUrl = "http://localhost:8080/lepusservices/rest/authentication/user";

		DynamicForm requestData = Form.form().bindFromRequest();
		
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
	}
}
