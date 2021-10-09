package seol.study.amqp.producer;

import static seol.study.amqp.producer.SampleQueueConfig.SAMPLE_EXCHANGE_NAME;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SampleController {

	private final RabbitTemplate rabbitTemplate;

	@GetMapping("/sample/publish")
	public String samplePublish(@RequestParam String message) {
		if (ObjectUtils.isEmpty(message)) {
			throw new IllegalArgumentException("message is empty");
		}

		log.info("message={}", message);
		rabbitTemplate.convertAndSend(SAMPLE_EXCHANGE_NAME, null, message);
		return "message sending!";
	}
}
