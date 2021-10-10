package seol.study.amqp.producer;

import static java.lang.Boolean.valueOf;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SampleQueueConfig {

	public static final String SAMPLE_EXCHANGE_NAME = "sample.topic.exchange";
	public static final String SAMPLE_QUEUE_NAME = "sample.topic.queue";
	public static final String SAMPLE_ROUTING_KEY = "sample.topic.queue";
	public static final String SAMPLE_DURABLE = "true";
	public static final String SAMPLE_AUTO_DELETE = "false";

	public static final String SAMPLE_EXCHANGE_DLX_NAME = "sample.topic.exchange.dlx";
	public static final String SAMPLE_QUEUE_DLX_NAME = "sample.topic.queue.dlx";
	public static final String SAMPLE_ROUTING_DLX_KEY = "sample.topic.queue.dlx";
	public static final String SAMPLE_DLX_DURABLE = "true";
	public static final String SAMPLE_DLX_AUTO_DELETE = "false";


	/**
	 * 지정된 이름으로 Queue를 등록합니다.
	 * 서로 다른 이름으로 여러개의 Queue를 등록할 수도 있습니다.
	 */
	@Bean(name = "sampleQueue")
	Queue sampleQueue() {
		Queue queue = new Queue(
				SAMPLE_QUEUE_NAME,
				valueOf(SAMPLE_DURABLE), // Durable: 대기열을 유지할지를 정할 플래그
				false, // Exclusive: 선언된 연결에 의해서만 사용할지 정할 플래그
				valueOf(SAMPLE_AUTO_DELETE) // Auto-Delete : 더 이상 사용되지 않는 큐를 삭제할지 정할 플래그
		);
		queue.addArgument("x-dead-letter-exchange", SAMPLE_EXCHANGE_DLX_NAME);
		queue.addArgument("x-dead-letter-routing-key", SAMPLE_ROUTING_DLX_KEY);
		return queue;
	}

	/**
	 * Exchange를 설정합니다.
	 * 위 코드에서는 TopicExchange를 사용해 주어진 패턴과 일치하는 Queue에 메시지를 전달합니다.
	 * 설정할 수 있는 Exchange에는 Direct, Fanout, Topic, Headers가 있습니다.
	 */
	@Bean(name = "sampleExchange")
	TopicExchange sampleExchange() {
		return new TopicExchange(
				SAMPLE_EXCHANGE_NAME,
				valueOf(SAMPLE_DURABLE),
				false
		);
	}

	/**
	 * Exchange가 Queue에게 메시지를 전달하기 위한 룰입니다.
	 * 빈으로 등록한 Queue와 Exchange를 바인딩하면서 Exchange에서 사용될 패턴을 설정해 주었습니다.
	 */
	@Bean(name = "sampleBinding")
	Binding sampleBinding(@Qualifier("sampleQueue") Queue queue, @Qualifier("sampleExchange") TopicExchange exchange) {
		return BindingBuilder.bind(queue)
				.to(exchange)
				.with(SAMPLE_ROUTING_KEY);
	}


	@Bean(name = "sampleQueueDlx")
	Queue sampleQueueDlx() {
		return new Queue(
				SAMPLE_QUEUE_DLX_NAME,
				valueOf(SAMPLE_DLX_DURABLE), // Durable: 대기열을 유지할지를 정할 플래그
				false, // Exclusive: 선언된 연결에 의해서만 사용할지 정할 플래그
				valueOf(SAMPLE_DLX_AUTO_DELETE) // Auto-Delete : 더 이상 사용되지 않는 큐를 삭제할지 정할 플래그
		);
	}

	@Bean(name = "sampleExchangeDlx")
	TopicExchange sampleExchangeDlx() {
		return new TopicExchange(
				SAMPLE_EXCHANGE_DLX_NAME,
				valueOf(SAMPLE_DLX_DURABLE),
				false
		);
	}

	@Bean(name = "sampleBindingDlx")
	Binding sampleBindingDlx(@Qualifier("sampleQueueDlx") Queue queue, @Qualifier("sampleExchangeDlx") TopicExchange exchange) {
		return BindingBuilder.bind(queue)
				.to(exchange)
				.with(SAMPLE_ROUTING_DLX_KEY);
	}

}
