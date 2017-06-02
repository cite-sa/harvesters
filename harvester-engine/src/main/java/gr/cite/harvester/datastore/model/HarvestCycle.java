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
        return totalElements.longValue();
    }

    public synchronized void setTotalElements(Long totalElements) {
        this.totalElements = new AtomicLong(totalElements);
    }


    public synchronized Long getNewElements() {
        return newElements.longValue();
    }

    public synchronized void setNewElements(Long newElements) {
        this.newElements = new AtomicLong(newElements);
    }

    public synchronized Long getUpdatedElements() {
        return updatedElements.longValue();
    }

    public synchronized void setUpdatedElements(Long updatedElements) {
        this.updatedElements = new AtomicLong(updatedElements);
    }

    public synchronized Long getFailedElements() {
        return failedElements.longValue();
    }

    public synchronized void setFailedElements(Long failedElements) {
        this.failedElements = new AtomicLong(failedElements);
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
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
}
