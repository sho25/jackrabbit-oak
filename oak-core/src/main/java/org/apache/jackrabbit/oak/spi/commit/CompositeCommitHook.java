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
name|spi
operator|.
name|commit
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
name|NodeStore
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

begin_comment
comment|/**  * This {@code CommitHook} aggregates a list of commit hooks into  * a single commit hook.  */
end_comment

begin_class
specifier|public
class|class
name|CompositeCommitHook
implements|implements
name|CommitHook
block|{
specifier|private
specifier|final
name|List
argument_list|<
name|CommitHook
argument_list|>
name|hooks
decl_stmt|;
specifier|public
name|CompositeCommitHook
parameter_list|(
name|List
argument_list|<
name|CommitHook
argument_list|>
name|hooks
parameter_list|)
block|{
name|this
operator|.
name|hooks
operator|=
name|hooks
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|NodeState
name|beforeCommit
parameter_list|(
name|NodeStore
name|store
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
name|NodeState
name|oldState
init|=
name|before
decl_stmt|;
name|NodeState
name|newState
init|=
name|after
decl_stmt|;
for|for
control|(
name|CommitHook
name|hook
range|:
name|hooks
control|)
block|{
name|NodeState
name|newOldState
init|=
name|newState
decl_stmt|;
name|newState
operator|=
name|hook
operator|.
name|beforeCommit
argument_list|(
name|store
argument_list|,
name|oldState
argument_list|,
name|newState
argument_list|)
expr_stmt|;
name|oldState
operator|=
name|newOldState
expr_stmt|;
block|}
return|return
name|newState
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|afterCommit
parameter_list|(
name|NodeStore
name|store
parameter_list|,
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
for|for
control|(
name|CommitHook
name|hook
range|:
name|hooks
control|)
block|{
name|hook
operator|.
name|afterCommit
argument_list|(
name|store
argument_list|,
name|before
argument_list|,
name|after
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

