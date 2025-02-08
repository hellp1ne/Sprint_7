package json;

public class DeleteCourierRequest {
    private String id;

    public DeleteCourierRequest(String id) {
        this.id = id;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}