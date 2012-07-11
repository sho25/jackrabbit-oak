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
name|MemoryNodeState
operator|.
name|EMPTY_NODE
import|;
end_import

begin_comment
comment|/**  * This commit editor implementation validates the changes to be committed  * against all {@link Validator}s provided by the {@link ValidatorProvider}  * passed to the class' constructor.  */
end_comment

begin_class
specifier|public
class|class
name|ValidatingEditor
implements|implements
name|CommitEditor
block|{
specifier|private
specifier|final
name|ValidatorProvider
name|validatorProvider
decl_stmt|;
comment|/**      * Create a new commit hook which validates the commit against all      * {@link Validator}s provided by {@code validatorProvider}.      * @param validatorProvider  validator provider      */
specifier|public
name|ValidatingEditor
parameter_list|(
name|ValidatorProvider
name|validatorProvider
parameter_list|)
block|{
name|this
operator|.
name|validatorProvider
operator|=
name|validatorProvider
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|NodeState
name|editCommit
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
name|Validator
name|validator
init|=
name|validatorProvider
operator|.
name|getRootValidator
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
decl_stmt|;
name|ValidatorDiff
operator|.
name|validate
argument_list|(
name|validator
argument_list|,
name|before
argument_list|,
name|after
argument_list|)
expr_stmt|;
return|return
name|after
return|;
block|}
comment|//------------------------------------------------------------< private>---
specifier|private
specifier|static
class|class
name|ValidatorDiff
implements|implements
name|NodeStateDiff
block|{
specifier|private
specifier|final
name|Validator
name|validator
decl_stmt|;
comment|/**          * Checked exceptions don't compose. So we need to hack around.          * See http://markmail.org/message/ak67n5k7mr3vqylm and          * http://markmail.org/message/bhocbruikljpuhu6          */
specifier|private
name|CommitFailedException
name|exception
decl_stmt|;
comment|/**          * Validates the given subtree by diffing and recursing through it.          *          * @param validator validator for the root of the subtree          * @param before state of the original subtree          * @param after state of the modified subtree          * @throws CommitFailedException if validation failed          */
specifier|public
specifier|static
name|void
name|validate
parameter_list|(
name|Validator
name|validator
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
operator|new
name|ValidatorDiff
argument_list|(
name|validator
argument_list|)
operator|.
name|validate
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
expr_stmt|;
block|}
specifier|private
name|ValidatorDiff
parameter_list|(
name|Validator
name|validator
parameter_list|)
block|{
name|this
operator|.
name|validator
operator|=
name|validator
expr_stmt|;
block|}
specifier|private
name|void
name|validate
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
name|after
operator|.
name|compareAgainstBaseState
argument_list|(
name|before
argument_list|,
name|this
argument_list|)
expr_stmt|;
if|if
condition|(
name|exception
operator|!=
literal|null
condition|)
block|{
throw|throw
name|exception
throw|;
block|}
block|}
comment|//-------------------------------------------------< NodeStateDiff>--
annotation|@
name|Override
specifier|public
name|void
name|propertyAdded
parameter_list|(
name|PropertyState
name|after
parameter_list|)
block|{
if|if
condition|(
name|exception
operator|==
literal|null
condition|)
block|{
try|try
block|{
name|validator
operator|.
name|propertyAdded
argument_list|(
name|after
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
name|exception
operator|=
name|e
expr_stmt|;
block|}
block|}
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
block|{
if|if
condition|(
name|exception
operator|==
literal|null
condition|)
block|{
try|try
block|{
name|validator
operator|.
name|propertyChanged
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
name|exception
operator|=
name|e
expr_stmt|;
block|}
block|}
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
block|{
if|if
condition|(
name|exception
operator|==
literal|null
condition|)
block|{
try|try
block|{
name|validator
operator|.
name|propertyDeleted
argument_list|(
name|before
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
name|exception
operator|=
name|e
expr_stmt|;
block|}
block|}
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
if|if
condition|(
name|exception
operator|==
literal|null
condition|)
block|{
try|try
block|{
name|Validator
name|v
init|=
name|validator
operator|.
name|childNodeAdded
argument_list|(
name|name
argument_list|,
name|after
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|!=
literal|null
condition|)
block|{
name|validate
argument_list|(
name|v
argument_list|,
name|EMPTY_NODE
argument_list|,
name|after
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
name|exception
operator|=
name|e
expr_stmt|;
block|}
block|}
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
name|exception
operator|==
literal|null
condition|)
block|{
try|try
block|{
name|Validator
name|v
init|=
name|validator
operator|.
name|childNodeChanged
argument_list|(
name|name
argument_list|,
name|before
argument_list|,
name|after
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|!=
literal|null
condition|)
block|{
name|validate
argument_list|(
name|v
argument_list|,
name|before
argument_list|,
name|after
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
name|exception
operator|=
name|e
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|childNodeDeleted
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|)
block|{
if|if
condition|(
name|exception
operator|==
literal|null
condition|)
block|{
try|try
block|{
name|Validator
name|v
init|=
name|validator
operator|.
name|childNodeDeleted
argument_list|(
name|name
argument_list|,
name|before
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|!=
literal|null
condition|)
block|{
name|validate
argument_list|(
name|v
argument_list|,
name|before
argument_list|,
name|EMPTY_NODE
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
name|exception
operator|=
name|e
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

