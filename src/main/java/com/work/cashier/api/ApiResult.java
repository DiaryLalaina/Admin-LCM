package com.work.cashier.api; // <-- adapte le package selon ton projet

import java.util.List;

public class ApiResult<T> {
    private final int total;
    private final List<T> list;

    public ApiResult(int total, List<T> list) {
        this.total = total;
        this.list = list;
    }

    public int getTotal() {
        return total;
    }

    public List<T> getList() {
        return list;
    }
}
