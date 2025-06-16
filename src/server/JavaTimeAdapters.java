package server;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.*;

public class JavaTimeAdapters {

    public static class DurationAdapter extends TypeAdapter<Duration> {
        @Override
        public void write(JsonWriter out, Duration value) throws IOException {
            out.value(value != null ? value.toMillis() : null);
        }

        @Override
        public Duration read(JsonReader in) throws IOException {
            return in.peek() == null ? null : Duration.ofMillis(in.nextLong());
        }
    }

    public static class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
        @Override
        public void write(JsonWriter out, LocalDateTime value) throws IOException {
            out.value(value != null ? value.toString() : null);
        }

        @Override
        public LocalDateTime read(JsonReader in) throws IOException {
            return in.peek() == null ? null : LocalDateTime.parse(in.nextString());
        }
    }

    public static class LocalDateAdapter extends TypeAdapter<LocalDate> {
        @Override
        public void write(JsonWriter out, LocalDate value) throws IOException {
            out.value(value != null ? value.toString() : null);
        }

        @Override
        public LocalDate read(JsonReader in) throws IOException {
            return in.peek() == null ? null : LocalDate.parse(in.nextString());
        }
    }

    public static class LocalTimeAdapter extends TypeAdapter<LocalTime> {
        @Override
        public void write(JsonWriter out, LocalTime value) throws IOException {
            out.value(value != null ? value.toString() : null);
        }

        @Override
        public LocalTime read(JsonReader in) throws IOException {
            return in.peek() == null ? null : LocalTime.parse(in.nextString());
        }
    }
}