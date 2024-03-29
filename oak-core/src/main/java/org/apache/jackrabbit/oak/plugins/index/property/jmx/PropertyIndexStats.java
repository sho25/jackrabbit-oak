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
name|index
operator|.
name|property
operator|.
name|jmx
package|;
end_package

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
name|management
operator|.
name|openmbean
operator|.
name|ArrayType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|CompositeData
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|CompositeDataSupport
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|CompositeType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|OpenDataException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|OpenType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|SimpleType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|TabularData
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|TabularDataSupport
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|TabularType
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
name|Iterables
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|TreeTraverser
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
name|commons
operator|.
name|jmx
operator|.
name|AnnotatedStandardMBean
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
name|osgi
operator|.
name|OsgiWhiteboard
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
name|tree
operator|.
name|factories
operator|.
name|TreeFactory
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
name|NodeState
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
name|NodeStateUtils
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
name|NodeStore
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
name|whiteboard
operator|.
name|Registration
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
name|osgi
operator|.
name|framework
operator|.
name|BundleContext
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
name|annotations
operator|.
name|Activate
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
name|annotations
operator|.
name|Component
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
name|annotations
operator|.
name|Deactivate
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
name|annotations
operator|.
name|Reference
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
name|plugins
operator|.
name|index
operator|.
name|IndexConstants
operator|.
name|INDEX_CONTENT_NODE_NAME
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
name|index
operator|.
name|IndexConstants
operator|.
name|UNIQUE_PROPERTY_NAME
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
name|whiteboard
operator|.
name|WhiteboardUtils
operator|.
name|registerMBean
import|;
end_import

