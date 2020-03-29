package com.mapper;

import com.entity.AccountPIMData;
import com.entity.Condition;
import com.entity.PIM;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PIMMapper {

    int updatePIM(PIM pim);

    List<AccountPIMData> selectAccountPIMData(Condition condition);
}
