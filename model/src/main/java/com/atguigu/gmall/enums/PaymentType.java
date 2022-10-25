package com.atguigu.gmall.enums;

public enum PaymentType {
    ALIPAY("支付宝"),
    WEIXIN("微信" );

    private String comment ;


    PaymentType(String comment ){
        this.comment=comment;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

}
