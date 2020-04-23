
package one.microstream.demo.readmecorp.ui;

import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.stream.IntStream;

import org.springframework.data.domain.PageRequest;

import one.microstream.demo.readmecorp.app.ActionExecutor;
import one.microstream.demo.readmecorp.app.QueryAction;
import one.microstream.demo.readmecorp.dal.DataAccess;
import one.microstream.demo.readmecorp.data.Country;
import one.microstream.demo.readmecorp.data.Shop;
import one.microstream.demo.readmecorp.jpa.dal.Repositories;
import one.microstream.demo.readmecorp.jpa.domain.CountryEntity;
import one.microstream.demo.readmecorp.jpa.domain.ShopEntity;


class Query
{
	public static Query[] All(
		final DataAccess dataAccess,
		final Repositories repositories
	)
	{
		return new Query[] {
			AllCustomersPaged(dataAccess, repositories),
			BooksByPrice(dataAccess, repositories),
			BooksByTitle(dataAccess, repositories),
			RevenueOfShopInYear(dataAccess, repositories),
			BestSellerList(dataAccess, repositories),
			EmployeeOfTheYear(dataAccess, repositories),
			PurchasesOfForeigners(dataAccess, repositories)
		};
	}
	
	public static Query AllCustomersPaged(
		final DataAccess dataAccess,
		final Repositories repositories
	)
	{
		return new Query(
			"All customers paged",
			(executor, iterations) ->
			{
				final int pageSize = 100;
				IntStream.rangeClosed(0, 2).forEach(page ->
					IntStream.rangeClosed(1, iterations).forEach(iteration ->
						executor.submit(QueryAction.New(
							"All customers paged, page=" + (page + 1) + " [" + iteration + "]",
							() -> dataAccess.data().customers()
								.skip(page * pageSize).limit(pageSize).collect(toList()),
							() -> repositories.customerRepository().findAll(PageRequest.of(page, pageSize))
						))
					)
				);
			}
		);
	}
	
	public static Query BooksByPrice(
		final DataAccess dataAccess,
		final Repositories repositories
	)
	{
		return new Query(
			"Books by price",
			(executor, iterations) ->
			{
				final int priceRange = 5;
				IntStream.rangeClosed(1, 3).forEach(priceStep ->
					IntStream.rangeClosed(1, iterations).forEach(iteration -> {
						final double minPrice = priceStep * priceRange;
						final double maxPrice = minPrice  + priceRange;
						executor.submit(QueryAction.New(
							"Books by price " + minPrice + " - " + maxPrice + " [" + iteration + "]",
							() -> dataAccess.data().books().all()
								.filter(b -> b.price() >= minPrice && b.price() < maxPrice).collect(toList()),
							() -> repositories.bookRepository().findByPriceGreaterThanEqualAndPriceLessThan(
								minPrice, maxPrice)
						));
					})
				);
			}
		);
	}
	
	public static Query BestSellerList(
		final DataAccess dataAccess,
		final Repositories repositories
	)
	{
		return new Query(
			"Bestseller list",
			(executor, iterations) ->
			{
				randomYears(dataAccess, 3).forEach(year ->
					randomCountries(dataAccess, repositories, 3).forEach((country, countryEntity) ->
						IntStream.rangeClosed(1, iterations).forEach(iteration ->
							executor.submit(QueryAction.New(
								"Bestseller of " + year + " in " + country.name() + " [" + iteration + "]",
								() -> dataAccess.bestSellerList(year, country),
								() -> repositories.purchaseItemRepository().bestSellerList(year, countryEntity)
							))
						)
					)
				);
			}
		);
	}
	
	public static Query EmployeeOfTheYear(
		final DataAccess dataAccess,
		final Repositories repositories
	)
	{
		return new Query(
			"Employee of the year",
			(executor, iterations) ->
			{
				randomYears(dataAccess, 3).forEach(year ->
					randomCountries(dataAccess, repositories, 3).forEach((country, countryEntity) ->
						IntStream.rangeClosed(1, iterations).forEach(iteration ->
							executor.submit(QueryAction.New(
								"Employee of " + year + " in " + country.name() + " [" + iteration + "]",
								() -> dataAccess.employeeOfTheYear(year, country),
								() -> repositories.employeeRepository().employeeOfTheYear(year, countryEntity.getId())
							))
						)
					)
				);
			}
		);
	}
	
