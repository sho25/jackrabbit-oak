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
name|plugins
operator|.
name|observation
operator|.
name|filter
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
import|import static
name|javax
operator|.
name|jcr
operator|.
name|observation
operator|.
name|Event
operator|.
name|NODE_ADDED
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
name|NODE_MOVED
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
import|import static
name|javax
operator|.
name|jcr
operator|.
name|observation
operator|.
name|Event
operator|.
name|PERSIST
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
name|PROPERTY_ADDED
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
name|PROPERTY_CHANGED
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
name|PROPERTY_REMOVED
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Objects
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
name|Predicate
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
name|Lists
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
name|plugins
operator|.
name|nodetype
operator|.
name|ReadOnlyNodeTypeManager
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
name|EventGenerator
operator|.
name|Filter
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
name|UniversalFilter
operator|.
name|Selector
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
name|commit
operator|.
name|CommitInfo
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
name|permission
operator|.
name|PermissionProvider
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
name|permission
operator|.
name|TreePermission
import|;
end_import

begin_comment
comment|/**  * Builder for {@link FilterProvider} instances.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|FilterBuilder
block|{
specifier|private
specifier|static
specifier|final
name|int
name|ALL_EVENTS
init|=
name|NODE_ADDED
operator||
name|NODE_REMOVED
operator||
name|NODE_MOVED
operator||
name|PROPERTY_ADDED
operator||
name|PROPERTY_REMOVED
operator||
name|PROPERTY_CHANGED
operator||
name|PERSIST
decl_stmt|;
specifier|private
name|boolean
name|includeSessionLocal
decl_stmt|;
specifier|private
name|boolean
name|includeClusterExternal
decl_stmt|;
specifier|private
name|String
name|basePath
init|=
literal|"/"
decl_stmt|;
specifier|private
name|Condition
name|condition
decl_stmt|;
specifier|public
interface|interface
name|Condition
block|{
annotation|@
name|Nonnull
name|Filter
name|createFilter
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|before
parameter_list|,
annotation|@
name|Nonnull
name|Tree
name|after
parameter_list|,
annotation|@
name|Nonnull
name|ReadOnlyNodeTypeManager
name|ntManager
parameter_list|)
function_decl|;
block|}
comment|/**      * Whether to include session local changes. Defaults to {@code false}.      * @param include if {@code true} session local changes are included,      *                otherwise session local changes are not included.      * @return  this instance      */
annotation|@
name|Nonnull
specifier|public
name|FilterBuilder
name|includeSessionLocal
parameter_list|(
name|boolean
name|include
parameter_list|)
block|{
name|this
operator|.
name|includeSessionLocal
operator|=
name|include
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Whether to include cluster external changes. Defaults to {@code false}.      * @param include if {@code true} cluster external changes are included,      *                otherwise cluster external changes are not included.      * @return  this instance      */
annotation|@
name|Nonnull
specifier|public
name|FilterBuilder
name|includeClusterExternal
parameter_list|(
name|boolean
name|include
parameter_list|)
block|{
name|this
operator|.
name|includeClusterExternal
operator|=
name|include
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The base determines a subtree which contains all filter results.      * In the most simple case where a filter should include all events      * at a given path that path is at time same time the base path.      *<p>      * The base path is used for optimising the filter implementation by      * upfront exclusion of all parts of the content tree that are out      * side of the sub tree designated by the base path.      *      * @param absPath  absolute path      * @return  this instance      */
annotation|@
name|Nonnull
specifier|public
name|FilterBuilder
name|basePath
parameter_list|(
annotation|@
name|Nonnull
name|String
name|absPath
parameter_list|)
block|{
name|this
operator|.
name|basePath
operator|=
name|checkNotNull
argument_list|(
name|absPath
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Set the condition of this filter. Conditions are obtained from      * the various methods on this instance that return a {@code Condition}      * instance.      *      * @param condition  the conditions to apply      * @return this instance      */
annotation|@
name|Nonnull
specifier|public
name|FilterBuilder
name|condition
parameter_list|(
annotation|@
name|Nonnull
name|Condition
name|condition
parameter_list|)
block|{
name|this
operator|.
name|condition
operator|=
name|checkNotNull
argument_list|(
name|condition
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|//------------------------------------------------------------< initial conditions>---
comment|/**      * A condition the always holds      * @return  true condition      */
annotation|@
name|Nonnull
specifier|public
name|Condition
name|includeAll
parameter_list|()
block|{
return|return
name|ConstantCondition
operator|.
name|INCLUDE_ALL
return|;
block|}
comment|/**      * A condition that never holds      * @return  false condition      */
annotation|@
name|Nonnull
specifier|public
name|Condition
name|excludeAll
parameter_list|()
block|{
return|return
name|ConstantCondition
operator|.
name|EXCLUDE_ALL
return|;
block|}
comment|/**      * A condition that hold for accessible items as determined by the passed permission      * provider.      *      * @param permissionProvider  permission provider for checking whether an item is accessible.      * @return  access control condition      * @see  ACFilter      */
annotation|@
name|Nonnull
specifier|public
name|Condition
name|accessControl
parameter_list|(
annotation|@
name|Nonnull
name|PermissionProvider
name|permissionProvider
parameter_list|)
block|{
return|return
operator|new
name|ACCondition
argument_list|(
name|checkNotNull
argument_list|(
name|permissionProvider
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * A condition that holds on the paths matching a certain pattern.      * @param pathPattern      * @return  path condition      * @see GlobbingPathFilter      */
annotation|@
name|Nonnull
specifier|public
name|Condition
name|path
parameter_list|(
annotation|@
name|Nonnull
name|String
name|pathPattern
parameter_list|)
block|{
return|return
operator|new
name|PathCondition
argument_list|(
name|checkNotNull
argument_list|(
name|pathPattern
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * A condition that holds for matching event types.      * @param eventTypes      * @return event type condition      * @see EventTypeFilter      */
annotation|@
name|Nonnull
specifier|public
name|Condition
name|eventType
parameter_list|(
name|int
name|eventTypes
parameter_list|)
block|{
if|if
condition|(
operator|(
name|ALL_EVENTS
operator|&
name|eventTypes
operator|)
operator|==
literal|0
condition|)
block|{
return|return
name|excludeAll
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
operator|(
name|ALL_EVENTS
operator|&
name|eventTypes
operator|)
operator|!=
name|ALL_EVENTS
condition|)
block|{
return|return
operator|new
name|EventTypeCondition
argument_list|(
name|eventTypes
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|includeAll
argument_list|()
return|;
block|}
block|}
comment|/**      * A condition that holds for matching node types.      * @param selector  selector selecting the node to check the condition on      * @param ntNames node type names to match. This conditions never matches if {@code null} and      *                always matches if empty.      * @return node type condition      */
annotation|@
name|Nonnull
specifier|public
name|Condition
name|nodeType
parameter_list|(
annotation|@
name|Nonnull
name|Selector
name|selector
parameter_list|,
annotation|@
name|CheckForNull
name|String
index|[]
name|ntNames
parameter_list|)
block|{
if|if
condition|(
name|ntNames
operator|==
literal|null
condition|)
block|{
return|return
name|includeAll
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
name|ntNames
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
name|excludeAll
argument_list|()
return|;
block|}
else|else
block|{
return|return
operator|new
name|NodeTypeCondition
argument_list|(
name|checkNotNull
argument_list|(
name|selector
argument_list|)
argument_list|,
name|ntNames
argument_list|)
return|;
block|}
block|}
comment|/**      * A condition that holds for matching uuids.      * @param selector  selector selecting the node to check the condition on      * @param uuids uuids to match. This conditions never matches if {@code null} and      *                always matches if empty.      * @return node type condition      */
annotation|@
name|Nonnull
specifier|public
name|Condition
name|uuid
parameter_list|(
annotation|@
name|Nonnull
name|Selector
name|selector
parameter_list|,
annotation|@
name|CheckForNull
name|String
index|[]
name|uuids
parameter_list|)
block|{
if|if
condition|(
name|uuids
operator|==
literal|null
condition|)
block|{
return|return
name|includeAll
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
name|uuids
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
name|excludeAll
argument_list|()
return|;
block|}
else|else
block|{
return|return
operator|new
name|UuidCondition
argument_list|(
name|checkNotNull
argument_list|(
name|selector
argument_list|)
argument_list|,
name|uuids
argument_list|)
return|;
block|}
block|}
comment|/**      * A condition that holds when the property predicate matches.      * @param selector  selector selecting the node to check the condition on      * @param name      the name of the property to check the predicate on      * @param predicate the predicate to check on the named property      * @return property condition      */
annotation|@
name|Nonnull
specifier|public
name|Condition
name|property
parameter_list|(
annotation|@
name|Nonnull
name|Selector
name|selector
parameter_list|,
annotation|@
name|Nonnull
name|String
name|name
parameter_list|,
annotation|@
name|Nonnull
name|Predicate
argument_list|<
name|PropertyState
argument_list|>
name|predicate
parameter_list|)
block|{
return|return
operator|new
name|PropertyCondition
argument_list|(
name|checkNotNull
argument_list|(
name|selector
argument_list|)
argument_list|,
name|checkNotNull
argument_list|(
name|name
argument_list|)
argument_list|,
name|checkNotNull
argument_list|(
name|predicate
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * A condition that holds when the predicate matches.      * @param selector  selector selecting the node to check the condition on      * @param predicate the predicate to check on the selected node      * @return universal condition      */
annotation|@
name|Nonnull
specifier|public
name|Condition
name|universal
parameter_list|(
annotation|@
name|Nonnull
name|Selector
name|selector
parameter_list|,
annotation|@
name|Nonnull
name|Predicate
argument_list|<
name|Tree
argument_list|>
name|predicate
parameter_list|)
block|{
return|return
operator|new
name|UniversalCondition
argument_list|(
name|checkNotNull
argument_list|(
name|selector
argument_list|)
argument_list|,
name|checkNotNull
argument_list|(
name|predicate
argument_list|)
argument_list|)
return|;
block|}
comment|//------------------------------------------------------------< Compound conditions>---
comment|/**      * A compound condition that holds when any of its constituents hold.      * @param conditions conditions of which any must hold in order for this condition to hold      * @return  any condition      */
annotation|@
name|Nonnull
specifier|public
name|Condition
name|any
parameter_list|(
annotation|@
name|Nonnull
name|Condition
modifier|...
name|conditions
parameter_list|)
block|{
return|return
operator|new
name|AnyCondition
argument_list|(
name|checkNotNull
argument_list|(
name|conditions
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * A compound condition that holds when all of its constituents hold.      * @param conditions conditions of which all must hold in order for this condition to hold      * @return  any condition      */
annotation|@
name|Nonnull
specifier|public
name|Condition
name|all
parameter_list|(
annotation|@
name|Nonnull
name|Condition
modifier|...
name|conditions
parameter_list|)
block|{
return|return
operator|new
name|AllCondition
argument_list|(
name|checkNotNull
argument_list|(
name|conditions
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Create a {@code FilterProvider} reflecting the current state of this builder.      * @return  filter provider of the current state of this builder      */
annotation|@
name|Nonnull
specifier|public
name|FilterProvider
name|build
parameter_list|()
block|{
return|return
operator|new
name|FilterProvider
argument_list|()
block|{
name|boolean
name|includeSessionLocal
init|=
name|FilterBuilder
operator|.
name|this
operator|.
name|includeSessionLocal
decl_stmt|;
name|boolean
name|includeClusterExternal
init|=
name|FilterBuilder
operator|.
name|this
operator|.
name|includeClusterExternal
decl_stmt|;
name|String
name|basePath
init|=
name|FilterBuilder
operator|.
name|this
operator|.
name|basePath
decl_stmt|;
name|Condition
name|condition
init|=
name|FilterBuilder
operator|.
name|this
operator|.
name|condition
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|includeCommit
parameter_list|(
annotation|@
name|Nonnull
name|String
name|sessionId
parameter_list|,
annotation|@
name|CheckForNull
name|CommitInfo
name|info
parameter_list|)
block|{
return|return
operator|(
name|includeSessionLocal
operator|||
operator|!
name|isLocal
argument_list|(
name|checkNotNull
argument_list|(
name|sessionId
argument_list|)
argument_list|,
name|info
argument_list|)
operator|)
operator|&&
operator|(
name|includeClusterExternal
operator|||
operator|!
name|isExternal
argument_list|(
name|info
argument_list|)
operator|)
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Filter
name|getFilter
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|beforeTree
parameter_list|,
annotation|@
name|Nonnull
name|Tree
name|afterTree
parameter_list|,
annotation|@
name|Nonnull
name|ReadOnlyNodeTypeManager
name|ntManager
parameter_list|)
block|{
return|return
name|condition
operator|.
name|createFilter
argument_list|(
name|checkNotNull
argument_list|(
name|beforeTree
argument_list|)
argument_list|,
name|checkNotNull
argument_list|(
name|afterTree
argument_list|)
argument_list|,
name|checkNotNull
argument_list|(
name|ntManager
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|String
name|getPath
parameter_list|()
block|{
return|return
name|basePath
return|;
block|}
specifier|private
name|boolean
name|isLocal
parameter_list|(
name|String
name|sessionId
parameter_list|,
name|CommitInfo
name|info
parameter_list|)
block|{
return|return
name|info
operator|!=
literal|null
operator|&&
name|Objects
operator|.
name|equal
argument_list|(
name|info
operator|.
name|getSessionId
argument_list|()
argument_list|,
name|sessionId
argument_list|)
return|;
block|}
specifier|private
name|boolean
name|isExternal
parameter_list|(
name|CommitInfo
name|info
parameter_list|)
block|{
return|return
name|info
operator|==
literal|null
return|;
block|}
block|}
return|;
block|}
comment|//------------------------------------------------------------< Conditions>---
specifier|private
specifier|static
class|class
name|ConstantCondition
implements|implements
name|Condition
block|{
specifier|public
specifier|static
name|ConstantCondition
name|INCLUDE_ALL
init|=
operator|new
name|ConstantCondition
argument_list|(
literal|true
argument_list|)
decl_stmt|;
specifier|public
specifier|static
name|ConstantCondition
name|EXCLUDE_ALL
init|=
operator|new
name|ConstantCondition
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|value
decl_stmt|;
specifier|public
name|ConstantCondition
parameter_list|(
name|boolean
name|value
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Filter
name|createFilter
parameter_list|(
name|Tree
name|before
parameter_list|,
name|Tree
name|after
parameter_list|,
name|ReadOnlyNodeTypeManager
name|ntManager
parameter_list|)
block|{
return|return
name|value
condition|?
name|Filters
operator|.
name|includeAll
argument_list|()
else|:
name|Filters
operator|.
name|excludeAll
argument_list|()
return|;
block|}
block|}
specifier|private
specifier|static
class|class
name|ACCondition
implements|implements
name|Condition
block|{
specifier|private
specifier|final
name|PermissionProvider
name|permissionProvider
decl_stmt|;
specifier|public
name|ACCondition
parameter_list|(
name|PermissionProvider
name|permissionProvider
parameter_list|)
block|{
name|this
operator|.
name|permissionProvider
operator|=
name|permissionProvider
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Filter
name|createFilter
parameter_list|(
name|Tree
name|before
parameter_list|,
name|Tree
name|after
parameter_list|,
name|ReadOnlyNodeTypeManager
name|ntManager
parameter_list|)
block|{
return|return
operator|new
name|ACFilter
argument_list|(
name|before
argument_list|,
name|after
argument_list|,
name|getTreePermission
argument_list|(
name|after
argument_list|)
argument_list|)
return|;
block|}
specifier|private
name|TreePermission
name|getTreePermission
parameter_list|(
name|Tree
name|tree
parameter_list|)
block|{
return|return
name|tree
operator|.
name|isRoot
argument_list|()
condition|?
name|permissionProvider
operator|.
name|getTreePermission
argument_list|(
name|tree
argument_list|,
name|TreePermission
operator|.
name|EMPTY
argument_list|)
else|:
name|permissionProvider
operator|.
name|getTreePermission
argument_list|(
name|tree
argument_list|,
name|getTreePermission
argument_list|(
name|tree
operator|.
name|getParent
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
block|}
specifier|private
specifier|static
class|class
name|PathCondition
implements|implements
name|Condition
block|{
specifier|private
specifier|final
name|String
name|pathGlob
decl_stmt|;
specifier|public
name|PathCondition
parameter_list|(
name|String
name|pathGlob
parameter_list|)
block|{
name|this
operator|.
name|pathGlob
operator|=
name|pathGlob
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Filter
name|createFilter
parameter_list|(
name|Tree
name|before
parameter_list|,
name|Tree
name|after
parameter_list|,
name|ReadOnlyNodeTypeManager
name|ntManager
parameter_list|)
block|{
return|return
operator|new
name|GlobbingPathFilter
argument_list|(
name|before
argument_list|,
name|after
argument_list|,
name|pathGlob
argument_list|)
return|;
block|}
block|}
specifier|private
specifier|static
class|class
name|EventTypeCondition
implements|implements
name|Condition
block|{
specifier|private
specifier|final
name|int
name|eventTypes
decl_stmt|;
specifier|public
name|EventTypeCondition
parameter_list|(
name|int
name|eventTypes
parameter_list|)
block|{
name|this
operator|.
name|eventTypes
operator|=
name|eventTypes
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Filter
name|createFilter
parameter_list|(
name|Tree
name|before
parameter_list|,
name|Tree
name|after
parameter_list|,
name|ReadOnlyNodeTypeManager
name|ntManager
parameter_list|)
block|{
return|return
operator|new
name|EventTypeFilter
argument_list|(
name|eventTypes
argument_list|)
return|;
block|}
block|}
specifier|private
specifier|static
class|class
name|NodeTypeCondition
implements|implements
name|Condition
block|{
specifier|private
specifier|final
name|Selector
name|selector
decl_stmt|;
specifier|private
specifier|final
name|String
index|[]
name|ntNames
decl_stmt|;
specifier|public
name|NodeTypeCondition
parameter_list|(
name|Selector
name|selector
parameter_list|,
name|String
index|[]
name|ntNames
parameter_list|)
block|{
name|this
operator|.
name|selector
operator|=
name|selector
expr_stmt|;
name|this
operator|.
name|ntNames
operator|=
name|ntNames
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Filter
name|createFilter
parameter_list|(
name|Tree
name|before
parameter_list|,
name|Tree
name|after
parameter_list|,
name|ReadOnlyNodeTypeManager
name|ntManager
parameter_list|)
block|{
name|NodeTypePredicate
name|predicate
init|=
operator|new
name|NodeTypePredicate
argument_list|(
name|ntManager
argument_list|,
name|ntNames
argument_list|)
decl_stmt|;
return|return
operator|new
name|UniversalFilter
argument_list|(
name|before
argument_list|,
name|after
argument_list|,
name|selector
argument_list|,
name|predicate
argument_list|)
return|;
block|}
block|}
specifier|private
specifier|static
class|class
name|UuidCondition
implements|implements
name|Condition
block|{
specifier|private
specifier|final
name|Selector
name|selector
decl_stmt|;
specifier|private
specifier|final
name|UuidPredicate
name|predicate
decl_stmt|;
specifier|public
name|UuidCondition
parameter_list|(
name|Selector
name|selector
parameter_list|,
name|String
index|[]
name|uuids
parameter_list|)
block|{
name|this
operator|.
name|selector
operator|=
name|selector
expr_stmt|;
name|this
operator|.
name|predicate
operator|=
operator|new
name|UuidPredicate
argument_list|(
name|uuids
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Filter
name|createFilter
parameter_list|(
name|Tree
name|before
parameter_list|,
name|Tree
name|after
parameter_list|,
name|ReadOnlyNodeTypeManager
name|ntManager
parameter_list|)
block|{
return|return
operator|new
name|UniversalFilter
argument_list|(
name|before
argument_list|,
name|after
argument_list|,
name|selector
argument_list|,
name|predicate
argument_list|)
return|;
block|}
block|}
specifier|private
specifier|static
class|class
name|PropertyCondition
implements|implements
name|Condition
block|{
specifier|private
specifier|final
name|Selector
name|selector
decl_stmt|;
specifier|private
specifier|final
name|PropertyPredicate
name|predicate
decl_stmt|;
specifier|public
name|PropertyCondition
parameter_list|(
name|Selector
name|selector
parameter_list|,
name|String
name|name
parameter_list|,
name|Predicate
argument_list|<
name|PropertyState
argument_list|>
name|predicate
parameter_list|)
block|{
name|this
operator|.
name|selector
operator|=
name|selector
expr_stmt|;
name|this
operator|.
name|predicate
operator|=
operator|new
name|PropertyPredicate
argument_list|(
name|name
argument_list|,
name|predicate
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Filter
name|createFilter
parameter_list|(
name|Tree
name|before
parameter_list|,
name|Tree
name|after
parameter_list|,
name|ReadOnlyNodeTypeManager
name|ntManager
parameter_list|)
block|{
return|return
operator|new
name|UniversalFilter
argument_list|(
name|before
argument_list|,
name|after
argument_list|,
name|selector
argument_list|,
name|predicate
argument_list|)
return|;
block|}
block|}
specifier|private
specifier|static
class|class
name|UniversalCondition
implements|implements
name|Condition
block|{
specifier|private
specifier|final
name|Selector
name|selector
decl_stmt|;
specifier|private
specifier|final
name|Predicate
argument_list|<
name|Tree
argument_list|>
name|predicate
decl_stmt|;
specifier|public
name|UniversalCondition
parameter_list|(
name|Selector
name|selector
parameter_list|,
name|Predicate
argument_list|<
name|Tree
argument_list|>
name|predicate
parameter_list|)
block|{
name|this
operator|.
name|selector
operator|=
name|selector
expr_stmt|;
name|this
operator|.
name|predicate
operator|=
name|predicate
expr_stmt|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Filter
name|createFilter
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|before
parameter_list|,
annotation|@
name|Nonnull
name|Tree
name|after
parameter_list|,
annotation|@
name|Nonnull
name|ReadOnlyNodeTypeManager
name|ntManager
parameter_list|)
block|{
return|return
operator|new
name|UniversalFilter
argument_list|(
name|before
argument_list|,
name|after
argument_list|,
name|selector
argument_list|,
name|predicate
argument_list|)
return|;
block|}
block|}
specifier|private
specifier|static
class|class
name|AnyCondition
implements|implements
name|Condition
block|{
specifier|private
specifier|final
name|Condition
index|[]
name|conditions
decl_stmt|;
specifier|public
name|AnyCondition
parameter_list|(
name|Condition
modifier|...
name|conditions
parameter_list|)
block|{
name|this
operator|.
name|conditions
operator|=
name|conditions
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Filter
name|createFilter
parameter_list|(
specifier|final
name|Tree
name|before
parameter_list|,
specifier|final
name|Tree
name|after
parameter_list|,
specifier|final
name|ReadOnlyNodeTypeManager
name|ntManager
parameter_list|)
block|{
name|List
argument_list|<
name|Filter
argument_list|>
name|filters
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|Condition
name|condition
range|:
name|conditions
control|)
block|{
if|if
condition|(
name|condition
operator|==
name|ConstantCondition
operator|.
name|INCLUDE_ALL
condition|)
block|{
return|return
name|ConstantFilter
operator|.
name|INCLUDE_ALL
return|;
block|}
elseif|else
if|if
condition|(
name|condition
operator|!=
name|ConstantCondition
operator|.
name|EXCLUDE_ALL
condition|)
block|{
name|filters
operator|.
name|add
argument_list|(
name|condition
operator|.
name|createFilter
argument_list|(
name|before
argument_list|,
name|after
argument_list|,
name|ntManager
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|filters
operator|.
name|isEmpty
argument_list|()
condition|?
name|ConstantFilter
operator|.
name|EXCLUDE_ALL
else|:
name|Filters
operator|.
name|any
argument_list|(
name|filters
argument_list|)
return|;
block|}
block|}
specifier|private
specifier|static
class|class
name|AllCondition
implements|implements
name|Condition
block|{
specifier|private
specifier|final
name|Condition
index|[]
name|conditions
decl_stmt|;
specifier|public
name|AllCondition
parameter_list|(
name|Condition
modifier|...
name|conditions
parameter_list|)
block|{
name|this
operator|.
name|conditions
operator|=
name|conditions
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Filter
name|createFilter
parameter_list|(
specifier|final
name|Tree
name|before
parameter_list|,
specifier|final
name|Tree
name|after
parameter_list|,
specifier|final
name|ReadOnlyNodeTypeManager
name|ntManager
parameter_list|)
block|{
name|List
argument_list|<
name|Filter
argument_list|>
name|filters
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|Condition
name|condition
range|:
name|conditions
control|)
block|{
if|if
condition|(
name|condition
operator|==
name|ConstantCondition
operator|.
name|EXCLUDE_ALL
condition|)
block|{
return|return
name|ConstantFilter
operator|.
name|EXCLUDE_ALL
return|;
block|}
elseif|else
if|if
condition|(
name|condition
operator|!=
name|ConstantCondition
operator|.
name|INCLUDE_ALL
condition|)
block|{
name|filters
operator|.
name|add
argument_list|(
name|condition
operator|.
name|createFilter
argument_list|(
name|before
argument_list|,
name|after
argument_list|,
name|ntManager
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|filters
operator|.
name|isEmpty
argument_list|()
condition|?
name|ConstantFilter
operator|.
name|INCLUDE_ALL
else|:
name|Filters
operator|.
name|all
argument_list|(
name|filters
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

