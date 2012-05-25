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
name|jcr
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
name|CoreValue
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
name|api
operator|.
name|Tree
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
name|Tree
operator|.
name|Status
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
name|util
operator|.
name|Function1
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
name|util
operator|.
name|Iterators
import|;
end_import

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
name|javax
operator|.
name|jcr
operator|.
name|InvalidItemStateException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
comment|/**  * {@code NodeDelegate} serve as internal representations of {@code Node}s.  * Most methods of this class throw an {@code InvalidItemStateException}  * exception if the instance is stale. An instance is stale if the underlying  * items does not exist anymore.  */
end_comment

begin_class
specifier|public
class|class
name|NodeDelegate
extends|extends
name|ItemDelegate
block|{
comment|/**      * The underlying {@link Tree} instance. In order to ensure the instance      * is up to date, this field<em>should not be accessed directly</em> but      * rather the {@link #getTree()} method should be used.      */
specifier|private
name|Tree
name|tree
decl_stmt|;
name|NodeDelegate
parameter_list|(
name|SessionDelegate
name|sessionDelegate
parameter_list|,
name|Tree
name|tree
parameter_list|)
block|{
name|super
argument_list|(
name|sessionDelegate
argument_list|)
expr_stmt|;
name|this
operator|.
name|tree
operator|=
name|tree
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
throws|throws
name|InvalidItemStateException
block|{
return|return
name|getTree
argument_list|()
operator|.
name|getName
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getPath
parameter_list|()
throws|throws
name|InvalidItemStateException
block|{
return|return
literal|'/'
operator|+
name|getTree
argument_list|()
operator|.
name|getPath
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeDelegate
name|getParent
parameter_list|()
throws|throws
name|InvalidItemStateException
block|{
name|Tree
name|parent
init|=
name|getParentTree
argument_list|()
decl_stmt|;
return|return
name|parent
operator|==
literal|null
condition|?
literal|null
else|:
operator|new
name|NodeDelegate
argument_list|(
name|sessionDelegate
argument_list|,
name|parent
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isStale
parameter_list|()
block|{
name|resolve
argument_list|()
expr_stmt|;
return|return
name|tree
operator|==
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Status
name|getStatus
parameter_list|()
throws|throws
name|InvalidItemStateException
block|{
name|Tree
name|parent
init|=
name|getParentTree
argument_list|()
decl_stmt|;
if|if
condition|(
name|parent
operator|==
literal|null
condition|)
block|{
return|return
name|Status
operator|.
name|EXISTING
return|;
comment|// FIXME: return correct status for root
block|}
else|else
block|{
name|Status
name|childStatus
init|=
name|parent
operator|.
name|getChildStatus
argument_list|(
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|childStatus
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|InvalidItemStateException
argument_list|(
literal|"Node is stale"
argument_list|)
throw|;
block|}
return|return
name|childStatus
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
comment|// don't disturb the state: avoid calling getTree()
return|return
literal|"NodeDelegate[/"
operator|+
name|tree
operator|.
name|getPath
argument_list|()
operator|+
literal|']'
return|;
block|}
specifier|public
name|String
name|getIdentifier
parameter_list|()
throws|throws
name|InvalidItemStateException
block|{
name|PropertyDelegate
name|pd
init|=
name|getProperty
argument_list|(
literal|"jcr:uuid"
argument_list|)
decl_stmt|;
if|if
condition|(
name|pd
operator|==
literal|null
condition|)
block|{
comment|// TODO should find the closest referenceable parent, and build an identifier based on that and the relative path
return|return
name|getPath
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|pd
operator|.
name|getValue
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
comment|/**      * Determine whether this is the root node      * @return  {@code true} iff this is the root node      */
specifier|public
name|boolean
name|isRoot
parameter_list|()
throws|throws
name|InvalidItemStateException
block|{
return|return
name|getParentTree
argument_list|()
operator|==
literal|null
return|;
block|}
comment|/**      * Get the number of properties of the node      * @return  number of properties of the node      */
specifier|public
name|long
name|getPropertyCount
parameter_list|()
throws|throws
name|InvalidItemStateException
block|{
return|return
name|getTree
argument_list|()
operator|.
name|getPropertyCount
argument_list|()
return|;
block|}
comment|/**      * Get a property      * @param relPath  oak path      * @return  property at the path given by {@code relPath} or {@code null} if      * no such property exists      */
annotation|@
name|CheckForNull
specifier|public
name|PropertyDelegate
name|getProperty
parameter_list|(
name|String
name|relPath
parameter_list|)
throws|throws
name|InvalidItemStateException
block|{
name|Tree
name|parent
init|=
name|getTree
argument_list|(
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|relPath
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|parent
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|String
name|name
init|=
name|PathUtils
operator|.
name|getName
argument_list|(
name|relPath
argument_list|)
decl_stmt|;
name|PropertyState
name|propertyState
init|=
name|parent
operator|.
name|getProperty
argument_list|(
name|name
argument_list|)
decl_stmt|;
return|return
name|propertyState
operator|==
literal|null
condition|?
literal|null
else|:
operator|new
name|PropertyDelegate
argument_list|(
name|sessionDelegate
argument_list|,
name|parent
argument_list|,
name|propertyState
argument_list|)
return|;
block|}
comment|/**      * Get the properties of the node      * @return  properties of the node      */
specifier|public
name|Iterator
argument_list|<
name|PropertyDelegate
argument_list|>
name|getProperties
parameter_list|()
throws|throws
name|InvalidItemStateException
block|{
return|return
name|propertyDelegateIterator
argument_list|(
name|getTree
argument_list|()
operator|.
name|getProperties
argument_list|()
operator|.
name|iterator
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Get the number of child nodes      * @return  number of child nodes of the node      */
specifier|public
name|long
name|getChildCount
parameter_list|()
throws|throws
name|InvalidItemStateException
block|{
return|return
name|getTree
argument_list|()
operator|.
name|getChildrenCount
argument_list|()
return|;
block|}
comment|/**      * Get child node      * @param relPath  oak path      * @return  node at the path given by {@code relPath} or {@code null} if      * no such node exists      */
annotation|@
name|CheckForNull
specifier|public
name|NodeDelegate
name|getChild
parameter_list|(
name|String
name|relPath
parameter_list|)
throws|throws
name|InvalidItemStateException
block|{
name|Tree
name|tree
init|=
name|getTree
argument_list|(
name|relPath
argument_list|)
decl_stmt|;
return|return
name|tree
operator|==
literal|null
condition|?
literal|null
else|:
operator|new
name|NodeDelegate
argument_list|(
name|sessionDelegate
argument_list|,
name|tree
argument_list|)
return|;
block|}
comment|/**      * Get child nodes      * @return  child nodes of the node      */
specifier|public
name|Iterator
argument_list|<
name|NodeDelegate
argument_list|>
name|getChildren
parameter_list|()
throws|throws
name|InvalidItemStateException
block|{
return|return
name|nodeDelegateIterator
argument_list|(
name|getTree
argument_list|()
operator|.
name|getChildren
argument_list|()
operator|.
name|iterator
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Set a property      * @param name  oak name      * @param value      * @return  the set property      */
annotation|@
name|Nonnull
specifier|public
name|PropertyDelegate
name|setProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|CoreValue
name|value
parameter_list|)
throws|throws
name|InvalidItemStateException
block|{
name|PropertyState
name|propertyState
init|=
name|getTree
argument_list|()
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
decl_stmt|;
return|return
operator|new
name|PropertyDelegate
argument_list|(
name|sessionDelegate
argument_list|,
name|getTree
argument_list|()
argument_list|,
name|propertyState
argument_list|)
return|;
block|}
comment|/**      * Set a multi valued property      * @param name  oak name      * @param value      * @return  the set property      */
annotation|@
name|Nonnull
specifier|public
name|PropertyDelegate
name|setProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|List
argument_list|<
name|CoreValue
argument_list|>
name|value
parameter_list|)
throws|throws
name|InvalidItemStateException
block|{
name|PropertyState
name|propertyState
init|=
name|getTree
argument_list|()
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
decl_stmt|;
return|return
operator|new
name|PropertyDelegate
argument_list|(
name|sessionDelegate
argument_list|,
name|getTree
argument_list|()
argument_list|,
name|propertyState
argument_list|)
return|;
block|}
comment|/**      * Add a child node      * @param name  oak name      * @return  the added node or {@code null} if such a node already exists      */
annotation|@
name|CheckForNull
specifier|public
name|NodeDelegate
name|addChild
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|InvalidItemStateException
block|{
name|Tree
name|tree
init|=
name|getTree
argument_list|()
decl_stmt|;
return|return
name|tree
operator|.
name|hasChild
argument_list|(
name|name
argument_list|)
condition|?
literal|null
else|:
operator|new
name|NodeDelegate
argument_list|(
name|sessionDelegate
argument_list|,
name|tree
operator|.
name|addChild
argument_list|(
name|name
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Remove the node if not root. Does nothing otherwise      */
specifier|public
name|void
name|remove
parameter_list|()
throws|throws
name|InvalidItemStateException
block|{
name|Tree
name|parentTree
init|=
name|getParentTree
argument_list|()
decl_stmt|;
if|if
condition|(
name|parentTree
operator|!=
literal|null
condition|)
block|{
name|parentTree
operator|.
name|removeChild
argument_list|(
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// -----------------------------------------------------------< private>---
specifier|private
name|Tree
name|getTree
parameter_list|(
name|String
name|relPath
parameter_list|)
throws|throws
name|InvalidItemStateException
block|{
name|Tree
name|tree
init|=
name|getTree
argument_list|()
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
name|relPath
argument_list|)
control|)
block|{
if|if
condition|(
name|tree
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|tree
operator|=
name|tree
operator|.
name|getChild
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
return|return
name|tree
return|;
block|}
annotation|@
name|CheckForNull
specifier|private
name|Tree
name|getParentTree
parameter_list|()
throws|throws
name|InvalidItemStateException
block|{
return|return
name|getTree
argument_list|()
operator|.
name|getParent
argument_list|()
return|;
block|}
specifier|private
specifier|synchronized
name|Tree
name|getTree
parameter_list|()
throws|throws
name|InvalidItemStateException
block|{
name|resolve
argument_list|()
expr_stmt|;
if|if
condition|(
name|tree
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|InvalidItemStateException
argument_list|(
literal|"Node is stale"
argument_list|)
throw|;
block|}
return|return
name|tree
return|;
block|}
specifier|private
specifier|synchronized
name|void
name|resolve
parameter_list|()
block|{
if|if
condition|(
name|tree
operator|!=
literal|null
condition|)
block|{
name|tree
operator|=
name|sessionDelegate
operator|.
name|getTree
argument_list|(
name|tree
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|Iterator
argument_list|<
name|NodeDelegate
argument_list|>
name|nodeDelegateIterator
parameter_list|(
name|Iterator
argument_list|<
name|Tree
argument_list|>
name|childNodeStates
parameter_list|)
block|{
return|return
name|Iterators
operator|.
name|map
argument_list|(
name|childNodeStates
argument_list|,
operator|new
name|Function1
argument_list|<
name|Tree
argument_list|,
name|NodeDelegate
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|NodeDelegate
name|apply
parameter_list|(
name|Tree
name|state
parameter_list|)
block|{
return|return
operator|new
name|NodeDelegate
argument_list|(
name|sessionDelegate
argument_list|,
name|state
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
specifier|private
name|Iterator
argument_list|<
name|PropertyDelegate
argument_list|>
name|propertyDelegateIterator
parameter_list|(
name|Iterator
argument_list|<
name|?
extends|extends
name|PropertyState
argument_list|>
name|properties
parameter_list|)
block|{
return|return
name|Iterators
operator|.
name|map
argument_list|(
name|properties
argument_list|,
operator|new
name|Function1
argument_list|<
name|PropertyState
argument_list|,
name|PropertyDelegate
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|PropertyDelegate
name|apply
parameter_list|(
name|PropertyState
name|propertyState
parameter_list|)
block|{
return|return
operator|new
name|PropertyDelegate
argument_list|(
name|sessionDelegate
argument_list|,
name|tree
argument_list|,
name|propertyState
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
block|}
end_class

end_unit

