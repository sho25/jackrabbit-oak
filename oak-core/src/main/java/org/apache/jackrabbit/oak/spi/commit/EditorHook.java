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
name|javax
operator|.
name|annotation
operator|.
name|CheckForNull
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
name|NodeStateDiff
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
name|base
operator|.
name|Preconditions
operator|.
name|checkNotNull
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

begin_comment
comment|/**  * This commit hook implementation processes changes to be committed  * using the {@link Editor} instance provided by the {@link EditorProvider}  * passed to the constructor.  *  * @since Oak 0.7  */
end_comment

begin_class
specifier|public
class|class
name|EditorHook
implements|implements
name|CommitHook
block|{
specifier|private
specifier|final
name|EditorProvider
name|provider
decl_stmt|;
specifier|public
name|EditorHook
parameter_list|(
annotation|@
name|Nonnull
name|EditorProvider
name|provider
parameter_list|)
block|{
name|this
operator|.
name|provider
operator|=
name|checkNotNull
argument_list|(
name|provider
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|NodeState
name|processCommit
parameter_list|(
annotation|@
name|Nonnull
name|NodeState
name|before
parameter_list|,
annotation|@
name|Nonnull
name|NodeState
name|after
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|checkNotNull
argument_list|(
name|before
argument_list|)
expr_stmt|;
name|checkNotNull
argument_list|(
name|after
argument_list|)
expr_stmt|;
name|NodeBuilder
name|builder
init|=
name|after
operator|.
name|builder
argument_list|()
decl_stmt|;
name|Editor
name|editor
init|=
name|provider
operator|.
name|getRootEditor
argument_list|(
name|before
argument_list|,
name|after
argument_list|,
name|builder
argument_list|)
decl_stmt|;
name|CommitFailedException
name|exception
init|=
name|process
argument_list|(
name|editor
argument_list|,
name|before
argument_list|,
name|after
argument_list|)
decl_stmt|;
if|if
condition|(
name|exception
operator|==
literal|null
condition|)
block|{
return|return
name|builder
operator|.
name|getNodeState
argument_list|()
return|;
block|}
else|else
block|{
throw|throw
name|exception
throw|;
block|}
block|}
comment|//------------------------------------------------------------< private>---
comment|/**      * Validates and possibly edits the given subtree by diffing and recursing through it.      *      * @param editor editor for the root of the subtree      * @param before state of the original subtree      * @param after state of the modified subtree      * @return exception if the processing failed, {@code null} otherwise      */
annotation|@
name|CheckForNull
specifier|private
specifier|static
name|CommitFailedException
name|process
parameter_list|(
annotation|@
name|CheckForNull
name|Editor
name|editor
parameter_list|,
annotation|@
name|Nonnull
name|NodeState
name|before
parameter_list|,
annotation|@
name|Nonnull
name|NodeState
name|after
parameter_list|)
block|{
name|checkNotNull
argument_list|(
name|before
argument_list|)
expr_stmt|;
name|checkNotNull
argument_list|(
name|after
argument_list|)
expr_stmt|;
if|if
condition|(
name|editor
operator|!=
literal|null
condition|)
block|{
name|EditorDiff
name|diff
init|=
operator|new
name|EditorDiff
argument_list|(
name|editor
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
return|return
name|diff
operator|.
name|exception
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
specifier|private
specifier|static
class|class
name|EditorDiff
implements|implements
name|NodeStateDiff
block|{
specifier|private
specifier|final
name|Editor
name|editor
decl_stmt|;
comment|/**          * Checked exceptions don't compose. So we need to hack around.          * See http://markmail.org/message/ak67n5k7mr3vqylm and          * http://markmail.org/message/bhocbruikljpuhu6          */
specifier|private
name|CommitFailedException
name|exception
decl_stmt|;
specifier|private
name|EditorDiff
parameter_list|(
name|Editor
name|editor
parameter_list|)
block|{
name|this
operator|.
name|editor
operator|=
name|editor
expr_stmt|;
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
name|editor
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
name|editor
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
name|editor
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
name|Editor
name|e
init|=
name|editor
operator|.
name|childNodeAdded
argument_list|(
name|name
argument_list|,
name|after
argument_list|)
decl_stmt|;
name|exception
operator|=
name|process
argument_list|(
name|e
argument_list|,
name|EMPTY_NODE
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
name|Editor
name|e
init|=
name|editor
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
name|exception
operator|=
name|process
argument_list|(
name|e
argument_list|,
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
name|Editor
name|e
init|=
name|editor
operator|.
name|childNodeDeleted
argument_list|(
name|name
argument_list|,
name|before
argument_list|)
decl_stmt|;
name|exception
operator|=
name|process
argument_list|(
name|e
argument_list|,
name|before
argument_list|,
name|EMPTY_NODE
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
block|}
block|}
end_class

end_unit

