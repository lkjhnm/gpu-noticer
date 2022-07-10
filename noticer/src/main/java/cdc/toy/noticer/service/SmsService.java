package cdc.toy.noticer.service;

import cdc.toy.noticer.entity.SmsBody;
import cdc.toy.noticer.entity.SmsRequestEntity;
import cdc.toy.noticer.entity.SmsResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.annotation.PostConstruct;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.Date;
import java.util.List;

@Service
public class SmsService {

	private final String ACCESS_KEY = "f4uamT8jfeh53F9kDGJZ";

	private final String secretKey = System.getenv("SMS_SECRET_KEY");

	@Value("${ncp.sms.api.url}")
	private String api;

	@Value("${ncp.sms.service.id}")
	private String serviceId;
	private URI smsUri;

	@PostConstruct
	private void init() throws URISyntaxException {
		smsUri = new URI(String.format(api, serviceId));
	}

	public Mono<SmsResponse> sendMessage(String contents) {
		try {
			String timestamp = String.valueOf(new Date().getTime());
			String signature = makeSignature(smsUri.getPath(), "POST", timestamp, ACCESS_KEY);
			return buildRequestEntity(smsUri, timestamp, signature, contents)
					.map(this::buildClient)
					.flatMap(v -> v.exchangeToMono(response -> response.bodyToMono(SmsResponse.class)));
		} catch (Throwable t) {
			return Mono.error(t);
		}
	}

	private Mono<SmsRequestEntity> buildRequestEntity
			(URI uri,
			 String timestamp,
			 String signature,
			 String contents) {
		return Mono.fromCallable(() ->
				new SmsRequestEntity(uri, timestamp, signature, new SmsBody(contents, getPhoneNumbers())
				)).subscribeOn(Schedulers.boundedElastic());
	}

	private List<String> getPhoneNumbers() throws IOException {
		return Files.readAllLines(new ClassPathResource("/SMS_PHONE_BOOK").getFile().toPath());
	}

	private WebClient.RequestHeadersSpec<?> buildClient(SmsRequestEntity requestEntity) {
		return WebClient.create()
		                .post()
		                .uri(requestEntity.getUri())
		                .contentType(MediaType.APPLICATION_JSON)
		                .accept(MediaType.APPLICATION_JSON)
		                .header("x-ncp-apigw-timestamp", requestEntity.getTimestamp())
		                .header("x-ncp-iam-access-key", ACCESS_KEY)
		                .header("x-ncp-apigw-signature-v2", requestEntity.getSignature())
		                .bodyValue(requestEntity.getBody());
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
