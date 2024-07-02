package edu.byu.cs.tweeter.server.dto;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;

public class postDTO {

    private Status status;
    private List<String> followers;
    private long epochTime;

    public postDTO(Status status, List<String> followers, long epochTime){
        this.status = status;
        this.followers = followers;
        this.epochTime = epochTime;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<String> getFollowers() {
        return followers;
    }

    public void setFollowers(List<String> followers) {
        this.followers = followers;
    }

    public long getEpochTime() {
        return epochTime;
    }

    public void setEpochTime(long epochTime) {
        this.epochTime = epochTime;
    }
}
