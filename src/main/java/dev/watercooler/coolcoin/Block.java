package dev.watercooler.coolcoin;

import dev.watercooler.coolcoin.Transaction.Transaction;
import dev.watercooler.coolcoin.Utility.HashUtil;
import dev.watercooler.coolcoin.Utility.MerkleRoot;

import java.util.ArrayList;
import java.util.Date;

public class Block {
    public String hash;
    public String previousHash;
    public String merkleRoot;
    public ArrayList<Transaction> transactions = new ArrayList<>();
    private long timeStamp;
    private int nonce;

    public Block(String previousHash) {
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
        this.hash = this.calculateHash();
    }

    public String calculateHash() {
        return HashUtil.applySha256(
                previousHash + Long.toString(timeStamp) + Integer.toString(nonce) + merkleRoot
        );
    }

    public void mineBlock(int difficulty) {
        merkleRoot = MerkleRoot.getMerkleRoot(transactions);
        String target = new String(new char[difficulty]).replace('\0', '0');
        while(!hash.substring(0, difficulty).equals(target)) {
            nonce++;
            hash = calculateHash();
        }
        System.out.println("성공적으로 채굴됨: " + hash);
    }

    public boolean addTransaction(Transaction transaction) {
        if(transaction == null) return false;

        if((previousHash != "0")) {
            if((!transaction.processTransaction())) {
                System.out.println("Transaction failed to process. Discarded.");
                return false;
            }
        }

        transactions.add(transaction);

        return true;
    }
}
