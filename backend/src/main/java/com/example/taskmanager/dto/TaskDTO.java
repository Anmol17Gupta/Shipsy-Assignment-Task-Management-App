package com.example.taskmanager.dto;

public class TaskDTO {
    private Long id;
    private String title;
    private String priority;
    private Boolean completed;
    private Double estimatedTime;
    private Double actualTime;
    private Double totalTime;

    public TaskDTO() {}

    public TaskDTO(Long id, String title, String priority, Boolean completed,
                   Double estimatedTime, Double actualTime, Double totalTime) {
        this.id = id;
        this.title = title;
        this.priority = priority;
        this.completed = completed;
        this.estimatedTime = estimatedTime;
        this.actualTime = actualTime;
        this.totalTime = totalTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public Double getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(Double estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public Double getActualTime() {
        return actualTime;
    }

    public void setActualTime(Double actualTime) {
        this.actualTime = actualTime;
    }

    public Double getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(Double totalTime) {
        this.totalTime = totalTime;
    }
}
