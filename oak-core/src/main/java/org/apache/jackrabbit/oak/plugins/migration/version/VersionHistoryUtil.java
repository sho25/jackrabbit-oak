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
name|plugins
operator|.
name|migration
operator|.
name|version
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
name|collect
operator|.
name|ImmutableSet
operator|.
name|of
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Iterables
operator|.
name|concat
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Sets
operator|.
name|newHashSet
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|singleton
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|JcrConstants
operator|.
name|JCR_BASEVERSION
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|JcrConstants
operator|.
name|JCR_CREATED
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|JcrConstants
operator|.
name|JCR_ISCHECKEDOUT
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|JcrConstants
operator|.
name|JCR_MIXINTYPES
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|JcrConstants
operator|.
name|JCR_PREDECESSORS
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|JcrConstants
operator|.
name|JCR_SYSTEM
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|JcrConstants
operator|.
name|JCR_UUID
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|JcrConstants
operator|.
name|JCR_VERSIONHISTORY
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|JcrConstants
operator|.
name|JCR_VERSIONSTORAGE
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|JcrConstants
operator|.
name|MIX_REFERENCEABLE
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|JcrConstants
operator|.
name|MIX_VERSIONABLE
import|;
end_import

begin_import
import|import static
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
name|MultiGenericPropertyState
operator|.
name|nameProperty
import|;
end_import

begin_import
import|import static
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
name|version
operator|.
name|VersionConstants
operator|.
name|REP_VERSIONSTORAGE
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Calendar
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
name|plugins
operator|.
name|nodetype
operator|.
name|TypePredicate
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
name|state
operator|.
name|ChildNodeEntry
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
name|state
operator|.
name|NodeBuilder
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
name|state
operator|.
name|NodeState
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
name|Joiner
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
name|util
operator|.
name|ISO8601
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

