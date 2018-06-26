package com.zin.toolutils.database;

import android.content.Context;

import com.google.gson.Gson;

public class ZinBuilder {

    /**
     * NEVER ever change STORAGE_TAG_DO_NOT_CHANGE and TAG_INFO.
     * It will break backward compatibility in terms of keeping previous data
     */
    private static final String STORAGE_TAG_DO_NOT_CHANGE = "Zin2";

    private Context context;
    private Storage cryptoStorage;
    private Converter converter;
    private Parser parser;
    private Encryption encryption;
    private Serializer serializer;
    private LogInterceptor logInterceptor;

    public ZinBuilder(Context context) {
        ZinUtils.checkNull("Context", context);

        this.context = context.getApplicationContext();
    }

    public ZinBuilder setStorage(Storage storage) {
        this.cryptoStorage = storage;
        return this;
    }

    public ZinBuilder setParser(Parser parser) {
        this.parser = parser;
        return this;
    }

    public ZinBuilder setSerializer(Serializer serializer) {
        this.serializer = serializer;
        return this;
    }

    public ZinBuilder setLogInterceptor(LogInterceptor logInterceptor) {
        this.logInterceptor = logInterceptor;
        return this;
    }

    public ZinBuilder setConverter(Converter converter) {
        this.converter = converter;
        return this;
    }

    public ZinBuilder setEncryption(Encryption encryption) {
        this.encryption = encryption;
        return this;
    }

    LogInterceptor getLogInterceptor() {
        if (logInterceptor == null) {
            logInterceptor = new LogInterceptor() {
                @Override
                public void onLog(String message) {
                    //empty implementation
                }
            };
        }
        return logInterceptor;
    }

    Storage getStorage() {
        if (cryptoStorage == null) {
            cryptoStorage = new SharedPreferencesStorage(context, STORAGE_TAG_DO_NOT_CHANGE);
        }
        return cryptoStorage;
    }

    Converter getConverter() {
        if (converter == null) {
            converter = new ZinConverter(getParser());
        }
        return converter;
    }

    Parser getParser() {
        if (parser == null) {
            parser = new GsonParser(new Gson());
        }
        return parser;
    }

    Encryption getEncryption() {
        if (encryption == null) {
            encryption = new ConcealEncryption(context);
            if (!encryption.init()) {
                encryption = new NoEncryption();
            }
        }
        return encryption;
    }

    Serializer getSerializer() {
        if (serializer == null) {
            serializer = new ZinSerializer(getLogInterceptor());
        }
        return serializer;
    }

    public void build() {
        Zin.build(this);
    }
}
