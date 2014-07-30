import java.util.regex.Pattern;

import org.craftercms.social.repositories.ugc.UGCRepository;
import org.craftercms.social.services.ugc.UGCService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertTrue;

/**
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/spring/root-context.xml")
public class TestOfStuff {
    private Logger log = LoggerFactory.getLogger(TestOfStuff.class);
    @Autowired
    private UGCService ugcService;
    @Autowired
    private UGCRepository repo;

    @Test
    public void Test() throws Exception {
        log.debug("CRAP");
       assertTrue(Pattern.compile("tenantId:|$where:",Pattern.CASE_INSENSITIVE|Pattern.UNICODE_CASE|Pattern.DOTALL).matcher
           ("{tenantId:\"Crap\",$where:{}," +
           "attributes.name:\"\"," +
           "" + "attributes.tenantId:\"MoreCrap\"}").find());


    }


}


