package dev.watercooler.coolcoin.P2P;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.security.PublicKey;

@Getter
@Setter
@RequiredArgsConstructor
public class P2PNode {
    private PublicKey publicKey;
    private String address;
    private int port;
}
