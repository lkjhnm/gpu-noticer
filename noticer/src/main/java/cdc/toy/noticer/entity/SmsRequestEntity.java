package cdc.toy.noticer.entity;

import java.net.URI;

public class SmsRequestEntity {

	private URI uri;
	private String timestamp;
	private String signature;
	private SmsBody body;

	public SmsRequestEntity(URI uri, String timestamp, String signature, SmsBody body) {
		this.uri = uri;
		this.timestamp = timestamp;
		this.signature = signature;
		this.body = body;
	}

	public URI getUri() {
		return uri;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public String getSignature() {
		return signature;
	}

	public SmsBody getBody() {
		return this.body;
	}
}
