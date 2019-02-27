package be.pyrrh4.pyrcore.convert.v6;

import java.io.IOException;

import be.pyrrh4.pyrcore.libs.com.google.gson.TypeAdapter;
import be.pyrrh4.pyrcore.libs.com.google.gson.stream.JsonReader;
import be.pyrrh4.pyrcore.libs.com.google.gson.stream.JsonWriter;

public class AdapterV6QuestCreatorFollowedObjectData extends TypeAdapter<V6QuestCreatorFollowedObjectData> {

	@Override
	public V6QuestCreatorFollowedObjectData read(JsonReader reader) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void write(JsonWriter writer, V6QuestCreatorFollowedObjectData object) throws IOException {
		if (object == null) {
			writer.nullValue();
			return;
		}
		writer.value(object.getBranchId() + "," + object.getObjectId() + "," + String.valueOf(object.getAdditionalValue()) + "," + String.valueOf(object.getWhen()));
	}

}
