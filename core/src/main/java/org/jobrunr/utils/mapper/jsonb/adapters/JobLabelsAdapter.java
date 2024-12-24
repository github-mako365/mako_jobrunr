package org.jobrunr.utils.mapper.jsonb.adapters;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import jakarta.json.bind.adapter.JsonbAdapter;
import org.jobrunr.utils.mapper.jsonb.JobRunrJsonb;

import java.util.Set;
import java.util.TreeSet;

public class JobLabelsAdapter implements JsonbAdapter<Set<String>, JsonArray> {

    private final JobRunrJsonb jsonb;

    public JobLabelsAdapter(JobRunrJsonb jsonb) {
        this.jsonb = jsonb;
    }

    @Override
    public JsonArray adaptToJson(Set<String> labels) {
        final JsonArrayBuilder historyJsonObject = Json.createArrayBuilder();
        for (String label : labels) {
            historyJsonObject.add(label);
        }
        return historyJsonObject.build();
    }

    @Override
    public Set<String> adaptFromJson(JsonArray jsonArray) {
        final Set<String> result = new TreeSet<>();
        if (jsonArray != null) {
            for (JsonValue jsonValue : jsonArray) {
                result.add(((JsonString) jsonValue).getString());
            }
        }
        return result;
    }
}
