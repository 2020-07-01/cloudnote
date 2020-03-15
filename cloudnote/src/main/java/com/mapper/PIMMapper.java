package com.mapper;

import com.entity.PIM;
import org.springframework.stereotype.Repository;

@Repository
public interface PIMMapper {

    int updatePIM(PIM pim);
}
