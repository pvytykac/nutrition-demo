package net.pvytykac.nutrition.common;

import lombok.AccessLevel;
import lombok.Getter;
import net.pvytykac.nutrition.common.WebTestClientConfiguration.RestHelper;
import net.pvytykac.nutrition.common.security.internal.SecurityConfiguration;
import net.pvytykac.nutrition.common.security.TestJwtDecoderConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.context.annotation.Import;

@WebMvcTest
@AutoConfigureWebTestClient
@Import({SecurityConfiguration.class, TestJwtDecoderConfiguration.class, WebTestClientConfiguration.class})
public abstract class ControllerTestBase {

    @Getter(AccessLevel.PROTECTED)
    @Autowired
    private RestHelper restHelper;

}
