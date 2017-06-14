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
name|spi
operator|.
name|state
package|;
end_package

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

begin_comment
comment|/**  * {@code AbstractRebaseDiff} serves as base for rebase implementations.  * It implements a {@link NodeStateDiff}, which performs the conflict  * handling as defined in {@link org.apache.jackrabbit.oak.spi.state.NodeStore#rebase(NodeBuilder)}  * on the Oak SPI state level.  *<p>  * Intended use of this class is to re-base a branched version of the node state  * tree. Given below situation:  *<pre>  *     + head (master)  *     |  *     | + branch  *     |/  *     + base  *     |  *</pre>  * The current state on the master branch is {@code head} and a branch  * was created at {@code base}. The current state on the branch is  * {@code branch}. Re-basing {@code branch} to the current  * {@code head} works as follows:  *<pre>  *     NodeState head = ...  *     NodeState branch = ...  *     NodeState base = ...  *     NodeBuilder builder = new MemoryNodeBuilder(head);  *     branch.compareAgainstBaseState(base, new MyRebaseDiff(builder));  *     branch = builder.getNodeState();  *</pre>  * The result is:  *<pre>  *       + branch  *      /  *     + head (master)  *     |  *</pre>  *<p>  * Conflicts during rebase cause calls to the various abstracts conflict resolution  * methods of this class. Concrete subclasses of this class need to implement these  * methods for handling such conflicts.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractRebaseDiff
implements|implements
name|NodeStateDiff
block|{
specifier|private
specifier|final
name|NodeBuilder
name|builder
decl_stmt|;
specifier|protected
name|AbstractRebaseDiff
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|)
block|{
name|this
operator|.
name|builder
operator|=
name|builder
expr_stmt|;
block|}
comment|/**      * Factory method for creating a rebase handler for the named child of the passed      * parent builder.      *      * @param builder  parent builder      * @param name  name of the child for which to return a rebase handler      * @return  rebase handler for child {@code name} in {@code builder}      */
specifier|protected
specifier|abstract
name|AbstractRebaseDiff
name|createDiff
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|,
name|String
name|name
parameter_list|)
function_decl|;
comment|/**      * Called when the property {@code after} was added on the branch but the property      * exists already in the trunk.      *      * @param builder  parent builder      * @param before existing property      * @param after  added property      */
specifier|protected
specifier|abstract
name|void
name|addExistingProperty
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|,
name|PropertyState
name|before
parameter_list|,
name|PropertyState
name|after
parameter_list|)
function_decl|;
comment|/**      * Called when the property {@code after} was changed on the branch but was      * deleted already in the trunk.      *      * @param builder  parent builder      * @param after  changed property      * @param base  base property      */
specifier|protected
specifier|abstract
name|void
name|changeDeletedProperty
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|,
name|PropertyState
name|after
parameter_list|,
name|PropertyState
name|base
parameter_list|)
function_decl|;
comment|/**      * Called when the property {@code after} was changed on the branch but was      * already changed to {@code before} in the trunk.      *      * @param builder  parent property      * @param before  changed property in branch      * @param after  changed property in trunk      */
specifier|protected
specifier|abstract
name|void
name|changeChangedProperty
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|,
name|PropertyState
name|before
parameter_list|,
name|PropertyState
name|after
parameter_list|)
function_decl|;
comment|/**      * Called when the property {@code before} was deleted in the branch but was      * already deleted in the trunk.      *      * @param builder  parent builder      * @param before  deleted property      */
specifier|protected
specifier|abstract
name|void
name|deleteDeletedProperty
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|,
name|PropertyState
name|before
parameter_list|)
function_decl|;
comment|/**      * Called when the property {@code before} was deleted in the branch but was      * already changed in the trunk.      *      * @param builder  parent builder      * @param before  deleted property      */
specifier|protected
specifier|abstract
name|void
name|deleteChangedProperty
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|,
name|PropertyState
name|before
parameter_list|)
function_decl|;
comment|/**      * Called when the node {@code after} was added on the branch but the node      * exists already in the trunk.      *      * @param builder  parent builder      * @param name  name of the added node      * @param before existing node      * @param after  added added      */
specifier|protected
specifier|abstract
name|void
name|addExistingNode
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|,
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
function_decl|;
comment|/**      * Called when the node {@code after} was changed on the branch but was      * deleted already in the trunk.      *      * @param builder  parent builder      * @param name  name of the changed node      * @param after  changed node      * @param base  base node      */
specifier|protected
specifier|abstract
name|void
name|changeDeletedNode
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|,
name|String
name|name
parameter_list|,
name|NodeState
name|after
parameter_list|,
name|NodeState
name|base
parameter_list|)
function_decl|;
comment|/**      * Called when the node {@code before} was deleted in the branch but was      * already deleted in the trunk.      *      * @param builder  parent builder      * @param before  deleted node      */
specifier|protected
specifier|abstract
name|void
name|deleteDeletedNode
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|,
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|)
function_decl|;
comment|/**      * Called when the node {@code before} was deleted in the branch but was      * already changed in the trunk.      *      * @param builder  parent builder      * @param before  deleted node      */
specifier|protected
specifier|abstract
name|void
name|deleteChangedNode
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|,
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|)
function_decl|;
annotation|@
name|Override
specifier|public
name|boolean
name|propertyAdded
parameter_list|(
name|PropertyState
name|after
parameter_list|)
block|{
name|PropertyState
name|other
init|=
name|builder
operator|.
name|getProperty
argument_list|(
name|after
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|other
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|setProperty
argument_list|(
name|after
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|other
operator|.
name|equals
argument_list|(
name|after
argument_list|)
condition|)
block|{
name|addExistingProperty
argument_list|(
name|builder
argument_list|,
name|other
argument_list|,
name|after
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|propertyChanged
parameter_list|(
name|PropertyState
name|before
parameter_list|,
name|PropertyState
name|after
parameter_list|)
block|{
name|PropertyState
name|other
init|=
name|builder
operator|.
name|getProperty
argument_list|(
name|before
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|other
operator|==
literal|null
condition|)
block|{
name|changeDeletedProperty
argument_list|(
name|builder
argument_list|,
name|after
argument_list|,
name|before
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|other
operator|.
name|equals
argument_list|(
name|before
argument_list|)
condition|)
block|{
name|builder
operator|.
name|setProperty
argument_list|(
name|after
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|other
operator|.
name|equals
argument_list|(
name|after
argument_list|)
condition|)
block|{
name|changeChangedProperty
argument_list|(
name|builder
argument_list|,
name|before
argument_list|,
name|after
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|propertyDeleted
parameter_list|(
name|PropertyState
name|before
parameter_list|)
block|{
name|PropertyState
name|other
init|=
name|builder
operator|.
name|getProperty
argument_list|(
name|before
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|other
operator|==
literal|null
condition|)
block|{
name|deleteDeletedProperty
argument_list|(
name|builder
argument_list|,
name|before
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|other
operator|.
name|equals
argument_list|(
name|before
argument_list|)
condition|)
block|{
name|builder
operator|.
name|removeProperty
argument_list|(
name|before
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|deleteChangedProperty
argument_list|(
name|builder
argument_list|,
name|before
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
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
name|builder
operator|.
name|hasChildNode
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|after
operator|.
name|compareAgainstBaseState
argument_list|(
name|EMPTY_NODE
argument_list|,
name|createDiff
argument_list|(
name|builder
argument_list|,
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|setChildNode
argument_list|(
name|name
argument_list|,
name|after
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
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
name|builder
operator|.
name|hasChildNode
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|after
operator|.
name|compareAgainstBaseState
argument_list|(
name|before
argument_list|,
name|createDiff
argument_list|(
name|builder
argument_list|,
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|after
operator|.
name|equals
argument_list|(
name|before
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
name|changeDeletedNode
argument_list|(
name|builder
argument_list|,
name|name
argument_list|,
name|after
argument_list|,
name|before
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
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
operator|!
name|builder
operator|.
name|hasChildNode
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|deleteDeletedNode
argument_list|(
name|builder
argument_list|,
name|name
argument_list|,
name|before
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|before
operator|.
name|equals
argument_list|(
name|builder
operator|.
name|child
argument_list|(
name|name
argument_list|)
operator|.
name|getNodeState
argument_list|()
argument_list|)
condition|)
block|{
name|builder
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|deleteChangedNode
argument_list|(
name|builder
argument_list|,
name|name
argument_list|,
name|before
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

