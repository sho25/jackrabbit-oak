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
name|progress
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
name|plugins
operator|.
name|index
operator|.
name|NodeTraversalCallback
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
name|Editor
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
name|jetbrains
operator|.
name|annotations
operator|.
name|Nullable
import|;
end_import

begin_comment
comment|/**  * Editor to track traversal and notify the callback for each node traversed.  * The editor also ensures that path is lazily constructed  */
end_comment

begin_class
class|class
name|ProgressTrackingEditor
implements|implements
name|Editor
implements|,
name|NodeTraversalCallback
operator|.
name|PathSource
block|{
specifier|private
specifier|final
name|Editor
name|editor
decl_stmt|;
specifier|private
specifier|final
name|NodeTraversalCallback
name|traversalCallback
decl_stmt|;
specifier|private
specifier|final
name|ProgressTrackingEditor
name|parent
decl_stmt|;
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
specifier|public
name|ProgressTrackingEditor
parameter_list|(
name|Editor
name|editor
parameter_list|,
name|String
name|name
parameter_list|,
name|NodeTraversalCallback
name|traversalCallback
parameter_list|)
block|{
name|this
operator|.
name|editor
operator|=
name|editor
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|traversalCallback
operator|=
name|traversalCallback
expr_stmt|;
name|this
operator|.
name|parent
operator|=
literal|null
expr_stmt|;
block|}
specifier|private
name|ProgressTrackingEditor
parameter_list|(
name|Editor
name|editor
parameter_list|,
name|String
name|name
parameter_list|,
name|NodeTraversalCallback
name|callback
parameter_list|,
name|ProgressTrackingEditor
name|parent
parameter_list|)
block|{
name|this
operator|.
name|editor
operator|=
name|editor
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|traversalCallback
operator|=
name|callback
expr_stmt|;
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
block|}
annotation|@
name|Nullable
specifier|public
specifier|static
name|Editor
name|wrap
parameter_list|(
annotation|@
name|Nullable
name|Editor
name|editor
parameter_list|,
name|NodeTraversalCallback
name|onProgress
parameter_list|)
block|{
if|if
condition|(
name|editor
operator|!=
literal|null
operator|&&
operator|!
operator|(
name|editor
operator|instanceof
name|ProgressTrackingEditor
operator|)
condition|)
block|{
return|return
operator|new
name|ProgressTrackingEditor
argument_list|(
name|editor
argument_list|,
literal|"/"
argument_list|,
name|onProgress
argument_list|)
return|;
block|}
return|return
name|editor
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|enter
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
name|traversalCallback
operator|.
name|traversedNode
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|editor
operator|.
name|enter
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|leave
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
name|editor
operator|.
name|leave
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|propertyAdded
parameter_list|(
name|PropertyState
name|after
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|editor
operator|.
name|propertyAdded
argument_list|(
name|after
argument_list|)
expr_stmt|;
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
throws|throws
name|CommitFailedException
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
annotation|@
name|Override
specifier|public
name|void
name|propertyDeleted
parameter_list|(
name|PropertyState
name|before
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|editor
operator|.
name|propertyDeleted
argument_list|(
name|before
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Editor
name|childNodeAdded
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|after
parameter_list|)
throws|throws
name|CommitFailedException
block|{
return|return
name|createChildEditor
argument_list|(
name|editor
operator|.
name|childNodeAdded
argument_list|(
name|name
argument_list|,
name|after
argument_list|)
argument_list|,
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Editor
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
throws|throws
name|CommitFailedException
block|{
return|return
name|createChildEditor
argument_list|(
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
argument_list|,
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Editor
name|childNodeDeleted
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|)
throws|throws
name|CommitFailedException
block|{
return|return
name|createChildEditor
argument_list|(
name|editor
operator|.
name|childNodeDeleted
argument_list|(
name|name
argument_list|,
name|before
argument_list|)
argument_list|,
name|name
argument_list|)
return|;
block|}
specifier|public
name|String
name|getPath
parameter_list|()
block|{
if|if
condition|(
name|parent
operator|==
literal|null
condition|)
block|{
return|return
name|PathUtils
operator|.
name|ROOT_PATH
return|;
block|}
else|else
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|128
argument_list|)
decl_stmt|;
name|buildPath
argument_list|(
name|sb
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
specifier|private
name|void
name|buildPath
parameter_list|(
annotation|@
name|NotNull
name|StringBuilder
name|sb
parameter_list|)
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
name|buildPath
argument_list|(
name|sb
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|'/'
argument_list|)
operator|.
name|append
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|ProgressTrackingEditor
name|createChildEditor
parameter_list|(
name|Editor
name|editor
parameter_list|,
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|editor
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
operator|new
name|ProgressTrackingEditor
argument_list|(
name|editor
argument_list|,
name|name
argument_list|,
name|traversalCallback
argument_list|,
name|this
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

