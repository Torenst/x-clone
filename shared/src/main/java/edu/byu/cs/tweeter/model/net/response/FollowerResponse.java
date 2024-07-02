package edu.byu.cs.tweeter.model.net.response;

import java.util.List;
import java.util.Objects;

import edu.byu.cs.tweeter.model.domain.User;

public class FollowerResponse extends PagedResponse{

    private List<User> followers;

    public FollowerResponse(String message){
        super(false, message, false);
    }

    public FollowerResponse(List<User> followers, boolean hasMorePages){
        super(true,hasMorePages);
        this.followers = followers;
    }

    public List<User> getFollowers(){
        return followers;
    }

    @Override
    public boolean equals(Object param) {
        if (this == param) {
            return true;
        }

        if (param == null || getClass() != param.getClass()) {
            return false;
        }

        FollowerResponse that = (FollowerResponse) param;

        return (Objects.equals(followers, that.followers) &&
                Objects.equals(this.getMessage(), that.getMessage()) &&
                this.isSuccess() == that.isSuccess());
    }

    @Override
    public int hashCode(){
        return Objects.hash(followers);
    }
}
