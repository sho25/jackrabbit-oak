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
name|index
operator|.
name|diffindex
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
name|plugins
operator|.
name|memory
operator|.
name|MemoryNodeState
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
name|query
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
name|spi
operator|.
name|state
operator|.
name|EmptyNodeStateDiff
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

begin_class
specifier|public
specifier|abstract
class|class
name|BaseDiffCollector
implements|implements
name|DiffCollector
block|{
specifier|private
specifier|final
name|NodeState
name|before
decl_stmt|;
specifier|private
specifier|final
name|NodeState
name|after
decl_stmt|;
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|results
decl_stmt|;
specifier|protected
name|boolean
name|init
init|=
literal|false
decl_stmt|;
comment|/**      * @param before      *            initial state      * @param after      *            after state      * @param filter      *            filter that verifies of a NodeState qualifies or not      */
specifier|public
name|BaseDiffCollector
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
name|this
operator|.
name|before
operator|=
name|before
expr_stmt|;
name|this
operator|.
name|after
operator|=
name|after
expr_stmt|;
name|results
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
block|}
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getResults
parameter_list|(
name|Filter
name|filter
parameter_list|)
block|{
if|if
condition|(
operator|!
name|init
condition|)
block|{
name|collect
argument_list|(
name|filter
argument_list|)
expr_stmt|;
block|}
return|return
name|results
return|;
block|}
specifier|public
name|double
name|getCost
parameter_list|(
name|Filter
name|filter
parameter_list|)
block|{
if|if
condition|(
operator|!
name|init
condition|)
block|{
name|collect
argument_list|(
name|filter
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|results
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|Double
operator|.
name|POSITIVE_INFINITY
return|;
block|}
comment|// TODO probably the number of read nodes during the diff
return|return
literal|0
return|;
block|}
specifier|public
name|void
name|collect
parameter_list|(
specifier|final
name|Filter
name|filter
parameter_list|)
block|{
name|DiffCollectorNodeStateDiff
name|diff
init|=
operator|new
name|DiffCollectorNodeStateDiff
argument_list|(
name|this
argument_list|,
name|filter
argument_list|)
decl_stmt|;
name|after
operator|.
name|compareAgainstBaseState
argument_list|(
name|before
argument_list|,
name|diff
argument_list|)
expr_stmt|;
name|this
operator|.
name|results
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|(
name|diff
operator|.
name|getResults
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|init
operator|=
literal|true
expr_stmt|;
block|}
specifier|abstract
name|boolean
name|match
parameter_list|(
name|NodeState
name|state
parameter_list|,
name|Filter
name|filter
parameter_list|)
function_decl|;
specifier|protected
name|boolean
name|isUnique
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|private
specifier|static
class|class
name|DiffCollectorNodeStateDiff
extends|extends
name|EmptyNodeStateDiff
block|{
specifier|private
specifier|final
name|BaseDiffCollector
name|collector
decl_stmt|;
specifier|private
specifier|final
name|Filter
name|filter
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|results
decl_stmt|;
specifier|private
specifier|final
name|DiffCollectorNodeStateDiff
name|parent
decl_stmt|;
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
specifier|private
name|boolean
name|done
decl_stmt|;
name|DiffCollectorNodeStateDiff
parameter_list|(
name|BaseDiffCollector
name|collector
parameter_list|,
name|Filter
name|filter
parameter_list|)
block|{
name|this
argument_list|(
name|collector
argument_list|,
name|filter
argument_list|,
literal|null
argument_list|,
literal|""
argument_list|,
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|DiffCollectorNodeStateDiff
parameter_list|(
name|BaseDiffCollector
name|collector
parameter_list|,
name|Filter
name|filter
parameter_list|,
name|DiffCollectorNodeStateDiff
name|parent
parameter_list|,
name|String
name|path
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|results
parameter_list|)
block|{
name|this
operator|.
name|collector
operator|=
name|collector
expr_stmt|;
name|this
operator|.
name|filter
operator|=
name|filter
expr_stmt|;
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|this
operator|.
name|results
operator|=
name|results
expr_stmt|;
block|}
specifier|private
name|boolean
name|isDone
parameter_list|()
block|{
if|if
condition|(
name|parent
operator|!=
literal|null
condition|)
block|{
return|return
name|parent
operator|.
name|isDone
argument_list|()
return|;
block|}
return|return
name|done
return|;
block|}
specifier|private
name|void
name|setDone
parameter_list|()
block|{
if|if
condition|(
name|parent
operator|!=
literal|null
condition|)
block|{
name|parent
operator|.
name|setDone
argument_list|()
expr_stmt|;
return|return;
block|}
name|done
operator|=
literal|true
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|childNodeAdded
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
name|childNodeChanged
argument_list|(
name|name
argument_list|,
name|MemoryNodeState
operator|.
name|EMPTY_NODE
argument_list|,
name|after
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|childNodeChanged
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
if|if
condition|(
name|NodeStateUtils
operator|.
name|isHidden
argument_list|(
name|name
argument_list|)
operator|||
name|isDone
argument_list|()
condition|)
block|{
return|return;
block|}
name|testNodeState
argument_list|(
name|after
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|after
operator|.
name|compareAgainstBaseState
argument_list|(
name|before
argument_list|,
operator|new
name|DiffCollectorNodeStateDiff
argument_list|(
name|collector
argument_list|,
name|filter
argument_list|,
name|this
argument_list|,
name|concat
argument_list|(
name|path
argument_list|,
name|name
argument_list|)
argument_list|,
name|results
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|testNodeState
parameter_list|(
name|NodeState
name|nodeState
parameter_list|,
name|String
name|currentPath
parameter_list|)
block|{
if|if
condition|(
name|isDone
argument_list|()
condition|)
block|{
return|return;
block|}
name|boolean
name|match
init|=
name|collector
operator|.
name|match
argument_list|(
name|nodeState
argument_list|,
name|filter
argument_list|)
decl_stmt|;
if|if
condition|(
name|match
condition|)
block|{
name|results
operator|.
name|add
argument_list|(
name|concat
argument_list|(
name|path
argument_list|,
name|currentPath
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|collector
operator|.
name|isUnique
argument_list|()
condition|)
block|{
name|setDone
argument_list|()
expr_stmt|;
return|return;
block|}
block|}
block|}
name|Set
argument_list|<
name|String
argument_list|>
name|getResults
parameter_list|()
block|{
return|return
name|results
return|;
block|}
block|}
block|}
end_class

end_unit

