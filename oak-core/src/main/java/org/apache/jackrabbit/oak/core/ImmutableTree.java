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
name|Type
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
name|ReadOnlyBuilder
import|;
end_import

begin_comment
comment|/**  * Immutable implementation of the {@code Tree} interface in order to provide  * the much feature rich API functionality for a given {@code NodeState}.  *  *<h3>Tree hierarchy</h3>  * Due to the nature of this {@code Tree} implementation creating a proper  * hierarchical view of the tree structure is the responsibility of the caller.  * It is recommended to start with the state of the  * {@link #ImmutableTree(org.apache.jackrabbit.oak.spi.state.NodeState) root node}  * and build up the hierarchy by calling  * {@link #ImmutableTree(ImmutableTree, String, org.apache.jackrabbit.oak.spi.state.NodeState)}  * for every subsequent child state. Note, that this implementation will not  * perform any kind of validation of the passed state and methods like {@link #isRoot()},  * {@link #getName()} or {@link #getPath()} will just make use of the hierarchy that has been  * create by that sequence. In order to create a disconnected individual tree in cases where  * the hierarchy information is not (yet) need or known it is suggested to use  * {@link #ImmutableTree(org.apache.jackrabbit.oak.core.ImmutableTree.ParentProvider, String, org.apache.jackrabbit.oak.spi.state.NodeState)}  * an specify an appropriate {@code ParentProvider} implementation.  *  *<h3>ParentProvider</h3>  * Apart from create the tree hierarchy in traversal mode this tree implementation  * allows to instantiate disconnected trees that depending on the use may  * never or on demand retrieve hierarchy information. The following default  * implementations of this internal interface are present:  *  *<ul>  *<li>{@link DefaultParentProvider}: used with the default usage where the  *     parent tree is passed to the constructor</li>  *<li>{@link ParentProvider#ROOT_PROVIDER}: the default parent provider for  *     the root tree. All children will get {@link DefaultParentProvider}</li>  *<li>{@link ParentProvider#UNSUPPORTED}: throws {@code UnsupportedOperationException}  *     upon hierarchy related methods like {@link #getParent()}, {@link #getPath()}</li>  *</ul>  *  *<h3>Equality and hash code</h3>  * In contrast to {@link MutableTree} the {@code ImmutableTree} implements  * {@link Object#equals(Object)} and {@link Object#hashCode()}: Two {@code ImmutableTree}s  * are consider equal if their name and the underlying {@code NodeState}s are equal. Note  * however, that according to the contract defined in {@code NodeState} these  * objects are not expected to be used as hash keys.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|ImmutableTree
extends|extends
name|AbstractTree
block|{
comment|/**      * Underlying node state      */
specifier|private
specifier|final
name|NodeState
name|state
decl_stmt|;
specifier|private
specifier|final
name|ParentProvider
name|parentProvider
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
name|ROOT_PROVIDER
argument_list|,
literal|""
argument_list|,
name|rootState
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
name|super
argument_list|(
name|name
argument_list|,
operator|new
name|ReadOnlyBuilder
argument_list|(
name|state
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|state
operator|=
name|state
expr_stmt|;
name|this
operator|.
name|parentProvider
operator|=
name|parentProvider
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|ImmutableTree
name|createChild
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|ImmutableTree
argument_list|(
name|this
argument_list|,
name|name
argument_list|,
name|state
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
argument_list|)
return|;
block|}
comment|//---------------------------------------------------------------< Tree>---
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
name|path
operator|=
name|super
operator|.
name|getPath
argument_list|()
expr_stmt|;
block|}
return|return
name|path
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasChild
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|state
operator|.
name|hasChildNode
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Status
name|getStatus
parameter_list|()
block|{
return|return
name|Status
operator|.
name|EXISTING
return|;
block|}
annotation|@
name|Override
specifier|protected
name|boolean
name|isNew
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|protected
name|boolean
name|isModified
parameter_list|()
block|{
return|return
literal|false
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
name|Status
name|getPropertyStatus
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|hasProperty
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|Status
operator|.
name|EXISTING
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|ImmutableTree
name|getChild
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|createChild
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|Tree
name|addChild
parameter_list|(
name|String
name|name
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setOrderableChildren
parameter_list|(
name|boolean
name|enable
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|orderBefore
parameter_list|(
name|String
name|name
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setProperty
parameter_list|(
name|PropertyState
name|property
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
parameter_list|>
name|void
name|setProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|T
name|value
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
parameter_list|>
name|void
name|setProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|T
name|value
parameter_list|,
name|Type
argument_list|<
name|T
argument_list|>
name|type
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|removeProperty
parameter_list|(
name|String
name|name
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
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
literal|"ImmutableTree '"
operator|+
name|getName
argument_list|()
operator|+
literal|"':"
operator|+
name|state
operator|.
name|toString
argument_list|()
return|;
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
name|ROOT_PROVIDER
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
name|IllegalStateException
argument_list|(
literal|"root tree does not have a parent"
argument_list|)
throw|;
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
specifier|final
name|ImmutableTree
name|parent
decl_stmt|;
name|DefaultParentProvider
parameter_list|(
annotation|@
name|Nonnull
name|ImmutableTree
name|parent
parameter_list|)
block|{
name|this
operator|.
name|parent
operator|=
name|parent
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

