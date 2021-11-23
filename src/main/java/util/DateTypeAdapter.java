package util;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Date;

/* This class is directly inspired by: https://stackoverflow.com/questions/41348055/gson-dateformat-to-serialize-deserialize-unix-timestamps */

public class DateTypeAdapter extends TypeAdapter<Date> {
    @Override
    public void write(JsonWriter out, Date value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        out.value(value.getTime() / 1000);
    }

    @Override
    public Date read(JsonReader in) throws IOException {
        if (in == null) {
            return null;
        }
        return new Date(in.nextLong() * 1000);
    }
}