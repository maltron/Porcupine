/*
 * 
 * Copyright 2014 Mauricio "Maltron" Leal <maltron@gmail.com>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package net.nortlam.porcupine.client.exception;

import java.io.Serializable;
import java.util.logging.Logger;

/**
 *
 * @author Mauricio "Maltron" Leal */
public class ResponseEvent implements Serializable {

    private static final Logger LOG = Logger.getLogger(ResponseEvent.class.getName());
    
    private int code;
    private String reason;
    private String body;

    public ResponseEvent() {
    }

    public ResponseEvent(int code, String reason, String body) {
        this.code = code;
        this.reason = reason;
        this.body = body;
    }

    public int getCode() {
        return code;
    }
    
    public String getReason() {
        return reason;
    }

    public String getBody() {
        return body;
    }
}
