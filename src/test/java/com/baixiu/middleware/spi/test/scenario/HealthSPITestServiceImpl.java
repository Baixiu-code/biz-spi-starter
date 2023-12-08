package com.baixiu.middleware.spi.test.scenario;

import com.baixiu.middleware.spi.annotation.Extension;
import com.baixiu.middleware.spi.test.IdentityParam;

/**
 * @author chenfanglin1
 * @date 创建时间 2023/12/7 9:28 PM
 */
@Extension(appName = "Health",scenario = "Health")
public class HealthSPITestServiceImpl implements SPITestService {
    @Override
    public void helloSPI(IdentityParam param) {
        System.out.println ("health SPI");
    }
}
