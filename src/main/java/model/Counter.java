package model;

public class Counter {
    String id;
    Long sequence_value;

    public Counter(){}

    public Counter(String id, Long seq){
        this.id = id;
        this.sequence_value = seq;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getSequence_value() {
        return sequence_value;
    }

    public void setSequence_value(Long sequence_value) {
        this.sequence_value = sequence_value;
    }
}
