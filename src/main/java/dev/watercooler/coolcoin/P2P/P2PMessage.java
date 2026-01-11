package dev.watercooler.coolcoin.P2P;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class P2PMessage {
    private P2PMessageType messageCommand;
    private String messageBody;

    @Override
    public String toString() {
        return String.format("메세지 헤더 타입: %s, 메세지 내용: %s", this.messageCommand, this.messageBody);
    }
}
