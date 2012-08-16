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
name|HashMap
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
name|Nonnull
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
name|CommitFailedException
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
name|ContentSession
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
name|CoreValueFactory
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
name|core
operator|.
name|DefaultConflictHandler
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
name|PrivilegeProvider
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
name|util
operator|.
name|NodeUtil
import|;
end_import

begin_comment
comment|/**  * PrivilegeRegistry... TODO  *  *  * TODO: define if/how built-in privileges are reflected in the mk  * TODO: define if custom privileges are read with editing content session (thus enforcing read permissions)  *  * FIXME: Session#refresh should refresh privileges exposed  */
end_comment

begin_class
specifier|public
class|class
name|PrivilegeRegistry
implements|implements
name|PrivilegeProvider
implements|,
name|PrivilegeConstants
block|{
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|SIMPLE_PRIVILEGES
init|=
operator|new
name|String
index|[]
block|{
name|JCR_READ
block|,
name|REP_ADD_PROPERTIES
block|,
name|REP_ALTER_PROPERTIES
block|,
name|REP_REMOVE_PROPERTIES
block|,
name|JCR_ADD_CHILD_NODES
block|,
name|JCR_REMOVE_CHILD_NODES
block|,
name|JCR_REMOVE_NODE
block|,
name|JCR_READ_ACCESS_CONTROL
block|,
name|JCR_MODIFY_ACCESS_CONTROL
block|,
name|JCR_NODE_TYPE_MANAGEMENT
block|,
name|JCR_VERSION_MANAGEMENT
block|,
name|JCR_LOCK_MANAGEMENT
block|,
name|JCR_LIFECYCLE_MANAGEMENT
block|,
name|JCR_RETENTION_MANAGEMENT
block|,
name|JCR_WORKSPACE_MANAGEMENT
block|,
name|JCR_NODE_TYPE_DEFINITION_MANAGEMENT
block|,
name|JCR_NAMESPACE_MANAGEMENT
block|,
name|REP_PRIVILEGE_MANAGEMENT
block|}
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|AGGREGATE_PRIVILEGES
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
argument_list|()
decl_stmt|;
static|static
block|{
name|AGGREGATE_PRIVILEGES
operator|.
name|put
argument_list|(
name|JCR_MODIFY_PROPERTIES
argument_list|,
operator|new
name|String
index|[]
block|{
name|REP_ADD_PROPERTIES
block|,
name|REP_ALTER_PROPERTIES
block|,
name|REP_REMOVE_PROPERTIES
block|}
argument_list|)
expr_stmt|;
name|AGGREGATE_PRIVILEGES
operator|.
name|put
argument_list|(
name|JCR_WRITE
argument_list|,
operator|new
name|String
index|[]
block|{
name|JCR_MODIFY_PROPERTIES
block|,
name|JCR_ADD_CHILD_NODES
block|,
name|JCR_REMOVE_CHILD_NODES
block|,
name|JCR_REMOVE_NODE
block|}
argument_list|)
expr_stmt|;
name|AGGREGATE_PRIVILEGES
operator|.
name|put
argument_list|(
name|REP_WRITE
argument_list|,
operator|new
name|String
index|[]
block|{
name|JCR_WRITE
block|,
name|JCR_NODE_TYPE_MANAGEMENT
block|}
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|final
name|ContentSession
name|contentSession
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|PrivilegeDefinition
argument_list|>
name|definitions
decl_stmt|;
specifier|public
name|PrivilegeRegistry
parameter_list|(
name|ContentSession
name|contentSession
parameter_list|)
block|{
name|this
operator|.
name|contentSession
operator|=
name|contentSession
expr_stmt|;
name|this
operator|.
name|definitions
operator|=
name|getAllDefinitions
argument_list|(
operator|new
name|PrivilegeDefinitionReader
argument_list|(
name|contentSession
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|PrivilegeDefinition
argument_list|>
name|getAllDefinitions
parameter_list|(
name|PrivilegeDefinitionReader
name|reader
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|PrivilegeDefinition
argument_list|>
name|definitions
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|PrivilegeDefinition
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|privilegeName
range|:
name|SIMPLE_PRIVILEGES
control|)
block|{
name|PrivilegeDefinition
name|def
init|=
operator|new
name|PrivilegeDefinitionImpl
argument_list|(
name|privilegeName
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|definitions
operator|.
name|put
argument_list|(
name|privilegeName
argument_list|,
name|def
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|privilegeName
range|:
name|AGGREGATE_PRIVILEGES
operator|.
name|keySet
argument_list|()
control|)
block|{
name|PrivilegeDefinition
name|def
init|=
operator|new
name|PrivilegeDefinitionImpl
argument_list|(
name|privilegeName
argument_list|,
literal|false
argument_list|,
name|AGGREGATE_PRIVILEGES
operator|.
name|get
argument_list|(
name|privilegeName
argument_list|)
argument_list|)
decl_stmt|;
name|definitions
operator|.
name|put
argument_list|(
name|privilegeName
argument_list|,
name|def
argument_list|)
expr_stmt|;
block|}
name|definitions
operator|.
name|putAll
argument_list|(
name|reader
operator|.
name|readDefinitions
argument_list|()
argument_list|)
expr_stmt|;
name|updateJcrAllPrivilege
argument_list|(
name|definitions
argument_list|)
expr_stmt|;
return|return
name|definitions
return|;
block|}
specifier|private
specifier|static
name|void
name|updateJcrAllPrivilege
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|PrivilegeDefinition
argument_list|>
name|definitions
parameter_list|)
block|{
comment|// TODO: add proper implementation taking custom privileges into account.
name|definitions
operator|.
name|put
argument_list|(
name|JCR_ALL
argument_list|,
operator|new
name|PrivilegeDefinitionImpl
argument_list|(
name|JCR_ALL
argument_list|,
literal|false
argument_list|,
name|JCR_READ
argument_list|,
name|JCR_READ_ACCESS_CONTROL
argument_list|,
name|JCR_MODIFY_ACCESS_CONTROL
argument_list|,
name|JCR_VERSION_MANAGEMENT
argument_list|,
name|JCR_LOCK_MANAGEMENT
argument_list|,
name|JCR_LIFECYCLE_MANAGEMENT
argument_list|,
name|JCR_RETENTION_MANAGEMENT
argument_list|,
name|JCR_WORKSPACE_MANAGEMENT
argument_list|,
name|JCR_NODE_TYPE_DEFINITION_MANAGEMENT
argument_list|,
name|JCR_NAMESPACE_MANAGEMENT
argument_list|,
name|REP_PRIVILEGE_MANAGEMENT
argument_list|,
name|REP_WRITE
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//--------------------------------------------------< PrivilegeProvider>---
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|PrivilegeDefinition
index|[]
name|getPrivilegeDefinitions
parameter_list|()
block|{
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
name|Override
specifier|public
name|PrivilegeDefinition
name|getPrivilegeDefinition
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|definitions
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|PrivilegeDefinition
name|registerDefinition
parameter_list|(
specifier|final
name|String
name|privilegeName
parameter_list|,
specifier|final
name|boolean
name|isAbstract
parameter_list|,
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|declaredAggregateNames
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|PrivilegeDefinition
name|definition
init|=
operator|new
name|PrivilegeDefinitionImpl
argument_list|(
name|privilegeName
argument_list|,
name|isAbstract
argument_list|,
name|declaredAggregateNames
argument_list|)
decl_stmt|;
name|internalRegisterDefinitions
argument_list|(
name|definition
argument_list|)
expr_stmt|;
return|return
name|definition
return|;
block|}
specifier|public
name|void
name|registerDefinition
parameter_list|(
name|PrivilegeDefinition
name|definition
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|PrivilegeDefinition
name|toRegister
decl_stmt|;
if|if
condition|(
name|definition
operator|instanceof
name|PrivilegeDefinitionImpl
condition|)
block|{
name|toRegister
operator|=
name|definition
expr_stmt|;
block|}
else|else
block|{
name|toRegister
operator|=
operator|new
name|PrivilegeDefinitionImpl
argument_list|(
name|definition
operator|.
name|getName
argument_list|()
argument_list|,
name|definition
operator|.
name|isAbstract
argument_list|()
argument_list|,
name|definition
operator|.
name|getDeclaredAggregateNames
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|internalRegisterDefinitions
argument_list|(
name|toRegister
argument_list|)
expr_stmt|;
block|}
comment|//------------------------------------------------------------< private>---
specifier|private
name|void
name|internalRegisterDefinitions
parameter_list|(
name|PrivilegeDefinition
name|toRegister
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|CoreValueFactory
name|vf
init|=
name|contentSession
operator|.
name|getCoreValueFactory
argument_list|()
decl_stmt|;
name|Root
name|root
init|=
name|contentSession
operator|.
name|getCurrentRoot
argument_list|()
decl_stmt|;
try|try
block|{
comment|// make sure the privileges path is defined
name|Tree
name|privilegesTree
init|=
name|root
operator|.
name|getTree
argument_list|(
name|PRIVILEGES_PATH
argument_list|)
decl_stmt|;
if|if
condition|(
name|privilegesTree
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
literal|"Repository doesn't contain node "
operator|+
name|PRIVILEGES_PATH
argument_list|)
throw|;
block|}
name|NodeUtil
name|privilegesNode
init|=
operator|new
name|NodeUtil
argument_list|(
name|privilegesTree
argument_list|,
name|contentSession
argument_list|)
decl_stmt|;
name|writeDefinition
argument_list|(
name|privilegesNode
argument_list|,
name|toRegister
argument_list|)
expr_stmt|;
comment|// delegate validation to the commit validation (see above)
name|root
operator|.
name|commit
argument_list|(
name|DefaultConflictHandler
operator|.
name|OURS
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
name|definitions
operator|.
name|put
argument_list|(
name|toRegister
operator|.
name|getName
argument_list|()
argument_list|,
name|toRegister
argument_list|)
expr_stmt|;
name|updateJcrAllPrivilege
argument_list|(
name|definitions
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|writeDefinition
parameter_list|(
name|NodeUtil
name|privilegesNode
parameter_list|,
name|PrivilegeDefinition
name|definition
parameter_list|)
block|{
name|NodeUtil
name|privNode
init|=
name|privilegesNode
operator|.
name|addChild
argument_list|(
name|definition
operator|.
name|getName
argument_list|()
argument_list|,
name|NT_REP_PRIVILEGE
argument_list|)
decl_stmt|;
if|if
condition|(
name|definition
operator|.
name|isAbstract
argument_list|()
condition|)
block|{
name|privNode
operator|.
name|setBoolean
argument_list|(
name|REP_IS_ABSTRACT
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|Set
argument_list|<
name|String
argument_list|>
name|declAggrNames
init|=
name|definition
operator|.
name|getDeclaredAggregateNames
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|declAggrNames
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|String
index|[]
name|names
init|=
name|definition
operator|.
name|getDeclaredAggregateNames
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|declAggrNames
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
name|privNode
operator|.
name|setNames
argument_list|(
name|REP_AGGREGATES
argument_list|,
name|names
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

