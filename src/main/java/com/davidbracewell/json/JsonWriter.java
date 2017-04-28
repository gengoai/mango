package com.davidbracewell.json;

import com.davidbracewell.EnumValue;
import com.davidbracewell.collection.Collect;
import com.davidbracewell.collection.counter.Counter;
import com.davidbracewell.collection.list.PrimitiveArrayList;
import com.davidbracewell.conversion.Cast;
import com.davidbracewell.conversion.Convert;
import com.davidbracewell.io.CommonBOM;
import com.davidbracewell.io.resource.Resource;
import com.davidbracewell.io.resource.StringResource;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Multimap;

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

/**
 * @author David B. Bracewell
 */
public class JsonWriter implements AutoCloseable, Closeable {
    private final com.google.gson.stream.JsonWriter writer;
    private Boolean isArray = null;

    public JsonWriter(Resource resource) throws IOException {
        this.writer = new com.google.gson.stream.JsonWriter(resource.writer());
        this.writer.setHtmlSafe(true);
        this.writer.setLenient(true);
    }

    public static void main(String[] args) throws Exception {
        Resource r = new StringResource();
        JsonWriter w = new JsonWriter(r);
        w
            .setIndentSpaces(1)
            .beginDocument(false)
            .keyValue("A", CommonBOM.UTF_8)
            .endDocument()
            .close();
        System.out.println(r.readToString());
    }

    public JsonWriter array(String name, Iterable<?> value) throws IOException {
        writer.name(name);
        value(value);
        return this;
    }

    public JsonWriter array(String name, Iterator<?> value) throws IOException {
        writer.name(name);
        value(value);
        return this;
    }

    public JsonWriter beginArray() throws IOException {
        writer.beginArray();
        return this;
    }

    public JsonWriter beginArray(String name) throws IOException {
        writer.name(name);
        writer.beginArray();
        return this;
    }

    public JsonWriter beginDocument(boolean isArray) throws IOException {
        Preconditions.checkArgument(this.isArray == null, "Document already started");
        if (isArray) {
            writer.beginArray();
            this.isArray = true;
        } else {
            writer.beginObject();
            this.isArray = false;
        }
        return this;
    }

    public JsonWriter beginObject() throws IOException {
        writer.beginObject();
        return this;
    }

    public JsonWriter beginObject(String name) throws IOException {
        writer.name(name);
        writer.beginObject();
        return this;
    }

    @Override
    public void close() throws IOException {
        writer.close();
    }

    public JsonWriter endArray() throws IOException {
        writer.endArray();
        return this;
    }

    public JsonWriter endDocument() throws IOException {
        if (isArray) {
            writer.endArray();
        } else {
            writer.endObject();
        }
        return this;
    }

    public JsonWriter endObject() throws IOException {
        writer.endObject();
        return this;
    }

    public JsonWriter keyValue(String key, double value) throws IOException {
        writer.name(key);
        writer.value(value);
        return this;
    }

    public JsonWriter keyValue(String key, int value) throws IOException {
        writer.name(key);
        writer.value(value);
        return this;
    }

    public JsonWriter keyValue(String key, boolean value) throws IOException {
        writer.name(key);
        writer.value(value);
        return this;
    }

    public JsonWriter keyValue(String key, String value) throws IOException {
        writer.name(key);
        writer.value(value);
        return this;
    }

    public JsonWriter keyValue(String key, long value) throws IOException {
        writer.name(key);
        writer.value(value);
        return this;
    }

    public JsonWriter keyValue(String key, Boolean value) throws IOException {
        writer.name(key);
        writer.value(value);
        return this;
    }

    public JsonWriter keyValue(String key, Number value) throws IOException {
        writer.name(key);
        writer.value(value);
        return this;
    }

    public JsonWriter keyValue(String key, Object value) throws IOException {
        writer.name(key);
        value(value);
        return this;
    }

    public JsonWriter nullValue() throws IOException {
        writer.nullValue();
        return this;
    }

    public JsonWriter object(String name, Map<String, ?> value) throws IOException {
        writer.name(name);
        value(value);
        return this;
    }

    public JsonWriter setIndentSpaces(int spaces) throws IOException {
        writer.setIndent(Strings.padEnd("", spaces, ' '));
        return this;
    }

    public JsonWriter value(Map<String, ?> value) throws IOException {
        if (value == null) {
            writer.nullValue();
        } else {
            writer.beginObject();
            for (String key : value.keySet()) {
                keyValue(key, value.get(key));
            }
            writer.endObject();
        }
        return this;
    }

    public JsonWriter value(Iterable<?> value) throws IOException {
        if (value == null) {
            writer.nullValue();
        } else {
            writer.beginArray();
            for (Object o : value) {
                value(o);
            }
            writer.endArray();
        }
        return this;
    }

    public JsonWriter value(Iterator<?> value) throws IOException {
        if (value == null) {
            writer.nullValue();
        } else {
            value(Collect.asIterable(value));
        }
        return this;
    }

    public JsonWriter value(Object value) throws IOException {
        if (value == null) {
            writer.nullValue();
        } else if (value instanceof String) {
            value((String) value);
        } else if (value instanceof Number) {
            value((Number) value);
        } else if (value instanceof Boolean) {
            value((Boolean) value);
        } else if (value instanceof Map) {
            value((Map<String, ?>) value);
        } else if (value instanceof Iterable) {
            value((Iterable<?>) value);
        } else if (value instanceof Iterator) {
            value((Iterator<?>) value);
        } else if (value instanceof Multimap) {
            value(Cast.<Multimap<String, ?>>as(value).asMap());
        } else if (value instanceof Counter) {
            value(Cast.<Counter<String>>as(value).asMap());
        } else if (value instanceof Enum) {
            value(Cast.<Enum<?>>as(value).name());
        } else if (value instanceof EnumValue) {
            value(Cast.<EnumValue>as(value).canonicalName());
        } else if (value
                       .getClass()
                       .isArray() && value
                                         .getClass()
                                         .isPrimitive()) {
            value(new PrimitiveArrayList<>(value, Object.class));
        } else if (value
                       .getClass()
                       .isArray()) {
            value(Collect.asIterable(value, Object.class));
        } else {
            value(Convert.convert(value, String.class));
        }
        return this;
    }

    public JsonWriter value(String value) throws IOException {
        writer.value(value);
        return this;
    }

    public JsonWriter value(int value) throws IOException {
        writer.value(value);
        return this;
    }

    public JsonWriter value(double value) throws IOException {
        writer.value(value);
        return this;
    }

    public JsonWriter value(long value) throws IOException {
        writer.value(value);
        return this;
    }

    public JsonWriter value(boolean value) throws IOException {
        writer.value(value);
        return this;
    }

    public JsonWriter value(Boolean value) throws IOException {
        writer.value(value);
        return this;
    }

    public JsonWriter value(Number value) throws IOException {
        writer.value(value);
        return this;
    }

}// END OF JsonWriter
