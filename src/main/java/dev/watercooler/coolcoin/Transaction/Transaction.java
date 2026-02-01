package dev.watercooler.coolcoin.Transaction;

import dev.watercooler.coolcoin.BlockChain;
import dev.watercooler.coolcoin.Utility.HashUtil;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Base64;

public class Transaction {
    public String transactionId;
    public transient PublicKey sender;
    public transient PublicKey recipient;

    public String senderKey;
    public String recipientKey;

    public float value;
    public byte[] signature;

    public ArrayList<TransactionInput> inputs = new ArrayList<>();
    public ArrayList<TransactionOutput> outputs = new ArrayList<>();

    private static int sequence = 0;

    public Transaction(PublicKey from, PublicKey to, float value, ArrayList<TransactionInput> inputs) {
        this.sender = from;
        this.recipient = to;
        this.value = value;
        this.inputs = inputs;

        this.senderKey = Base64.getEncoder().encodeToString(sender.getEncoded());
        this.recipientKey = Base64.getEncoder().encodeToString(recipient.getEncoded());
    }

    private String calculateHash() {
        sequence++;
        return HashUtil.applySha256(
                HashUtil.getStringFromKey(sender) + HashUtil.getStringFromKey(recipient) + Float.toString(value) + sequence
        );
    }

    public void generateSignature(PrivateKey privateKey) {
        String data = HashUtil.getStringFromKey(sender) + HashUtil.getStringFromKey(recipient) + Float.toString(value)	;
        signature = HashUtil.applyECDSASig(privateKey,data);
    }

    public boolean verifySignature() {
        String data = HashUtil.getStringFromKey(sender) + HashUtil.getStringFromKey(recipient) + Float.toString(value)	;
        return HashUtil.verifyECDSASig(sender, data, signature);
    }

    public boolean processTransaction() {
        if(!verifySignature()) {
            return false;
        }

        for(TransactionInput i : inputs) {
            i.UTXO = BlockChain.UTXOs.get(i.transactionOutputId);
        }

        if(this.getInputsValue() < BlockChain.minimumTransaction) {
            return false;
        }

        float leftOver = this.getInputsValue() - value;
        transactionId = calculateHash();
        outputs.add(new TransactionOutput(this.recipient, value,transactionId));
        outputs.add(new TransactionOutput(this.sender, leftOver,transactionId));

        for(TransactionOutput o : outputs) {
            BlockChain.UTXOs.put(o.id , o);
        }

        for(TransactionInput i : inputs) {
            if(i.UTXO == null) continue;
            BlockChain.UTXOs.remove(i.UTXO.id);
        }

        return true;
    }

    public float getInputsValue() {
        float total = 0;
        for(TransactionInput i : inputs) {
            if(i.UTXO == null) continue;
            total += i.UTXO.value;
        }
        return total;
    }

    public float getOutputsValue() {
        float total = 0;
        for(TransactionOutput o : outputs) {
            total += o.value;
        }
        return total;
    }
}
