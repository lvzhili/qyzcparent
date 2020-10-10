package com.offcn.webui.vo.resp;

import java.io.Serializable;

public class OrderFormInfoSubmitVo implements Serializable {
    //收货地址id
    private String address;
    // 0代表不开发票  1-代表开发票
    private Byte invoice;
    //发票抬头
    private String invoictitle;
    //订单的备注
    private String remark;


    private Integer rtncount;
    private String accessToken;
    private Integer projectid;
    private Integer returnid;

    public OrderFormInfoSubmitVo() {
    }

    public OrderFormInfoSubmitVo(String address, Byte invoice, String invoictitle, String remark, Integer rtncount, String accessToken, Integer projectid, Integer returnid) {
        this.address = address;
        this.invoice = invoice;
        this.invoictitle = invoictitle;
        this.remark = remark;
        this.rtncount = rtncount;
        this.accessToken = accessToken;
        this.projectid = projectid;
        this.returnid = returnid;
    }

    @Override
    public String toString() {
        return "OrderFormInfoSubmitVo{" +
                "address='" + address + '\'' +
                ", invoice=" + invoice +
                ", invoictitle='" + invoictitle + '\'' +
                ", remark='" + remark + '\'' +
                ", rtncount=" + rtncount +
                ", accessToken='" + accessToken + '\'' +
                ", projectid=" + projectid +
                ", returnid=" + returnid +
                '}';
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Byte getInvoice() {
        return invoice;
    }

    public void setInvoice(Byte invoice) {
        this.invoice = invoice;
    }

    public String getInvoictitle() {
        return invoictitle;
    }

    public void setInvoictitle(String invoictitle) {
        this.invoictitle = invoictitle;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getRtncount() {
        return rtncount;
    }

    public void setRtncount(Integer rtncount) {
        this.rtncount = rtncount;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Integer getProjectid() {
        return projectid;
    }

    public void setProjectid(Integer projectid) {
        this.projectid = projectid;
    }

    public Integer getReturnid() {
        return returnid;
    }

    public void setReturnid(Integer returnid) {
        this.returnid = returnid;
    }
}
