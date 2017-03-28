package parameterizer;
/* allows to add additional processing to parsed string */

public interface Parameterizer<T> {
	public T apply(T param);
}
