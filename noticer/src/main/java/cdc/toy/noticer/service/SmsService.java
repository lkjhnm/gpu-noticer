package cdc.toy.noticer.service;

import cdc.toy.noticer.entity.SmsBody;
import cdc.toy.noticer.entity.SmsResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

@Service
public class SmsService {

	public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	private final String ACCESS_KEY = "f4uamT8jfeh53F9kDGJZ";

	private final String secretKey = System.getenv("SMS_SECRET_KEY");

	@Value("${ncp.sms.api.url}")
	private String api;

	@Value("${ncp.sms.service.id}")
	private String serviceId;

	public Mono<SmsResponse> sendMessage(String contents) {
		try {
			String timestamp = String.valueOf(new Date().getTime());
			URI uri = new URI(String.format(api, serviceId));
			String body = OBJECT_MAPPER.writeValueAsString(new SmsBody(contents, "01090238852"));
			String signature = makeSignature(uri.getPath(), "POST", timestamp, ACCESS_KEY);
			return buildClient(timestamp, uri, signature, body)
					.exchangeToMono(response -> response.bodyToMono(SmsResponse.class));
		} catch (Throwable t) {
			return Mono.error(t);
		}
	}

	private WebClient.RequestHeadersSpec<?> buildClient(String timestamp, URI uri, String signature, String body) {
		return WebClient.create()
		                .post()
		                .uri(uri)
		                .contentType(MediaType.APPLICATION_JSON)
		                .accept(MediaType.APPLICATION_JSON)
		                .header("x-ncp-apigw-timestamp", timestamp)
		                .header("x-ncp-iam-access-key", ACCESS_KEY)
		                .header("x-ncp-apigw-signature-v2", signature)
		                .bodyValue(body);
	}

	public String makeSignature(String uri, String method, String timestamp, String accessKey)
			throws Throwable {
		String space = " ";                    // one space
		String newLine = "\n";                    // new line
		String message = new StringBuilder()
				.append(method)
				.append(space)
				.append(uri)
				.append(newLine)
				.append(timestamp)
				.append(newLine)
				.append(accessKey)
				.toString();
		SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes("UTF-8"), "HmacSHA256");
		Mac mac = Mac.getInstance("HmacSHA256");
		mac.init(signingKey);
		byte[] rawHmac = mac.doFinal(message.getBytes("UTF-8"));
		String encodeBase64String = Base64Utils.encodeToString(rawHmac);
		return encodeBase64String;
	}
}
