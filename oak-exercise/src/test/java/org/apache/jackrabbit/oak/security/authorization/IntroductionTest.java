begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|security
operator|.
name|authorization
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|security
operator|.
name|AccessControlManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|AbstractSecurityTest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|spi
operator|.
name|security
operator|.
name|authorization
operator|.
name|AuthorizationConfiguration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|spi
operator|.
name|security
operator|.
name|authorization
operator|.
name|permission
operator|.
name|PermissionProvider
import|;
end_import

begin_comment
comment|/**  *<pre>  * Module: Authorization  * =============================================================================  *  * Title: Introduction to Authorization  * -----------------------------------------------------------------------------  *  * Goal:  * Get a basic understanding how authorization is organized in Oak and become  * familiar with distiction between access control management and permission  * evaluation.  *  * Exercises:  *  * - Read JCR Specification and the Oak Documentation with focus on authorization  *   and the distiction between access control management and permission  *   evaluation.  *  *   Question: Can you explain the difference between access control management and permission evaluation?  *  * - Overview  *   Take a look at the package structure in {@code org.apache.jackrabbit.oak.spi.security.authorization}  *   and {@code org.apache.jackrabbit.oak.security.authorization} and try to  *   become familiar with the interfaces and classes defined therein.  *   Look for usages of the key entry points ({@link AccessControlManager} and  *   {@link PermissionProvider} and try to get a big picture.  *  *   Question: Can you identify this distinction when looking at {@link AuthorizationConfiguration}?  *   Question: What can you say about the usage of {@link AccessControlManager} in oak-core and oak-jcr?  *   Question: What can you say about the usage of {@link PermissionProvider} in oak-core and oak-jcr?  *  * - Configuration  *   Look at the default implementation of the {@link AuthorizationConfiguration}  *   and try to identify the configurable parts. Compare your results with the  *   Oak documentation.  *  *   Question: Can you provide a separate list for access control management and permission options?  *   Question: Are the configuration options that affect both parts?  *  * - Pluggability  *   Starting from the {@link AuthorizationConfiguration} again, investigate  *   how the default implementation could be replaced.  *  *   Question: Is it possible to combine different authorization implementations?  *  *</pre>  */
end_comment

begin_class
specifier|public
class|class
name|IntroductionTest
extends|extends
name|AbstractSecurityTest
block|{ }
end_class

end_unit

