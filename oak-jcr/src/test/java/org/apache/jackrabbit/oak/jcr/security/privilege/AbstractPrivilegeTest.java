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
name|jcr
operator|.
name|security
operator|.
name|privilege
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|RepositoryException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Session
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Workspace
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|security
operator|.
name|Privilege
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
name|api
operator|.
name|JackrabbitWorkspace
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
name|api
operator|.
name|security
operator|.
name|authorization
operator|.
name|PrivilegeManager
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
name|test
operator|.
name|AbstractJCRTest
import|;
end_import

begin_comment
comment|/**  * Base class for privilege management tests.  */
end_comment

begin_class
specifier|abstract
class|class
name|AbstractPrivilegeTest
extends|extends
name|AbstractJCRTest
block|{
specifier|static
name|PrivilegeManager
name|getPrivilegeManager
parameter_list|(
name|Session
name|session
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Workspace
name|workspace
init|=
name|session
operator|.
name|getWorkspace
argument_list|()
decl_stmt|;
return|return
operator|(
operator|(
name|JackrabbitWorkspace
operator|)
name|workspace
operator|)
operator|.
name|getPrivilegeManager
argument_list|()
return|;
block|}
specifier|static
name|String
index|[]
name|getAggregateNames
parameter_list|(
name|String
modifier|...
name|names
parameter_list|)
block|{
return|return
name|names
return|;
block|}
specifier|static
name|void
name|assertContainsDeclared
parameter_list|(
name|Privilege
name|privilege
parameter_list|,
name|String
name|aggrName
parameter_list|)
block|{
name|boolean
name|found
init|=
literal|false
decl_stmt|;
for|for
control|(
name|Privilege
name|p
range|:
name|privilege
operator|.
name|getDeclaredAggregatePrivileges
argument_list|()
control|)
block|{
if|if
condition|(
name|aggrName
operator|.
name|equals
argument_list|(
name|p
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|found
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
name|assertTrue
argument_list|(
name|found
argument_list|)
expr_stmt|;
block|}
name|void
name|assertPrivilege
parameter_list|(
name|Privilege
name|priv
parameter_list|,
name|String
name|name
parameter_list|,
name|boolean
name|isAggregate
parameter_list|,
name|boolean
name|isAbstract
parameter_list|)
block|{
name|assertNotNull
argument_list|(
name|priv
argument_list|)
expr_stmt|;
name|String
name|privName
init|=
name|priv
operator|.
name|getName
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|privName
argument_list|,
name|privName
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|privName
argument_list|,
name|isAggregate
argument_list|,
name|priv
operator|.
name|isAggregate
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|privName
argument_list|,
name|isAbstract
argument_list|,
name|priv
operator|.
name|isAbstract
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

