package com.service;

import com.entity.AccountPIMData;
import com.entity.Condition;
import com.entity.PIM;
import org.checkerframework.checker.units.qual.C;

import java.util.List;
import java.util.Map;

public interface PIMService {

    Map insertPIM(PIM pim);

    boolean updatePIM(PIM pim);

    List<AccountPIMData> getAccountPIMData(Condition condition);
}
