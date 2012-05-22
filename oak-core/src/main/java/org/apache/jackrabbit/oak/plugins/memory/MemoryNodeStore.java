begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|memory
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
name|spi
operator|.
name|state
operator|.
name|AbstractNodeStore
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
name|NodeStateBuilder
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

begin_comment
comment|/**  * Abstract node store base class with in-memory node state builder  * functionality.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|MemoryNodeStore
extends|extends
name|AbstractNodeStore
block|{
annotation|@
name|Override
specifier|public
name|NodeStateBuilder
name|getBuilder
parameter_list|(
name|NodeState
name|base
parameter_list|)
block|{
return|return
operator|new
name|MemoryNodeStateBuilder
argument_list|(
name|base
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|compare
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|,
name|NodeStateDiff
name|diff
parameter_list|)
block|{
if|if
condition|(
name|after
operator|instanceof
name|ModifiedNodeState
condition|)
block|{
name|ModifiedNodeState
name|modified
init|=
operator|(
name|ModifiedNodeState
operator|)
name|after
decl_stmt|;
if|if
condition|(
name|before
operator|.
name|equals
argument_list|(
name|modified
operator|.
name|getBase
argument_list|()
argument_list|)
condition|)
block|{
name|modified
operator|.
name|diffAgainstBase
argument_list|(
name|diff
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|super
operator|.
name|compare
argument_list|(
name|before
argument_list|,
name|after
argument_list|,
name|diff
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|super
operator|.
name|compare
argument_list|(
name|before
argument_list|,
name|after
argument_list|,
name|diff
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

