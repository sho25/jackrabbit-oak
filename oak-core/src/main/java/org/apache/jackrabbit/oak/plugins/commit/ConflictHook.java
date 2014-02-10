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
name|commit
package|;
end_package

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
name|commit
operator|.
name|CommitHook
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
name|ConflictHandler
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

begin_comment
comment|/**  * This commit hook implementation is responsible for resolving  * conflicts. It does so by detecting the presence of conflict  * markers added by the Microkernel and delegating to a  * {@link org.apache.jackrabbit.oak.spi.commit.ConflictHandler}  * for resolving the conflicts.  *  * @see org.apache.jackrabbit.mk.api.MicroKernel#rebase(String, String)  */
end_comment

begin_class
specifier|public
class|class
name|ConflictHook
implements|implements
name|CommitHook
block|{
specifier|private
specifier|final
name|ConflictHandler
name|conflictHandler
decl_stmt|;
comment|/**      * Create a new instance of the conflict hook using the      * passed conflict handler for resolving conflicts.      * @param conflictHandler  a conflict handler      */
specifier|public
name|ConflictHook
parameter_list|(
name|ConflictHandler
name|conflictHandler
parameter_list|)
block|{
name|this
operator|.
name|conflictHandler
operator|=
name|conflictHandler
expr_stmt|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|NodeState
name|processCommit
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
throws|throws
name|CommitFailedException
block|{
return|return
name|MergingNodeStateDiff
operator|.
name|merge
argument_list|(
name|before
argument_list|,
name|after
argument_list|,
name|conflictHandler
argument_list|)
return|;
block|}
block|}
end_class

end_unit

