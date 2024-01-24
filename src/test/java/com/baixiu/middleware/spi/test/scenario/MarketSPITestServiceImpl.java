package com.baixiu.middleware.spi.test.scenario;

import com.baixiu.middleware.spi.annotation.Extension;
import com.baixiu.middleware.spi.test.IdentityParam;
import org.springframework.stereotype.Service;

/**
 * @author baixiu
 * @date 创建时间 2023/12/7 9:29 PM
 */
@Service
@Extension(appName = "Market")
public class MarketSPITestServiceImpl implements SPITestService {
    @Override
    public void helloSPI(IdentityParam param) {
        System.out.println ("market hello SPI");
    }
}
