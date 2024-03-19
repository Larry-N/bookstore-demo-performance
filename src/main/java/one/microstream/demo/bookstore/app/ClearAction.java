
package one.microstream.demo.bookstore.app;

import org.hibernate.Cache;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import one.microstream.reference.LazyReferenceManager;
import one.microstream.storage.embedded.types.EmbeddedStorageManager;

import java.util.concurrent.Callable;


public interface ClearAction extends Action
{
	public String title();
	
	public String verb();
	
	
	public static ClearAction GarbageCollector()
	{
		return new Default("Garbage Collector", "Execute", () -> {
			System.gc();
			return null;
		});
	}
	
	public static ClearAction LazyRefsAndObjectCache(
		final EmbeddedStorageManager storageManager
	)
	{
		return new Default("Lazy References", "Clear", () ->
		{
			LazyReferenceManager.get().clear();
			
//			storageManager
//				.persistenceManager()
//				.objectRegistry()
//				.clear();
			return null;
		});
	}
	
	public static ClearAction StorageCache(
		final EmbeddedStorageManager storageManager
	)
	{
		return new Default("Storage Cache", "Clear", () ->
		{
			storageManager.issueCacheCheck(Long.MAX_VALUE, (s, t, e) -> true);
			return null;
		});
	}
	
	public static ClearAction SessionCache(
		final SessionFactory sessionFactory
	)
	{
		return new Default("Session Cache", "Clear", () ->
		{
			final Session session = sessionFactory.getCurrentSession();
			if(session != null)
			{
				session.clear();
			}
			return null;
		});
	}
	
	public static ClearAction SecondLevelCache(
		final SessionFactory sessionFactory
	)
	{
		return new Default("Second-Level Cache", "Clear", () ->
		{
			final Cache cache = sessionFactory.getCache();
			if(cache != null)
			{
				cache.evictAllRegions();
			}
			return null;
		});
	}
	
	public static ClearAction New(
		final String title,
		final String verb,
		final Callable<?> logic
	)
	{
		return new Default(title, verb, logic);
	}
	
	public static class Default implements ClearAction
	{
		private final String   title;
		private final String   verb;
		private final Callable<?> logic;
		
		Default(
			final String title,
			final String verb,
			final Callable<?> logic
		)
		{
			super();
			this.title = title;
			this.verb  = verb;
			this.logic = logic;
		}
		
		@Override
		public String title()
		{
			return this.title;
		}
		
		@Override
		public String verb()
		{
			return this.verb;
		}
		
		@Override
		public String description()
		{
			return this.verb + " " + this.title;
		}
		
		@Override
		public Callable<?> logic()
		{
			return this.logic;
		}
		
	}
	
}
