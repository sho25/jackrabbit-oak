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
package|;
end_package

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
name|commons
operator|.
name|PathUtils
operator|.
name|concat
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
name|commons
operator|.
name|PropertiesUtil
operator|.
name|toInteger
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
name|commons
operator|.
name|PropertiesUtil
operator|.
name|toBoolean
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
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Activate
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Component
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Modified
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Property
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Service
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
name|JcrConstants
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
name|spi
operator|.
name|commit
operator|.
name|CommitContext
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
name|commit
operator|.
name|Validator
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
name|ValidatorProvider
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
name|org
operator|.
name|osgi
operator|.
name|service
operator|.
name|component
operator|.
name|ComponentContext
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
comment|/**  * A ChangeCollectorProvider can be hooked into Oak thus enabling the collection  * of ChangeSets of changed items of a commit, which downstream Observers can  * then use at their convenience.  *<p>  *   * @see ChangeSet for details on what is tracked and how that data should be  *      interpreted  */
end_comment

begin_class
annotation|@
name|Component
argument_list|(
name|immediate
operator|=
literal|true
argument_list|,
name|metatype
operator|=
literal|true
argument_list|,
name|label
operator|=
literal|"Apache Jackrabbit Oak Change Collector Service"
argument_list|,
name|description
operator|=
literal|"It hooks into the commit and collects a ChangeSet of changed items of a commit which "
operator|+
literal|"is then used to speed up observation processing"
argument_list|)
annotation|@
name|Property
argument_list|(
name|name
operator|=
literal|"type"
argument_list|,
name|value
operator|=
name|ChangeCollectorProvider
operator|.
name|TYPE
argument_list|,
name|propertyPrivate
operator|=
literal|true
argument_list|)
annotation|@
name|Service
argument_list|(
name|ValidatorProvider
operator|.
name|class
argument_list|)
specifier|public
class|class
name|ChangeCollectorProvider
extends|extends
name|ValidatorProvider
block|{
specifier|public
specifier|static
specifier|final
name|String
name|TYPE
init|=
literal|"changeCollectorProvider"
decl_stmt|;
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
name|ChangeCollectorProvider
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|COMMIT_CONTEXT_OBSERVATION_CHANGESET
init|=
literal|"oak.observation.changeSet"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_MAX_ITEMS
init|=
literal|50
decl_stmt|;
annotation|@
name|Property
argument_list|(
name|longValue
operator|=
name|DEFAULT_MAX_ITEMS
argument_list|,
name|label
operator|=
literal|"Maximum Number of Collected Items (per type)"
argument_list|,
name|description
operator|=
literal|"Integer value indicating maximum number of individual items of changes - "
operator|+
literal|"such as property, nodeType, node name, path - to be collected. If there are "
operator|+
literal|"more changes, the collection is considered failed and marked as such. "
operator|+
literal|"Default is "
operator|+
name|DEFAULT_MAX_ITEMS
argument_list|)
specifier|private
specifier|static
specifier|final
name|String
name|PROP_MAX_ITEMS
init|=
literal|"maxItems"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_MAX_PATH_DEPTH
init|=
literal|9
decl_stmt|;
annotation|@
name|Property
argument_list|(
name|longValue
operator|=
name|DEFAULT_MAX_PATH_DEPTH
argument_list|,
name|label
operator|=
literal|"Maximum depth of paths to collect"
argument_list|,
name|description
operator|=
literal|"Integer value indicating maximum depth of paths to collect. "
operator|+
literal|"Paths deeper than this will not be individually reported, and instead "
operator|+
literal|"a path at this max depth will be added. Note that this doesn't affect "
operator|+
literal|"any other collected item such as property, nodeType - ie those will "
operator|+
literal|"all be collected irrespective of this config param."
operator|+
literal|"Default is "
operator|+
name|DEFAULT_MAX_PATH_DEPTH
argument_list|)
specifier|private
specifier|static
specifier|final
name|String
name|PROP_MAX_PATH_DEPTH
init|=
literal|"maxPathDepth"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|boolean
name|DEFAULT_ENABLED
init|=
literal|true
decl_stmt|;
annotation|@
name|Property
argument_list|(
name|boolValue
operator|=
name|DEFAULT_ENABLED
argument_list|,
name|label
operator|=
literal|"enable/disable this validator"
argument_list|,
name|description
operator|=
literal|"Whether this validator is enabled. If disabled no ChangeSet will be generated. Default is "
operator|+
name|DEFAULT_ENABLED
argument_list|)
specifier|private
specifier|static
specifier|final
name|String
name|PROP_ENABLED
init|=
literal|"enabled"
decl_stmt|;
comment|/**      * There is one CollectorSupport per validation process - it is shared      * between multiple instances of ChangeCollector (Validator) - however it      * can remain unsynchronized as validators are executed single-threaded.      */
specifier|private
specifier|static
class|class
name|CollectorSupport
block|{
specifier|final
name|CommitInfo
name|info
decl_stmt|;
specifier|final
name|int
name|maxPathDepth
decl_stmt|;
specifier|final
name|ChangeSetBuilder
name|changeSetBuilder
decl_stmt|;
specifier|public
name|CollectorSupport
parameter_list|(
annotation|@
name|Nonnull
name|CommitInfo
name|info
parameter_list|,
annotation|@
name|Nonnull
name|ChangeSetBuilder
name|changeSetBuilder
parameter_list|,
name|int
name|maxPathDepth
parameter_list|)
block|{
name|this
operator|.
name|info
operator|=
name|info
expr_stmt|;
name|this
operator|.
name|changeSetBuilder
operator|=
name|changeSetBuilder
expr_stmt|;
name|this
operator|.
name|maxPathDepth
operator|=
name|maxPathDepth
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"CollectorSupport with "
operator|+
name|changeSetBuilder
return|;
block|}
block|}
comment|/**      * ChangeCollectors are the actual working-horse Validators that are created      * for each level thus as a whole propage through the entire change.      *<p>      * The actual data is collected via a per-commit CollectorSupport and its      * underlying ChangeSet (the latter is where the actual changes end up in).      *<p>      * When finished - ie in the last==root leave() - the resulting ChangeSet is      * marked immutable and set in the CommitContext.      */
specifier|private
specifier|static
class|class
name|ChangeCollector
implements|implements
name|Validator
block|{
specifier|private
specifier|final
name|CollectorSupport
name|support
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|isRoot
decl_stmt|;
specifier|private
specifier|final
name|NodeState
name|beforeParentNodeOrNull
decl_stmt|;
specifier|private
specifier|final
name|NodeState
name|afterParentNodeOrNull
decl_stmt|;
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
specifier|private
specifier|final
name|String
name|childName
decl_stmt|;
specifier|private
specifier|final
name|int
name|level
decl_stmt|;
specifier|private
name|boolean
name|changed
decl_stmt|;
specifier|private
specifier|static
name|ChangeCollector
name|newRootCollector
parameter_list|(
annotation|@
name|Nonnull
name|CommitInfo
name|info
parameter_list|,
name|int
name|maxItems
parameter_list|,
name|int
name|maxPathDepth
parameter_list|)
block|{
name|ChangeSetBuilder
name|changeSetBuilder
init|=
operator|new
name|ChangeSetBuilder
argument_list|(
name|maxItems
argument_list|,
name|maxPathDepth
argument_list|)
decl_stmt|;
name|CollectorSupport
name|support
init|=
operator|new
name|CollectorSupport
argument_list|(
name|info
argument_list|,
name|changeSetBuilder
argument_list|,
name|maxPathDepth
argument_list|)
decl_stmt|;
return|return
operator|new
name|ChangeCollector
argument_list|(
name|support
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|"/"
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|)
return|;
block|}
specifier|private
name|ChangeCollector
name|newChildCollector
parameter_list|(
annotation|@
name|Nullable
name|NodeState
name|beforeParentNodeOrNull
parameter_list|,
annotation|@
name|Nullable
name|NodeState
name|afterParentNodeOrNull
parameter_list|,
annotation|@
name|Nonnull
name|String
name|childName
parameter_list|)
block|{
return|return
operator|new
name|ChangeCollector
argument_list|(
name|support
argument_list|,
literal|false
argument_list|,
name|beforeParentNodeOrNull
argument_list|,
name|afterParentNodeOrNull
argument_list|,
name|concat
argument_list|(
name|path
argument_list|,
name|childName
argument_list|)
argument_list|,
name|childName
argument_list|,
name|level
operator|+
literal|1
argument_list|)
return|;
block|}
specifier|private
name|ChangeCollector
parameter_list|(
annotation|@
name|Nonnull
name|CollectorSupport
name|support
parameter_list|,
name|boolean
name|isRoot
parameter_list|,
annotation|@
name|Nullable
name|NodeState
name|beforeParentNodeOrNull
parameter_list|,
annotation|@
name|Nullable
name|NodeState
name|afterParentNodeOrNull
parameter_list|,
annotation|@
name|Nonnull
name|String
name|path
parameter_list|,
annotation|@
name|Nullable
name|String
name|childNameOrNull
parameter_list|,
name|int
name|level
parameter_list|)
block|{
name|this
operator|.
name|support
operator|=
name|support
expr_stmt|;
name|this
operator|.
name|isRoot
operator|=
name|isRoot
expr_stmt|;
name|this
operator|.
name|beforeParentNodeOrNull
operator|=
name|beforeParentNodeOrNull
expr_stmt|;
name|this
operator|.
name|afterParentNodeOrNull
operator|=
name|afterParentNodeOrNull
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|this
operator|.
name|childName
operator|=
name|childNameOrNull
expr_stmt|;
name|this
operator|.
name|level
operator|=
name|level
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"ChangeCollector[path="
operator|+
name|path
operator|+
literal|"]"
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|enter
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
throws|throws
name|CommitFailedException
block|{
comment|// nothing to be done here
block|}
annotation|@
name|Override
specifier|public
name|void
name|leave
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
throws|throws
name|CommitFailedException
block|{
comment|// first check if we have to add anything to paths and/or nodeNames
if|if
condition|(
name|changed
operator|&&
name|level
operator|<=
name|support
operator|.
name|maxPathDepth
condition|)
block|{
name|support
operator|.
name|changeSetBuilder
operator|.
name|addParentPath
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|changed
operator|&&
name|childName
operator|!=
literal|null
condition|)
block|{
name|support
operator|.
name|changeSetBuilder
operator|.
name|addParentNodeName
argument_list|(
name|childName
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|changed
condition|)
block|{
name|addParentNodeType
argument_list|(
name|beforeParentNodeOrNull
argument_list|)
expr_stmt|;
name|addParentNodeType
argument_list|(
name|afterParentNodeOrNull
argument_list|)
expr_stmt|;
block|}
comment|// then if we're not at the root, we're done
if|if
condition|(
operator|!
name|isRoot
condition|)
block|{
return|return;
block|}
comment|// but if we're at the root, then we add the ChangeSet to the
comment|// CommitContext of the CommitInfo
name|CommitContext
name|commitContext
init|=
operator|(
name|CommitContext
operator|)
name|support
operator|.
name|info
operator|.
name|getInfo
argument_list|()
operator|.
name|get
argument_list|(
name|CommitContext
operator|.
name|NAME
argument_list|)
decl_stmt|;
name|ChangeSet
name|changeSet
init|=
name|support
operator|.
name|changeSetBuilder
operator|.
name|build
argument_list|()
decl_stmt|;
name|commitContext
operator|.
name|set
argument_list|(
name|COMMIT_CONTEXT_OBSERVATION_CHANGESET
argument_list|,
name|changeSet
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Collected changeSet for commit {} is {}"
argument_list|,
name|support
operator|.
name|info
argument_list|,
name|changeSet
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|propertyAdded
parameter_list|(
name|PropertyState
name|after
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|addPropertyName
argument_list|(
name|after
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|propertyChanged
parameter_list|(
name|PropertyState
name|before
parameter_list|,
name|PropertyState
name|after
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|addPropertyName
argument_list|(
name|before
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|propertyDeleted
parameter_list|(
name|PropertyState
name|before
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|addPropertyName
argument_list|(
name|before
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Validator
name|childNodeAdded
parameter_list|(
name|String
name|childName
parameter_list|,
name|NodeState
name|after
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|changed
operator|=
literal|true
expr_stmt|;
name|addToAllNodeType
argument_list|(
name|after
argument_list|)
expr_stmt|;
return|return
name|newChildCollector
argument_list|(
literal|null
argument_list|,
name|after
argument_list|,
name|childName
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Validator
name|childNodeChanged
parameter_list|(
name|String
name|childName
parameter_list|,
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
throws|throws
name|CommitFailedException
block|{
if|if
condition|(
name|level
operator|==
name|support
operator|.
name|maxPathDepth
condition|)
block|{
comment|// then we'll cut off further paths below.
comment|// to compensate, add the current path at this level
name|support
operator|.
name|changeSetBuilder
operator|.
name|addParentPath
argument_list|(
name|path
argument_list|)
expr_stmt|;
comment|// however, continue normally to handle names/types/properties
comment|// below
block|}
comment|// in theory the node type could be changed, so collecting both before and after
name|addToAllNodeType
argument_list|(
name|before
argument_list|)
expr_stmt|;
name|addToAllNodeType
argument_list|(
name|after
argument_list|)
expr_stmt|;
return|return
name|newChildCollector
argument_list|(
name|before
argument_list|,
name|after
argument_list|,
name|childName
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Validator
name|childNodeDeleted
parameter_list|(
name|String
name|childName
parameter_list|,
name|NodeState
name|before
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|changed
operator|=
literal|true
expr_stmt|;
name|addToAllNodeType
argument_list|(
name|before
argument_list|)
expr_stmt|;
return|return
name|newChildCollector
argument_list|(
name|before
argument_list|,
literal|null
argument_list|,
name|childName
argument_list|)
return|;
block|}
specifier|private
name|void
name|addToAllNodeType
parameter_list|(
name|NodeState
name|state
parameter_list|)
block|{
name|String
name|primaryType
init|=
name|state
operator|.
name|getName
argument_list|(
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
argument_list|)
decl_stmt|;
if|if
condition|(
name|primaryType
operator|!=
literal|null
condition|)
block|{
name|support
operator|.
name|changeSetBuilder
operator|.
name|addNodeType
argument_list|(
name|primaryType
argument_list|)
expr_stmt|;
block|}
name|support
operator|.
name|changeSetBuilder
operator|.
name|addNodeTypes
argument_list|(
name|state
operator|.
name|getNames
argument_list|(
name|JcrConstants
operator|.
name|JCR_MIXINTYPES
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|addParentNodeType
parameter_list|(
annotation|@
name|Nullable
name|NodeState
name|state
parameter_list|)
block|{
if|if
condition|(
name|state
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|String
name|primaryType
init|=
name|state
operator|.
name|getName
argument_list|(
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
argument_list|)
decl_stmt|;
if|if
condition|(
name|primaryType
operator|!=
literal|null
condition|)
block|{
name|support
operator|.
name|changeSetBuilder
operator|.
name|addParentNodeType
argument_list|(
name|primaryType
argument_list|)
expr_stmt|;
block|}
name|support
operator|.
name|changeSetBuilder
operator|.
name|addParentNodeTypes
argument_list|(
name|state
operator|.
name|getNames
argument_list|(
name|JcrConstants
operator|.
name|JCR_MIXINTYPES
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|addPropertyName
parameter_list|(
name|PropertyState
name|after
parameter_list|)
block|{
name|changed
operator|=
literal|true
expr_stmt|;
name|support
operator|.
name|changeSetBuilder
operator|.
name|addPropertyName
argument_list|(
name|after
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|int
name|maxItems
init|=
name|DEFAULT_MAX_ITEMS
decl_stmt|;
specifier|private
name|int
name|maxPathDepth
init|=
name|DEFAULT_MAX_PATH_DEPTH
decl_stmt|;
specifier|private
name|boolean
name|enabled
init|=
name|DEFAULT_ENABLED
decl_stmt|;
annotation|@
name|Activate
specifier|protected
name|void
name|activate
parameter_list|(
name|ComponentContext
name|context
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|config
parameter_list|)
block|{
name|reconfig
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"activate: maxItems="
operator|+
name|maxItems
operator|+
literal|", maxPathDepth="
operator|+
name|maxPathDepth
operator|+
literal|", enabled="
operator|+
name|enabled
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Modified
specifier|protected
name|void
name|modified
parameter_list|(
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|config
parameter_list|)
block|{
name|reconfig
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"modified: maxItems="
operator|+
name|maxItems
operator|+
literal|", maxPathDepth="
operator|+
name|maxPathDepth
operator|+
literal|", enabled="
operator|+
name|enabled
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|reconfig
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|config
parameter_list|)
block|{
name|maxItems
operator|=
name|toInteger
argument_list|(
name|config
operator|.
name|get
argument_list|(
name|PROP_MAX_ITEMS
argument_list|)
argument_list|,
name|DEFAULT_MAX_ITEMS
argument_list|)
expr_stmt|;
name|maxPathDepth
operator|=
name|toInteger
argument_list|(
name|config
operator|.
name|get
argument_list|(
name|PROP_MAX_PATH_DEPTH
argument_list|)
argument_list|,
name|DEFAULT_MAX_PATH_DEPTH
argument_list|)
expr_stmt|;
name|enabled
operator|=
name|toBoolean
argument_list|(
name|config
operator|.
name|get
argument_list|(
name|PROP_ENABLED
argument_list|)
argument_list|,
name|DEFAULT_ENABLED
argument_list|)
expr_stmt|;
block|}
comment|/** FOR TESTING-ONLY **/
specifier|protected
name|void
name|setMaxPathDepth
parameter_list|(
name|int
name|maxPathDepth
parameter_list|)
block|{
name|this
operator|.
name|maxPathDepth
operator|=
name|maxPathDepth
expr_stmt|;
block|}
comment|/** FOR TESTING-ONLY **/
specifier|protected
name|int
name|getMaxPathDepth
parameter_list|()
block|{
return|return
name|this
operator|.
name|maxPathDepth
return|;
block|}
comment|/** FOR TESTING-ONLY **/
specifier|protected
name|void
name|setMaxItems
parameter_list|(
name|int
name|maxItems
parameter_list|)
block|{
name|this
operator|.
name|maxItems
operator|=
name|maxItems
expr_stmt|;
block|}
comment|/** FOR TESTING-ONLY **/
specifier|protected
name|int
name|getMaxItems
parameter_list|()
block|{
return|return
name|this
operator|.
name|maxItems
return|;
block|}
annotation|@
name|Override
specifier|protected
name|Validator
name|getRootValidator
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|,
name|CommitInfo
name|info
parameter_list|)
block|{
if|if
condition|(
operator|!
name|enabled
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|info
operator|==
literal|null
operator|||
operator|!
name|info
operator|.
name|getInfo
argument_list|()
operator|.
name|containsKey
argument_list|(
name|CommitContext
operator|.
name|NAME
argument_list|)
condition|)
block|{
comment|// then we cannot do change-collecting, as we can't store
comment|// it in the info
return|return
literal|null
return|;
block|}
return|return
name|ChangeCollector
operator|.
name|newRootCollector
argument_list|(
name|info
argument_list|,
name|maxItems
argument_list|,
name|maxPathDepth
argument_list|)
return|;
block|}
block|}
end_class

end_unit

