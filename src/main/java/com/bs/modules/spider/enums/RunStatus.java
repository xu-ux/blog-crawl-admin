package com.bs.modules.spider.enums;

import java.util.Arrays;

/**
 * @Author xucl
 * @Version 1.0
 * @ClassName RunStatus
 * @Description
 * @date 2021/8/5
 */
public enum  RunStatus {

    INIT(-1,"待开始"),

    STOP(0,"停止"),

    ING(1,"进行中"),

    ERROR(2,"异常"),

    COMPLETE(3,"已完成"),

    OTHER(66,"其它")
    ;

    public static RunStatus ofId(int id){
        return Arrays.asList(RunStatus.values()).stream().filter(s -> s.getId() == id).findFirst().orElse(OTHER);
    }
    /**
     * Id
     */
    private int id;
    /**
     * 名称
     */
    private String name;

    RunStatus(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
