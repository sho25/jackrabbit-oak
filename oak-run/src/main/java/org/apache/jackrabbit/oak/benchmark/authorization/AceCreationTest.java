begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|benchmark
operator|.
name|authorization
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

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
name|Value
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
name|AccessControlManager
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
name|AccessControlPolicy
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableMap
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
name|JackrabbitAccessControlList
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
name|commons
operator|.
name|jackrabbit
operator|.
name|authorization
operator|.
name|AccessControlUtils
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
name|benchmark
operator|.
name|AbstractTest
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
name|accesscontrol
operator|.
name|AccessControlConstants
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
name|principal
operator|.
name|EveryonePrincipal
import|;
end_import

begin_class
specifier|public
class|class
name|AceCreationTest
extends|extends
name|AbstractTest
block|{
specifier|public
specifier|static
specifier|final
name|int
name|NUMBER_OF_INITIAL_ACE_DEFAULT
init|=
literal|0
decl_stmt|;
specifier|private
specifier|final
name|int
name|numberOfAce
decl_stmt|;
specifier|private
specifier|final
name|int
name|numberOfInitialAce
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|transientWrites
decl_stmt|;
specifier|private
name|String
name|nodePath
decl_stmt|;
specifier|private
name|Session
name|transientSession
decl_stmt|;
specifier|public
name|AceCreationTest
parameter_list|(
name|int
name|numberOfAce
parameter_list|,
name|int
name|numberOfInitialAce
parameter_list|,
name|boolean
name|transientWrites
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|numberOfAce
operator|=
name|numberOfAce
expr_stmt|;
name|this
operator|.
name|numberOfInitialAce
operator|=
name|numberOfInitialAce
expr_stmt|;
name|this
operator|.
name|transientWrites
operator|=
name|transientWrites
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|beforeSuite
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|beforeSuite
argument_list|()
expr_stmt|;
name|Session
name|session
init|=
name|createOrGetSystemSession
argument_list|()
decl_stmt|;
name|nodePath
operator|=
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
literal|"test"
operator|+
operator|new
name|Random
argument_list|()
operator|.
name|nextInt
argument_list|()
argument_list|)
operator|.
name|getPath
argument_list|()
expr_stmt|;
name|save
argument_list|(
name|session
argument_list|,
name|transientWrites
argument_list|)
expr_stmt|;
name|logout
argument_list|(
name|session
argument_list|,
name|transientWrites
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|beforeTest
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|beforeTest
argument_list|()
expr_stmt|;
name|Session
name|session
init|=
name|createOrGetSystemSession
argument_list|()
decl_stmt|;
name|createAce
argument_list|(
name|session
argument_list|,
name|numberOfInitialAce
argument_list|)
expr_stmt|;
name|save
argument_list|(
name|session
argument_list|,
name|transientWrites
argument_list|)
expr_stmt|;
name|logout
argument_list|(
name|session
argument_list|,
name|transientWrites
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|afterTest
parameter_list|()
throws|throws
name|Exception
block|{
name|Session
name|session
init|=
name|createOrGetSystemSession
argument_list|()
decl_stmt|;
name|AccessControlManager
name|acm
init|=
name|session
operator|.
name|getAccessControlManager
argument_list|()
decl_stmt|;
for|for
control|(
name|AccessControlPolicy
name|policy
range|:
name|acm
operator|.
name|getPolicies
argument_list|(
name|nodePath
argument_list|)
control|)
block|{
name|acm
operator|.
name|removePolicy
argument_list|(
name|nodePath
argument_list|,
name|policy
argument_list|)
expr_stmt|;
block|}
name|save
argument_list|(
name|session
argument_list|,
name|transientWrites
argument_list|)
expr_stmt|;
name|super
operator|.
name|afterTest
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
if|if
condition|(
name|transientSession
operator|!=
literal|null
condition|)
block|{
name|transientSession
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|void
name|runTest
parameter_list|()
throws|throws
name|Exception
block|{
name|Session
name|session
init|=
name|createOrGetSystemSession
argument_list|()
decl_stmt|;
name|createAce
argument_list|(
name|session
argument_list|,
name|numberOfAce
argument_list|)
expr_stmt|;
name|save
argument_list|(
name|session
argument_list|,
name|transientWrites
argument_list|)
expr_stmt|;
name|logout
argument_list|(
name|session
argument_list|,
name|transientWrites
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|createAce
parameter_list|(
name|Session
name|session
parameter_list|,
name|int
name|count
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|AccessControlManager
name|acManager
init|=
name|session
operator|.
name|getAccessControlManager
argument_list|()
decl_stmt|;
name|JackrabbitAccessControlList
name|acl
init|=
name|AccessControlUtils
operator|.
name|getAccessControlList
argument_list|(
name|acManager
argument_list|,
name|nodePath
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|count
condition|;
name|i
operator|++
control|)
block|{
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|Value
argument_list|>
name|restrictions
init|=
name|ImmutableMap
operator|.
name|of
argument_list|(
name|AccessControlConstants
operator|.
name|REP_GLOB
argument_list|,
name|session
operator|.
name|getValueFactory
argument_list|()
operator|.
name|createValue
argument_list|(
name|i
operator|+
literal|""
argument_list|)
argument_list|)
decl_stmt|;
name|acl
operator|.
name|addEntry
argument_list|(
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
argument_list|,
name|AccessControlUtils
operator|.
name|privilegesFromNames
argument_list|(
name|acManager
argument_list|,
name|Privilege
operator|.
name|JCR_ADD_CHILD_NODES
argument_list|)
argument_list|,
literal|true
argument_list|,
name|restrictions
argument_list|)
expr_stmt|;
block|}
name|acManager
operator|.
name|setPolicy
argument_list|(
name|nodePath
argument_list|,
name|acl
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|save
parameter_list|(
name|Session
name|session
parameter_list|,
name|boolean
name|transientWrites
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
operator|!
name|transientWrites
condition|)
block|{
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|void
name|logout
parameter_list|(
name|Session
name|session
parameter_list|,
name|boolean
name|transientWrites
parameter_list|)
block|{
if|if
condition|(
operator|!
name|transientWrites
condition|)
block|{
name|session
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|Session
name|createOrGetSystemSession
parameter_list|()
block|{
if|if
condition|(
name|transientWrites
operator|&&
name|transientSession
operator|!=
literal|null
condition|)
block|{
return|return
name|transientSession
return|;
block|}
return|return
operator|(
name|transientSession
operator|=
name|systemLogin
argument_list|()
operator|)
return|;
block|}
block|}
end_class

end_unit

