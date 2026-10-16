
package io.github.demonfiddler.ee.client.util;

import java.util.Arrays;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
class ProfileLogger {

    ProfileLogger(Environment env) {
        System.out.println("Active profiles: " + Arrays.toString(env.getActiveProfiles()));
    }

}
