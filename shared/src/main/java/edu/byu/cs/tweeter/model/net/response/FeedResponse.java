package edu.byu.cs.tweeter.model.net.response;

import java.util.List;
import java.util.Objects;

import edu.byu.cs.tweeter.model.domain.Status;

public class FeedResponse extends PagedResponse{

    private List<Status> statusList;

    public FeedResponse(String message){
        super(false,message,false);
    }

    public FeedResponse(List<Status> statusList, boolean hasMorePages){
        super(true, hasMorePages);
        this.statusList = statusList;
    }

    public List<Status> getStatusList(){
        return statusList;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj){
            return true;
        }

        if(obj == null || getClass() != obj.getClass()){
            return false;
        }

        FeedResponse that = (FeedResponse) obj;

        return (Objects.equals(statusList, that.statusList) &&
                Objects.equals(this.getMessage(), that.getMessage()) &&
                this.isSuccess() == that.isSuccess());
    }

    @Override
    public int hashCode(){
        return Objects.hash(statusList);
    }
}
