package com.baixiu.middleware.spi.test;

import com.baixiu.middleware.spi.annotation.SPIScan;
import com.baixiu.middleware.spi.test.scenario.SPITestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * @author baixiu
 * @date 2023年12月19日
 */
@RunWith(SpringRunner.class)
@Configuration
@SPIScan(basePackages = {"com.baixiu.middleware.spi"})
@SpringBootTest(classes = Application.class)
public class BootTest{

    @Autowired
    private SPITestService spiTestService;

    @Test
    public void testSPIHealth(){
        IdentityParam healIdentityParam=new IdentityParam ();
        healIdentityParam.setIdentityField ("Health");
        spiTestService.helloSPI(healIdentityParam);
    }

    @Test
    public void testSPIMarket(){
        IdentityParam marketIdentityParam=new IdentityParam ();
        marketIdentityParam.setIdentityField ("Market");
        spiTestService.helloSPI(marketIdentityParam);
    }
}
