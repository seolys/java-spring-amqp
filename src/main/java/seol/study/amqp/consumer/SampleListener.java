package seol.study.amqp.consumer;

import static seol.study.amqp.producer.SampleQueueConfig.SAMPLE_DURABLE;
import static seol.study.amqp.producer.SampleQueueConfig.SAMPLE_EXCHANGE_DLX_NAME;
import static seol.study.amqp.producer.SampleQueueConfig.SAMPLE_EXCHANGE_NAME;
import static seol.study.amqp.producer.SampleQueueConfig.SAMPLE_QUEUE_NAME;
import static seol.study.amqp.producer.SampleQueueConfig.SAMPLE_ROUTING_DLX_KEY;
import static seol.study.amqp.producer.SampleQueueConfig.SAMPLE_ROUTING_KEY;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Argument;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class SampleListener {

	private static final Logger log = LoggerFactory.getLogger(SampleListener.class);

	private int index;

	//	@RabbitListener(queues = "sample.queue")
	@RabbitListener(
			bindings = @QueueBinding(
					value = @Queue(value = SAMPLE_QUEUE_NAME, durable = SAMPLE_DURABLE, arguments = {
							@Argument(name = "x-dead-letter-exchange", value = SAMPLE_EXCHANGE_DLX_NAME),
							@Argument(name = "x-dead-letter-routing-key", value = SAMPLE_ROUTING_DLX_KEY),
					}),
					exchange = @Exchange(value = SAMPLE_EXCHANGE_NAME, type = ExchangeTypes.TOPIC),
					key = SAMPLE_ROUTING_KEY
			)
	)
	public void receiveMessage(final Message message) throws Exception {
		log.info("message.getBody()={}", new String(message.getBody()));
		if (++index % 2 == 0) {
			// 메시지 소비 실패.
			// AmqpRejectAndDontRequeueException을 발생시키면 Reject되면서 다시 Queue에 들어가지 않는다.
			// Manual Rejection 또는 AmqpRejectAndDontRequeueException처리를 안하면, 메시지가 다시 Queue로 들어가서 반복된 에러가 발생할 수 있다.
			// Reject처리 후 DLQ로 보낼지 여부는 RabbitMQ Queue의 x-dead-letter-exchange(+ x-dead-letter-routing-key) 설정에 따라간다.
			throw new AmqpRejectAndDontRequeueException("실패처리. Reject And Don't Requeue Exception");
		}

		log.info("메시지 소비 성공");
	}

//	@RabbitListener(
//			bindings = @QueueBinding(
//					value = @Queue(
//							value = SAMPLE_QUEUE_DLX_NAME,
//							durable = SAMPLE_DLX_DURABLE,
//							autoDelete = SAMPLE_DLX_AUTO_DELETE
//					),
//					exchange = @Exchange(value = SAMPLE_EXCHANGE_DLX_NAME, type = ExchangeTypes.FANOUT)
//			)
//	)
//	public void receiveDlxMessage(final Message message) {
//		log.info("[receiveDlxMessage] message.getBody()={}", new String(message.getBody()));
//		log.info("[receiveDlxMessage] 메시지 소비 성공");
//	}

}
