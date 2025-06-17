package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import server.adapters.JavaTimeAdapters;

import java.time.Duration;
import java.time.LocalDateTime;

public class Serializator {
    public static Gson gsonForTasks = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new JavaTimeAdapters.DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new JavaTimeAdapters.LocalDateTimeAdapter())
            .create();

}
