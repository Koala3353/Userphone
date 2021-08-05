package com.general_hello.commands.commands.VoiceCall;

import java.util.Queue;

public class AudioStorage {
    public static Audio[] audio = new Audio[2000];

    public static class Audio {
        public Queue<byte[]> client1;
        public String client1ID;
        public String message1ID;
        public Queue<byte[]> client2;
        public String client2ID;
        public String message2ID;
        public boolean connected;

        public Audio(Queue<byte[]> client1, String client1ID, String message1ID, Queue<byte[]> client2, String client2ID, String message2ID, boolean connected){
            this.client1 = client1;
            this.client1ID = client1ID;
            this.message1ID = message1ID;
            this.client2 = client2;
            this.client2ID = client2ID;
            this.message2ID = message2ID;
            this.connected = connected;
        }
    }
}
