package ru.yandex.javacource.emelyanov.schedule.http;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;

class DurationAdapter extends TypeAdapter<Duration> {
    @Override
    public Duration read(JsonReader jsonReader) throws IOException {
        return Duration.ofMinutes(jsonReader.nextInt());
    }

    @Override
    public void write(JsonWriter jsonWriter, Duration obj) throws IOException {
        jsonWriter.value(obj.toMinutes());
    }
}
