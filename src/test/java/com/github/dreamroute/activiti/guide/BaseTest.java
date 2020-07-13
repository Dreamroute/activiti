package com.github.dreamroute.activiti.guide;

import com.github.dreamroute.activiti.domain.Gender;
import org.junit.jupiter.api.Test;

public class BaseTest {

    @Test
    public void genTest() {
        Gender common = Gender.COMMON.common(2000);
        System.err.println(common);

        switch (Gender.MALE) {
            case MALE:
                System.err.println(Gender.MALE.getCode());
                break;
            case FEMALE:
                System.err.println(Gender.FEMALE.getCode());
                break;
            case COMMON:
                System.err.println(Gender.COMMON.getCode());
                break;
            default:
                System.err.println("ERROR");
        }
    }

}