begin_class
annotation|@
name|Component
argument_list|(
name|service
operator|=
block|{}
argument_list|)
specifier|public
class|class
name|PropertyIndexStats
extends|extends
name|AnnotatedStandardMBean
implements|implements
name|PropertyIndexStatsMBean
block|{
annotation|@
name|Reference
specifier|private
name|NodeStore
name|store
decl_stmt|;
specifier|private
name|Registration
name|reg
decl_stmt|;
specifier|public
name|PropertyIndexStats
parameter_list|()
block|{
name|super
argument_list|(
name|PropertyIndexStatsMBean
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Activate
specifier|private
name|void
name|activate
parameter_list|(
name|BundleContext
name|context
parameter_list|)
block|{
name|reg
operator|=
name|registerMBean
argument_list|(
operator|new
name|OsgiWhiteboard
argument_list|(
name|context
argument_list|)
argument_list|,
name|PropertyIndexStatsMBean
operator|.
name|class
argument_list|,
name|this
argument_list|,
name|PropertyIndexStatsMBean
operator|.
name|TYPE
argument_list|,
literal|"Property Index statistics"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Deactivate
specifier|private
name|void
name|deactivate
parameter_list|()
block|{
if|if
condition|(
name|reg
operator|!=
literal|null
condition|)
block|{
name|reg
operator|.
name|unregister
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|TabularData
name|getStatsForAllIndexes
parameter_list|(
name|String
name|path
parameter_list|,
name|int
name|maxValueCount
parameter_list|,
name|int
name|maxDepth
parameter_list|,
name|int
name|maxPathCount
parameter_list|)
throws|throws
name|OpenDataException
block|{
name|String
name|indexRootPath
init|=
name|concat
argument_list|(
name|path
argument_list|,
literal|"oak:index"
argument_list|)
decl_stmt|;
name|NodeState
name|idxRoot
init|=
name|NodeStateUtils
operator|.
name|getNode
argument_list|(
name|store
operator|.
name|getRoot
argument_list|()
argument_list|,
name|indexRootPath
argument_list|)
decl_stmt|;
name|TabularType
name|tt
init|=
operator|new
name|TabularType
argument_list|(
name|PropertyIndexStats
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
literal|"Property Index Stats"
argument_list|,
name|getType
argument_list|()
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"path"
block|}
argument_list|)
decl_stmt|;
name|TabularDataSupport
name|tds
init|=
operator|new
name|TabularDataSupport
argument_list|(
name|tt
argument_list|)
decl_stmt|;
for|for
control|(
name|ChildNodeEntry
name|cne
range|:
name|idxRoot
operator|.
name|getChildNodeEntries
argument_list|()
control|)
block|{
if|if
condition|(
literal|"property"
operator|.
name|equals
argument_list|(
name|cne
operator|.
name|getNodeState
argument_list|()
operator|.
name|getString
argument_list|(
literal|"type"
argument_list|)
argument_list|)
condition|)
block|{
name|CompositeData
name|stats
init|=
name|getStatsForIndex
argument_list|(
name|concat
argument_list|(
name|indexRootPath
argument_list|,
name|cne
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|,
name|cne
operator|.
name|getNodeState
argument_list|()
argument_list|,
name|maxValueCount
argument_list|,
name|maxDepth
argument_list|,
name|maxPathCount
argument_list|)
decl_stmt|;
name|tds
operator|.
name|put
argument_list|(
name|stats
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|tds
return|;
block|}
annotation|@
name|Override
specifier|public
name|CompositeData
name|getStatsForSpecificIndex
parameter_list|(
name|String
name|path
parameter_list|,
name|int
name|maxValueCount
parameter_list|,
name|int
name|maxDepth
parameter_list|,
name|int
name|maxPathCount
parameter_list|)
throws|throws
name|OpenDataException
block|{
name|NodeState
name|idx
init|=
name|NodeStateUtils
operator|.
name|getNode
argument_list|(
name|store
operator|.
name|getRoot
argument_list|()
argument_list|,
name|path
argument_list|)
decl_stmt|;
return|return
name|getStatsForIndex
argument_list|(
name|path
argument_list|,
name|idx
argument_list|,
name|maxValueCount
argument_list|,
name|maxDepth
argument_list|,
name|maxPathCount
argument_list|)
return|;
block|}
specifier|private
name|CompositeData
name|getStatsForIndex
parameter_list|(
name|String
name|path
parameter_list|,
name|NodeState
name|idx
parameter_list|,
name|int
name|maxValueCount
parameter_list|,
name|int
name|maxDepth
parameter_list|,
name|int
name|maxPathCount
parameter_list|)
throws|throws
name|OpenDataException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|result
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
comment|//Add placeholder
name|result
operator|.
name|put
argument_list|(
literal|"path"
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|result
operator|.
name|put
argument_list|(
literal|"values"
argument_list|,
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|result
operator|.
name|put
argument_list|(
literal|"paths"
argument_list|,
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|result
operator|.
name|put
argument_list|(
literal|"valueCount"
argument_list|,
operator|-
literal|1L
argument_list|)
expr_stmt|;
name|result
operator|.
name|put
argument_list|(
literal|"pathCount"
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|result
operator|.
name|put
argument_list|(
literal|"maxPathCount"
argument_list|,
name|maxPathCount
argument_list|)
expr_stmt|;
name|result
operator|.
name|put
argument_list|(
literal|"maxDepth"
argument_list|,
name|maxDepth
argument_list|)
expr_stmt|;
name|result
operator|.
name|put
argument_list|(
literal|"maxValueCount"
argument_list|,
name|maxValueCount
argument_list|)
expr_stmt|;
name|String
name|status
init|=
literal|"No index found at path "
operator|+
name|path
decl_stmt|;
name|NodeState
name|data
init|=
name|idx
operator|.
name|getChildNode
argument_list|(
name|INDEX_CONTENT_NODE_NAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|data
operator|.
name|exists
argument_list|()
condition|)
block|{
if|if
condition|(
name|idx
operator|.
name|getBoolean
argument_list|(
name|UNIQUE_PROPERTY_NAME
argument_list|)
condition|)
block|{
name|status
operator|=
literal|"stats not supported for unique indexes"
expr_stmt|;
block|}
else|else
block|{
name|long
name|childNodeCount
init|=
name|data
operator|.
name|getChildNodeCount
argument_list|(
name|maxValueCount
argument_list|)
decl_stmt|;
if|if
condition|(
name|childNodeCount
operator|==
name|Long
operator|.
name|MAX_VALUE
operator|||
name|childNodeCount
operator|>
name|maxValueCount
condition|)
block|{
name|status
operator|=
name|String
operator|.
name|format
argument_list|(
literal|"stats cannot be determined as number of values exceed the max limit of "
operator|+
literal|"[%d]. Estimated value count [%d]"
argument_list|,
name|maxValueCount
argument_list|,
name|childNodeCount
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
index|[]
name|values
init|=
name|Iterables
operator|.
name|toArray
argument_list|(
name|Iterables
operator|.
name|limit
argument_list|(
name|data
operator|.
name|getChildNodeNames
argument_list|()
argument_list|,
name|maxValueCount
argument_list|)
argument_list|,
name|String
operator|.
name|class
argument_list|)
decl_stmt|;
name|String
index|[]
name|paths
init|=
name|determineIndexedPaths
argument_list|(
name|data
operator|.
name|getChildNodeEntries
argument_list|()
argument_list|,
name|maxDepth
argument_list|,
name|maxPathCount
argument_list|)
decl_stmt|;
name|result
operator|.
name|put
argument_list|(
literal|"values"
argument_list|,
name|values
argument_list|)
expr_stmt|;
name|result
operator|.
name|put
argument_list|(
literal|"paths"
argument_list|,
name|paths
argument_list|)
expr_stmt|;
name|result
operator|.
name|put
argument_list|(
literal|"pathCount"
argument_list|,
name|paths
operator|.
name|length
argument_list|)
expr_stmt|;
name|status
operator|=
literal|"Result determined and above path list can be safely used based on current indexed data"
expr_stmt|;
block|}
name|result
operator|.
name|put
argument_list|(
literal|"valueCount"
argument_list|,
name|childNodeCount
argument_list|)
expr_stmt|;
block|}
block|}
name|result
operator|.
name|put
argument_list|(
literal|"status"
argument_list|,
name|status
argument_list|)
expr_stmt|;
return|return
operator|new
name|CompositeDataSupport
argument_list|(
name|getType
argument_list|()
argument_list|,
name|result
argument_list|)
return|;
block|}
specifier|private
name|String
index|[]
name|determineIndexedPaths
parameter_list|(
name|Iterable
argument_list|<
name|?
extends|extends
name|ChildNodeEntry
argument_list|>
name|values
parameter_list|,
specifier|final
name|int
name|maxDepth
parameter_list|,
name|int
name|maxPathCount
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|paths
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|intermediatePaths
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
name|int
name|maxPathLimitBreachedAtLevel
init|=
operator|-
literal|1
decl_stmt|;
name|topLevel
label|:
for|for
control|(
name|ChildNodeEntry
name|cne
range|:
name|values
control|)
block|{
name|Tree
name|t
init|=
name|TreeFactory
operator|.
name|createReadOnlyTree
argument_list|(
name|cne
operator|.
name|getNodeState
argument_list|()
argument_list|)
decl_stmt|;
name|TreeTraverser
argument_list|<
name|Tree
argument_list|>
name|traverser
init|=
operator|new
name|TreeTraverser
argument_list|<
name|Tree
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterable
argument_list|<
name|Tree
argument_list|>
name|children
parameter_list|(
annotation|@
name|NotNull
name|Tree
name|root
parameter_list|)
block|{
comment|//Break at maxLevel
if|if
condition|(
name|PathUtils
operator|.
name|getDepth
argument_list|(
name|root
operator|.
name|getPath
argument_list|()
argument_list|)
operator|>=
name|maxDepth
condition|)
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
return|return
name|root
operator|.
name|getChildren
argument_list|()
return|;
block|}
block|}
decl_stmt|;
for|for
control|(
name|Tree
name|node
range|:
name|traverser
operator|.
name|breadthFirstTraversal
argument_list|(
name|t
argument_list|)
control|)
block|{
name|PropertyState
name|matchState
init|=
name|node
operator|.
name|getProperty
argument_list|(
literal|"match"
argument_list|)
decl_stmt|;
name|boolean
name|match
init|=
name|matchState
operator|==
literal|null
condition|?
literal|false
else|:
name|matchState
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|BOOLEAN
argument_list|)
decl_stmt|;
name|int
name|depth
init|=
name|PathUtils
operator|.
name|getDepth
argument_list|(
name|node
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
comment|//Intermediate nodes which are not leaf are not to be included
if|if
condition|(
name|depth
operator|<
name|maxDepth
operator|&&
operator|!
name|match
condition|)
block|{
name|intermediatePaths
operator|.
name|add
argument_list|(
name|node
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|paths
operator|.
name|size
argument_list|()
operator|<
name|maxPathCount
condition|)
block|{
name|paths
operator|.
name|add
argument_list|(
name|node
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|maxPathLimitBreachedAtLevel
operator|=
name|depth
expr_stmt|;
break|break
name|topLevel
break|;
block|}
block|}
block|}
if|if
condition|(
name|maxPathLimitBreachedAtLevel
operator|<
literal|0
condition|)
block|{
return|return
name|Iterables
operator|.
name|toArray
argument_list|(
name|paths
argument_list|,
name|String
operator|.
name|class
argument_list|)
return|;
block|}
comment|//If max limit for path is reached then we can safely
comment|//say about includedPaths upto depth = level at which limit reached - 1
comment|//As for that level we know *all* the path roots
name|Set
argument_list|<
name|String
argument_list|>
name|result
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
name|int
name|safeDepth
init|=
name|maxPathLimitBreachedAtLevel
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|safeDepth
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|String
name|path
range|:
name|intermediatePaths
control|)
block|{
name|int
name|pathDepth
init|=
name|PathUtils
operator|.
name|getDepth
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|pathDepth
operator|==
name|safeDepth
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|Iterables
operator|.
name|toArray
argument_list|(
name|result
argument_list|,
name|String
operator|.
name|class
argument_list|)
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|private
specifier|static
name|CompositeType
name|getType
parameter_list|()
throws|throws
name|OpenDataException
block|{
return|return
operator|new
name|CompositeType
argument_list|(
literal|"PropertyIndexStats"
argument_list|,
literal|"Property index related stats"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"path"
block|,
literal|"values"
block|,
literal|"paths"
block|,
literal|"valueCount"
block|,
literal|"status"
block|,
literal|"pathCount"
block|,
literal|"maxPathCount"
block|,
literal|"maxDepth"
block|,
literal|"maxValueCount"
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"path"
block|,
literal|"values"
block|,
literal|"paths"
block|,
literal|"valueCount"
block|,
literal|"status"
block|,
literal|"pathCount"
block|,
literal|"maxPathCount"
block|,
literal|"maxDepth"
block|,
literal|"maxValueCount"
block|}
argument_list|,
operator|new
name|OpenType
index|[]
block|{
name|SimpleType
operator|.
name|STRING
block|,
operator|new
name|ArrayType
argument_list|(
name|SimpleType
operator|.
name|STRING
argument_list|,
literal|false
argument_list|)
block|,
operator|new
name|ArrayType
argument_list|(
name|SimpleType
operator|.
name|STRING
argument_list|,
literal|false
argument_list|)
block|,
name|SimpleType
operator|.
name|LONG
block|,
name|SimpleType
operator|.
name|STRING
block|,
name|SimpleType
operator|.
name|INTEGER
block|,
name|SimpleType
operator|.
name|INTEGER
block|,
name|SimpleType
operator|.
name|INTEGER
block|,
name|SimpleType
operator|.
name|INTEGER
block|,                 }
argument_list|)
return|;
block|}
block|}
end_class

end_unit

