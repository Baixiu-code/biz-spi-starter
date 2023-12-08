package com.baixiu.middleware.spi.test.scenario;

import com.baixiu.middleware.spi.annotation.Extension;
import com.baixiu.middleware.spi.test.IdentityParam;

/**
 * @author chenfanglin1
 * @date 创建时间 2023/12/7 9:29 PM
 */
@Extension(appName = "Market",scenario = "Market")
public class MarketSPITestServiceImpl implements SPITestService {
    @Override
    public void helloSPI(IdentityParam param) {
        System.out.println ("market hello SPI");
    }
}
