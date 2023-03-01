import dev.redio.test.SampleClass;
import dev.redio.test.SampleService;
import dev.redio.test.SampleServiceImpl;

module red.service {
	exports dev.redio.service;

	requires red.base;

	provides SampleClass with SampleClass;
	provides SampleService with SampleServiceImpl;

	uses SampleClass;
	uses SampleService;
}