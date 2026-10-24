package com.cybercraft.data;

import com.google.gson.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import net.minecraft.data.*;

public abstract class JsonDataProvider implements DataProvider {
  protected final PackOutput output;
  private final Map<String, JsonElement> entries = new TreeMap<>();

  protected JsonDataProvider(PackOutput output) {
    this.output = output;
  }

  protected static JsonObject obj(Object... pairs) {
    var result = new JsonObject();
    for (int i = 0; i < pairs.length; i += 2) result.add((String) pairs[i], json(pairs[i + 1]));
    return result;
  }

  protected static JsonElement json(Object value) {
    return value instanceof JsonElement j ? j : new Gson().toJsonTree(value);
  }

  protected static JsonArray array(Object... values) {
    var a = new JsonArray();
    for (Object v : values) a.add(json(v));
    return a;
  }

  protected void add(String path, JsonElement json) {
    if (entries.putIfAbsent(path, json) != null)
      throw new IllegalArgumentException("Duplicate generated resource: " + path);
  }

  protected abstract void generate();

  @Override
  public CompletableFuture<?> run(CachedOutput cache) {
    entries.clear();
    generate();
    return CompletableFuture.allOf(
        entries.entrySet().stream()
            .map(
                e ->
                    DataProvider.saveStable(
                        cache, e.getValue(), output.getOutputFolder().resolve(e.getKey())))
            .toArray(CompletableFuture[]::new));
  }

  @Override
  public String getName() {
    return "CyberCraft " + getClass().getSimpleName();
  }
}
