package project_idea.idea.tools;

import kong.unirest.core.HttpResponse;
import kong.unirest.core.JsonNode;
import kong.unirest.core.Unirest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import project_idea.idea.entities.User;

@Component
public class MailgunSender {
	private String apiKey;
	private String domain;

	public MailgunSender(@Value("${mailgun.apikey}") String apiKey,
	                     @Value("${mailgun.domain}") String domain) {
		this.apiKey = apiKey;
		this.domain = domain;
	}

	public void sendRegistrationEmail(User recipient) {
		HttpResponse<JsonNode> response = Unirest.post("https://api.mailgun.net/v3/" + this.domain + "/messages")
				.basicAuth("api", this.apiKey)
				.queryString("from", "dev@dev.com")
				.queryString("to", recipient.getEmail())
				.queryString("subject", "Registration completed!")
				.queryString("text", "Welcome " + recipient.getSocialProfile().getFirstName() + " to our platform!")
				.asJson();
	}
}
