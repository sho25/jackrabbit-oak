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
name|exercise
operator|.
name|security
operator|.
name|privilege
package|;
end_package

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

begin_comment
comment|/**  *<pre>  * Module: Privilege Management  * =============================================================================  *  * Title: Introduction  * -----------------------------------------------------------------------------  *  * Goal:  * Become familiar with the Privilege Management API defined in  * JCR Access Control Management and by the extensions present in Jackrabbit API.  * Having completed this section should also be familiar with the internals of  * privilege management in Oak.  *  * Exercises:  *  * - Privilege Management in the JCR API  *   Search for interfaces and methods related to privilege management in JSR 283  *   and the corresponding JCR API definitions.  *  *   Question: Can you list all interfaces and methods?  *  * - Privilege Management in Jackrabbit API extensions  *   Search for interfaces and methods related to privilege management in the  *   Jackrabbit API extensions.  *  *   Question: Can you list all interfaces and methods?  *   Question: What can you say wrt the difference in privilege management between JCR and Jackrabbit API?  *  * - Privilege Management in Oak  *   Search the Oak security modules for privilege related extensions.  *  *   Question: Can you list the interfaces, classes, methods?  *   Question: Can you describe the relation between these extensions and the JCR|Jackrabbit API?  *  * Related Exercises:  * -----------------------------------------------------------------------------  *  * - {@link L2_PrivilegeManagementTest}  * - {@link L7_PrivilegeDiscoveryTest}  *  *</pre>  *  */
end_comment

begin_class
specifier|public
class|class
name|L1_IntroductionTest
extends|extends
name|AbstractSecurityTest
block|{   }
end_class

end_unit

