package logprocessing;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class StatisticFactory {
	private HashMap m_RegisteredProducts = new HashMap();

	private static StatisticFactory sf=new StatisticFactory();
	public static StatisticFactory instance(){
		return sf;
	}
	
	public void registerProduct (String productID, Class productClass)
	{
		m_RegisteredProducts.put(productID, productClass);
	}

	@SuppressWarnings("unchecked")
	public StatisticDefinition getProduct(String productID, Object[] args)
	{
		Class productClass = (Class)m_RegisteredProducts.get(productID);
		Constructor<StatisticDefinition> productConstructor;
		try {
			productConstructor = productClass.getDeclaredConstructor(new Class[]{String.class,Map.class});
			return (StatisticDefinition)productConstructor.newInstance((Object[])args);

		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