	public static Query PurchasesOfForeigners(
		final DataAccess dataAccess,
		final Repositories repositories
	)
	{
		return new Query(
			"Purchases of foreigners",
			(executor, iterations) ->
			{
				randomYears(dataAccess, 3).forEach(year ->
					randomCountries(dataAccess, repositories, 3).forEach((country, countryEntity) ->
						IntStream.rangeClosed(1, iterations).forEach(iteration ->
							executor.submit(QueryAction.New(
								"Purchases of foreigners " + year + " in " + country.name() + " [" + iteration + "]",
								() -> dataAccess.purchasesOfForeigners(year, country),
								() -> repositories.purchaseRepository().findPurchasesOfForeigners(year, countryEntity)
							))
						)
					)
				);
			}
		);
	}
	
	public static Query BooksByTitle(
		final DataAccess dataAccess,
		final Repositories repositories
	)
	{
		return new Query(
			"Books by title",
			(executor, iterations) ->
			{
				randomCountries(dataAccess, repositories, 3).forEach((country, countryEntity) ->
					Arrays.asList("the","light","black","hero","of").forEach(pattern ->
						IntStream.rangeClosed(1, iterations).forEach(iteration ->
							executor.submit(QueryAction.New(
								"Books by title *" + pattern + "* in " + country.name() + " [" + iteration + "]",
								() -> dataAccess.booksByTitle(pattern, country),
								() -> repositories.bookRepository()
									.findByTitleLikeAndAuthorAddressCityStateCountry(pattern, countryEntity)
							))
						)
					)
				);
			}
		);
	}
	
	public static Query RevenueOfShopInYear(
		final DataAccess dataAccess,
		final Repositories repositories
	)
	{
		return new Query(
			"Revenue of shop",
			(executor, iterations) ->
			{
				final int shopCount = (int)dataAccess.data().shops().count();
				final int shopIndex = RANDOM.nextInt(shopCount);
				final Shop shop = dataAccess.data().shops().skip(shopIndex).findFirst().get();
				final ShopEntity shopEntity = repositories.shopRepository().findById(shopIndex + 1L).get();
				randomYears(dataAccess, 3).forEach(year ->
				{
					IntStream.rangeClosed(1, iterations).forEach(iteration ->
						executor.submit(QueryAction.New(
							"Revenue of shop " + shop.name() + " in " + year + " [" + iteration + "]",
							() -> dataAccess.revenueOfShopInYear(shop, year),
							() -> repositories.purchaseItemRepository().revenueOfShopInYear(shopEntity, year)
						))
					);
				});
			}
		);
	}
	
	
	private final static Random RANDOM = new Random();
	
	private static IntStream randomYears(final DataAccess dataAccess, final int yearSpan)
	{
		final IntSummaryStatistics stats     = dataAccess.data().purchases().years().summaryStatistics();
		final int                  minYear   = stats.getMin();
		final int                  maxYear   = stats.getMax();
		final int                  startYear = minYear + RANDOM.nextInt(maxYear - minYear - yearSpan + 1);
		return IntStream.range(startYear, startYear + yearSpan);
	}
	
	private static Map<Country, CountryEntity> randomCountries(
		final DataAccess dataAccess,
		final Repositories repositories,
		final int maxCount
	)
	{
		final List<Country> countries = dataAccess.data().shops()
			.map(s -> s.address().city().state().country())
			.distinct()
			.collect(toList());
		Collections.shuffle(countries);
		final Map<Country, CountryEntity> randomCountries = new HashMap<>();
		final int                         count           = Math.min(maxCount, countries.size());
		for(int i = 0; i < count; i++)
		{
			final Country       country       = countries.get(i);
			final CountryEntity countryEntity = repositories.countryRepository().findByName(country.name()).get();
			randomCountries.put(country, countryEntity);
		}
		return randomCountries;
	}
	
	
	private final String                              name;
	private final BiConsumer<ActionExecutor, Integer> actionSubmitter;
	
	Query(
		final String name,
		final BiConsumer<ActionExecutor, Integer> actionSubmitter
	)
	{
		super();
		this.name            = name;
		this.actionSubmitter = actionSubmitter;
	}
	
	public BiConsumer<ActionExecutor, Integer> actionSubmitter()
	{
		return this.actionSubmitter;
	}
	
	@Override
	public String toString()
	{
		return this.name;
	}
	
}