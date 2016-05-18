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
name|segment
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
name|checkArgument
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
name|Lists
operator|.
name|newArrayList
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
name|List
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Supplier
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
comment|// FIXME OAK-4277: Finalise de-duplication caches
end_comment

begin_comment
comment|// implement configuration, monitoring and management
end_comment

begin_comment
comment|// add unit tests
end_comment

begin_comment
comment|// document, nullability
end_comment

begin_class
specifier|public
class|class
name|NodeCache
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
name|NodeCache
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|int
name|capacity
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|RecordId
argument_list|>
argument_list|>
name|nodes
decl_stmt|;
specifier|private
name|int
name|size
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|Integer
argument_list|>
name|muteDepths
init|=
name|newHashSet
argument_list|()
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|Supplier
argument_list|<
name|NodeCache
argument_list|>
name|factory
parameter_list|(
specifier|final
name|int
name|capacity
parameter_list|,
specifier|final
name|int
name|maxDepth
parameter_list|)
block|{
return|return
operator|new
name|Supplier
argument_list|<
name|NodeCache
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|NodeCache
name|get
parameter_list|()
block|{
return|return
operator|new
name|NodeCache
argument_list|(
name|capacity
argument_list|,
name|maxDepth
argument_list|)
return|;
block|}
block|}
return|;
block|}
specifier|public
specifier|static
specifier|final
name|Supplier
argument_list|<
name|NodeCache
argument_list|>
name|empty
parameter_list|()
block|{
return|return
operator|new
name|Supplier
argument_list|<
name|NodeCache
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|NodeCache
name|get
parameter_list|()
block|{
return|return
operator|new
name|NodeCache
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
block|{
annotation|@
name|Override
specifier|public
specifier|synchronized
name|void
name|put
parameter_list|(
name|String
name|key
parameter_list|,
name|RecordId
name|value
parameter_list|,
name|int
name|depth
parameter_list|)
block|{ }
annotation|@
name|Override
specifier|public
specifier|synchronized
name|RecordId
name|get
parameter_list|(
name|String
name|key
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
return|;
block|}
block|}
return|;
block|}
specifier|public
name|NodeCache
parameter_list|(
name|int
name|capacity
parameter_list|,
name|int
name|maxDepth
parameter_list|)
block|{
name|checkArgument
argument_list|(
name|capacity
operator|>
literal|0
argument_list|)
expr_stmt|;
name|checkArgument
argument_list|(
name|maxDepth
operator|>
literal|0
argument_list|)
expr_stmt|;
name|this
operator|.
name|capacity
operator|=
name|capacity
expr_stmt|;
name|this
operator|.
name|nodes
operator|=
name|newArrayList
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
name|maxDepth
condition|;
name|k
operator|++
control|)
block|{
name|nodes
operator|.
name|add
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|RecordId
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
specifier|synchronized
name|void
name|put
parameter_list|(
name|String
name|key
parameter_list|,
name|RecordId
name|value
parameter_list|,
name|int
name|depth
parameter_list|)
block|{
comment|// FIXME OAK-4277: Finalise de-duplication caches
comment|// Validate and optimise the eviction strategy.
comment|// Nodes with many children should probably get a boost to
comment|// protecting them from preemptive eviction. Also it might be
comment|// necessary to implement pinning (e.g. for checkpoints).
while|while
condition|(
name|size
operator|>=
name|capacity
condition|)
block|{
name|int
name|d
init|=
name|nodes
operator|.
name|size
argument_list|()
operator|-
literal|1
decl_stmt|;
name|int
name|removed
init|=
name|nodes
operator|.
name|remove
argument_list|(
name|d
argument_list|)
operator|.
name|size
argument_list|()
decl_stmt|;
name|size
operator|-=
name|removed
expr_stmt|;
if|if
condition|(
name|removed
operator|>
literal|0
condition|)
block|{
comment|// FIXME OAK-4165: Too verbose logging during revision gc
name|LOG
operator|.
name|info
argument_list|(
literal|"Evicted cache at depth {} as size {} reached capacity {}. "
operator|+
literal|"New size is {}"
argument_list|,
name|d
argument_list|,
name|size
operator|+
name|removed
argument_list|,
name|capacity
argument_list|,
name|size
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|depth
operator|<
name|nodes
operator|.
name|size
argument_list|()
condition|)
block|{
if|if
condition|(
name|nodes
operator|.
name|get
argument_list|(
name|depth
argument_list|)
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
operator|==
literal|null
condition|)
block|{
name|size
operator|++
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|muteDepths
operator|.
name|add
argument_list|(
name|depth
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Not caching {} -> {} as depth {} reaches or exceeds the maximum of {}"
argument_list|,
name|key
argument_list|,
name|value
argument_list|,
name|depth
argument_list|,
name|nodes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
specifier|synchronized
name|RecordId
name|get
parameter_list|(
name|String
name|key
parameter_list|)
block|{
for|for
control|(
name|Map
argument_list|<
name|String
argument_list|,
name|RecordId
argument_list|>
name|map
range|:
name|nodes
control|)
block|{
if|if
condition|(
operator|!
name|map
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|RecordId
name|recordId
init|=
name|map
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|recordId
operator|!=
literal|null
condition|)
block|{
return|return
name|recordId
return|;
block|}
block|}
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

