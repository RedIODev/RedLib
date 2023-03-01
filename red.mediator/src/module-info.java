import dev.redio.internal.mediator.MediatorImpl;
import dev.redio.mediator.Mediator;

module red.mediator {

	exports dev.redio.mediator;
	requires red.base;
	requires red.concurrent;
	requires transitive red.service;

	provides Mediator with MediatorImpl;
}