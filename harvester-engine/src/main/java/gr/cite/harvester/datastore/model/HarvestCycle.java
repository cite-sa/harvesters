package gr.cite.harvester.datastore.model;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

public class HarvestCycle {

    private String id;
    private Instant startTime;
    private Instant endTime;
    private AtomicLong totalElements;
    private AtomicLong newElements;
    private AtomicLong updatedElements;
    private AtomicLong failedElements;
    private String errorMessage;

    public HarvestCycle() {
        this.startTime = Instant.now();
        this.endTime = null;
        this.totalElements = new AtomicLong(0);
        this.newElements = new AtomicLong(0);
        this.updatedElements = new AtomicLong(0);
        this.failedElements = new AtomicLong(0);
        this.errorMessage = null;
    }

    public HarvestCycle(HarvestCycle harvestCycle) {
        this.startTime = harvestCycle.getStartTime();
        this.endTime = harvestCycle.getEndTime();
        this.totalElements = new AtomicLong(harvestCycle.getTotalElements());
        this.newElements = new AtomicLong(harvestCycle.getNewElements());
        this.updatedElements = new AtomicLong(harvestCycle.getUpdatedElements());
        this.failedElements = new AtomicLong(harvestCycle.getFailedElements());
        this.errorMessage = harvestCycle.getErrorMessage();
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

    public synchronized Long getTotalElements() {
        return totalElements.get();
    }

    public synchronized void setTotalElements(Long totalElements) {
        this.totalElements = new AtomicLong(totalElements);
    }


    public synchronized Long getNewElements() {
        return newElements.get();
    }

    public synchronized void setNewElements(Long newElements) {
        this.newElements = new AtomicLong(newElements);
    }

    public synchronized Long getUpdatedElements() {
        return updatedElements.get();
    }

    public synchronized void setUpdatedElements(Long updatedElements) {
        this.updatedElements = new AtomicLong(updatedElements);
    }

    public synchronized Long getFailedElements() {
        return failedElements.get();
    }

    public synchronized void setFailedElements(Long failedElements) {
        this.failedElements = new AtomicLong(failedElements);
    }

    public synchronized String getErrorMessage() {
        return errorMessage;
    }

    public synchronized void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public synchronized Long incrementTotalElements() {
        return this.totalElements.incrementAndGet();
    }

    public synchronized Long incrementNewElements() {
        return this.newElements.incrementAndGet();
    }

    public synchronized Long incrementUpdatedElements() {
        return this.updatedElements.incrementAndGet();
    }

    public synchronized Long incrementFailedElements() {
        return this.failedElements.incrementAndGet();
    }

    public synchronized HarvestCycle copy() {
        return new HarvestCycle(this);
    }
}
