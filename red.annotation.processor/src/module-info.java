import javax.annotation.processing.Processor;
import dev.redio.internal.annotation.concurrent.AsyncProcessor;

module red.annotation.processor {
    requires static java.compiler;
    requires static jdk.compiler;

	provides Processor with AsyncProcessor;
}