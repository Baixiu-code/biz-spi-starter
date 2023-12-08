package com.baixiu.middleware.spi.test.scenario;

import com.baixiu.middleware.spi.annotation.SPIDefine;
import com.baixiu.middleware.spi.test.IdentityParam;

/**
 * @author chenfanglin1
 * @date 创建时间 2023/12/7 9:26 PM
 */
@SPIDefine
public interface SPITestService {

    void helloSPI(IdentityParam param);

}
