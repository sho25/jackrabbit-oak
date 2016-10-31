begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|observation
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkNotNull
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
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
import|import static
name|javax
operator|.
name|jcr
operator|.
name|observation
operator|.
name|Event
operator|.
name|NODE_REMOVED
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
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|api
operator|.
name|observation
operator|.
name|JackrabbitEventFilter
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
name|commons
operator|.
name|PathUtils
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
name|jcr
operator|.
name|observation
operator|.
name|filter
operator|.
name|OakEventFilter
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
name|observation
operator|.
name|filter
operator|.
name|FilterBuilder
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
name|observation
operator|.
name|filter
operator|.
name|PermissionProviderFactory
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
name|observation
operator|.
name|filter
operator|.
name|FilterBuilder
operator|.
name|Condition
import|;
end_import

begin_comment
comment|/**  * Implements OakEventFilter which is an extension to the JackrabbitEventFilter  * with features only supported by Oak.  */
end_comment

begin_class
specifier|public
class|class
name|OakEventFilterImpl
extends|extends
name|OakEventFilter
block|{
specifier|private
specifier|final
name|JackrabbitEventFilter
name|delegate
decl_stmt|;
comment|/** whether or not applyNodeTypeOnSelf feature is enabled */
specifier|private
name|boolean
name|applyNodeTypeOnSelf
decl_stmt|;
comment|/** whether or not includeAncestorsRemove feature is enabled */
specifier|private
name|boolean
name|includeAncestorRemove
decl_stmt|;
comment|/** whether or not includeSubTreeOnRemove feature is enabled */
specifier|private
name|boolean
name|includeSubtreeOnRemove
decl_stmt|;
specifier|private
name|String
index|[]
name|globPaths
decl_stmt|;
specifier|public
name|OakEventFilterImpl
parameter_list|(
annotation|@
name|Nonnull
name|JackrabbitEventFilter
name|delegate
parameter_list|)
block|{
name|checkNotNull
argument_list|(
name|delegate
argument_list|)
expr_stmt|;
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getAbsPath
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|getAbsPath
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|JackrabbitEventFilter
name|setAbsPath
parameter_list|(
name|String
name|absPath
parameter_list|)
block|{
name|delegate
operator|.
name|setAbsPath
argument_list|(
name|absPath
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
index|[]
name|getAdditionalPaths
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|getAdditionalPaths
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|JackrabbitEventFilter
name|setAdditionalPaths
parameter_list|(
name|String
modifier|...
name|absPaths
parameter_list|)
block|{
name|delegate
operator|.
name|setAdditionalPaths
argument_list|(
name|absPaths
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getEventTypes
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|getEventTypes
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|JackrabbitEventFilter
name|setEventTypes
parameter_list|(
name|int
name|eventTypes
parameter_list|)
block|{
name|delegate
operator|.
name|setEventTypes
argument_list|(
name|eventTypes
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
index|[]
name|getExcludedPaths
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|getExcludedPaths
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|JackrabbitEventFilter
name|setExcludedPaths
parameter_list|(
name|String
modifier|...
name|excludedPaths
parameter_list|)
block|{
name|delegate
operator|.
name|setExcludedPaths
argument_list|(
name|excludedPaths
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
index|[]
name|getIdentifiers
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|getIdentifiers
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|JackrabbitEventFilter
name|setIdentifiers
parameter_list|(
name|String
index|[]
name|identifiers
parameter_list|)
block|{
name|delegate
operator|.
name|setIdentifiers
argument_list|(
name|identifiers
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|getIsDeep
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|getIsDeep
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|JackrabbitEventFilter
name|setIsDeep
parameter_list|(
name|boolean
name|isDeep
parameter_list|)
block|{
name|delegate
operator|.
name|setIsDeep
argument_list|(
name|isDeep
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
index|[]
name|getNodeTypes
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|getNodeTypes
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|JackrabbitEventFilter
name|setNodeTypes
parameter_list|(
name|String
index|[]
name|nodeTypeNames
parameter_list|)
block|{
name|delegate
operator|.
name|setNodeTypes
argument_list|(
name|nodeTypeNames
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|getNoExternal
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|getNoExternal
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|JackrabbitEventFilter
name|setNoExternal
parameter_list|(
name|boolean
name|noExternal
parameter_list|)
block|{
name|delegate
operator|.
name|setNoExternal
argument_list|(
name|noExternal
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|getNoInternal
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|getNoInternal
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|JackrabbitEventFilter
name|setNoInternal
parameter_list|(
name|boolean
name|noInternal
parameter_list|)
block|{
name|delegate
operator|.
name|setNoInternal
argument_list|(
name|noInternal
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|getNoLocal
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|getNoLocal
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|JackrabbitEventFilter
name|setNoLocal
parameter_list|(
name|boolean
name|noLocal
parameter_list|)
block|{
name|delegate
operator|.
name|setNoLocal
argument_list|(
name|noLocal
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
specifier|public
name|OakEventFilter
name|withApplyNodeTypeOnSelf
parameter_list|()
block|{
name|this
operator|.
name|applyNodeTypeOnSelf
operator|=
literal|true
expr_stmt|;
return|return
name|this
return|;
block|}
name|boolean
name|getApplyNodeTypeOnSelf
parameter_list|()
block|{
return|return
name|applyNodeTypeOnSelf
return|;
block|}
annotation|@
name|Override
specifier|public
name|OakEventFilter
name|withIncludeAncestorsRemove
parameter_list|()
block|{
name|this
operator|.
name|includeAncestorRemove
operator|=
literal|true
expr_stmt|;
return|return
name|this
return|;
block|}
name|boolean
name|getIncludeAncestorsRemove
parameter_list|()
block|{
return|return
name|includeAncestorRemove
return|;
block|}
specifier|private
name|void
name|addAncestorsRemoveCondition
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|parentPaths
parameter_list|,
name|String
name|globPath
parameter_list|)
block|{
if|if
condition|(
name|globPath
operator|==
literal|null
operator|||
operator|!
name|globPath
operator|.
name|contains
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
return|return;
block|}
comment|// from /a/b/c         => add /a and /a/b
comment|// from /a/b/**        => add /a
comment|// from /a             => add nothing
comment|// from /              => add nothing
comment|// from /a/b/**/*.html => add /a
comment|// from /a/b/*/*.html  => add /a
name|Iterator
argument_list|<
name|String
argument_list|>
name|it
init|=
name|PathUtils
operator|.
name|elements
argument_list|(
name|globPath
argument_list|)
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|StringBuffer
name|sb
init|=
operator|new
name|StringBuffer
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
name|String
name|element
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|element
operator|.
name|contains
argument_list|(
literal|"*"
argument_list|)
condition|)
block|{
if|if
condition|(
name|parentPaths
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|parentPaths
operator|.
name|remove
argument_list|(
name|parentPaths
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
break|break;
block|}
elseif|else
if|if
condition|(
operator|!
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
break|break;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|element
argument_list|)
expr_stmt|;
name|parentPaths
operator|.
name|add
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
operator|+
literal|"/*"
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|Condition
name|wrapMainCondition
parameter_list|(
name|Condition
name|mainCondition
parameter_list|,
name|FilterBuilder
name|filterBuilder
parameter_list|,
name|PermissionProviderFactory
name|permissionProviderFactory
parameter_list|)
block|{
if|if
condition|(
operator|!
name|includeAncestorRemove
operator|||
operator|(
name|getEventTypes
argument_list|()
operator|&
name|NODE_REMOVED
operator|)
operator|!=
name|NODE_REMOVED
condition|)
block|{
return|return
name|mainCondition
return|;
block|}
name|Set
argument_list|<
name|String
argument_list|>
name|parentPaths
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|addAncestorsRemoveCondition
argument_list|(
name|parentPaths
argument_list|,
name|getAbsPath
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|getAdditionalPaths
argument_list|()
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|absPath
range|:
name|getAdditionalPaths
argument_list|()
control|)
block|{
name|addAncestorsRemoveCondition
argument_list|(
name|parentPaths
argument_list|,
name|absPath
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|globPaths
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|globPath
range|:
name|globPaths
control|)
block|{
name|addAncestorsRemoveCondition
argument_list|(
name|parentPaths
argument_list|,
name|globPath
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|parentPaths
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
name|mainCondition
return|;
block|}
name|List
argument_list|<
name|Condition
argument_list|>
name|ancestorsRemoveConditions
init|=
operator|new
name|LinkedList
argument_list|<
name|Condition
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|aParentPath
range|:
name|parentPaths
control|)
block|{
name|ancestorsRemoveConditions
operator|.
name|add
argument_list|(
name|filterBuilder
operator|.
name|path
argument_list|(
name|aParentPath
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|filterBuilder
operator|.
name|any
argument_list|(
name|mainCondition
argument_list|,
name|filterBuilder
operator|.
name|all
argument_list|(
name|filterBuilder
operator|.
name|eventType
argument_list|(
name|NODE_REMOVED
argument_list|)
argument_list|,
name|filterBuilder
operator|.
name|any
argument_list|(
name|ancestorsRemoveConditions
argument_list|)
argument_list|,
name|filterBuilder
operator|.
name|deleteSubtree
argument_list|()
argument_list|,
name|filterBuilder
operator|.
name|accessControl
argument_list|(
name|permissionProviderFactory
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|OakEventFilter
name|withIncludeSubtreeOnRemove
parameter_list|()
block|{
name|this
operator|.
name|includeSubtreeOnRemove
operator|=
literal|true
expr_stmt|;
return|return
name|this
return|;
block|}
name|boolean
name|getIncludeSubtreeOnRemove
parameter_list|()
block|{
return|return
name|includeSubtreeOnRemove
return|;
block|}
annotation|@
name|Override
specifier|public
name|OakEventFilter
name|withIncludeGlobPaths
parameter_list|(
name|String
modifier|...
name|globPaths
parameter_list|)
block|{
if|if
condition|(
name|this
operator|.
name|globPaths
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"can only set globPaths once"
argument_list|)
throw|;
block|}
comment|//        for (String aGlobPath : globPaths) {
comment|//            return or(builder().path(aGlobPath));
comment|//        }
name|this
operator|.
name|globPaths
operator|=
name|globPaths
expr_stmt|;
return|return
name|this
return|;
block|}
name|String
index|[]
name|getIncludeGlobPaths
parameter_list|()
block|{
return|return
name|globPaths
return|;
block|}
block|}
end_class

end_unit

