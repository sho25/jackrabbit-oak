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
name|index
package|;
end_package

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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|HashMultimap
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
name|Multimap
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
name|plugins
operator|.
name|index
operator|.
name|AsyncIndexUpdate
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
name|index
operator|.
name|IndexConstants
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
name|index
operator|.
name|IndexEditorProvider
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
name|index
operator|.
name|IndexUtils
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
name|index
operator|.
name|NodeStoreUtils
operator|.
name|childBuilder
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
name|index
operator|.
name|NodeStoreUtils
operator|.
name|mergeWithConcurrentCheck
import|;
end_import

begin_class
specifier|public
class|class
name|SimpleAsyncReindexer
block|{
specifier|private
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|IndexHelper
name|indexHelper
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|indexPaths
decl_stmt|;
specifier|private
specifier|final
name|IndexEditorProvider
name|indexEditorProvider
decl_stmt|;
specifier|public
name|SimpleAsyncReindexer
parameter_list|(
name|IndexHelper
name|indexHelper
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|indexPaths
parameter_list|,
name|IndexEditorProvider
name|indexEditorProvider
parameter_list|)
block|{
name|this
operator|.
name|indexHelper
operator|=
name|indexHelper
expr_stmt|;
name|this
operator|.
name|indexPaths
operator|=
name|indexPaths
expr_stmt|;
name|this
operator|.
name|indexEditorProvider
operator|=
name|indexEditorProvider
expr_stmt|;
block|}
specifier|public
name|void
name|reindex
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|setReindexFlag
argument_list|()
expr_stmt|;
name|Multimap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|laneMapping
init|=
name|getIndexesPerLane
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|laneName
range|:
name|laneMapping
operator|.
name|keySet
argument_list|()
control|)
block|{
name|reindex
argument_list|(
name|laneName
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|setReindexFlag
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|NodeBuilder
name|builder
init|=
name|getNodeStore
argument_list|()
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|indexPath
range|:
name|indexPaths
control|)
block|{
name|NodeBuilder
name|idx
init|=
name|childBuilder
argument_list|(
name|builder
argument_list|,
name|indexPath
argument_list|)
decl_stmt|;
name|idx
operator|.
name|setProperty
argument_list|(
name|IndexConstants
operator|.
name|REINDEX_PROPERTY_NAME
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|mergeWithConcurrentCheck
argument_list|(
name|getNodeStore
argument_list|()
argument_list|,
name|builder
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|reindex
parameter_list|(
name|String
name|laneName
parameter_list|)
block|{
name|AsyncIndexUpdate
name|async
init|=
operator|new
name|AsyncIndexUpdate
argument_list|(
name|laneName
argument_list|,
name|getNodeStore
argument_list|()
argument_list|,
name|indexEditorProvider
argument_list|,
name|indexHelper
operator|.
name|getStatisticsProvider
argument_list|()
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|//TODO Expose the JMX
name|boolean
name|done
init|=
literal|false
decl_stmt|;
while|while
condition|(
operator|!
name|done
condition|)
block|{
comment|//TODO Check for timeout
name|async
operator|.
name|run
argument_list|()
expr_stmt|;
name|done
operator|=
name|async
operator|.
name|isFinished
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|Multimap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getIndexesPerLane
parameter_list|()
block|{
name|NodeState
name|root
init|=
name|getNodeStore
argument_list|()
operator|.
name|getRoot
argument_list|()
decl_stmt|;
name|Multimap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
init|=
name|HashMultimap
operator|.
name|create
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|indexPath
range|:
name|indexPaths
control|)
block|{
name|NodeState
name|idxState
init|=
name|NodeStateUtils
operator|.
name|getNode
argument_list|(
name|root
argument_list|,
name|indexPath
argument_list|)
decl_stmt|;
name|String
name|asyncName
init|=
name|IndexUtils
operator|.
name|getAsyncLaneName
argument_list|(
name|idxState
argument_list|,
name|indexPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|asyncName
operator|!=
literal|null
condition|)
block|{
name|map
operator|.
name|put
argument_list|(
name|asyncName
argument_list|,
name|indexPath
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"No async value for indexPath {}. Ignoring it"
argument_list|,
name|indexPath
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|map
return|;
block|}
specifier|private
name|NodeStore
name|getNodeStore
parameter_list|()
block|{
return|return
name|indexHelper
operator|.
name|getNodeStore
argument_list|()
return|;
block|}
block|}
end_class

end_unit
