package seol.study.amqp.consumer;

import static seol.study.amqp.producer.SampleQueueConfig.SAMPLE_AUTO_DELETE;
import static seol.study.amqp.producer.SampleQueueConfig.SAMPLE_DLX_AUTO_DELETE;
import static seol.study.amqp.producer.SampleQueueConfig.SAMPLE_DLX_DURABLE;
import static seol.study.amqp.producer.SampleQueueConfig.SAMPLE_DURABLE;
import static seol.study.amqp.producer.SampleQueueConfig.SAMPLE_EXCHANGE_DLX_NAME;
import static seol.study.amqp.producer.SampleQueueConfig.SAMPLE_EXCHANGE_NAME;
import static seol.study.amqp.producer.SampleQueueConfig.SAMPLE_QUEUE_DLX_NAME;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Argument;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SampleListener {

	private int index;

	@RabbitListener(
			bindings = @QueueBinding(
					value = @Queue(
							value = "#{sampleQueueName}",
							durable = SAMPLE_DURABLE,
							autoDelete = SAMPLE_AUTO_DELETE,
							arguments = {
									@Argument(name = "x-dead-letter-exchange", value = SAMPLE_EXCHANGE_DLX_NAME),
							}
					),
					exchange = @Exchange(value = SAMPLE_EXCHANGE_NAME, type = ExchangeTypes.FANOUT)
			)
	)
	public void receiveMessage(final Message message) {
		log.info("[receiveMessage] message.getBody()={}", new String(message.getBody()));
		if (++index % 2 == 0) {
			// 메시지 소비 실패.
			// AmqpRejectAndDontRequeueException을 발생시키면 Reject되면서 다시 Queue에 들어가지 않는다.
			// Manual Rejection 또는 AmqpRejectAndDontRequeueException처리를 안하면, 메시지가 다시 Queue로 들어가서 반복된 에러가 발생할 수 있다.
			// Reject처리 후 DLQ로 보낼지 여부는 RabbitMQ Queue의 x-dead-letter-exchange(+ x-dead-letter-routing-key) 설정에 따라간다.
			throw new AmqpRejectAndDontRequeueException("실패처리. Reject And Don't Requeue Exception");
		}

		log.info("[receiveMessage] 메시지 소비 성공");
	}

	@RabbitListener(
			bindings = @QueueBinding(
					value = @Queue(
							value = SAMPLE_QUEUE_DLX_NAME,
							durable = SAMPLE_DLX_DURABLE,
							autoDelete = SAMPLE_DLX_AUTO_DELETE
					),
					exchange = @Exchange(value = SAMPLE_EXCHANGE_DLX_NAME, type = ExchangeTypes.FANOUT)
			)
	)
	public void receiveDlxMessage(final Message message) {
		log.info("[receiveDlxMessage] message.getBody()={}", new String(message.getBody()));
		log.info("[receiveDlxMessage] 메시지 소비 성공");
	}

}
