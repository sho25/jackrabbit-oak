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
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

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
name|HashSet
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
name|LinkedHashMap
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

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
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|JcrConstants
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
name|CoreValueFactory
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
specifier|public
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
name|getTree
argument_list|()
operator|.
name|getParent
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
return|return
name|getTree
argument_list|()
operator|.
name|getStatus
argument_list|()
return|;
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
comment|/**      * Determine whether this is the root node      * @return  {@code true} iff this is the root node      */
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
comment|/**      * Get the number of properties of the node      * @return  number of properties of the node      */
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
comment|/**      * Get the number of child nodes      * @return  number of child nodes of the node      */
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
comment|/**      * Returns an iterator for traversing all the children of this node.      * If the node is orderable (there is an {@link PropertyState#OAK_CHILD_ORDER}      * property) then the iterator will return child nodes in the specified      * order. Otherwise the ordering of the iterator is undefined.      *      * @return child nodes of the node      */
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
name|PropertyState
name|order
init|=
name|tree
operator|.
name|getProperty
argument_list|(
name|PropertyState
operator|.
name|OAK_CHILD_ORDER
argument_list|)
decl_stmt|;
if|if
condition|(
name|order
operator|==
literal|null
operator|||
operator|!
name|order
operator|.
name|isArray
argument_list|()
condition|)
block|{
comment|// No specified ordering
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
else|else
block|{
comment|// Collect child nodes in the specified order
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|NodeDelegate
argument_list|>
name|ordered
init|=
operator|new
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|NodeDelegate
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|CoreValue
name|value
range|:
name|order
operator|.
name|getValues
argument_list|()
control|)
block|{
name|String
name|name
init|=
name|value
operator|.
name|getString
argument_list|()
decl_stmt|;
name|Tree
name|child
init|=
name|tree
operator|.
name|getChild
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|child
operator|!=
literal|null
operator|&&
operator|!
name|name
operator|.
name|startsWith
argument_list|(
literal|":"
argument_list|)
condition|)
block|{
name|ordered
operator|.
name|put
argument_list|(
name|name
argument_list|,
operator|new
name|NodeDelegate
argument_list|(
name|sessionDelegate
argument_list|,
name|child
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|ordered
operator|.
name|size
argument_list|()
operator|==
name|count
condition|)
block|{
comment|// We have all the child nodes
return|return
name|ordered
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
return|;
block|}
else|else
block|{
comment|// The specified ordering didn't cover all the children,
comment|// so return a combined iterator that first iterates
comment|// through the ordered subset and then all the remaining
comment|// children in an undefined order
name|Iterator
argument_list|<
name|Tree
argument_list|>
name|remaining
init|=
name|Iterators
operator|.
name|filter
argument_list|(
name|tree
operator|.
name|getChildren
argument_list|()
operator|.
name|iterator
argument_list|()
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
name|ordered
operator|.
name|containsKey
argument_list|(
name|tree
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
return|return
name|Iterators
operator|.
name|concat
argument_list|(
name|ordered
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
argument_list|,
name|nodeDelegateIterator
argument_list|(
name|remaining
argument_list|)
argument_list|)
return|;
block|}
block|}
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
name|List
argument_list|<
name|CoreValue
argument_list|>
name|order
init|=
operator|new
name|ArrayList
argument_list|<
name|CoreValue
argument_list|>
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|added
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|CoreValueFactory
name|factory
init|=
name|sessionDelegate
operator|.
name|getContentSession
argument_list|()
operator|.
name|getCoreValueFactory
argument_list|()
decl_stmt|;
name|PropertyState
name|property
init|=
name|tree
operator|.
name|getProperty
argument_list|(
name|PropertyState
operator|.
name|OAK_CHILD_ORDER
argument_list|)
decl_stmt|;
if|if
condition|(
name|property
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|CoreValue
name|value
range|:
name|property
operator|.
name|getValues
argument_list|()
control|)
block|{
name|String
name|name
init|=
name|value
operator|.
name|getString
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|name
operator|.
name|equals
argument_list|(
name|source
argument_list|)
operator|&&
operator|!
name|added
operator|.
name|contains
argument_list|(
name|property
argument_list|)
operator|&&
operator|!
name|name
operator|.
name|startsWith
argument_list|(
literal|":"
argument_list|)
condition|)
block|{
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
name|target
argument_list|)
condition|)
block|{
name|order
operator|.
name|add
argument_list|(
name|factory
operator|.
name|createValue
argument_list|(
name|source
argument_list|)
argument_list|)
expr_stmt|;
name|added
operator|.
name|add
argument_list|(
name|source
argument_list|)
expr_stmt|;
block|}
name|order
operator|.
name|add
argument_list|(
name|factory
operator|.
name|createValue
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
name|added
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
operator|!
name|added
operator|.
name|contains
argument_list|(
name|source
argument_list|)
condition|)
block|{
name|order
operator|.
name|add
argument_list|(
name|factory
operator|.
name|createValue
argument_list|(
name|source
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|target
operator|!=
literal|null
operator|&&
operator|!
name|added
operator|.
name|contains
argument_list|(
name|target
argument_list|)
condition|)
block|{
name|order
operator|.
name|add
argument_list|(
name|factory
operator|.
name|createValue
argument_list|(
name|source
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|tree
operator|.
name|setProperty
argument_list|(
name|PropertyState
operator|.
name|OAK_CHILD_ORDER
argument_list|,
name|order
argument_list|)
expr_stmt|;
block|}
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
specifier|public
name|void
name|removeProperty
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|InvalidItemStateException
block|{
name|getTree
argument_list|()
operator|.
name|removeProperty
argument_list|(
name|name
argument_list|)
expr_stmt|;
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
name|getTree
argument_list|()
operator|.
name|remove
argument_list|()
expr_stmt|;
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
name|String
name|absPath
init|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|getPath
argument_list|()
argument_list|,
name|relPath
argument_list|)
decl_stmt|;
return|return
name|sessionDelegate
operator|.
name|getTree
argument_list|(
name|absPath
argument_list|)
return|;
block|}
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
name|tree
operator|.
name|getStatus
argument_list|()
operator|==
name|Status
operator|.
name|REMOVED
condition|?
literal|null
else|:
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
name|transform
argument_list|(
name|Iterators
operator|.
name|filter
argument_list|(
name|childNodeStates
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

