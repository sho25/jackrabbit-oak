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
name|test
operator|.
name|AbstractJCRTest
import|;
end_import

begin_comment
comment|/**  *<pre>  * Module: Privilege Management | Authorization  * =============================================================================  *  * Title: Privilege Discovery  * -----------------------------------------------------------------------------  *  * Goal:  * The aim of this exercise is to make you familiar on how to discover privileges  * granted for a given {@link javax.jcr.Session} or a given set of {@link java.security.Principal}s.  * After having completed this exercise you should be able to explain the difference  * compare to permission discovery as well as the benefit/drawback of using  * this API.  *  * Exercises:  *  * - {@link #testHasPrivileges()}  *   TODO  *  * - {@link #testGetPrivileges()}  *   TODO  *  * - {@link #testCanAddNode()}  *   TODO  *  *  * Related Exercises:  * -----------------------------------------------------------------------------  *  * - {@link org.apache.jackrabbit.oak.security.authorization.permission.L2_PermissionDiscoveryTest}  * - {@link org.apache.jackrabbit.oak.security.authorization.permission.L4_PrivilegesAndPermissionsTest}  *  *</pre>  *  * @see TODO  */
end_comment

begin_class
specifier|public
class|class
name|L7_PrivilegeDiscoveryTest
extends|extends
name|AbstractJCRTest
block|{
specifier|public
name|void
name|testHasPrivileges
parameter_list|()
block|{
comment|// TODO
block|}
specifier|public
name|void
name|testGetPrivileges
parameter_list|()
block|{
comment|// TODO
block|}
specifier|public
name|void
name|testCanAddNode
parameter_list|()
block|{
comment|// TODO
block|}
comment|// TODO; diff wrt session.haspermission
comment|// TODO: Acmgr.hasPrivilege || getPrivileges
block|}
end_class

end_unit

