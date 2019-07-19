package cn.slfeng.bbstoper;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ResultContainer {
    private String user;
    private String date;
    private String action;
    public boolean isTopCard(){
        return action.contains("提升卡");
    }
}
