package com.bs.common.web.domain.request;

import lombok.Data;

/**
 * Describe: 分 页 参 数 封 装
 * Author: test-admin
 * CreateTime: 2019/10/23
 * */
@Data
public class PageDomain {

    /**
     * 当前页
     * */
    private Integer page = 1;

    /**
     * 每页数量
     * */
    private Integer limit = 10;

    /**
     * 获取开始的数据行
     * */
    public Integer start(){
        return (this.page-1)*this.limit;
    }

    /**
     * 获取结束的数据行
     * */
    public Integer end(){
        return this.page*this.limit;
    }

}
