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

/**
 * @author Mauricio "Maltron" Leal */
public class UnableToFetchResourceException extends Exception {
    
    private ResponseEvent event;

    /**
     * Creates a new instance of <code>UnableToFetchResourceException</code>
     * without detail message.
     */
    public UnableToFetchResourceException() {
    }

    /**
     * Constructs an instance of <code>UnableToFetchResourceException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public UnableToFetchResourceException(ResponseEvent event) {
        super(String.format("%d:%s %s", event.getCode(), event.getReason(), 
                event.getBody() != null ? event.getBody() : ""));
        this.event = event;
    }
    
    public ResponseEvent getEvent() {
        return event;
    }
}
