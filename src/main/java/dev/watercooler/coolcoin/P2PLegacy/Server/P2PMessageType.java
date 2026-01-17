package dev.watercooler.coolcoin.P2PLegacy.Server;

import lombok.Getter;

@Getter
public enum P2PMessageType {
    TRANSACTION_CREATED((byte) 0x01),
    BLOCK_MINED((byte) 0x02),
    CHAIN_SYNC_REQUEST((byte) 0x03);

    private final byte value;

    P2PMessageType(byte value) {
        this.value = value;
    }

    public static P2PMessageType of(byte code) {
        return java.util.Arrays.stream(P2PMessageType.values())
                .filter(v -> v.getValue() == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 코드입니다: " + code));
    }
}
