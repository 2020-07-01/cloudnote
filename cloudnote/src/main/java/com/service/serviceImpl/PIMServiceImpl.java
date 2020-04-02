package com.service.serviceImpl;

import com.entity.AccountPIMData;
import com.entity.Condition;
import com.entity.PIM;
import com.mapper.PIMMapper;
import com.service.PIMService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @program: PIMServiceImpl
 * @description:
 * @create: 2020-03-15 13:03
 **/
@Service
public class PIMServiceImpl implements PIMService {


    @Autowired
    PIMMapper pimMapper;

    /**
     * 存储pim
     *
     * @param pim
     * @return
     */
    @Override
    public Map insertPIM(PIM pim) {
        return null;
    }

    @Override
    public boolean updatePIM(PIM pim) {
        pimMapper.updatePIM(pim);
        return true;
    }


    @Override
    public List<AccountPIMData> getAccountPIMData(Condition condition) {

        List<AccountPIMData> list = pimMapper.selectAccountPIMData(condition);
        return list;
    }


}
