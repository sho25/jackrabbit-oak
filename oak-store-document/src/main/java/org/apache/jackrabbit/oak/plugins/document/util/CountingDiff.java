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
name|document
operator|.
name|util
package|;
end_package

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
name|NodeStateDiff
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
name|EmptyNodeState
operator|.
name|EMPTY_NODE
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
name|EmptyNodeState
operator|.
name|MISSING_NODE
import|;
end_import

begin_comment
comment|/**  * A {@link NodeStateDiff} implementation that counts the differences between  * two node states, including their sub tree.  */
end_comment

begin_class
specifier|public
class|class
name|CountingDiff
implements|implements
name|NodeStateDiff
block|{
specifier|private
name|int
name|changes
init|=
literal|0
decl_stmt|;
specifier|public
specifier|static
name|int
name|countChanges
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
name|CountingDiff
name|counter
init|=
operator|new
name|CountingDiff
argument_list|()
decl_stmt|;
name|after
operator|.
name|compareAgainstBaseState
argument_list|(
name|before
argument_list|,
name|counter
argument_list|)
expr_stmt|;
return|return
name|counter
operator|.
name|getNumChanges
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|propertyAdded
parameter_list|(
name|PropertyState
name|after
parameter_list|)
block|{
name|inc
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|propertyChanged
parameter_list|(
name|PropertyState
name|before
parameter_list|,
name|PropertyState
name|after
parameter_list|)
block|{
name|inc
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|propertyDeleted
parameter_list|(
name|PropertyState
name|before
parameter_list|)
block|{
name|inc
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|childNodeAdded
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
name|inc
argument_list|()
expr_stmt|;
return|return
name|after
operator|.
name|compareAgainstBaseState
argument_list|(
name|EMPTY_NODE
argument_list|,
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
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
name|inc
argument_list|()
expr_stmt|;
return|return
name|after
operator|.
name|compareAgainstBaseState
argument_list|(
name|before
argument_list|,
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|childNodeDeleted
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|)
block|{
name|inc
argument_list|()
expr_stmt|;
return|return
name|MISSING_NODE
operator|.
name|compareAgainstBaseState
argument_list|(
name|before
argument_list|,
name|this
argument_list|)
return|;
block|}
specifier|public
name|int
name|getNumChanges
parameter_list|()
block|{
return|return
name|changes
return|;
block|}
specifier|private
name|void
name|inc
parameter_list|()
block|{
name|changes
operator|++
expr_stmt|;
block|}
block|}
end_class

end_unit

