package StudyMe;

public class Error extends Exception{
    private String message;

    public Error(String message){
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }
}
