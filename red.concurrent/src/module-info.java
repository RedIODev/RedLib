import javax.annotation.processing.Processor;
import dev.redio.internal.concurrent.annotation.processor.AsyncProcessor;

module red.concurrent {

	exports dev.redio.concurrent;
	exports dev.redio.concurrent.task;

	requires red.base;
	requires static java.compiler;

	provides Processor with AsyncProcessor;

}