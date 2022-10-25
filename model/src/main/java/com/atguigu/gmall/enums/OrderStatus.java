package com.atguigu.gmall.enums;

public enum OrderStatus {
    UNPAID("未支付"),
    PAID("已支付" ),
    WAITING_DELEVER("待发货"), //库存扣减成功，只剩发货逻辑，物流服务（电子面单【快递鸟、菜鸟】）
    WAITING_SCHEDULE("等待调货"), //库存扣减失败，需要从另一个仓库再调货，扣另外仓库的库存
    DELEVERED("已发货"),
    CLOSED("已关闭"),
    FINISHED("已完结") ,
    SPLIT("订单已拆分");

    private String comment ;

    public static String getStatusNameByStatus(String status) {
        OrderStatus arrObj[] = OrderStatus.values();
        for (OrderStatus obj : arrObj) {
            if (obj.name().equals(status)) {
                return obj.getComment();
            }
        }
        return "";
    }

    OrderStatus(String comment ){
        this.comment=comment;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
