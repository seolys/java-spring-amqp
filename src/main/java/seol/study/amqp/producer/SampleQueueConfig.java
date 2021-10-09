package seol.study.amqp.producer;

import static java.lang.Boolean.valueOf;

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SampleQueueConfig {

	public static final String SAMPLE_EXCHANGE_NAME = "sample.fanout.exchange";
	public static final String SAMPLE_QUEUE_BASE_NAME = "sample.fanout.queue.";
	public static final String SAMPLE_DURABLE = "true";
	public static final String SAMPLE_AUTO_DELETE = "true";

	public static final String SAMPLE_EXCHANGE_DLX_NAME = "sample.fanout.exchange.dlx";
	public static final String SAMPLE_QUEUE_DLX_NAME = "sample.fanout.queue.dlx";
	public static final String SAMPLE_DLX_DURABLE = "true";
	public static final String SAMPLE_DLX_AUTO_DELETE = "false";


	@Bean(name = "sampleQueue")
	Queue sampleQueue(@Value("#{sampleQueueName}") String sampleQueueName) {
		Queue queue = new Queue(
				sampleQueueName,
				valueOf(SAMPLE_DURABLE), // Durable: 대기열을 유지할지를 정할 플래그
				false, // Exclusive: 선언된 연결에 의해서만 사용할지 정할 플래그
				valueOf(SAMPLE_AUTO_DELETE) // Auto-Delete : 더 이상 사용되지 않는 큐를 삭제할지 정할 플래그
		);
		queue.addArgument("x-dead-letter-exchange", SAMPLE_EXCHANGE_DLX_NAME);
		return queue;
	}

	@Bean(name = "sampleExchange")
	FanoutExchange sampleExchange() {
//		return new TopicExchange(
//				SAMPLE_EXCHANGE_NAME,
//				Boolean.valueOf(SAMPLE_DURABLE),
//				false
//		);
		return new FanoutExchange(
				SAMPLE_EXCHANGE_NAME,
				valueOf(SAMPLE_DURABLE),
				false
		);
	}

	@Bean(name = "sampleBinding")
	Binding sampleBinding(@Qualifier("sampleQueue") Queue queue, @Qualifier("sampleExchange") FanoutExchange exchange) {
		return BindingBuilder.bind(queue)
				.to(exchange);
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
	FanoutExchange sampleExchangeDlx() {
		return new FanoutExchange(
				SAMPLE_EXCHANGE_DLX_NAME,
				valueOf(SAMPLE_DLX_DURABLE),
				false
		);
	}

	@Bean(name = "sampleBindingDlx")
	Binding sampleBindingDlx(@Qualifier("sampleQueueDlx") Queue queue, @Qualifier("sampleExchangeDlx") FanoutExchange exchange) {
		return BindingBuilder.bind(queue)
				.to(exchange);
	}

	@Bean
	public String localServerIp() throws UnknownHostException {
		InetAddress ip = InetAddress.getLocalHost();
		return ip.getHostAddress();
	}

	@Bean
	public String sampleQueueName() throws UnknownHostException {
		return SAMPLE_QUEUE_BASE_NAME + localServerIp();
	}

}
