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
operator|.
name|delegate
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|javax
operator|.
name|jcr
operator|.
name|ItemNotFoundException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|RepositoryException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|ValueFormatException
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
name|base
operator|.
name|Function
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
name|base
operator|.
name|Predicate
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
name|Iterators
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
name|TreeLocation
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
comment|/**      * Create a new {@code NodeDelegate} instance for a valid {@code TreeLocation}. That      * is for one where {@code getTree() != null}.      *      * @param sessionDelegate      * @param location      * @return      */
specifier|static
name|NodeDelegate
name|create
parameter_list|(
name|SessionDelegate
name|sessionDelegate
parameter_list|,
name|TreeLocation
name|location
parameter_list|)
block|{
return|return
name|location
operator|.
name|getTree
argument_list|()
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
name|location
argument_list|)
return|;
block|}
specifier|protected
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
argument_list|,
name|tree
operator|.
name|getLocation
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|NodeDelegate
parameter_list|(
name|SessionDelegate
name|sessionDelegate
parameter_list|,
name|TreeLocation
name|location
parameter_list|)
block|{
name|super
argument_list|(
name|sessionDelegate
argument_list|,
name|location
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Nonnull
specifier|public
name|String
name|getIdentifier
parameter_list|()
throws|throws
name|InvalidItemStateException
block|{
return|return
name|sessionDelegate
operator|.
name|getIdManager
argument_list|()
operator|.
name|getIdentifier
argument_list|(
name|getTree
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Determine whether this is the root node      *      * @return {@code true} iff this is the root node      */
specifier|public
name|boolean
name|isRoot
parameter_list|()
throws|throws
name|InvalidItemStateException
block|{
return|return
name|getTree
argument_list|()
operator|.
name|isRoot
argument_list|()
return|;
block|}
comment|/**      * Get the number of properties of the node      *      * @return number of properties of the node      */
specifier|public
name|long
name|getPropertyCount
parameter_list|()
throws|throws
name|InvalidItemStateException
block|{
comment|// TODO: Exclude "invisible" internal properties (OAK-182)
return|return
name|getTree
argument_list|()
operator|.
name|getPropertyCount
argument_list|()
return|;
block|}
comment|/**      * Get a property      *      * @param relPath oak path      * @return property at the path given by {@code relPath} or {@code null} if      *         no such property exists      */
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
name|RepositoryException
block|{
name|TreeLocation
name|propertyLocation
init|=
name|getChildLocation
argument_list|(
name|relPath
argument_list|)
decl_stmt|;
name|PropertyState
name|propertyState
init|=
name|propertyLocation
operator|.
name|getProperty
argument_list|()
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
name|propertyLocation
argument_list|)
return|;
block|}
comment|/**      * Get the properties of the node      *      * @return properties of the node      */
annotation|@
name|Nonnull
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
comment|/**      * Get the number of child nodes      *      * @return number of child nodes of the node      */
specifier|public
name|long
name|getChildCount
parameter_list|()
throws|throws
name|InvalidItemStateException
block|{
comment|// TODO: Exclude "invisible" internal child nodes (OAK-182)
return|return
name|getTree
argument_list|()
operator|.
name|getChildrenCount
argument_list|()
return|;
block|}
comment|/**      * Get child node      *      * @param relPath oak path      * @return node at the path given by {@code relPath} or {@code null} if      *         no such node exists      */
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
name|RepositoryException
block|{
return|return
name|create
argument_list|(
name|sessionDelegate
argument_list|,
name|getChildLocation
argument_list|(
name|relPath
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Returns an iterator for traversing all the children of this node.      * If the node is orderable then the iterator will return child nodes in the      * specified order. Otherwise the ordering of the iterator is undefined.      *      * @return child nodes of the node      */
annotation|@
name|Nonnull
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
name|Tree
name|tree
init|=
name|getTree
argument_list|()
decl_stmt|;
name|long
name|count
init|=
name|tree
operator|.
name|getChildrenCount
argument_list|()
decl_stmt|;
if|if
condition|(
name|count
operator|==
literal|0
condition|)
block|{
comment|// Optimise the most common case
return|return
name|Collections
operator|.
expr|<
name|NodeDelegate
operator|>
name|emptySet
argument_list|()
operator|.
name|iterator
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
name|count
operator|==
literal|1
condition|)
block|{
comment|// Optimise another typical case
name|Tree
name|child
init|=
name|tree
operator|.
name|getChildren
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|child
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|":"
argument_list|)
condition|)
block|{
name|NodeDelegate
name|delegate
init|=
operator|new
name|NodeDelegate
argument_list|(
name|sessionDelegate
argument_list|,
name|child
argument_list|)
decl_stmt|;
return|return
name|Collections
operator|.
name|singleton
argument_list|(
name|delegate
argument_list|)
operator|.
name|iterator
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|Collections
operator|.
expr|<
name|NodeDelegate
operator|>
name|emptySet
argument_list|()
operator|.
name|iterator
argument_list|()
return|;
block|}
block|}
else|else
block|{
return|return
name|nodeDelegateIterator
argument_list|(
name|tree
operator|.
name|getChildren
argument_list|()
operator|.
name|iterator
argument_list|()
argument_list|)
return|;
block|}
block|}
specifier|public
name|void
name|orderBefore
parameter_list|(
name|String
name|source
parameter_list|,
name|String
name|target
parameter_list|)
throws|throws
name|ItemNotFoundException
throws|,
name|InvalidItemStateException
block|{
name|Tree
name|tree
init|=
name|getTree
argument_list|()
decl_stmt|;
if|if
condition|(
name|tree
operator|.
name|getChild
argument_list|(
name|source
argument_list|)
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ItemNotFoundException
argument_list|(
literal|"Not a child: "
operator|+
name|source
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|target
operator|!=
literal|null
operator|&&
name|tree
operator|.
name|getChild
argument_list|(
name|target
argument_list|)
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ItemNotFoundException
argument_list|(
literal|"Not a child: "
operator|+
name|target
argument_list|)
throw|;
block|}
else|else
block|{
name|tree
operator|.
name|getChild
argument_list|(
name|source
argument_list|)
operator|.
name|orderBefore
argument_list|(
name|target
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Set a property      *      * @param propertyState      * @return the set property      */
annotation|@
name|Nonnull
specifier|public
name|PropertyDelegate
name|setProperty
parameter_list|(
name|PropertyState
name|propertyState
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Tree
name|tree
init|=
name|getTree
argument_list|()
decl_stmt|;
name|String
name|name
init|=
name|propertyState
operator|.
name|getName
argument_list|()
decl_stmt|;
name|PropertyState
name|old
init|=
name|tree
operator|.
name|getProperty
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|old
operator|!=
literal|null
operator|&&
name|old
operator|.
name|isArray
argument_list|()
operator|&&
operator|!
name|propertyState
operator|.
name|isArray
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ValueFormatException
argument_list|(
literal|"Attempt to assign a single value to multi-valued property."
argument_list|)
throw|;
block|}
if|if
condition|(
name|old
operator|!=
literal|null
operator|&&
operator|!
name|old
operator|.
name|isArray
argument_list|()
operator|&&
name|propertyState
operator|.
name|isArray
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ValueFormatException
argument_list|(
literal|"Attempt to assign multiple values to single valued property."
argument_list|)
throw|;
block|}
name|tree
operator|.
name|setProperty
argument_list|(
name|propertyState
argument_list|)
expr_stmt|;
return|return
operator|new
name|PropertyDelegate
argument_list|(
name|sessionDelegate
argument_list|,
name|tree
operator|.
name|getLocation
argument_list|()
operator|.
name|getChild
argument_list|(
name|name
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Add a child node      *      * @param name oak name      * @return the added node or {@code null} if such a node already exists      */
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
name|getTree
argument_list|()
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
comment|/**      * Enables or disabled orderable children on the underlying tree.      *      * @param enable whether to enable or disable orderable children.      */
specifier|public
name|void
name|setOrderableChildren
parameter_list|(
name|boolean
name|enable
parameter_list|)
throws|throws
name|InvalidItemStateException
block|{
name|getTree
argument_list|()
operator|.
name|setOrderableChildren
argument_list|(
name|enable
argument_list|)
expr_stmt|;
block|}
comment|//------------------------------------------------------------< internal>---
annotation|@
name|Nonnull
comment|// FIXME this should be package private. OAK-672
specifier|public
name|Tree
name|getTree
parameter_list|()
throws|throws
name|InvalidItemStateException
block|{
name|Tree
name|tree
init|=
name|getLocation
argument_list|()
operator|.
name|getTree
argument_list|()
decl_stmt|;
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
argument_list|()
throw|;
block|}
return|return
name|tree
return|;
block|}
comment|// -----------------------------------------------------------< private>---
specifier|private
name|TreeLocation
name|getChildLocation
parameter_list|(
name|String
name|relPath
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|PathUtils
operator|.
name|isAbsolute
argument_list|(
name|relPath
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
literal|"Not a relative path: "
operator|+
name|relPath
argument_list|)
throw|;
block|}
name|TreeLocation
name|loc
init|=
name|getLocation
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|element
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
name|PathUtils
operator|.
name|denotesParent
argument_list|(
name|element
argument_list|)
condition|)
block|{
name|loc
operator|=
name|loc
operator|.
name|getParent
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|PathUtils
operator|.
name|denotesCurrent
argument_list|(
name|element
argument_list|)
condition|)
block|{
name|loc
operator|=
name|loc
operator|.
name|getChild
argument_list|(
name|element
argument_list|)
expr_stmt|;
block|}
comment|// else . -> skip to next element
block|}
return|return
name|loc
return|;
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
name|children
parameter_list|)
block|{
return|return
name|Iterators
operator|.
name|transform
argument_list|(
name|Iterators
operator|.
name|filter
argument_list|(
name|children
argument_list|,
operator|new
name|Predicate
argument_list|<
name|Tree
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
name|Tree
name|tree
parameter_list|)
block|{
return|return
operator|!
name|tree
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|":"
argument_list|)
return|;
block|}
block|}
argument_list|)
argument_list|,
operator|new
name|Function
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
name|tree
parameter_list|)
block|{
return|return
operator|new
name|NodeDelegate
argument_list|(
name|sessionDelegate
argument_list|,
name|tree
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
throws|throws
name|InvalidItemStateException
block|{
specifier|final
name|TreeLocation
name|location
init|=
name|getLocation
argument_list|()
decl_stmt|;
return|return
name|Iterators
operator|.
name|transform
argument_list|(
name|Iterators
operator|.
name|filter
argument_list|(
name|properties
argument_list|,
operator|new
name|Predicate
argument_list|<
name|PropertyState
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
name|PropertyState
name|property
parameter_list|)
block|{
return|return
operator|!
name|property
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|":"
argument_list|)
return|;
block|}
block|}
argument_list|)
argument_list|,
operator|new
name|Function
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
name|location
operator|.
name|getChild
argument_list|(
name|propertyState
operator|.
name|getName
argument_list|()
argument_list|)
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

