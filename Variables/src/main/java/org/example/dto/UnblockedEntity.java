package org.example.dto;

import java.util.List;

public class UnblockedEntity {
    private List<Long> unblockedEntityList;

    public UnblockedEntity(){}

    public void addUnblockedEntity(Long id){
        this.unblockedEntityList.add(id);
    }

    public void setUnblockedEntityList(List<Long> unblockedEntityList) {
        this.unblockedEntityList = unblockedEntityList;
    }

    public List<Long> getUnblockedEntityList(){
        return this.unblockedEntityList;
    }

    @Override
    public String toString() {
        return "UnblockedEntity{" +
                "unblockedEntityList=" + unblockedEntityList +
                '}';
    }
}
