
package one.microstream.demo.bookstore.app;

import java.util.concurrent.Callable;

public interface QueryAction extends Action
{
	public QueryStats queryStats();
	
	public static QueryAction New(
		final String description,
		final Callable<?> msQuery,
		final Callable<?> jpaQuery
	)
	{
		return new Default(description, msQuery, jpaQuery);
	}
	
	public static class Default implements QueryAction, Callable<Object>
	{
		private final String   description;
		private final Callable<?> msQuery;
		private final Callable<?> jpaQuery;
		private QueryStats     queryStats;
		
		Default(
			final String description,
			final Callable<?> msQuery,
			final Callable<?> jpaQuery
		)
		{
			super();
			this.description = description;
			this.msQuery     = msQuery;
			this.jpaQuery    = jpaQuery;
		}
		
		@Override
		public String description()
		{
			return this.description;
		}
		
		@Override
		public Callable<?> logic()
		{
			return this;
		}
		
		@Override
		public Object call() throws Exception
		{
//			final Stopwatch stopwatch = Stopwatch.StartNanotime();
			
//			this.msQuery.run();
//
//			final long msNanos = stopwatch.restart();
			
			return this.jpaQuery.call();
			
//			final long jpaNanos = stopwatch.stop();
			
//			this.queryStats = QueryStats.New(this.description, msNanos, jpaNanos);
		}
		
		@Override
		public QueryStats queryStats()
		{
			return this.queryStats;
		}
		
		@Override
		public String toString()
		{
			return this.description;
		}
		
	}
	
}
