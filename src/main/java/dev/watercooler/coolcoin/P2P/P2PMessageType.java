package dev.watercooler.coolcoin.P2P;

import lombok.Getter;

@Getter
public enum P2PMessageType {
    HANDSHAKE_VERSION((byte)0x01),
    HANDSHAKE_VERACK((byte)0x02),
    HEARTBEAT_PING((byte)0x03),
    HEARTBEAT_PONG((byte)0x04);
//    TRANSACTION_CREATED((byte) 0x02),
//    BLOCK_MINED((byte) 0x03),
//    CHAIN_SYNC_REQUEST((byte) 0x04);

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
