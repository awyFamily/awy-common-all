package com.awyFamily.common.test.cloud;

import com.awy.data.base.BaseDomainService;
import com.awy.data.base.IDDDBaseRepository;
import com.awyFamily.common.test.MockBaseTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;

/**
 * 使用S 需要使用set方法注入
 * @author yhw
 * @date 2021-12-10
 */
public class AwyCloudMockBaseTest<S extends BaseDomainService,R extends IDDDBaseRepository> extends MockBaseTest {

    @InjectMocks
    S baseDomainService;
    @Mock
    R baseRepository;

}
