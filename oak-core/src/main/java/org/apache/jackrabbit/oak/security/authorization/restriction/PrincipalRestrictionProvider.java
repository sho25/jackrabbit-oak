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
operator|.
name|restriction
package|;
end_package

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
name|Iterator
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
name|annotation
operator|.
name|Nullable
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|PropertyType
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
name|Value
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
name|Sets
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
name|PropertyState
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
name|api
operator|.
name|Type
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
name|plugins
operator|.
name|memory
operator|.
name|PropertyStates
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
name|authorization
operator|.
name|restriction
operator|.
name|Restriction
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
name|restriction
operator|.
name|RestrictionDefinition
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
name|restriction
operator|.
name|RestrictionDefinitionImpl
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
name|restriction
operator|.
name|RestrictionImpl
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
name|restriction
operator|.
name|RestrictionPattern
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
name|restriction
operator|.
name|RestrictionProvider
import|;
end_import

begin_comment
comment|/**  * Restriction provider implementation used for editing access control by  * principal. It wraps the configured base provider and adds a mandatory  * restriction definition with name {@link #REP_NODE_PATH} and type {@link Type#PATH PATH}  * which stores the path of the access controlled node to which a given  * access control entry will be applied.  */
end_comment

begin_class
specifier|public
class|class
name|PrincipalRestrictionProvider
implements|implements
name|RestrictionProvider
implements|,
name|AccessControlConstants
block|{
specifier|private
specifier|final
name|RestrictionProvider
name|base
decl_stmt|;
specifier|public
name|PrincipalRestrictionProvider
parameter_list|(
name|RestrictionProvider
name|base
parameter_list|)
block|{
name|this
operator|.
name|base
operator|=
name|base
expr_stmt|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|RestrictionDefinition
argument_list|>
name|getSupportedRestrictions
parameter_list|(
annotation|@
name|Nullable
name|String
name|oakPath
parameter_list|)
block|{
name|Set
argument_list|<
name|RestrictionDefinition
argument_list|>
name|definitions
init|=
operator|new
name|HashSet
argument_list|<
name|RestrictionDefinition
argument_list|>
argument_list|(
name|base
operator|.
name|getSupportedRestrictions
argument_list|(
name|oakPath
argument_list|)
argument_list|)
decl_stmt|;
name|definitions
operator|.
name|add
argument_list|(
operator|new
name|RestrictionDefinitionImpl
argument_list|(
name|REP_NODE_PATH
argument_list|,
name|Type
operator|.
name|PATH
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|definitions
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Restriction
name|createRestriction
parameter_list|(
annotation|@
name|Nullable
name|String
name|oakPath
parameter_list|,
annotation|@
name|Nonnull
name|String
name|oakName
parameter_list|,
annotation|@
name|Nonnull
name|Value
name|value
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|REP_NODE_PATH
operator|.
name|equals
argument_list|(
name|oakName
argument_list|)
operator|&&
name|PropertyType
operator|.
name|PATH
operator|==
name|value
operator|.
name|getType
argument_list|()
condition|)
block|{
return|return
operator|new
name|RestrictionImpl
argument_list|(
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|oakName
argument_list|,
name|value
argument_list|)
argument_list|,
literal|true
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|base
operator|.
name|createRestriction
argument_list|(
name|oakPath
argument_list|,
name|oakName
argument_list|,
name|value
argument_list|)
return|;
block|}
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Restriction
name|createRestriction
parameter_list|(
annotation|@
name|Nullable
name|String
name|oakPath
parameter_list|,
annotation|@
name|Nonnull
name|String
name|oakName
parameter_list|,
annotation|@
name|Nonnull
name|Value
modifier|...
name|values
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|base
operator|.
name|createRestriction
argument_list|(
name|oakPath
argument_list|,
name|oakName
argument_list|,
name|values
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|Restriction
argument_list|>
name|readRestrictions
parameter_list|(
annotation|@
name|Nullable
name|String
name|oakPath
parameter_list|,
annotation|@
name|Nonnull
name|Tree
name|aceTree
parameter_list|)
block|{
name|Set
argument_list|<
name|Restriction
argument_list|>
name|restrictions
init|=
operator|new
name|HashSet
argument_list|<
name|Restriction
argument_list|>
argument_list|(
name|base
operator|.
name|readRestrictions
argument_list|(
name|oakPath
argument_list|,
name|aceTree
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|value
init|=
operator|(
name|oakPath
operator|==
literal|null
operator|)
condition|?
literal|""
else|:
name|oakPath
decl_stmt|;
name|PropertyState
name|nodePathProp
init|=
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|REP_NODE_PATH
argument_list|,
name|value
argument_list|,
name|Type
operator|.
name|PATH
argument_list|)
decl_stmt|;
name|restrictions
operator|.
name|add
argument_list|(
operator|new
name|RestrictionImpl
argument_list|(
name|nodePathProp
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|restrictions
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|writeRestrictions
parameter_list|(
name|String
name|oakPath
parameter_list|,
name|Tree
name|aceTree
parameter_list|,
name|Set
argument_list|<
name|Restriction
argument_list|>
name|restrictions
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Iterator
argument_list|<
name|Restriction
argument_list|>
name|it
init|=
name|Sets
operator|.
name|newHashSet
argument_list|(
name|restrictions
argument_list|)
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Restriction
name|r
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|REP_NODE_PATH
operator|.
name|equals
argument_list|(
name|r
operator|.
name|getDefinition
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|it
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
name|base
operator|.
name|writeRestrictions
argument_list|(
name|oakPath
argument_list|,
name|aceTree
argument_list|,
name|restrictions
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|validateRestrictions
parameter_list|(
name|String
name|oakPath
parameter_list|,
annotation|@
name|Nonnull
name|Tree
name|aceTree
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|base
operator|.
name|validateRestrictions
argument_list|(
name|oakPath
argument_list|,
name|aceTree
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|RestrictionPattern
name|getPattern
parameter_list|(
annotation|@
name|Nullable
name|String
name|oakPath
parameter_list|,
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|)
block|{
return|return
name|base
operator|.
name|getPattern
argument_list|(
name|oakPath
argument_list|,
name|tree
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|RestrictionPattern
name|getPattern
parameter_list|(
annotation|@
name|Nullable
name|String
name|oakPath
parameter_list|,
annotation|@
name|Nonnull
name|Set
argument_list|<
name|Restriction
argument_list|>
name|restrictions
parameter_list|)
block|{
return|return
name|base
operator|.
name|getPattern
argument_list|(
name|oakPath
argument_list|,
name|restrictions
argument_list|)
return|;
block|}
block|}
end_class

end_unit

