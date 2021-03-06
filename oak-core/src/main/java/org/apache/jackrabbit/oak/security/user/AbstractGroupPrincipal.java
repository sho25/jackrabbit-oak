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
name|user
package|;
end_package

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|Principal
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Enumeration
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Function
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
name|base
operator|.
name|Predicates
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
name|Iterators
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
name|principal
operator|.
name|GroupPrincipal
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
name|user
operator|.
name|Authorizable
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
name|user
operator|.
name|UserManager
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
name|api
operator|.
name|Tree
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
name|namepath
operator|.
name|NamePathMapper
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

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * Base class for {@code Group} principals.  */
end_comment

begin_class
specifier|abstract
class|class
name|AbstractGroupPrincipal
extends|extends
name|TreeBasedPrincipal
implements|implements
name|GroupPrincipal
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|AbstractGroupPrincipal
operator|.
name|class
argument_list|)
decl_stmt|;
name|AbstractGroupPrincipal
parameter_list|(
annotation|@
name|NotNull
name|String
name|principalName
parameter_list|,
annotation|@
name|NotNull
name|Tree
name|groupTree
parameter_list|,
annotation|@
name|NotNull
name|NamePathMapper
name|namePathMapper
parameter_list|)
block|{
name|super
argument_list|(
name|principalName
argument_list|,
name|groupTree
argument_list|,
name|namePathMapper
argument_list|)
expr_stmt|;
block|}
name|AbstractGroupPrincipal
parameter_list|(
annotation|@
name|NotNull
name|String
name|principalName
parameter_list|,
annotation|@
name|NotNull
name|String
name|groupPath
parameter_list|,
annotation|@
name|NotNull
name|NamePathMapper
name|namePathMapper
parameter_list|)
block|{
name|super
argument_list|(
name|principalName
argument_list|,
name|groupPath
argument_list|,
name|namePathMapper
argument_list|)
expr_stmt|;
block|}
specifier|abstract
name|UserManager
name|getUserManager
parameter_list|()
function_decl|;
specifier|abstract
name|boolean
name|isEveryone
parameter_list|()
throws|throws
name|RepositoryException
function_decl|;
specifier|abstract
name|boolean
name|isMember
parameter_list|(
annotation|@
name|NotNull
name|Authorizable
name|authorizable
parameter_list|)
throws|throws
name|RepositoryException
function_decl|;
annotation|@
name|NotNull
specifier|abstract
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
name|getMembers
parameter_list|()
throws|throws
name|RepositoryException
function_decl|;
comment|//--------------------------------------------------------------< Group>---
annotation|@
name|Override
specifier|public
name|boolean
name|isMember
parameter_list|(
annotation|@
name|NotNull
name|Principal
name|principal
parameter_list|)
block|{
name|boolean
name|isMember
init|=
literal|false
decl_stmt|;
try|try
block|{
comment|// shortcut for everyone group -> avoid collecting all members
comment|// as all users and groups are member of everyone.
if|if
condition|(
name|isEveryone
argument_list|()
condition|)
block|{
name|isMember
operator|=
operator|!
name|EveryonePrincipal
operator|.
name|NAME
operator|.
name|equals
argument_list|(
name|principal
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Authorizable
name|a
init|=
name|getUserManager
argument_list|()
operator|.
name|getAuthorizable
argument_list|(
name|principal
argument_list|)
decl_stmt|;
if|if
condition|(
name|a
operator|!=
literal|null
condition|)
block|{
name|isMember
operator|=
name|isMember
argument_list|(
name|a
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Failed to determine group membership: {}"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// principal doesn't represent a known authorizable or an error occurred.
return|return
name|isMember
return|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|Enumeration
argument_list|<
name|?
extends|extends
name|Principal
argument_list|>
name|members
parameter_list|()
block|{
specifier|final
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
name|members
decl_stmt|;
try|try
block|{
name|members
operator|=
name|getMembers
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
comment|// should not occur.
name|String
name|msg
init|=
literal|"Unable to retrieve Group members: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
decl_stmt|;
name|log
operator|.
name|error
argument_list|(
name|msg
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|msg
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|Iterator
argument_list|<
name|Principal
argument_list|>
name|principals
init|=
name|Iterators
operator|.
name|transform
argument_list|(
name|members
argument_list|,
operator|new
name|Function
argument_list|<
name|Authorizable
argument_list|,
name|Principal
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Principal
name|apply
parameter_list|(
name|Authorizable
name|authorizable
parameter_list|)
block|{
if|if
condition|(
name|authorizable
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
try|try
block|{
return|return
name|authorizable
operator|.
name|getPrincipal
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
name|String
name|msg
init|=
literal|"Internal error while retrieving principal: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
decl_stmt|;
name|log
operator|.
name|error
argument_list|(
name|msg
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|msg
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
argument_list|)
decl_stmt|;
return|return
name|Iterators
operator|.
name|asEnumeration
argument_list|(
name|Iterators
operator|.
name|filter
argument_list|(
name|principals
argument_list|,
name|Predicates
operator|.
expr|<
name|Object
operator|>
name|notNull
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

