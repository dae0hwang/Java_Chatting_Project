class MessagePacket {
    byte[] sendJsonBytes;
    byte[] serverHeader;
    MessagePacket(byte[] sendJsonBytes, byte[] serverHeader) {
        this.sendJsonBytes = sendJsonBytes;
        this.serverHeader = serverHeader;
    }
}