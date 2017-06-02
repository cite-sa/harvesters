package gr.cite.harvester.datastore.model;

public class HarvestedElementsStatistics {

    private Integer totalElements;

    private Integer newElements;

    private Integer updatedElements;

    private Integer failedElements;

    public Integer getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(Integer totalElements) {
        this.totalElements = totalElements;
    }

    public Integer getNewElements() {
        return newElements;
    }

    public void setNewElements(Integer newElements) {
        this.newElements = newElements;
    }

    public Integer getUpdatedElements() {
        return updatedElements;
    }

    public void setUpdatedElements(Integer updatedElements) {
        this.updatedElements = updatedElements;
    }

    public Integer getFailedElements() {
        return failedElements;
    }

    public void setFailedElements(Integer failedElements) {
        this.failedElements = failedElements;
    }

}
