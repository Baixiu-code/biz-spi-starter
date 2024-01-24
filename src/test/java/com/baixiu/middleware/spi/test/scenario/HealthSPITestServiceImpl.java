package com.baixiu.middleware.spi.test.scenario;

import com.baixiu.middleware.spi.annotation.Extension;
import com.baixiu.middleware.spi.test.IdentityParam;
import org.springframework.stereotype.Service;

/**
 * @author baixiu
 * @date 创建时间 2023/12/7 9:28 PM
 */
@Service
@Extension(appName = "Health")
public class HealthSPITestServiceImpl implements SPITestService {
    @Override
    public void helloSPI(IdentityParam param) {
        System.out.println ("health SPI executed succeed");
    }
    
}
