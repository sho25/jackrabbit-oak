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
name|core
package|;
end_package

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
name|base
operator|.
name|Objects
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
name|Root
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
name|nodetype
operator|.
name|NodeTypeConstants
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
name|version
operator|.
name|VersionConstants
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
name|security
operator|.
name|Context
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
name|ChildNodeEntry
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
name|NodeStateUtils
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
name|api
operator|.
name|Type
operator|.
name|STRING
import|;
end_import

begin_comment
comment|/**  * Immutable implementation of the {@code Tree} interface in order to provide  * the much feature rich API functionality for a given {@code NodeState}.  *  *<h3>Tree hierarchy</h3>  * Due to the nature of this {@code Tree} implementation creating a proper  * hierarchical view of the tree structure is the responsibility of the caller.  * It is recommended to start with the state of the  * {@link #ImmutableTree(org.apache.jackrabbit.oak.spi.state.NodeState) root node}  * and build up the hierarchy by calling  * {@link #ImmutableTree(ImmutableTree, String, org.apache.jackrabbit.oak.spi.state.NodeState)}  * for every subsequent child state. Note, that this implementation will not  * perform any kind of validation of the passed state and methods like {@link #isRoot()},  * {@link #getName()}, {@link #getPath()} or {@link #getLocation()} will just  * make use of the hierarchy that has been create by that sequence.  * In order to create a disconnected individual tree in cases where the hierarchy  * information is not (yet) need or known it is suggested to use  * {@link #ImmutableTree(org.apache.jackrabbit.oak.core.ImmutableTree.ParentProvider, String, org.apache.jackrabbit.oak.spi.state.NodeState)}  * an specify an appropriate {@code ParentProvider} implementation.  *  *<h3>ParentProvider</h3>  * Apart from create the tree hierarchy in traversal mode this tree implementation  * allows to instantiate disconnected trees that depending on the use may  * never or on demand retrieve hierarchy information. The following default  * implementations of this internal interface are present:  *  *<ul>  *<li>{@link DefaultParentProvider}: used with the default usage where the  *     parent tree is passed to the constructor</li>  *<li>{@link ParentProvider#ROOTPROVIDER}: the default parent provider for  *     the root tree. All children will get {@link DefaultParentProvider}</li>  *<li>{@link ParentProvider#UNSUPPORTED}: throws {@code UnsupportedOperationException}  *     upon hierarchy related methods like {@link #getParent()}, {@link #getPath()} and  *     {@link #getIdentifier()}</li>  *</ul>  *  *<h3>TypeProvider</h3>  * For optimization purpose an Immutable tree will be associated with a  * {@code TypeProvider} that allows for fast detection of the following types  * of Trees:  *  *<ul>  *<li>{@link TypeProvider#TYPE_HIDDEN}: a hidden tree whose name starts with ":".  *     Please note that the whole subtree of a hidden node is considered hidden.</li>  *<li>{@link TypeProvider#TYPE_AC}: A tree that stores access control content  *     and requires special access {@link org.apache.jackrabbit.oak.spi.security.authorization.permission.Permissions#READ_ACCESS_CONTROL permissions}.</li>  *<li>{@link TypeProvider#TYPE_VERSION}: if a given tree is located within  *     any of the version related stores defined by JSR 283. Depending on the  *     permission evaluation implementation those items require special treatment.</li>  *<li>{@link TypeProvider#TYPE_DEFAULT}: the default type for trees that don't  *     match any of the uper types.</li>  *</ul>  *  *<h3>Equality and hash code</h3>  * In contrast to {@link TreeImpl} the {@code ImmutableTree} implements  * {@link Object#equals(Object)} and {@link Object#hashCode()}: Two {@code ImmutableTree}s  * are consider equal if their name and the underlying {@code NodeState}s are equal. Note  * however, that according to the contract defined in {@code NodeState} these  * objects are not expected to be used as hash keys.  *  * FIXME: merge with ReadOnlyTree  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|ImmutableTree
extends|extends
name|ReadOnlyTree
block|{
specifier|private
specifier|final
name|ParentProvider
name|parentProvider
decl_stmt|;
specifier|private
specifier|final
name|TypeProvider
name|typeProvider
decl_stmt|;
specifier|private
name|String
name|path
decl_stmt|;
specifier|public
name|ImmutableTree
parameter_list|(
annotation|@
name|Nonnull
name|NodeState
name|rootState
parameter_list|)
block|{
name|this
argument_list|(
name|ParentProvider
operator|.
name|ROOTPROVIDER
argument_list|,
literal|""
argument_list|,
name|rootState
argument_list|,
name|TypeProvider
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ImmutableTree
parameter_list|(
annotation|@
name|Nonnull
name|NodeState
name|rootState
parameter_list|,
annotation|@
name|Nonnull
name|TypeProvider
name|typeProvider
parameter_list|)
block|{
name|this
argument_list|(
name|ParentProvider
operator|.
name|ROOTPROVIDER
argument_list|,
literal|""
argument_list|,
name|rootState
argument_list|,
name|typeProvider
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ImmutableTree
parameter_list|(
annotation|@
name|Nonnull
name|ImmutableTree
name|parent
parameter_list|,
annotation|@
name|Nonnull
name|String
name|name
parameter_list|,
annotation|@
name|Nonnull
name|NodeState
name|state
parameter_list|)
block|{
name|this
argument_list|(
operator|new
name|DefaultParentProvider
argument_list|(
name|parent
argument_list|)
argument_list|,
name|name
argument_list|,
name|state
argument_list|,
name|parent
operator|.
name|typeProvider
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ImmutableTree
parameter_list|(
annotation|@
name|Nonnull
name|ParentProvider
name|parentProvider
parameter_list|,
annotation|@
name|Nonnull
name|String
name|name
parameter_list|,
annotation|@
name|Nonnull
name|NodeState
name|state
parameter_list|)
block|{
name|this
argument_list|(
name|parentProvider
argument_list|,
name|name
argument_list|,
name|state
argument_list|,
name|TypeProvider
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ImmutableTree
parameter_list|(
annotation|@
name|Nonnull
name|ParentProvider
name|parentProvider
parameter_list|,
annotation|@
name|Nonnull
name|String
name|name
parameter_list|,
annotation|@
name|Nonnull
name|NodeState
name|state
parameter_list|,
annotation|@
name|Nonnull
name|TypeProvider
name|typeProvider
parameter_list|)
block|{
name|super
argument_list|(
literal|null
argument_list|,
name|name
argument_list|,
name|state
argument_list|)
expr_stmt|;
name|this
operator|.
name|parentProvider
operator|=
name|checkNotNull
argument_list|(
name|parentProvider
argument_list|)
expr_stmt|;
name|this
operator|.
name|typeProvider
operator|=
name|typeProvider
expr_stmt|;
block|}
specifier|public
specifier|static
name|ImmutableTree
name|createFromRoot
parameter_list|(
annotation|@
name|Nonnull
name|Root
name|root
parameter_list|,
annotation|@
name|Nonnull
name|TypeProvider
name|typeProvider
parameter_list|)
block|{
if|if
condition|(
name|root
operator|instanceof
name|RootImpl
condition|)
block|{
return|return
operator|new
name|ImmutableTree
argument_list|(
operator|(
operator|(
name|RootImpl
operator|)
name|root
operator|)
operator|.
name|getBaseState
argument_list|()
argument_list|,
name|typeProvider
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|root
operator|instanceof
name|ImmutableRoot
condition|)
block|{
return|return
operator|(
operator|(
name|ImmutableRoot
operator|)
name|root
operator|)
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unsupported Root implementation."
argument_list|)
throw|;
block|}
block|}
comment|//---------------------------------------------------------------< Tree>---
annotation|@
name|Override
specifier|public
name|boolean
name|isRoot
parameter_list|()
block|{
return|return
literal|""
operator|.
name|equals
argument_list|(
name|getName
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getPath
parameter_list|()
block|{
if|if
condition|(
name|path
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|isRoot
argument_list|()
condition|)
block|{
comment|// shortcut
name|path
operator|=
literal|"/"
expr_stmt|;
block|}
else|else
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|ImmutableTree
name|parent
init|=
name|getParent
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|parent
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|parent
operator|.
name|isRoot
argument_list|()
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|'/'
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|path
operator|=
name|sb
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|path
return|;
block|}
annotation|@
name|Override
specifier|public
name|ImmutableTree
name|getParent
parameter_list|()
block|{
return|return
name|parentProvider
operator|.
name|getParent
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|ImmutableTree
name|getChild
parameter_list|(
annotation|@
name|Nonnull
name|String
name|name
parameter_list|)
block|{
name|NodeState
name|child
init|=
name|state
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|child
operator|.
name|exists
argument_list|()
condition|)
block|{
return|return
operator|new
name|ImmutableTree
argument_list|(
name|this
argument_list|,
name|name
argument_list|,
name|child
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
comment|/**      * This implementation does not respect ordered child nodes, but always      * returns them in some implementation specific order.      *<p/>      * TODO: respect orderable children (needed?)      *      * @return the children.      */
annotation|@
name|Override
specifier|public
name|Iterable
argument_list|<
name|Tree
argument_list|>
name|getChildren
parameter_list|()
block|{
return|return
operator|new
name|Iterable
argument_list|<
name|Tree
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|Tree
argument_list|>
name|iterator
parameter_list|()
block|{
specifier|final
name|Iterator
argument_list|<
name|?
extends|extends
name|ChildNodeEntry
argument_list|>
name|iterator
init|=
name|state
operator|.
name|getChildNodeEntries
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
return|return
operator|new
name|Iterator
argument_list|<
name|Tree
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|iterator
operator|.
name|hasNext
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Tree
name|next
parameter_list|()
block|{
name|ChildNodeEntry
name|entry
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
return|return
operator|new
name|ImmutableTree
argument_list|(
name|ImmutableTree
operator|.
name|this
argument_list|,
name|entry
operator|.
name|getName
argument_list|()
argument_list|,
name|entry
operator|.
name|getNodeState
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
return|;
block|}
block|}
return|;
block|}
comment|//-------------------------------------------------------------< Object>---
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|hashCode
argument_list|(
name|getName
argument_list|()
argument_list|,
name|state
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|==
name|this
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|o
operator|instanceof
name|ImmutableTree
condition|)
block|{
name|ImmutableTree
name|other
init|=
operator|(
name|ImmutableTree
operator|)
name|o
decl_stmt|;
return|return
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|other
operator|.
name|getName
argument_list|()
argument_list|)
operator|&&
name|state
operator|.
name|equals
argument_list|(
name|other
operator|.
name|state
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
operator|new
name|StringBuilder
argument_list|()
operator|.
name|append
argument_list|(
literal|"ImmutableTree '"
argument_list|)
operator|.
name|append
argument_list|(
name|getName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"':"
argument_list|)
operator|.
name|append
argument_list|(
name|state
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
comment|//--------------------------------------------------------------------------
specifier|public
name|NodeState
name|getNodeState
parameter_list|()
block|{
return|return
name|state
return|;
block|}
specifier|public
name|int
name|getType
parameter_list|()
block|{
return|return
name|typeProvider
operator|.
name|getType
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|// TODO
specifier|public
specifier|static
name|int
name|getType
parameter_list|(
name|Tree
name|tree
parameter_list|)
block|{
if|if
condition|(
name|tree
operator|instanceof
name|ImmutableTree
condition|)
block|{
return|return
operator|(
operator|(
name|ImmutableTree
operator|)
name|tree
operator|)
operator|.
name|getType
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|TypeProvider
operator|.
name|TYPE_DEFAULT
return|;
block|}
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
name|String
name|getIdentifier
parameter_list|()
block|{
name|PropertyState
name|property
init|=
name|state
operator|.
name|getProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_UUID
argument_list|)
decl_stmt|;
if|if
condition|(
name|property
operator|!=
literal|null
condition|)
block|{
return|return
name|property
operator|.
name|getValue
argument_list|(
name|STRING
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|getParent
argument_list|()
operator|.
name|isRoot
argument_list|()
condition|)
block|{
return|return
literal|"/"
return|;
block|}
else|else
block|{
return|return
name|PathUtils
operator|.
name|concat
argument_list|(
name|getParent
argument_list|()
operator|.
name|getIdentifier
argument_list|()
argument_list|,
name|getName
argument_list|()
argument_list|)
return|;
block|}
block|}
comment|//--------------------------------------------------------------------------
comment|// TODO
specifier|public
interface|interface
name|TypeProvider
block|{
comment|// regular trees
name|int
name|TYPE_DEFAULT
init|=
literal|1
decl_stmt|;
comment|// version store(s) content
name|int
name|TYPE_VERSION
init|=
literal|2
decl_stmt|;
comment|// access control content
name|int
name|TYPE_AC
init|=
literal|4
decl_stmt|;
comment|// node type definition content
name|int
name|TYPE_NODE_TYPE
init|=
literal|8
decl_stmt|;
comment|// hidden trees
name|int
name|TYPE_HIDDEN
init|=
literal|16
decl_stmt|;
name|TypeProvider
name|EMPTY
init|=
operator|new
name|TypeProvider
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|getType
parameter_list|(
annotation|@
name|Nullable
name|ImmutableTree
name|tree
parameter_list|)
block|{
return|return
name|TYPE_DEFAULT
return|;
block|}
block|}
decl_stmt|;
name|int
name|getType
parameter_list|(
name|ImmutableTree
name|tree
parameter_list|)
function_decl|;
block|}
specifier|public
specifier|static
specifier|final
class|class
name|DefaultTypeProvider
implements|implements
name|TypeProvider
block|{
specifier|private
specifier|final
name|Context
name|contextInfo
decl_stmt|;
specifier|public
name|DefaultTypeProvider
parameter_list|(
annotation|@
name|Nonnull
name|Context
name|contextInfo
parameter_list|)
block|{
name|this
operator|.
name|contextInfo
operator|=
name|contextInfo
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getType
parameter_list|(
name|ImmutableTree
name|tree
parameter_list|)
block|{
name|ImmutableTree
name|parent
init|=
name|tree
operator|.
name|getParent
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
name|TYPE_DEFAULT
return|;
block|}
name|int
name|type
decl_stmt|;
switch|switch
condition|(
name|parent
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|TYPE_HIDDEN
case|:
name|type
operator|=
name|TYPE_HIDDEN
expr_stmt|;
break|break;
case|case
name|TYPE_NODE_TYPE
case|:
name|type
operator|=
name|TYPE_NODE_TYPE
expr_stmt|;
break|break;
case|case
name|TYPE_VERSION
case|:
name|type
operator|=
name|TYPE_VERSION
expr_stmt|;
break|break;
case|case
name|TYPE_AC
case|:
name|type
operator|=
name|TYPE_AC
expr_stmt|;
break|break;
default|default:
name|String
name|name
init|=
name|tree
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|NodeStateUtils
operator|.
name|isHidden
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|type
operator|=
name|TYPE_HIDDEN
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|NodeTypeConstants
operator|.
name|JCR_NODE_TYPES
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|type
operator|=
name|TYPE_NODE_TYPE
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|VersionConstants
operator|.
name|VERSION_NODE_NAMES
operator|.
name|contains
argument_list|(
name|name
argument_list|)
operator|||
name|VersionConstants
operator|.
name|VERSION_NODE_TYPE_NAMES
operator|.
name|contains
argument_list|(
name|NodeStateUtils
operator|.
name|getPrimaryTypeName
argument_list|(
name|tree
operator|.
name|state
argument_list|)
argument_list|)
condition|)
block|{
name|type
operator|=
name|TYPE_VERSION
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|contextInfo
operator|.
name|definesTree
argument_list|(
name|tree
argument_list|)
condition|)
block|{
name|type
operator|=
name|TYPE_AC
expr_stmt|;
block|}
else|else
block|{
name|type
operator|=
name|TYPE_DEFAULT
expr_stmt|;
block|}
block|}
return|return
name|type
return|;
block|}
block|}
comment|//--------------------------------------------------------------------------
specifier|public
interface|interface
name|ParentProvider
block|{
name|ParentProvider
name|UNSUPPORTED
init|=
operator|new
name|ParentProvider
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|ImmutableTree
name|getParent
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"not supported."
argument_list|)
throw|;
block|}
block|}
decl_stmt|;
name|ParentProvider
name|ROOTPROVIDER
init|=
operator|new
name|ParentProvider
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|ImmutableTree
name|getParent
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
decl_stmt|;
annotation|@
name|CheckForNull
name|ImmutableTree
name|getParent
parameter_list|()
function_decl|;
block|}
specifier|public
specifier|static
specifier|final
class|class
name|DefaultParentProvider
implements|implements
name|ParentProvider
block|{
specifier|private
name|ImmutableTree
name|parent
decl_stmt|;
name|DefaultParentProvider
parameter_list|(
name|ImmutableTree
name|parent
parameter_list|)
block|{
name|this
operator|.
name|parent
operator|=
name|checkNotNull
argument_list|(
name|parent
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|ImmutableTree
name|getParent
parameter_list|()
block|{
return|return
name|parent
return|;
block|}
block|}
block|}
end_class

end_unit

