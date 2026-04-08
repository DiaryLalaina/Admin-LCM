package com.work.cashier.constants;

import com.work.cashier.controller.infoTable.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class VARIABLE_STATIC {

    public static List<String> data = new ArrayList<>();

    public static double sum_amount,sum_paid,sum_expense,sum_remain = 0;

    public static int month = LocalDate.now().getMonthValue();

    public static List<CashOutInfo> cashOutInfoList = new ArrayList<>();

    public static List<DailyUnpaidInfo> dailyUnpaidInfoList = new ArrayList<>();

    public static List<FileUserOrderInfo> fileUserOrderInfoList = new ArrayList<>();

    public static List<StockTransactionInfo> stockTransactionInfos = new ArrayList<>();

    public static List<FollowSaleInfo> followSaleInfos = new ArrayList<>();
}
