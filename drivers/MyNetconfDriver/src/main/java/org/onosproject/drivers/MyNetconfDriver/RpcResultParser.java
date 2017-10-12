/*
 * Copyright 2016-present Open Networking Laboratory
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
 */
package org.onosproject.drivers.MyNetconfDriver;

public final class RpcResultParser {

    private RpcResultParser() {
        //Not called
    }

    public static String parseXml(final String deviceDescriptionResponse, final String keyWord) {

        int end = deviceDescriptionResponse.lastIndexOf(keyWord);
        end = deviceDescriptionResponse.lastIndexOf('<', end);
        int start = deviceDescriptionResponse.lastIndexOf('>', end);
        if (start > 1 && end > start) {
            return deviceDescriptionResponse.substring(start + 1, end);
        } else {
            return null;
        }
    }

}