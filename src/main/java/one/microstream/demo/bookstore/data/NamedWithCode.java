
package one.microstream.demo.bookstore.data;

import com.fasterxml.jackson.annotation.JsonGetter;

/**
 * Feature type for all named entities with a code.
 *
 */
public abstract class NamedWithCode extends Named
{
	private final String code;

	protected NamedWithCode(
		final String name,
		final String code
	)
	{
		super(name);
		
		this.code = code;
	}

	/**
	 * Get the code.
	 *
	 * @return the code
	 */
	@JsonGetter
	public String code()
	{
		return this.code;
	}

	@Override
	public String toString()
	{
		return this.getClass().getSimpleName() + " [" + this.name() + " - " + this.code + "]";
	}

}
