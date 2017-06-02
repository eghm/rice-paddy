/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information regarding copyright ownership. The ASF licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
// START SNIPPET: service
package org.kuali.student.kplus2.databus.decorators;

import org.kuali.student.r2.common.exceptions.OperationFailedException;
import ${service_package};
import ${decorator_package};

public class ${service_class}UnsupportedOperationImpl extends ${service_class}Decorator implements ${service_class} {

    /**
     * UnsupportedOperationImpl
     */
    public ${service_class}UnsupportedOperationImpl() {
    }

    @Override
    public ${service_class} getNextDecorator() {
        throw new UnsupportedOperationException ("Unsupported Operation");
    }

    
}
