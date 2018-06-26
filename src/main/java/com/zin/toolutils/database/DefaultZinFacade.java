package com.zin.toolutils.database;

public class DefaultZinFacade implements ZinFacade {

  private final Storage storage;
  private final Converter converter;
  private final Encryption encryption;
  private final Serializer serializer;
  private final LogInterceptor logInterceptor;

  public DefaultZinFacade(ZinBuilder builder) {
    encryption = builder.getEncryption();
    storage = builder.getStorage();
    converter = builder.getConverter();
    serializer = builder.getSerializer();
    logInterceptor = builder.getLogInterceptor();

    logInterceptor.onLog("Zin.init -> Encryption : " + encryption.getClass().getSimpleName());
  }

  @Override public <T> boolean put(String key, T value) {
    // Validate
    ZinUtils.checkNull("Key", key);
    log("Zin.put -> key: " + key + ", value: " + value);

    // If the value is null, delete it
    if (value == null) {
      log("Zin.put -> Value is null. Any existing value will be deleted with the given key");
      return delete(key);
    }

    // 1. Convert to text
    String plainText = converter.toString(value);
    log("Zin.put -> Converted to " + plainText);
    if (plainText == null) {
      log("Zin.put -> Converter failed");
      return false;
    }

    // 2. Encrypt the text
    String cipherText = null;
    try {
      cipherText = encryption.encrypt(key, plainText);
      log("Zin.put -> Encrypted to " + cipherText);
    } catch (Exception e) {
      e.printStackTrace();
    }
    if (cipherText == null) {
      log("Zin.put -> Encryption failed");
      return false;
    }

    // 3. Serialize the given object along with the cipher text
    String serializedText = serializer.serialize(cipherText, value);
    log("Zin.put -> Serialized to " + serializedText);
    if (serializedText == null) {
      log("Zin.put -> Serialization failed");
      return false;
    }

    // 4. Save to the storage
    if (storage.put(key, serializedText)) {
      log("Zin.put -> Stored successfully");
      return true;
    } else {
      log("Zin.put -> Store operation failed");
      return false;
    }
  }

  @Override public <T> T get(String key) {
    log("Zin.get -> key: " + key);
    if (key == null) {
      log("Zin.get -> null key, returning null value ");
      return null;
    }

    // 1. Get serialized text from the storage
    String serializedText = storage.get(key);
    log("Zin.get -> Fetched from storage : " + serializedText);
    if (serializedText == null) {
      log("Zin.get -> Fetching from storage failed");
      return null;
    }

    // 2. Deserialize
    DataInfo dataInfo = serializer.deserialize(serializedText);
    log("Zin.get -> Deserialized");
    if (dataInfo == null) {
      log("Zin.get -> Deserialization failed");
      return null;
    }

    // 3. Decrypt
    String plainText = null;
    try {
      plainText = encryption.decrypt(key, dataInfo.cipherText);
      log("Zin.get -> Decrypted to : " + plainText);
    } catch (Exception e) {
      log("Zin.get -> Decrypt failed: " + e.getMessage());
    }
    if (plainText == null) {
      log("Zin.get -> Decrypt failed");
      return null;
    }

    // 4. Convert the text to original data along with original type
    T result = null;
    try {
      result = converter.fromString(plainText, dataInfo);
      log("Zin.get -> Converted to : " + result);
    } catch (Exception e) {
      log("Zin.get -> Converter failed");
    }

    return result;
  }

  @Override public <T> T get(String key, T defaultValue) {
    T t = get(key);
    if (t == null) return defaultValue;
    return t;
  }

  @Override public long count() {
    return storage.count();
  }

  @Override public boolean deleteAll() {
    return storage.deleteAll();
  }

  @Override public boolean delete(String key) {
    return storage.delete(key);
  }

  @Override public boolean contains(String key) {
    return storage.contains(key);
  }

  @Override public boolean isBuilt() {
    return true;
  }

  @Override public void destroy() {
  }

  private void log(String message) {
    logInterceptor.onLog(message);
  }
}
