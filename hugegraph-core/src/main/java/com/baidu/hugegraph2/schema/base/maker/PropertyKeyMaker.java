package com.baidu.hugegraph2.schema.base.maker;

/**
 * Created by jishilei on 17/3/17.
 */
public interface PropertyKeyMaker extends SchemaMaker {

    public PropertyKeyMaker toText();
    public PropertyKeyMaker toInt();


}