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
        return String.format(
                "--------[%s]--------\n" + "%s",
                this.messageCommand, this.messageBody
        );
    }
}
