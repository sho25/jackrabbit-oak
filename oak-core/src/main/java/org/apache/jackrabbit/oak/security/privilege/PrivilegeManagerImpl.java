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
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|CheckForNull
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|InvalidItemStateException
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
name|UnsupportedRepositoryOperationException
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
name|AccessControlException
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
name|oak
operator|.
name|api
operator|.
name|Root
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
name|core
operator|.
name|RootImpl
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
name|privilege
operator|.
name|PrivilegeDefinition
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
comment|/**  * {@code PrivilegeManager} implementation reading from and storing privileges  * into the repository.  */
end_comment

begin_class
specifier|public
class|class
name|PrivilegeManagerImpl
implements|implements
name|PrivilegeManager
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
name|PrivilegeManagerImpl
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Root
name|root
decl_stmt|;
specifier|private
specifier|final
name|NamePathMapper
name|namePathMapper
decl_stmt|;
specifier|public
name|PrivilegeManagerImpl
parameter_list|(
name|Root
name|root
parameter_list|,
name|NamePathMapper
name|namePathMapper
parameter_list|)
block|{
name|this
operator|.
name|root
operator|=
name|root
expr_stmt|;
name|this
operator|.
name|namePathMapper
operator|=
name|namePathMapper
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Privilege
index|[]
name|getRegisteredPrivileges
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Set
argument_list|<
name|Privilege
argument_list|>
name|privileges
init|=
operator|new
name|HashSet
argument_list|<
name|Privilege
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|PrivilegeDefinition
name|def
range|:
name|getPrivilegeDefinitions
argument_list|()
control|)
block|{
name|privileges
operator|.
name|add
argument_list|(
name|getPrivilege
argument_list|(
name|def
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|privileges
operator|.
name|toArray
argument_list|(
operator|new
name|Privilege
index|[
name|privileges
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Privilege
name|getPrivilege
parameter_list|(
name|String
name|privilegeName
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|PrivilegeDefinition
name|def
init|=
name|getPrivilegeDefinition
argument_list|(
name|getOakName
argument_list|(
name|privilegeName
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|def
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|AccessControlException
argument_list|(
literal|"No such privilege "
operator|+
name|privilegeName
argument_list|)
throw|;
block|}
else|else
block|{
return|return
name|getPrivilege
argument_list|(
name|def
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|Privilege
name|registerPrivilege
parameter_list|(
name|String
name|privilegeName
parameter_list|,
name|boolean
name|isAbstract
parameter_list|,
name|String
index|[]
name|declaredAggregateNames
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|root
operator|.
name|hasPendingChanges
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|InvalidItemStateException
argument_list|(
literal|"Attempt to register a new privilege while there are pending changes."
argument_list|)
throw|;
block|}
if|if
condition|(
name|privilegeName
operator|==
literal|null
operator|||
name|privilegeName
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
literal|"Invalid privilege name "
operator|+
name|privilegeName
argument_list|)
throw|;
block|}
name|PrivilegeDefinitionImpl
name|definition
init|=
operator|new
name|PrivilegeDefinitionImpl
argument_list|(
name|getOakName
argument_list|(
name|privilegeName
argument_list|)
argument_list|,
name|isAbstract
argument_list|,
name|getOakNames
argument_list|(
name|declaredAggregateNames
argument_list|)
argument_list|)
decl_stmt|;
name|PrivilegeDefinitionWriter
name|writer
init|=
operator|new
name|PrivilegeDefinitionWriter
argument_list|(
name|getWriteRoot
argument_list|()
argument_list|)
decl_stmt|;
name|writer
operator|.
name|writeDefinition
argument_list|(
name|definition
argument_list|)
expr_stmt|;
comment|// refresh the current root to make sure the definition is visible
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
return|return
name|getPrivilege
argument_list|(
name|definition
argument_list|)
return|;
block|}
comment|//------------------------------------------------------------< private>---
annotation|@
name|Nonnull
specifier|private
name|Root
name|getWriteRoot
parameter_list|()
throws|throws
name|UnsupportedRepositoryOperationException
block|{
if|if
condition|(
name|root
operator|instanceof
name|RootImpl
condition|)
block|{
return|return
operator|(
operator|(
name|RootImpl
operator|)
name|root
operator|)
operator|.
name|getLatest
argument_list|()
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|UnsupportedRepositoryOperationException
argument_list|(
literal|"Privilege registration not supported"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Nonnull
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|getOakNames
parameter_list|(
name|String
index|[]
name|jcrNames
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|oakNames
decl_stmt|;
if|if
condition|(
name|jcrNames
operator|==
literal|null
operator|||
name|jcrNames
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|oakNames
operator|=
name|Collections
operator|.
name|emptySet
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|oakNames
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|(
name|jcrNames
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|jcrName
range|:
name|jcrNames
control|)
block|{
name|String
name|oakName
init|=
name|getOakName
argument_list|(
name|jcrName
argument_list|)
decl_stmt|;
name|oakNames
operator|.
name|add
argument_list|(
name|oakName
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|oakNames
return|;
block|}
annotation|@
name|Nonnull
specifier|private
name|String
name|getOakName
parameter_list|(
name|String
name|jcrName
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|jcrName
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|AccessControlException
argument_list|(
literal|"Invalid privilege name 'null'"
argument_list|)
throw|;
block|}
return|return
name|namePathMapper
operator|.
name|getOakName
argument_list|(
name|jcrName
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
specifier|private
name|Privilege
name|getPrivilege
parameter_list|(
name|PrivilegeDefinition
name|definition
parameter_list|)
block|{
return|return
operator|new
name|PrivilegeImpl
argument_list|(
name|definition
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
specifier|private
name|PrivilegeDefinition
index|[]
name|getPrivilegeDefinitions
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|PrivilegeDefinition
argument_list|>
name|definitions
init|=
name|getReader
argument_list|()
operator|.
name|readDefinitions
argument_list|()
decl_stmt|;
return|return
name|definitions
operator|.
name|values
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|PrivilegeDefinition
index|[
name|definitions
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
annotation|@
name|CheckForNull
specifier|private
name|PrivilegeDefinition
name|getPrivilegeDefinition
parameter_list|(
name|String
name|oakName
parameter_list|)
block|{
return|return
name|getReader
argument_list|()
operator|.
name|readDefinition
argument_list|(
name|oakName
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
specifier|private
name|PrivilegeDefinitionReader
name|getReader
parameter_list|()
block|{
return|return
operator|new
name|PrivilegeDefinitionReader
argument_list|(
name|root
argument_list|)
return|;
block|}
comment|//--------------------------------------------------------------------------
comment|/**      * Privilege implementation based on a {@link org.apache.jackrabbit.oak.spi.security.privilege.PrivilegeDefinition}.      */
specifier|private
specifier|final
class|class
name|PrivilegeImpl
implements|implements
name|Privilege
block|{
specifier|private
specifier|final
name|PrivilegeDefinition
name|definition
decl_stmt|;
specifier|private
name|PrivilegeImpl
parameter_list|(
name|PrivilegeDefinition
name|definition
parameter_list|)
block|{
name|this
operator|.
name|definition
operator|=
name|definition
expr_stmt|;
block|}
comment|//------------------------------------------------------< Privilege>---
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|namePathMapper
operator|.
name|getJcrName
argument_list|(
name|definition
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isAbstract
parameter_list|()
block|{
return|return
name|definition
operator|.
name|isAbstract
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isAggregate
parameter_list|()
block|{
return|return
operator|!
name|definition
operator|.
name|getDeclaredAggregateNames
argument_list|()
operator|.
name|isEmpty
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Privilege
index|[]
name|getDeclaredAggregatePrivileges
parameter_list|()
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|declaredAggregateNames
init|=
name|definition
operator|.
name|getDeclaredAggregateNames
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|Privilege
argument_list|>
name|declaredAggregates
init|=
operator|new
name|HashSet
argument_list|<
name|Privilege
argument_list|>
argument_list|(
name|declaredAggregateNames
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|oakName
range|:
name|declaredAggregateNames
control|)
block|{
if|if
condition|(
name|oakName
operator|.
name|equals
argument_list|(
name|definition
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Found cyclic privilege aggregation -> ignore declared aggregate "
operator|+
name|oakName
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|PrivilegeDefinition
name|def
init|=
name|getPrivilegeDefinition
argument_list|(
name|oakName
argument_list|)
decl_stmt|;
if|if
condition|(
name|def
operator|!=
literal|null
condition|)
block|{
name|declaredAggregates
operator|.
name|add
argument_list|(
name|getPrivilege
argument_list|(
name|def
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Invalid privilege '{}' in declared aggregates of '{}'"
argument_list|,
name|oakName
argument_list|,
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|declaredAggregates
operator|.
name|toArray
argument_list|(
operator|new
name|Privilege
index|[
name|declaredAggregates
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Privilege
index|[]
name|getAggregatePrivileges
parameter_list|()
block|{
name|Set
argument_list|<
name|Privilege
argument_list|>
name|aggr
init|=
operator|new
name|HashSet
argument_list|<
name|Privilege
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Privilege
name|decl
range|:
name|getDeclaredAggregatePrivileges
argument_list|()
control|)
block|{
name|aggr
operator|.
name|add
argument_list|(
name|decl
argument_list|)
expr_stmt|;
if|if
condition|(
name|decl
operator|.
name|isAggregate
argument_list|()
condition|)
block|{
comment|// TODO: defensive check to prevent circular aggregation that might occur with inconsistent repositories
name|aggr
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|decl
operator|.
name|getAggregatePrivileges
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|aggr
operator|.
name|toArray
argument_list|(
operator|new
name|Privilege
index|[
name|aggr
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
comment|//---------------------------------------------------------< Object>---
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|definition
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|==
name|this
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|o
operator|instanceof
name|PrivilegeImpl
condition|)
block|{
return|return
name|definition
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|PrivilegeImpl
operator|)
name|o
operator|)
operator|.
name|definition
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|definition
operator|.
name|getName
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

