import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.watercooler.coolcoin.Transaction.Transaction;
import dev.watercooler.coolcoin.Wallet;

import java.security.KeyFactory;
import java.security.Security;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class GsonTest {
    static Wallet walletA;
    static Wallet walletB;

    public static void main(String[] args) throws Exception {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        walletA = new Wallet();
        walletB = new Wallet();

        Transaction test = new Transaction(
                walletA.publicKey,
                walletB.publicKey,
                100.0f,
                null
        );

        System.out.println(test.sender.toString());

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println(gson.toJson(test));

        System.out.println(Base64.getDecoder().decode(test.senderKey));
        System.out.println(Base64.getDecoder().decode(test.recipientKey));

        // base64 키 역직렬화
        X509EncodedKeySpec spec = new X509EncodedKeySpec(Base64.getDecoder().decode(test.senderKey));
        KeyFactory kf = KeyFactory.getInstance("ECDSA","BC");
        System.out.println(kf.generatePublic(spec));

    }
}
