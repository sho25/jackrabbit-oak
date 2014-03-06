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
name|observation
package|;
end_package

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
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nullable
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
name|Sets
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
name|core
operator|.
name|ImmutableRoot
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
name|namepath
operator|.
name|GlobalNameMapper
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
name|namepath
operator|.
name|NamePathMapper
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
name|namepath
operator|.
name|NamePathMapperImpl
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
name|observation
operator|.
name|filter
operator|.
name|VisibleFilter
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
name|Observer
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
comment|/**  * Base class for {@code Observer} instances that group changes  * by node instead of tracking them down to individual properties.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|NodeObserver
implements|implements
name|Observer
block|{
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
specifier|private
name|NodeState
name|previousRoot
decl_stmt|;
comment|/**      * Create a new instance for observing the given path.      * @param path      */
specifier|protected
name|NodeObserver
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
block|}
comment|/**      * A node at {@code path} has been added.      * @param path       Path of the added node.      * @param added      Names of the added properties.      * @param deleted    Names of the deleted properties.      * @param changed    Names of the changed properties.      * @param commitInfo commit info associated with this change.      */
specifier|protected
specifier|abstract
name|void
name|added
parameter_list|(
annotation|@
name|Nonnull
name|String
name|path
parameter_list|,
annotation|@
name|Nonnull
name|Set
argument_list|<
name|String
argument_list|>
name|added
parameter_list|,
annotation|@
name|Nonnull
name|Set
argument_list|<
name|String
argument_list|>
name|deleted
parameter_list|,
annotation|@
name|Nonnull
name|Set
argument_list|<
name|String
argument_list|>
name|changed
parameter_list|,
annotation|@
name|Nonnull
name|CommitInfo
name|commitInfo
parameter_list|)
function_decl|;
comment|/**      * A node at {@code path} has been deleted.      * @param path       Path of the deleted node.      * @param added      Names of the added properties.      * @param deleted    Names of the deleted properties.      * @param changed    Names of the changed properties.      * @param commitInfo commit info associated with this change.      */
specifier|protected
specifier|abstract
name|void
name|deleted
parameter_list|(
annotation|@
name|Nonnull
name|String
name|path
parameter_list|,
annotation|@
name|Nonnull
name|Set
argument_list|<
name|String
argument_list|>
name|added
parameter_list|,
annotation|@
name|Nonnull
name|Set
argument_list|<
name|String
argument_list|>
name|deleted
parameter_list|,
annotation|@
name|Nonnull
name|Set
argument_list|<
name|String
argument_list|>
name|changed
parameter_list|,
annotation|@
name|Nonnull
name|CommitInfo
name|commitInfo
parameter_list|)
function_decl|;
comment|/**      * A node at {@code path} has been changed.      * @param path       Path of the changed node.      * @param added      Names of the added properties.      * @param deleted    Names of the deleted properties.      * @param changed    Names of the changed properties.      * @param commitInfo commit info associated with this change.      */
specifier|protected
specifier|abstract
name|void
name|changed
parameter_list|(
annotation|@
name|Nonnull
name|String
name|path
parameter_list|,
annotation|@
name|Nonnull
name|Set
argument_list|<
name|String
argument_list|>
name|added
parameter_list|,
annotation|@
name|Nonnull
name|Set
argument_list|<
name|String
argument_list|>
name|deleted
parameter_list|,
annotation|@
name|Nonnull
name|Set
argument_list|<
name|String
argument_list|>
name|changed
parameter_list|,
annotation|@
name|Nonnull
name|CommitInfo
name|commitInfo
parameter_list|)
function_decl|;
annotation|@
name|Override
specifier|public
name|void
name|contentChanged
parameter_list|(
annotation|@
name|Nonnull
name|NodeState
name|root
parameter_list|,
annotation|@
name|Nullable
name|CommitInfo
name|info
parameter_list|)
block|{
if|if
condition|(
name|previousRoot
operator|!=
literal|null
condition|)
block|{
name|NamePathMapper
name|namePathMapper
init|=
operator|new
name|NamePathMapperImpl
argument_list|(
operator|new
name|GlobalNameMapper
argument_list|(
operator|new
name|ImmutableRoot
argument_list|(
name|root
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|NodeState
name|before
init|=
name|previousRoot
decl_stmt|;
name|NodeState
name|after
init|=
name|root
decl_stmt|;
name|EventHandler
name|handler
init|=
operator|new
name|FilteredHandler
argument_list|(
operator|new
name|VisibleFilter
argument_list|()
argument_list|,
operator|new
name|NodeEventHandler
argument_list|(
literal|"/"
argument_list|,
name|info
argument_list|,
name|namePathMapper
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|path
argument_list|)
control|)
block|{
name|before
operator|=
name|before
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|after
operator|=
name|after
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|handler
operator|=
name|handler
operator|.
name|getChildHandler
argument_list|(
name|name
argument_list|,
name|before
argument_list|,
name|after
argument_list|)
expr_stmt|;
block|}
name|EventGenerator
name|generator
init|=
operator|new
name|EventGenerator
argument_list|(
name|previousRoot
argument_list|,
name|root
argument_list|,
name|handler
argument_list|)
decl_stmt|;
while|while
condition|(
operator|!
name|generator
operator|.
name|isDone
argument_list|()
condition|)
block|{
name|generator
operator|.
name|generate
argument_list|()
expr_stmt|;
block|}
block|}
name|previousRoot
operator|=
name|root
expr_stmt|;
block|}
specifier|private
enum|enum
name|EventType
block|{
name|ADDED
block|,
name|DELETED
block|,
name|CHANGED
block|}
specifier|private
class|class
name|NodeEventHandler
extends|extends
name|DefaultEventHandler
block|{
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
specifier|private
specifier|final
name|CommitInfo
name|commitInfo
decl_stmt|;
specifier|private
specifier|final
name|NamePathMapper
name|namePathMapper
decl_stmt|;
specifier|private
specifier|final
name|EventType
name|eventType
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|added
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|deleted
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|changed
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
specifier|public
name|NodeEventHandler
parameter_list|(
name|String
name|path
parameter_list|,
name|CommitInfo
name|commitInfo
parameter_list|,
name|NamePathMapper
name|namePathMapper
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|this
operator|.
name|commitInfo
operator|=
name|commitInfo
operator|==
literal|null
condition|?
name|CommitInfo
operator|.
name|EMPTY
else|:
name|commitInfo
expr_stmt|;
name|this
operator|.
name|namePathMapper
operator|=
name|namePathMapper
expr_stmt|;
name|this
operator|.
name|eventType
operator|=
name|EventType
operator|.
name|CHANGED
expr_stmt|;
block|}
specifier|private
name|NodeEventHandler
parameter_list|(
name|NodeEventHandler
name|parent
parameter_list|,
name|String
name|name
parameter_list|,
name|EventType
name|eventType
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
literal|"/"
operator|.
name|equals
argument_list|(
name|parent
operator|.
name|path
argument_list|)
condition|?
literal|'/'
operator|+
name|name
else|:
name|parent
operator|.
name|path
operator|+
literal|'/'
operator|+
name|name
expr_stmt|;
name|this
operator|.
name|commitInfo
operator|=
name|parent
operator|.
name|commitInfo
expr_stmt|;
name|this
operator|.
name|namePathMapper
operator|=
name|parent
operator|.
name|namePathMapper
expr_stmt|;
name|this
operator|.
name|eventType
operator|=
name|eventType
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
block|{
switch|switch
condition|(
name|eventType
condition|)
block|{
case|case
name|ADDED
case|:
name|added
argument_list|(
name|namePathMapper
operator|.
name|getJcrPath
argument_list|(
name|path
argument_list|)
argument_list|,
name|added
argument_list|,
name|deleted
argument_list|,
name|changed
argument_list|,
name|commitInfo
argument_list|)
expr_stmt|;
break|break;
case|case
name|DELETED
case|:
name|deleted
argument_list|(
name|namePathMapper
operator|.
name|getJcrPath
argument_list|(
name|path
argument_list|)
argument_list|,
name|added
argument_list|,
name|deleted
argument_list|,
name|changed
argument_list|,
name|commitInfo
argument_list|)
expr_stmt|;
break|break;
case|case
name|CHANGED
case|:
if|if
condition|(
operator|!
name|added
operator|.
name|isEmpty
argument_list|()
operator|||
operator|!
name|deleted
operator|.
name|isEmpty
argument_list|()
operator|||
operator|!
name|changed
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|changed
argument_list|(
name|namePathMapper
operator|.
name|getJcrPath
argument_list|(
name|path
argument_list|)
argument_list|,
name|added
argument_list|,
name|deleted
argument_list|,
name|changed
argument_list|,
name|commitInfo
argument_list|)
expr_stmt|;
block|}
break|break;
block|}
block|}
annotation|@
name|Override
specifier|public
name|EventHandler
name|getChildHandler
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
operator|!
name|before
operator|.
name|exists
argument_list|()
condition|)
block|{
return|return
operator|new
name|NodeEventHandler
argument_list|(
name|this
argument_list|,
name|name
argument_list|,
name|EventType
operator|.
name|ADDED
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|after
operator|.
name|exists
argument_list|()
condition|)
block|{
return|return
operator|new
name|NodeEventHandler
argument_list|(
name|this
argument_list|,
name|name
argument_list|,
name|EventType
operator|.
name|DELETED
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|NodeEventHandler
argument_list|(
name|this
argument_list|,
name|name
argument_list|,
name|EventType
operator|.
name|CHANGED
argument_list|)
return|;
block|}
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
block|{
name|added
operator|.
name|add
argument_list|(
name|namePathMapper
operator|.
name|getJcrName
argument_list|(
name|after
operator|.
name|getName
argument_list|()
argument_list|)
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
block|{
name|changed
operator|.
name|add
argument_list|(
name|namePathMapper
operator|.
name|getJcrName
argument_list|(
name|after
operator|.
name|getName
argument_list|()
argument_list|)
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
block|{
name|deleted
operator|.
name|add
argument_list|(
name|namePathMapper
operator|.
name|getJcrName
argument_list|(
name|before
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

