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
name|util
operator|.
name|ArrayUtils
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

begin_comment
comment|/**  * PrivilegeProviderImpl... TODO  */
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
comment|// TODO: define if/how built-in privileges are reflected in the mk
comment|// TODO: define where custom privileges are being stored.
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
argument_list|,
operator|new
name|String
index|[
literal|0
index|]
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
comment|// TODO: jcr:all needs to be recalculated if custom privileges are registered
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
operator|new
name|String
index|[]
block|{
name|JCR_READ
block|,
name|JCR_READ_ACCESS_CONTROL
block|,
name|JCR_MODIFY_ACCESS_CONTROL
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
block|,
name|REP_WRITE
block|}
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
name|String
name|privilegeName
parameter_list|,
name|boolean
name|isAbstract
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|declaredAggregateNames
parameter_list|)
throws|throws
name|RepositoryException
block|{
comment|// TODO: check permission, validate and persist the custom definition
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
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|declaredAggregateNames
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
decl_stmt|;
name|definitions
operator|.
name|put
argument_list|(
name|privilegeName
argument_list|,
name|definition
argument_list|)
expr_stmt|;
return|return
name|definition
return|;
block|}
comment|//--------------------------------------------------------------------------
specifier|private
specifier|static
class|class
name|PrivilegeDefinitionImpl
implements|implements
name|PrivilegeDefinition
block|{
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|isAbstract
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|declaredAggregateNames
decl_stmt|;
specifier|private
name|PrivilegeDefinitionImpl
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|isAbstract
parameter_list|,
name|String
index|[]
name|declaredAggregateNames
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|isAbstract
operator|=
name|isAbstract
expr_stmt|;
name|this
operator|.
name|declaredAggregateNames
operator|=
name|ArrayUtils
operator|.
name|toSet
argument_list|(
name|declaredAggregateNames
argument_list|)
expr_stmt|;
block|}
comment|//--------------------------------------------< PrivilegeDefinition>---
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
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
name|isAbstract
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|String
index|[]
name|getDeclaredAggregateNames
parameter_list|()
block|{
return|return
name|declaredAggregateNames
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|declaredAggregateNames
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
name|int
name|result
init|=
name|name
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
operator|(
name|isAbstract
condition|?
literal|1
else|:
literal|0
operator|)
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|declaredAggregateNames
operator|.
name|hashCode
argument_list|()
expr_stmt|;
return|return
name|result
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
name|PrivilegeDefinitionImpl
condition|)
block|{
name|PrivilegeDefinitionImpl
name|other
init|=
operator|(
name|PrivilegeDefinitionImpl
operator|)
name|o
decl_stmt|;
return|return
name|name
operator|.
name|equals
argument_list|(
name|other
operator|.
name|name
argument_list|)
operator|&&
name|isAbstract
operator|==
name|other
operator|.
name|isAbstract
operator|&&
name|declaredAggregateNames
operator|.
name|equals
argument_list|(
name|other
operator|.
name|declaredAggregateNames
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
literal|"PrivilegeDefinition: "
operator|+
name|name
return|;
block|}
block|}
block|}
end_class

end_unit

