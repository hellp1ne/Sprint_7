package json;

public class CancelOrderRequest {
    private int track;

    public CancelOrderRequest(int track) {
        this.track = track;
    }

    public int getTrack() {
        return track;
    }

    public void setTrack(int track) {
        this.track = track;
    }
}