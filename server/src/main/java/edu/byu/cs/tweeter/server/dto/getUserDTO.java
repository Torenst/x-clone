package edu.byu.cs.tweeter.server.dto;

import edu.byu.cs.tweeter.model.domain.Status;

public class getUserDTO {
    private Status status;
    private long epochTime;

    public getUserDTO(Status status, long epochTime){
        this.status = status;
        this.epochTime = epochTime;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public long getEpochTime() {
        return epochTime;
    }

    public void setEpochTime(long epochTime) {
        this.epochTime = epochTime;
    }
}
