package one.microstream.demo.bookstore.app;

import java.util.concurrent.Callable;

public interface Action
{
	public String description();
	
	public Callable<?> logic();
}
