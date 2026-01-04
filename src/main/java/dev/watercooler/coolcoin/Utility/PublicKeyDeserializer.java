package dev.watercooler.coolcoin.Utility;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

class PublicKeyDeserializer implements JsonDeserializer<PublicKey> {
    @Override
    public PublicKey deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        PublicKey result = null;

        try {
            byte[] byteKey = Base64.getDecoder().decode(json.getAsString());
            X509EncodedKeySpec spec = new X509EncodedKeySpec(byteKey);
            KeyFactory kf = KeyFactory.getInstance("ECDSA", "BC");

            result = kf.generatePublic(spec);
        } catch (Exception e) {
            new RuntimeException(e);
        }

        return result;
    }
}
