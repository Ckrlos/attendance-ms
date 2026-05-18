package cl.duocuc.edutrack.ms.attendance.dto.response;

public class ApiResponse<T> {

    private final T data;

    public ApiResponse(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }
}
