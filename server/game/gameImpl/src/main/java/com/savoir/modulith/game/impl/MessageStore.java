/*
 * Copyright (c) 2012-2024 Savoir Technologies, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.savoir.modulith.game.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class MessageStore {

    private Map<String, Stack<String>> messages;

    public MessageStore() {
        this.messages = new HashMap<>();
    }

    public String getMessage(String key) {
        Stack<String> stack = this.messages.get(key);
        if (stack == null) {
            return null;
        }
        if (stack.isEmpty()) {
            return null;
        }
        return stack.pop();
    }

    public void putMessage(String key, String message) {
        Stack<String> stack = this.messages.get(key);
        if (stack == null) {
            stack = new Stack<>();
        }
        stack.push(message);
        messages.put(key, stack);
    }



}