begin_class
specifier|public
class|class
name|VersionHistoryUtil
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|VersionHistoryUtil
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
name|String
name|getRelativeVersionHistoryPath
parameter_list|(
name|String
name|versionableUuid
parameter_list|)
block|{
return|return
name|Joiner
operator|.
name|on
argument_list|(
literal|'/'
argument_list|)
operator|.
name|join
argument_list|(
name|concat
argument_list|(
name|singleton
argument_list|(
literal|""
argument_list|)
argument_list|,
name|getRelativeVersionHistoryPathSegments
argument_list|(
name|versionableUuid
argument_list|)
argument_list|,
name|singleton
argument_list|(
name|versionableUuid
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Constructs the version history path based on the versionable's UUID.      *      * @param versionStorage below which to look for the version.      * @param versionableUuid The String representation of the versionable's UUID.      * @return The NodeState corresponding to the version history, or {@code null}      *         if it does not exist.      */
specifier|static
name|NodeState
name|getVersionHistoryNodeState
parameter_list|(
name|NodeState
name|versionStorage
parameter_list|,
name|String
name|versionableUuid
parameter_list|)
block|{
name|NodeState
name|historyParent
init|=
name|versionStorage
decl_stmt|;
for|for
control|(
name|String
name|segment
range|:
name|getRelativeVersionHistoryPathSegments
argument_list|(
name|versionableUuid
argument_list|)
control|)
block|{
name|historyParent
operator|=
name|historyParent
operator|.
name|getChildNode
argument_list|(
name|segment
argument_list|)
expr_stmt|;
block|}
return|return
name|historyParent
operator|.
name|getChildNode
argument_list|(
name|versionableUuid
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|NodeBuilder
name|getVersionHistoryBuilder
parameter_list|(
name|NodeBuilder
name|versionStorage
parameter_list|,
name|String
name|versionableUuid
parameter_list|)
block|{
name|NodeBuilder
name|history
init|=
name|versionStorage
decl_stmt|;
for|for
control|(
name|String
name|segment
range|:
name|getRelativeVersionHistoryPathSegments
argument_list|(
name|versionableUuid
argument_list|)
control|)
block|{
name|history
operator|=
name|history
operator|.
name|getChildNode
argument_list|(
name|segment
argument_list|)
expr_stmt|;
block|}
return|return
name|history
operator|.
name|getChildNode
argument_list|(
name|versionableUuid
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|getRelativeVersionHistoryPathSegments
parameter_list|(
name|String
name|versionableUuid
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|segments
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
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
literal|3
condition|;
name|i
operator|++
control|)
block|{
name|segments
operator|.
name|add
argument_list|(
name|versionableUuid
operator|.
name|substring
argument_list|(
name|i
operator|*
literal|2
argument_list|,
name|i
operator|*
literal|2
operator|+
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|segments
return|;
block|}
specifier|public
specifier|static
name|NodeState
name|getVersionStorage
parameter_list|(
name|NodeState
name|root
parameter_list|)
block|{
return|return
name|root
operator|.
name|getChildNode
argument_list|(
name|JCR_SYSTEM
argument_list|)
operator|.
name|getChildNode
argument_list|(
name|JCR_VERSIONSTORAGE
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|NodeBuilder
name|getVersionStorage
parameter_list|(
name|NodeBuilder
name|root
parameter_list|)
block|{
return|return
name|root
operator|.
name|getChildNode
argument_list|(
name|JCR_SYSTEM
argument_list|)
operator|.
name|getChildNode
argument_list|(
name|JCR_VERSIONSTORAGE
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|NodeBuilder
name|createVersionStorage
parameter_list|(
name|NodeBuilder
name|root
parameter_list|)
block|{
name|NodeBuilder
name|vs
init|=
name|root
operator|.
name|child
argument_list|(
name|JCR_SYSTEM
argument_list|)
operator|.
name|child
argument_list|(
name|JCR_VERSIONSTORAGE
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|vs
operator|.
name|hasProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|)
condition|)
block|{
name|vs
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|REP_VERSIONSTORAGE
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
block|}
return|return
name|vs
return|;
block|}
specifier|public
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|getVersionableNodes
parameter_list|(
name|NodeState
name|root
parameter_list|,
name|NodeState
name|versionStorage
parameter_list|,
name|TypePredicate
name|isVersionable
parameter_list|,
name|Calendar
name|olderThan
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|paths
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|getVersionableNodes
argument_list|(
name|root
argument_list|,
name|versionStorage
argument_list|,
name|isVersionable
argument_list|,
name|olderThan
argument_list|,
name|PathUtils
operator|.
name|ROOT_PATH
argument_list|,
name|paths
argument_list|)
expr_stmt|;
return|return
name|paths
return|;
block|}
specifier|private
specifier|static
name|void
name|getVersionableNodes
parameter_list|(
name|NodeState
name|node
parameter_list|,
name|NodeState
name|versionStorage
parameter_list|,
name|TypePredicate
name|isVersionable
parameter_list|,
name|Calendar
name|olderThan
parameter_list|,
name|String
name|path
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|paths
parameter_list|)
block|{
if|if
condition|(
name|isVersionable
operator|.
name|apply
argument_list|(
name|node
argument_list|)
condition|)
block|{
if|if
condition|(
name|olderThan
operator|==
literal|null
condition|)
block|{
name|paths
operator|.
name|add
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|NodeState
name|versionHistory
init|=
name|getVersionHistoryNodeState
argument_list|(
name|versionStorage
argument_list|,
name|node
operator|.
name|getString
argument_list|(
name|JCR_UUID
argument_list|)
argument_list|)
decl_stmt|;
name|Calendar
name|lastModified
init|=
name|getVersionHistoryLastModified
argument_list|(
name|versionHistory
argument_list|)
decl_stmt|;
if|if
condition|(
name|lastModified
operator|.
name|before
argument_list|(
name|olderThan
argument_list|)
condition|)
block|{
name|paths
operator|.
name|add
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
block|}
block|}
for|for
control|(
name|ChildNodeEntry
name|c
range|:
name|node
operator|.
name|getChildNodeEntries
argument_list|()
control|)
block|{
name|getVersionableNodes
argument_list|(
name|c
operator|.
name|getNodeState
argument_list|()
argument_list|,
name|versionStorage
argument_list|,
name|isVersionable
argument_list|,
name|olderThan
argument_list|,
name|PathUtils
operator|.
name|concat
argument_list|(
name|path
argument_list|,
name|c
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|,
name|paths
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
specifier|static
name|Calendar
name|getVersionHistoryLastModified
parameter_list|(
name|NodeState
name|versionHistory
parameter_list|)
block|{
name|Calendar
name|youngest
init|=
name|Calendar
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|youngest
operator|.
name|setTimeInMillis
argument_list|(
literal|0
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|ChildNodeEntry
name|entry
range|:
name|versionHistory
operator|.
name|getChildNodeEntries
argument_list|()
control|)
block|{
specifier|final
name|NodeState
name|version
init|=
name|entry
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
if|if
condition|(
name|version
operator|.
name|hasProperty
argument_list|(
name|JCR_CREATED
argument_list|)
condition|)
block|{
specifier|final
name|Calendar
name|created
init|=
name|ISO8601
operator|.
name|parse
argument_list|(
name|version
operator|.
name|getProperty
argument_list|(
name|JCR_CREATED
argument_list|)
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|DATE
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|created
operator|.
name|after
argument_list|(
name|youngest
argument_list|)
condition|)
block|{
name|youngest
operator|=
name|created
expr_stmt|;
block|}
block|}
block|}
return|return
name|youngest
return|;
block|}
specifier|public
specifier|static
name|void
name|removeVersionProperties
parameter_list|(
name|NodeBuilder
name|versionableBuilder
parameter_list|,
name|TypePredicate
name|isReferenceable
parameter_list|)
block|{
assert|assert
name|versionableBuilder
operator|.
name|exists
argument_list|()
assert|;
name|removeMixin
argument_list|(
name|versionableBuilder
argument_list|,
name|MIX_VERSIONABLE
argument_list|)
expr_stmt|;
comment|// we don't know if the UUID is otherwise referenced,
comment|// so make sure the node remains referencable
if|if
condition|(
operator|!
name|isReferenceable
operator|.
name|apply
argument_list|(
name|versionableBuilder
operator|.
name|getNodeState
argument_list|()
argument_list|)
condition|)
block|{
name|addMixin
argument_list|(
name|versionableBuilder
argument_list|,
name|MIX_REFERENCEABLE
argument_list|)
expr_stmt|;
block|}
name|versionableBuilder
operator|.
name|removeProperty
argument_list|(
name|JCR_VERSIONHISTORY
argument_list|)
expr_stmt|;
name|versionableBuilder
operator|.
name|removeProperty
argument_list|(
name|JCR_PREDECESSORS
argument_list|)
expr_stmt|;
name|versionableBuilder
operator|.
name|removeProperty
argument_list|(
name|JCR_BASEVERSION
argument_list|)
expr_stmt|;
name|versionableBuilder
operator|.
name|removeProperty
argument_list|(
name|JCR_ISCHECKEDOUT
argument_list|)
expr_stmt|;
block|}
specifier|static
name|void
name|addMixin
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|,
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|builder
operator|.
name|hasProperty
argument_list|(
name|JCR_MIXINTYPES
argument_list|)
condition|)
block|{
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|mixins
init|=
name|newHashSet
argument_list|(
name|builder
operator|.
name|getProperty
argument_list|(
name|JCR_MIXINTYPES
argument_list|)
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|NAMES
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|mixins
operator|.
name|add
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|builder
operator|.
name|setProperty
argument_list|(
name|nameProperty
argument_list|(
name|JCR_MIXINTYPES
argument_list|,
name|mixins
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|builder
operator|.
name|setProperty
argument_list|(
name|nameProperty
argument_list|(
name|JCR_MIXINTYPES
argument_list|,
name|of
argument_list|(
name|name
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|void
name|removeMixin
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|,
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|builder
operator|.
name|hasProperty
argument_list|(
name|JCR_MIXINTYPES
argument_list|)
condition|)
block|{
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|mixins
init|=
name|newHashSet
argument_list|(
name|builder
operator|.
name|getProperty
argument_list|(
name|JCR_MIXINTYPES
argument_list|)
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|NAMES
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|mixins
operator|.
name|remove
argument_list|(
name|name
argument_list|)
condition|)
block|{
if|if
condition|(
name|mixins
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|builder
operator|.
name|removeProperty
argument_list|(
name|JCR_MIXINTYPES
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|setProperty
argument_list|(
name|nameProperty
argument_list|(
name|JCR_MIXINTYPES
argument_list|,
name|mixins
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|public
specifier|static
name|NodeBuilder
name|removeVersions
parameter_list|(
name|NodeState
name|root
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|toRemove
parameter_list|)
block|{
name|NodeBuilder
name|rootBuilder
init|=
name|root
operator|.
name|builder
argument_list|()
decl_stmt|;
name|TypePredicate
name|isReferenceable
init|=
operator|new
name|TypePredicate
argument_list|(
name|root
argument_list|,
name|MIX_REFERENCEABLE
argument_list|)
decl_stmt|;
name|NodeBuilder
name|versionStorage
init|=
name|getVersionStorage
argument_list|(
name|rootBuilder
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|p
range|:
name|toRemove
control|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Removing version history for {}"
argument_list|,
name|p
argument_list|)
expr_stmt|;
name|NodeBuilder
name|b
init|=
name|getBuilder
argument_list|(
name|rootBuilder
argument_list|,
name|p
argument_list|)
decl_stmt|;
name|String
name|uuid
init|=
name|b
operator|.
name|getString
argument_list|(
name|JCR_UUID
argument_list|)
decl_stmt|;
name|VersionHistoryUtil
operator|.
name|removeVersionProperties
argument_list|(
name|b
argument_list|,
name|isReferenceable
argument_list|)
expr_stmt|;
name|getVersionHistoryBuilder
argument_list|(
name|versionStorage
argument_list|,
name|uuid
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
return|return
name|rootBuilder
return|;
block|}
specifier|private
specifier|static
name|NodeBuilder
name|getBuilder
parameter_list|(
name|NodeBuilder
name|root
parameter_list|,
name|String
name|path
parameter_list|)
block|{
name|NodeBuilder
name|builder
init|=
name|root
decl_stmt|;
for|for
control|(
name|String
name|e
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|path
argument_list|)
control|)
block|{
name|builder
operator|=
name|builder
operator|.
name|child
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
return|;
block|}
block|}
end_class

end_unit

