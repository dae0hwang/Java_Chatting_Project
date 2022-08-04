public enum Type {
    RESISTERNAME(1111), MESSAGETOSERVER(2222), MESSAGETOCLIENT(3333), CLIENTCLOSEMESSAGE(4444);
    private final int value;
    Type(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

<<<<<<< HEAD
}
=======
}
>>>>>>> origin/master
