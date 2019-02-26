package com.terwergreen.jvue.vendor.j2v8;

import com.eclipsesource.v8.NodeJS;
import com.eclipsesource.v8.V8;

public interface V8Context  {
    V8 getV8() ;
    NodeJS getNodeJS();
}
