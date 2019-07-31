package com.leo.starter;

public class DemoService {

    DemoProperties demoProperties;

    public String demoFunction(String s){
        return demoProperties.getPrifix()+"-"+s+"-"+demoProperties.getSuffix();
    }

    public DemoProperties getDemoProperties() {
        return demoProperties;
    }

    public DemoService setDemoProperties(DemoProperties demoProperties) {
        this.demoProperties = demoProperties;
        return this;
    }
}
