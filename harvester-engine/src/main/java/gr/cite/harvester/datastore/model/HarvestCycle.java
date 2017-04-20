package gr.cite.harvester.datastore.model;

import java.time.Instant;

public class HarvestCycle {

    private String id;

    private Instant startTime;

    private Instant endTime;

    private Long totalElements;

    private Long newElements;

    private Long updatedElements;

    private Long failedElements;

    private String errorMessage;

    public HarvestCycle() {
        this.startTime = Instant.now();
        this.endTime = null;
        this.totalElements = 0L;
        this.newElements = 0L;
        this.updatedElements = 0L;
        this.failedElements = 0L;
        this.errorMessage = null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public Long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(Long totalElements) {
        this.totalElements = totalElements;
    }

    public Long getNewElements() {
        return newElements;
    }

    public void setNewElements(Long newElements) {
        this.newElements = newElements;
    }

    public Long getUpdatedElements() {
        return updatedElements;
    }

    public void setUpdatedElements(Long updatedElements) {
        this.updatedElements = updatedElements;
    }

    public Long getFailedElements() {
        return failedElements;
    }

    public void setFailedElements(Long failedElements) {
        this.failedElements = failedElements;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

}
