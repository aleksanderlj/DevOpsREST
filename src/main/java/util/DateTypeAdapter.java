package util;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Date;

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