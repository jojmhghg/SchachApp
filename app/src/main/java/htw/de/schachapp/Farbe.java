package htw.de.schachapp;

public enum Farbe {
    SCHWARZ, WEISS;

    public static Farbe parseFarbe(String line) {
        switch(line){
            case "SCHWARZ":
                return SCHWARZ;
            case "WEISS":
                return WEISS;
            default:
                return null;
        }
    }

    @Override
    public String toString() {
        switch(this){
            case SCHWARZ:
                return "SCHWARZ";
            case WEISS:
                return "WEISS";
            default:
                return "";
        }
    }

    public String getAbk() {
        switch(this){
            case SCHWARZ:
                return "(S)";
            case WEISS:
                return "(W)";
            default:
                return "(?)";
        }
    }

    public Farbe andereFarbe() {
        switch(this){
            case SCHWARZ:
                return WEISS;
            case WEISS:
                return SCHWARZ;
            default:
                return null;
        }
    }
}
