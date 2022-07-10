package cdc.toy.noticer.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:application-test.properties")
@Import(SmsService.class)
public class SmsServiceTest {

	@Autowired
	private SmsService smsService;

	@Test
	public void smsTest() {
		StepVerifier.create(smsService.sendMessage("SMS Test!").doOnNext(System.out::println))
		            .expectNextMatches(v -> v.getStatusCode().equals("202"))
		            .expectComplete()
		            .verify();
	}
}
