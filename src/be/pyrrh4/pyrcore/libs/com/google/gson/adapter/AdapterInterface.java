package be.pyrrh4.pyrcore.libs.com.google.gson.adapter;

import java.lang.reflect.Type;

import be.pyrrh4.pyrcore.libs.com.google.gson.JsonDeserializationContext;
import be.pyrrh4.pyrcore.libs.com.google.gson.JsonDeserializer;
import be.pyrrh4.pyrcore.libs.com.google.gson.JsonElement;
import be.pyrrh4.pyrcore.libs.com.google.gson.JsonObject;
import be.pyrrh4.pyrcore.libs.com.google.gson.JsonParseException;
import be.pyrrh4.pyrcore.libs.com.google.gson.JsonSerializationContext;
import be.pyrrh4.pyrcore.libs.com.google.gson.JsonSerializer;

public class AdapterInterface<T> implements JsonSerializer<T>, JsonDeserializer<T> {

	@Override
	public final JsonElement serialize(final T object, final Type interfaceType, final JsonSerializationContext context) 
	{
		final JsonObject member = new JsonObject();

		member.addProperty("type", object.getClass().getName());
		member.add("data", context.serialize(object));

		return member;
	}

	@Override
	public final T deserialize(final JsonElement elem, final Type interfaceType, final JsonDeserializationContext context) 
			throws JsonParseException 
	{
		final JsonObject member = elem.getAsJsonObject();
		final JsonElement typeString = get(member, "type");
		final JsonElement data = get(member, "data");
		final Type actualType = typeForName(typeString);

		return context.deserialize(data, actualType);
	}

	private Type typeForName(final JsonElement typeElem) 
	{
		try 
		{
			return Class.forName(typeElem.getAsString());
		} 
		catch (ClassNotFoundException e) 
		{
			throw new JsonParseException(e);
		}
	}

	private JsonElement get(final JsonObject wrapper, final String memberName) 
	{
		final JsonElement elem = wrapper.get(memberName);

		if (elem == null) 
		{
			throw new JsonParseException(
					"no '" + memberName + "' member found in json file.");
		}
		return elem;
	}

}
